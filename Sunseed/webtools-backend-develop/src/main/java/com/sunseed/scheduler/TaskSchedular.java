package com.sunseed.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sunseed.repository.RunsRepository;
import com.sunseed.entity.Runs;
import com.sunseed.enums.RunStatus;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.model.requestDTO.Simulation;
import com.sunseed.serviceImpl.RunServiceImpl;

import jakarta.persistence.EntityManager;

@Component
public class TaskSchedular {

	@Autowired
	private RunsRepository runsRepository;

	@Autowired
	private RunServiceImpl runService;

	@Autowired
	private EntityManager entityManager;

	private static final Logger logger = LoggerFactory.getLogger(TaskSchedular.class);

//    @Scheduled(cron = "0 0 * * * *") // Runs at the start of every hour
	@Scheduled(cron = "0 * * * * *") // Every 1 minute
	public void executeHourlyTask() {
		logger.info("Hourly task started at {}", System.currentTimeMillis());

		// Task logic here
		performTask();

		logger.info("Hourly task completed at {}", System.currentTimeMillis());
	}

	private void performTask() {
		// Your task implementation here
		logger.info("Executing scheduled task...");
		List<Runs> runList = runsRepository.getRunsWithCompletedSimulationsByStatus(RunStatus.RUNNING.toString());

		List<Long> simulatedIds;

		// setting simulatedIds
		simulatedIds = runList.stream().filter(t -> t.getSimulatedRun() != null).map(t -> {
			if (t.getSimulatedRun() != null && t.getSimulatedRun().getSimulatedId() == null)
				throw new ResourceNotFoundException("simulation.id.not.matched");
			return t.getSimulatedRun().getSimulatedId();
		}).collect(Collectors.toList());

		// now getting simulation data from simtool
		List<Simulation> simulationResult = findDataFromSimtool(simulatedIds);

		// creating map for faster retrieval
		Map<Long, Simulation> simulationResultMap = new HashMap<>();

		simulationResult.forEach(t -> simulationResultMap.put(t.getRunId().longValue(), t));

		for (Runs run : runList) {

			if (simulationResultMap.containsKey(run.getRunId())) {
				Simulation s = simulationResultMap.get(run.getRunId());
//                Runs run = runsRepository.getReferenceById(run.getRunId());

				if (s.getStatus().equals("SUCCESS")) {
					if (!run.getRunStatus().equals(RunStatus.COMPLETED))
						runService.updateRunStatusOnly(run, RunStatus.COMPLETED);
//                    runResponseDto.setRunStatus(RunStatus.COMPLETED.getValue());
				} else if (s.getStatus().equals("FAILED")) {
					if (!run.getRunStatus().equals(RunStatus.FAILED))
						runService.updateRunStatusOnly(run, RunStatus.FAILED);
//                    runResponseDto.setRunStatus(RunStatus.FAILED.getValue());
				}

			}

		}

	}

	public List<Simulation> findDataFromSimtool(List<Long> id) {
		String sql = "Select * from simtool.simulations r where r.id IN :id";

		List<Object[]> results = entityManager.createNativeQuery(sql).setParameter("id", id).getResultList();
		return results.stream()
				.map(result -> new Simulation(((Number) result[0]).longValue(), ((Number) result[1]).longValue(),
						((Number) result[2]).longValue(), ((Number) result[3]).longValue(),
						((Number) result[4]).longValue(), ((Long) result[12]).longValue(), (String) result[13]))
				.collect(Collectors.toList());
	}
}
