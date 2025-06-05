package com.sunseed.simtool.scheduler;

import com.sunseed.simtool.constant.SceneType;
import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.Scene;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationBlock;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.repository.SimulationRepository;
import com.sunseed.simtool.service.DatFileProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DliProcessingScheduler {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final int MAX_RETRIES = 2;

	private final DatFileProcessorService datFileProcessorService;
	private final SimulationRepository simulationRepository;

	public void scheduleDliProcessing(Simulation simulation) {
		Runnable task = createProcessingTask(simulation, 0);
		scheduler.schedule(task, 5, TimeUnit.MINUTES);
	}

	private Runnable createProcessingTask(Simulation simulation, int attempt) {
		return () -> {
			log.debug("Starting DLI output generation attempt {} for simulation ID {}", attempt + 1,
					simulation.getId());

			try {
				if (simulation.getStatus() != Status.SUCCESS) {
					log.debug("Simulation ID {} is not in SUCCESS status. Skipping DLI generation.",
							simulation.getId());
					return;
				}

				for (SimulationBlock simBlock : simulation.getSimulationBlock()) {
					List<String> radiationFiles = simBlock.getSimulationTasks().stream()
							.filter(task -> task.getScenes() != null).flatMap(task -> task.getScenes().stream())
							.filter(scene -> {
								if (scene.getType() == null) {
									log.debug("Scene type is null. Skipping scene in simulation block ID {}",
											simBlock.getId());
									return false;
								}
								return scene.getType().equals(SceneType.radiation);
							}).map(Scene::getUrl).collect(Collectors.toList());

					if (radiationFiles.isEmpty()) {
						log.debug(
								"No radiation files found for simulation block ID {}. Skipping DLI generation for this block.",
								simBlock.getId());
						continue;
					}

					String result = datFileProcessorService.processFilesAndMultiply(radiationFiles,simBlock.getBlockIndex());
					String[] parts = result.split(",");

					String outputPath = parts[0];
					double minDli = Double.parseDouble(parts[1].split(":")[1].trim());
					double maxDli = Double.parseDouble(parts[2].split(":")[1].trim());

					SimulationTask lastTask = simBlock.getSimulationTasks().stream()
							.max(Comparator.comparing(SimulationTask::getId))
							.orElseThrow(() -> new IllegalStateException(
									"No SimulationTask found for SimulationBlock ID " + simBlock.getId()));

					Scene dliScene = new Scene();
					dliScene.setSimulationTask(lastTask);
					dliScene.setUrl(outputPath);
					dliScene.setType(SceneType.dli_output);
					dliScene.setMinimum(BigDecimal.valueOf(minDli));
					dliScene.setMaximum(BigDecimal.valueOf(maxDli));

					lastTask.getScenes().add(dliScene);
				}

				simulationRepository.save(simulation);
				log.debug("DLI output scenes saved successfully for simulation ID {}", simulation.getId());

			} catch (Exception e) {
				log.warn("Error during DLI output generation for simulation ID {} on attempt {}: {}",
						simulation.getId(), attempt + 1, e.getMessage(), e);

				if (attempt + 1 < MAX_RETRIES) {
					log.info("Retrying DLI output generation for simulation ID {} (retry {}/{})", simulation.getId(),
							attempt + 2, MAX_RETRIES);
					scheduler.schedule(createProcessingTask(simulation, attempt + 1), 5, TimeUnit.MINUTES);
				} else {
					simulation.setComment("DLI output generation failed after retries");
					simulationRepository.save(simulation);
					log.error("DLI output generation failed completely for simulation ID {}", simulation.getId());
				}
			}
		};
	}
}
