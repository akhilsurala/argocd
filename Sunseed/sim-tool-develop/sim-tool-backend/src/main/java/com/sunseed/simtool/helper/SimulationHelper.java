package com.sunseed.simtool.helper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Session;
import com.rabbitmq.client.Channel;
import com.sunseed.simtool.config.SimulationServer;
import com.sunseed.simtool.constant.SceneType;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.exception.TaskTimeExceededException;
import com.sunseed.simtool.model.BaseServer;
import com.sunseed.simtool.model.E2EServer;
import com.sunseed.simtool.rabbitmq.MessageProducer;
import com.sunseed.simtool.repository.SimulationRepository;
import com.sunseed.simtool.repository.SimulationTaskRepository;
import com.sunseed.simtool.service.ResourceReleaserService;
import com.sunseed.simtool.util.LogUtils;
import com.sunseed.simtool.util.ProcessBuilderUtils;
import com.sunseed.simtool.util.SimulationUtils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimulationHelper {

	private SimulationTaskRepository simulationTaskRepository;
	private ObjectMapper objectMapper;
	private List<SimulationServer> simulationServers;
	private MessageProducer messageProducer;
	private String exchangeName;
	private String routingKey;
	private ConcurrentHashMap<String, Future<?>> tasksFuture;
	private ExecutorService executorService;
	private String heliosLocation;
	private String bifacialLocation;
	private Integer simulationMaxWaitTime;
	private SimulationRepository simulationRepository;

	@Autowired
	private ResourceReleaserService resourceReleaserService;

	@Value("${status-monitor.timeout-minutes}")
	private int timeoutMinutes;

	@Value("${task.exceeded.time.to.requeue}")
	private boolean timeExceededTaskToBeRequeued;

	public SimulationHelper(SimulationTaskRepository simulationTaskRepository, ObjectMapper objectMapper,
			List<SimulationServer> simulationServers, MessageProducer messageProducer,
			@Value("${rabbitmq.exchange}") String exchangeName,
			@Value("${rabbitmq.routingkey.result}") String routingKey, ExecutorService executorService,
			ConcurrentHashMap<String, Future<?>> tasksFuture, @Value("${helios.file.location}") String heliosLocation,
			@Value("${bifacial.file.location}") String bifacialLocation,
			@Value("${simulation.max-wait-time.minutes}") Integer simulationMaxWaitTime,
			SimulationRepository simulationRepository) {
		super();
		this.simulationTaskRepository = simulationTaskRepository;
		this.objectMapper = objectMapper;
		this.simulationServers = simulationServers;
		this.messageProducer = messageProducer;
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
//		this.tasksFuture = new ConcurrentHashMap<>();
		this.executorService = executorService;
		this.tasksFuture = tasksFuture;
		this.heliosLocation = heliosLocation;
		this.bifacialLocation = bifacialLocation;
		this.simulationMaxWaitTime = simulationMaxWaitTime;
		this.simulationRepository = simulationRepository;
	}

	public Boolean isResourceAvailable(SceneType sceneType, Integer cpuRequired) {
		SimulationServer simulationServer = simulationServers.stream()
				.filter(server -> sceneType.toString().equalsIgnoreCase(server.getType())
						&& server.getCpu().availablePermits() >= cpuRequired)
				.findFirst().orElse(null);
		
		if (simulationServer == null) {
			return false;
		}

		Session session = null;
		try {
			// Use session pool to get or reuse an existing session
			session = ProcessBuilderUtils.getSession(simulationServer);

			// Check if session is connected and resources are available
			if (session != null && session.isConnected()) {
				boolean hasRequiredResources = simulationServer.getCpu().availablePermits() >= cpuRequired;
				return hasRequiredResources;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public boolean acquireSession(BaseServer baseServer, SceneType sceneType, SimulationTask simulationTask) {
		Session session = null;
		if (baseServer instanceof E2EServer) {
			try {

				log.debug("Trying to acquire resource for host : {} for taskId: {}" + baseServer.getHost(),
						simulationTask.getId());
				// Use session pool to get or reuse an existing session
				session = ProcessBuilderUtils.getSession(baseServer);

				// Check if session is connected and resources are available
				if (session != null && session.isConnected()) {
					return true;
				} else {
					// Session not connected, release the load and retry for another server
					log.warn("Session not connected for server: {}. Releasing assigned load. for taskId: {}",
							baseServer.getHost(), simulationTask.getId());
					resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);
					return false;
				}
			} catch (Exception e) {
				// Session not connected, release the load and retry for another server
				log.warn("Session not connected for server: {}. Releasing assigned load. for taskId: {}",
						baseServer.getHost(), simulationTask.getId());
				resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);
				return false;
			}
		} else if (baseServer instanceof SimulationServer) {
			SimulationServer simulationServer = simulationServers.stream()
					.filter(server -> sceneType.toString().equalsIgnoreCase(server.getType())
							&& server.getCpu().availablePermits() >= simulationTask.getCpuRequired())
					.findFirst().orElse(null);

			if (simulationServer == null) {
				return false;
			}

			try {
				// Use session pool to get or reuse an existing session
				session = ProcessBuilderUtils.getSession(simulationServer);

				// Check if session is connected and resources are available
				if (session != null && session.isConnected()) {
					boolean hasRequiredResources = simulationServer.getCpu().availablePermits() >= simulationTask
							.getCpuRequired();
					return hasRequiredResources;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public SimulationServer acquireResource(SceneType sceneType, Integer cpuRequired) {
		SimulationServer simulationServer = simulationServers.stream()
				.filter(server -> sceneType.toString().equalsIgnoreCase(server.getType())
						&& server.getCpu().tryAcquire(cpuRequired))
				.findFirst().orElse(null);

		return simulationServer;
	}

	public void runSimulation(SimulationTask simulationTask, BaseServer baseServer, Channel channel, long tag,
			SceneType sceneType) {

		log.info("Starting simulation .... ");

		log.info("Checking simulation status for cancellation and pause for taskId: {}",simulationTask.getId());

		// if task is not running then acknowledge it release resources
		if (!isTaskUpdatedToRunning(simulationTask.getId())) {

			acknowledge(channel, tag);
			resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);

			log.info("Simulation is cancelled or paused, failed to start");
			return;
		}

		// uuid for logging
		String uuid = MDC.get("uuid");

		if (sceneType.equals(SceneType.BIFACIAL)) {
			Future<?> future = executorService.submit(() -> {
				log.info("before bifacial");
				MDC.put("uuid", uuid);
				runBifacialSimulation(simulationTask, channel, tag);
				log.info("after bifacial");
			});

			tasksFuture.put(simulationTask.getId() + "_bifacial", future);
		} else if (sceneType.equals(SceneType.HELIOS)) {
			Future<?> future = executorService.submit(() -> {
				MDC.put("uuid", uuid);
				log.info("Inside Helios service---------");
				try {
					runHeliosSimulation(simulationTask,baseServer, channel, tag);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				log.info("Exit Helios service---------");
			});

			tasksFuture.put(simulationTask.getId() + "_helios", future);
		}

		log.info("Simulation started .... ");
	}

	public void runBifacialSimulation(SimulationTask simulationTask, Channel channel, long tag) {

		log.info("Acquiring resources ....");

		SimulationServer simulationServer = acquireResource(SceneType.BIFACIAL, 1); // change to BIFACIAL

		while (simulationServer == null) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				log.debug("{}", e);
			}
			simulationServer = acquireResource(SceneType.BIFACIAL, 1); // BIFACIAL
		}

		log.info("Resource acquired successfully");

		log.info("Creating command for PV execution");

		log.info("Before cmd");

		String command = createBifacialCommandLineArgs(simulationTask.getSimulation().getRunPayload(),
				simulationTask.getWeatherCondition(), simulationTask);

		log.debug("Bifacial CMD ARGS:    " + command);
		log.info("After cmd");

		String result = null;

		try {

			log.info("Submitting command on resource");

			long startTime = System.currentTimeMillis();

			result = ProcessBuilderUtils.runCommandBifacial(simulationServer, command, simulationMaxWaitTime);

			long endTime = System.currentTimeMillis();
			long taskExecutionTimeOnServer = endTime - startTime;

			acknowledge(channel, tag);

			processResult(result, simulationTask, SceneType.BIFACIAL, simulationServer, taskExecutionTimeOnServer);

		} catch (Exception e) {

			log.debug("{}", e);

			try {

				if (simulationTaskCancelledOrPausedOrSuccess(simulationTask.getId(), SceneType.BIFACIAL)) {

					log.info("Simulation Run cancelled/paused");

					channel.basicNack(tag, false, false);
				} else {
					log.info("Encountered an exception, Sending unacknowledgement for tag {} and requeing ", tag);
					channel.basicNack(tag, false, true);

				}
			} catch (IOException e1) {
				log.debug("{}", e1);
			}
		} finally {

			log.info("Releasing the acquired resources .... ");
			simulationServer.getCpu().release(1);
			tasksFuture.remove(simulationTask.getId() + "_bifacial");
		}

	}

	public void runHeliosSimulation(SimulationTask simulationTask,BaseServer baseServer, Channel channel, long tag) {

		if (baseServer instanceof SimulationServer) {
			log.info("Acquiring resources ....");
			SimulationServer simulationServer = acquireResource(SceneType.HELIOS, simulationTask.getCpuRequired());

			while (simulationServer == null) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					log.debug("{}", e);
				}
				simulationServer = acquireResource(SceneType.HELIOS, simulationTask.getCpuRequired());
			}
		}

		log.info("Resource acquired successfully");

		log.info("Creating command for Agri execution");
		log.info("Enter In simulation before cmd");

		String command = null;
		try {
			command = createHeliosCommandLineArgs(simulationTask.getSimulation().getRunPayload(),
					simulationTask.getWeatherCondition(), simulationTask);
		} catch (Exception e) {
			log.debug("Exception occured during command creation: for taskId: {}",simulationTask.getId());
			log.debug("{}", e);
			handleCommandCreationError(e, simulationTask, channel, tag, baseServer);
			return;
		}
		log.debug("Helios CMD ARGS:    " + command);
		log.info("Helios cmd " + command);

		String result = null;

		try {

			log.info("Submitting command on resource");

			long startTime = System.currentTimeMillis();

			result = ProcessBuilderUtils.runCommandWithXvfb(baseServer, command, simulationMaxWaitTime);

			long endTime = System.currentTimeMillis();
			long taskExecutionTimeOnServer = endTime - startTime;

			acknowledge(channel, tag);

			processResult(result, simulationTask, SceneType.HELIOS, baseServer, taskExecutionTimeOnServer);

		} catch (TaskTimeExceededException e) {
			log.debug("{}", e);
			try {
				if (timeExceededTaskToBeRequeued == true) {
					log.info("Task exceeded time limit, Sending unacknowledgement for tag {} and requeing ", tag);
					channel.basicNack(tag, false, true);
				} else {
					log.info("Task exceeded time limit , sending unacknowledgement for tag {}", tag);
					channel.basicNack(tag, false, false);
					setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(simulationTask, SceneType.HELIOS.toString(),
							e.getMessage(), null);
				}
			} catch (IOException e1) {
				log.debug("{}", e1);
			}
		} catch (Exception e) {

			log.debug("{}", e);

			try {

				if (simulationTaskCancelledOrPausedOrSuccess(simulationTask.getId(), SceneType.HELIOS)) {

					log.info("Simulation Run cancelled/paused");

					channel.basicNack(tag, false, false);
				} else {
					log.info("Encountered an exception, Sending unacknowledgement for tag {} ", tag);
					channel.basicNack(tag, false, false);
					setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(simulationTask, SceneType.HELIOS.toString(),
							e.getMessage(), null);
				}
			} catch (IOException e1) {
				log.debug("{}", e1);
			}
		} finally {
			log.info("Releasing the acquired resources... for taskId: {}", simulationTask.getId());
			resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);
			tasksFuture.remove(simulationTask.getId() + "_helios");
		}
	}

	public void handleCommandCreationError(Exception e, SimulationTask simulationTask, Channel channel, long tag,
			BaseServer baseServer) {
		log.info("Handling command creation error for task {}" + simulationTask.getId());
		try {
			log.info("Encountered an exception, Sending unacknowledgement for tag {} and passing it ", tag);
			channel.basicNack(tag, false, false);
			setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(simulationTask, SceneType.HELIOS.toString(),
					e.getMessage(), null);
		} catch (IOException e1) {
			log.debug("{}", e1);
		} finally {
			log.info("Releasing the acquired resources... for taskId: {}", simulationTask.getId());
			resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);
			tasksFuture.remove(simulationTask.getId() + "_helios");
		}
	}

	@Transactional
	public void setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(SimulationTask simulationTask, String type,
			String message, Map<String, String> map) {
		log.debug("Failing task and cancelling other tasks as well as simulation");
		try {
			Simulation simulation = simulationRepository.findById(simulationTask.getSimulation().getId()).orElseThrow();
			simulation.getSimulationTasks().stream().forEach(t -> {
				Status agriStatus = t.getAgriStatus();
				Status pvStatus = t.getPvStatus();
				if (t.getId().equals(simulationTask.getId())) {
					if (agriStatus == Status.RUNNING)
						t.setAgriStatus(Status.FAILED);
					if (pvStatus == Status.RUNNING)
						t.setPvStatus(Status.FAILED);

					// if task has run on server then saving it's server name and it's execution
					// time and it's completion time
					if (map != null) {
						t.setCompletedAt(
								LocalDateTime.parse(map.get("completedAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
						t.setServerName(map.get("serverName") != null ? map.get("serverName") : null);
						t.setTaskExecutionTimeOnServer(map.get("taskExecutionTimeOnServer") != null
								? Long.parseLong(map.get("taskExecutionTimeOnServer"))
								: null);
					}
				} else {
					if (agriStatus == Status.QUEUED)
						t.setAgriStatus(Status.CANCELLED);
					if (pvStatus == Status.QUEUED)
						t.setPvStatus(Status.CANCELLED);
				}
			});

			simulation.setComment(message);
			simulation.setStatus(Status.FAILED);
			simulationRepository.save(simulation);
		} catch (Exception e) {
			log.debug("{}" + e);
		}
	}

	private String createHeliosCommandLineArgs(Map<String, Object> runPayload, Map<String, String> weatherCondition,
			SimulationTask simulationTask) {
		log.debug("Enter into createHeliosCommandLineArgs");
		log.info("IN heios args");

		TreeMap<Integer, Object> commandLineArgs = new TreeMap<>();
		int counter = 1;

		JsonNode payload = objectMapper.valueToTree(runPayload);

		// => common data
		commandLineArgs.put(counter++, simulationTask.getId());
		commandLineArgs.put(counter++, simulationTask.getDate().toLocalDate());
		commandLineArgs.put(counter++, simulationTask.getDate().toLocalTime());
		commandLineArgs.put(counter++, payload.get("latitude").asText());
		commandLineArgs.put(counter++, payload.get("longitude").asText());
		commandLineArgs.put(counter++,
				SimulationUtils.convertToTimeZoneString(Double.parseDouble(weatherCondition.get("timeZone")))); // UTC
		commandLineArgs.put(counter++, weatherCondition.get("airPressure"));
		commandLineArgs.put(counter++, 0.03); // turbidity
		commandLineArgs.put(counter++, Double.parseDouble((String) weatherCondition.get("airTemperature")) + 273.15);
		commandLineArgs.put(counter++, Double.parseDouble((String) weatherCondition.get("airHumidity")) / 100.0);
		commandLineArgs.put(counter++, weatherCondition.get("windSpeed"));
		commandLineArgs.put(counter++, Double.parseDouble((String) weatherCondition.get("directNormalRad"))); // directNormalRad
		commandLineArgs.put(counter++, Double.parseDouble((String) weatherCondition.get("diffuseHorizRad"))); // diffuseHorizRad

		// => simulation Ground area
		commandLineArgs.put(counter++, simulationTask.getSimulationBlock().getSimulationGroundArea().getUnitXLength());// unitXLength
		commandLineArgs.put(counter++, simulationTask.getSimulationBlock().getSimulationGroundArea().getUnitYLength());// unitYLength

		// => soil type values
		if (payload.has("preProcessorToggles") && payload.get("preProcessorToggles").has("soilType")
				&& payload.get("preProcessorToggles").get("soilType").has("soilName")) {

			// Retrieve the value of soilName if it exists
			commandLineArgs.put(counter++,
					"\"" + payload.get("preProcessorToggles").get("soilType").get("soilName").asText() + "\"");
		} else {
			// Set an empty string as the default value if soilType entity is missing
			commandLineArgs.put(counter++, "\"\"");
		}

		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("reflectionPAR").asDouble(0.2));// soilType reflectivity_PAR
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("reflectionNIR").asDouble(0.2)); // soilType reflectivity_NIR
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("transmissionPAR").asDouble(0)); // soilType transmissivity_PAR
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("transmissionNIR").asDouble(0)); // soilType transmissivity_NIR

		// soil optical property file path
		String soilOpticalPropertyFilePath = (payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("opticalPropertyFile").isNull()
				|| payload.get("preProcessorToggles").get("soilType").get("opticalProperty").path("opticalPropertyFile")
						.isMissingNode()) ? ""
								: payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
										.get("opticalPropertyFile").asText();

		commandLineArgs.put(counter++, "\"" + soilOpticalPropertyFilePath + "\"");// optical_file_soil

		// soil texture file path
		String linkToTextureFileSoil = (payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
				.path("linkToTexture").isNull()
				|| payload.get("preProcessorToggles").get("soilType").get("opticalProperty").path("linkToTexture")
						.isMissingNode()) ? ""
								: payload.get("preProcessorToggles").get("soilType").get("opticalProperty")
										.get("linkToTexture").asText();

		commandLineArgs.put(counter++, "\"" + linkToTextureFileSoil + "\"");// texture_file_soil
		commandLineArgs.put(counter++, 0); // save geometry
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("azimuth").asText());
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("lengthOfOneRow").asText());
		commandLineArgs.put(counter++, payload.get("preProcessorToggles").get("pitchOfRows").asText());
		commandLineArgs.put(counter++, 2); // pv_rows
		commandLineArgs.put(counter++,
				"\"" + simulationTask.getSimulationBlock().getBlockSimulationType().getValue().toLowerCase() + "\"");

		// => pv parameters
		if (simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.APV)
				|| simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.ONLY_PV)) {
			if (simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.ONLY_PV))
				commandLineArgs.put(counter++, 0.0);
			else
				commandLineArgs.put(counter++, payload.get("cropParameters").get("startPointOffset").asInt() / 1000.0);
			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().charAt(1));
			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().charAt(0));
			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().contains("-")
							? 1
							: 0);
			commandLineArgs.put(counter++, payload.get("pvParameters").get("height").asText());
			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").get("width").asDouble() / 1000.0);
			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").get("length").asDouble() / 1000.0);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("xcell")
							&& !payload.get("pvParameters").get("pvModule").get("xcell").isNull()
									? payload.get("pvParameters").get("pvModule").get("xcell").asDouble() / 1000.0
									: 0.1);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("ycell")
							&& !payload.get("pvParameters").get("pvModule").get("ycell").isNull()
									? payload.get("pvParameters").get("pvModule").get("ycell").asDouble() / 1000.0
									: 0.1);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("xcellGap")
							&& !payload.get("pvParameters").get("pvModule").get("xcellGap").isNull()
									? payload.get("pvParameters").get("pvModule").get("xcellGap").asDouble() / 1000.0
									: 0.1);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("ycellGap")
							&& !payload.get("pvParameters").get("pvModule").get("ycellGap").isNull()
									? payload.get("pvParameters").get("pvModule").get("ycellGap").asDouble() / 1000.0
									: 0.1);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("numCellX")
							&& !payload.get("pvParameters").get("pvModule").get("numCellX").isNull()
									? payload.get("pvParameters").get("pvModule").get("numCellX").asDouble()
									: 4);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("numCellY")
							&& !payload.get("pvParameters").get("pvModule").get("numCellY").isNull()
									? payload.get("pvParameters").get("pvModule").get("numCellY").asDouble()
									: 6);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("pdc0")
							&& !payload.get("pvParameters").get("pvModule").get("pdc0").isNull()
									? payload.get("pvParameters").get("pvModule").get("pdc0").asDouble()
									: 690);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("gammaPdc")
							&& !payload.get("pvParameters").get("pvModule").get("gammaPdc").isNull()
									? payload.get("pvParameters").get("pvModule").get("gammaPdc").asDouble()
									: -0.3);

			commandLineArgs.put(counter++,
					payload.get("pvParameters").get("pvModule").has("bifaciality")
							&& !payload.get("pvParameters").get("pvModule").get("bifaciality").isNull()
									? payload.get("pvParameters").get("pvModule").get("bifaciality").asDouble()
									: 0.7);

			commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("temRef").asText());

			commandLineArgs.put(counter++, payload.get("pvParameters").get("gapBetweenModules").asInt() / 1000.0);
			commandLineArgs.put(counter++, simulationTask.getSimulation().getWithTracking() ? 1 : 0);

			if (payload.get("pvParameters").hasNonNull("maxAngleOfTracking"))
				commandLineArgs.put(counter++, payload.get("pvParameters").get("maxAngleOfTracking").asText());
			else
				commandLineArgs.put(counter++, payload.get("pvParameters").get("tiltIfFt").asText());

			if (payload.get("pvParameters").hasNonNull("moduleMaskPattern")
					&& payload.get("pvParameters").get("moduleMaskPattern").asText().length() != 0)
				commandLineArgs.put(counter++, payload.get("pvParameters").get("moduleMaskPattern").asText());
			else
				commandLineArgs.put(counter++, "\"\"");

			// front optical property
			if (payload.get("pvParameters").get("pvModule").has("frontOpticalProperty")) {
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
						.path("reflectionPAR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
						.path("reflectionNIR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
						.path("transmissionPAR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
						.path("transmissionNIR").asDouble(0.2));

				String pvModuleOpticalPropertyFilePath = (payload.get("pvParameters").get("pvModule")
						.get("frontOpticalProperty").path("opticalPropertyFile").isNull()
						|| payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
								.path("opticalPropertyFile").isMissingNode()) ? ""
										: payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
												.get("opticalPropertyFile").asText();

				commandLineArgs.put(counter++, "\"" + pvModuleOpticalPropertyFilePath + "\"");

				String pvModuleTextureFilePath = (payload.get("pvParameters").get("pvModule")
						.get("frontOpticalProperty").path("linkToTexture").isNull()
						|| payload.get("pvParameters").get("pvModule").get("frontOpticalProperty").path("linkToTexture")
								.isMissingNode()) ? ""
										: payload.get("pvParameters").get("pvModule").get("frontOpticalProperty")
												.get("linkToTexture").asText();

				commandLineArgs.put(counter++, "\"" + pvModuleTextureFilePath + "\"");
			} else {
				// Set default values if frontOpticalProperty is missing
				commandLineArgs.put(counter++, 0); // reflectivityPAR default
				commandLineArgs.put(counter++, 0); // reflectivityNIR default
				commandLineArgs.put(counter++, 0); // transmissivityPAR default
				commandLineArgs.put(counter++, 0); // transmissivityNIR default
				commandLineArgs.put(counter++, "\"\"");
				commandLineArgs.put(counter++, "\"\"");
			}

			// back optical property
			if (payload.get("pvParameters").get("pvModule").has("backOpticalProperty")) {
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
						.path("reflectionPAR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
						.path("reflectionNIR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
						.path("transmissionPAR").asDouble(0.2));
				commandLineArgs.put(counter++, payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
						.path("transmissionNIR").asDouble(0.2));

				String pvModuleOpticalPropertyFilePath = (payload.get("pvParameters").get("pvModule")
						.get("backOpticalProperty").path("opticalPropertyFile").isNull()
						|| payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
								.path("opticalPropertyFile").isMissingNode()) ? ""
										: payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
												.get("opticalPropertyFile").asText();

				commandLineArgs.put(counter++, "\"" + pvModuleOpticalPropertyFilePath + "\"");

				String pvModuleTextureFilePath = (payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
						.path("linkToTexture").isNull()
						|| payload.get("pvParameters").get("pvModule").get("backOpticalProperty").path("linkToTexture")
								.isMissingNode()) ? ""
										: payload.get("pvParameters").get("pvModule").get("backOpticalProperty")
												.get("linkToTexture").asText();

				commandLineArgs.put(counter++, "\"" + pvModuleTextureFilePath + "\"");
			} else {
				// Set default values if backOpticalProperty is missing
				commandLineArgs.put(counter++, 0); // reflectivityPAR default
				commandLineArgs.put(counter++, 0); // reflectivityNIR default
				commandLineArgs.put(counter++, 0); // transmissivityPAR default
				commandLineArgs.put(counter++, 0); // transmissivityNIR default
				commandLineArgs.put(counter++, "\"\"");
				commandLineArgs.put(counter++, "\"\"");
			}
		} else {
			commandLineArgs.put(counter++, 0);
			commandLineArgs.put(counter++, "\"\""); // P or L
			commandLineArgs.put(counter++, "\"\""); // n from Pn
			commandLineArgs.put(counter++, 0); // 0 for Pn and 1 for Pn-Pn
			commandLineArgs.put(counter++, 0); // height
			commandLineArgs.put(counter++, 0); // pvModule width
			commandLineArgs.put(counter++, 0); // pvModule length
			// If pvParameters or pvModule is missing, set all relevant fields to 0
			commandLineArgs.put(counter++, 0.0); // xcell
			commandLineArgs.put(counter++, 0.0); // ycell
			commandLineArgs.put(counter++, 0.0); // xcellGap
			commandLineArgs.put(counter++, 0.0); // ycellGap
			commandLineArgs.put(counter++, 0.0); // numCellX
			commandLineArgs.put(counter++, 0.0); // numCellY
			commandLineArgs.put(counter++, 0.0); // pdc0
			commandLineArgs.put(counter++, 0.0); // gamma_pdc
			commandLineArgs.put(counter++, 0.0); // bifaciality

			commandLineArgs.put(counter++, 0); // temp ref
			commandLineArgs.put(counter++, 0); // gapBetweenModules
			commandLineArgs.put(counter++, 0); // withTracking -> false
			commandLineArgs.put(counter++, 0); // tilt
			commandLineArgs.put(counter++, "\"\""); // module mask pattern
			// Set default values if frontOpticalProperty is missing
			commandLineArgs.put(counter++, 0); // front reflectivityPAR default
			commandLineArgs.put(counter++, 0); // front reflectivityNIR default
			commandLineArgs.put(counter++, 0); // front transmissivityPAR default
			commandLineArgs.put(counter++, 0); // front transmissivityNIR default
			commandLineArgs.put(counter++, "\"\""); // front optical property file
			commandLineArgs.put(counter++, "\"\""); // front texture file
			// Set default values if backOpticalProperty is missing
			commandLineArgs.put(counter++, 0); // back reflectivityPAR default
			commandLineArgs.put(counter++, 0); // back reflectivityNIR default
			commandLineArgs.put(counter++, 0); // back transmissivityPAR default
			commandLineArgs.put(counter++, 0); // back transmissivityNIR default
			commandLineArgs.put(counter++, "\"\""); // back optical property file
			commandLineArgs.put(counter++, "\"\""); // back texture file
		}

		// => crop parameters
		if (simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.APV)
				|| simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.ONLY_AGRI)) {

			JsonNode protectionLayer = payload.get("cropParameters").get("protectionLayer");

			commandLineArgs.put(counter++, protectionLayer.size());

			List<String> protectionLayerArgs = new ArrayList<>();
			if (protectionLayer.isArray()) {
				for (JsonNode layer : protectionLayer) {
					protectionLayerArgs.add("\"" + layer.get("protectionLayerType").asText() + "\"");
					protectionLayerArgs.add(layer.get("height").asText());
					if (layer.has("opticalProperty")) {

						JsonNode opticalProperty = layer.get("opticalProperty");
						protectionLayerArgs.add(opticalProperty.has("reflectionPAR")
								? opticalProperty.get("reflectionPAR").asText("0.2")
								: "0.2");
						protectionLayerArgs.add(opticalProperty.has("reflectionNIR")
								? opticalProperty.get("reflectionNIR").asText("0.2")
								: "0.2");
						protectionLayerArgs.add(opticalProperty.has("transmissionPAR")
								? opticalProperty.get("transmissionPAR").asText("0.2")
								: "0.2");
						protectionLayerArgs.add(opticalProperty.has("transmissionNIR")
								? opticalProperty.get("transmissionNIR").asText("0.2")
								: "0.2");

						String pLayerOpticalPropertyFilePath = (opticalProperty.path("opticalPropertyFile").isNull()
								|| opticalProperty.path("opticalPropertyFile").isMissingNode()) ? ""
										: opticalProperty.get("opticalPropertyFile").asText();

						protectionLayerArgs.add("\"" + pLayerOpticalPropertyFilePath + "\"");

						String pLayerTextureFilePath = (opticalProperty.path("linkToTexture").isNull()
								|| opticalProperty.path("linkToTexture").isMissingNode()) ? ""
										: opticalProperty.get("linkToTexture").asText();

						protectionLayerArgs.add("\"" + pLayerTextureFilePath + "\"");
					} else {
						protectionLayerArgs.add("0"); // pLayer reflectionPAR
						protectionLayerArgs.add("0"); // pLayer reflectionNIR
						protectionLayerArgs.add("0"); // pLayer transmissionPAR
						protectionLayerArgs.add("0"); // pLayer transmissionNIR
						protectionLayerArgs.add("\"\""); // pLayer optical property file
						protectionLayerArgs.add("\"\""); // pLayer texture file
					}
				}
			}

			commandLineArgs.put(counter++, protectionLayerArgs.stream().collect(Collectors.joining(" ")));
			commandLineArgs.put(counter++, payload.get("cropParameters").get("bedParameter").get("noOfBeds").asText());
			commandLineArgs.put(counter++,
					payload.get("cropParameters").get("bedParameter").get("bedHeight").asInt() / 1000.0);
			commandLineArgs.put(counter++,
					payload.get("cropParameters").get("bedParameter").get("bedWidth").asInt() / 1000.0);
			commandLineArgs.put(counter++, payload.get("cropParameters").get("bedParameter").get("bedAngle").asText());
			commandLineArgs.put(counter++,
					payload.get("cropParameters").get("bedParameter").hasNonNull("bedAzimuth")
							? payload.get("cropParameters").get("bedParameter").get("bedAzimuth").asText()
							: 0);
			commandLineArgs.put(counter++, payload.get("cropParameters").get("isMulching").asBoolean() ? 1 : 0);
			commandLineArgs.put(counter++, "\"" + payload.get("cropParameters").get("irrigationType").asText() + "\"");

			JsonNode cycle = SimulationUtils.getCycleNode(payload, simulationTask);

			commandLineArgs.put(counter++, cycle.get("interBedPattern").size());

			String interBedPattern = "";
			for (JsonNode node : cycle.get("interBedPattern")) {
				interBedPattern += "\"" + node.asText() + "\" ";
			}
			commandLineArgs.put(counter++, interBedPattern);
			commandLineArgs.put(counter++, cycle.get("cycleBedDetails").size());

			JsonNode cycleBedDetails = cycle.get("cycleBedDetails");

			if (cycleBedDetails.isArray()) {
				List<String> cycleBedDetailsCmdArgs = new ArrayList<>();
				for (JsonNode cycleBed : cycleBedDetails) {
					cycleBedDetailsCmdArgs.add("\"" + cycleBed.get("bedName").asText() + "\"");
					long cropDuration = ChronoUnit.DAYS.between(LocalDate.parse(cycle.get("cycleStartDate").asText()),
							simulationTask.getDate()) + 1;

					JsonNode cropDetails = cycleBed.get("cropDetails");

					if (cropDetails.isArray()) {
						// calculating crops size for the block
						int cropSize = 0;
						for (JsonNode crop : cropDetails) {
							if (cropDuration <= crop.get("duration").asInt())
								cropSize++;
						}
						cycleBedDetailsCmdArgs.add(cropSize + "");

						for (JsonNode crop : cropDetails) {
							if (cropDuration <= crop.get("duration").asInt()) {
								cycleBedDetailsCmdArgs.add("\"" + crop.get("cropLabel").asText() + "\"");

								LocalDate cycleStartDate = LocalDate.parse(cycle.get("cycleStartDate").asText());
								int plantStage = calculatePlantStage(crop, cycleStartDate, cropDuration);

								cycleBedDetailsCmdArgs.add(plantStage + ""); // plant stage
								cycleBedDetailsCmdArgs.add(crop.get("o1").asDouble() / 1000.0 + "");
								cycleBedDetailsCmdArgs.add(crop.get("o2").asDouble() / 1000.0 + "");
								cycleBedDetailsCmdArgs.add(crop.get("s1").asDouble() / 1000.0 + "");

								// crop optical property
								if (crop.has("opticalProperty")) {
									JsonNode opticalProperty = crop.get("opticalProperty");

									cycleBedDetailsCmdArgs.add(opticalProperty.has("reflectionPAR")
											? opticalProperty.get("reflectionPAR").asText("0.2")
											: "0.2");
									cycleBedDetailsCmdArgs.add(opticalProperty.has("reflectionNIR")
											? opticalProperty.get("reflectionNIR").asText("0.2")
											: "0.2");
									cycleBedDetailsCmdArgs.add(opticalProperty.has("transmissionPAR")
											? opticalProperty.get("transmissionPAR").asText("0.2")
											: "0.2");
									cycleBedDetailsCmdArgs.add(opticalProperty.has("transmissionNIR")
											? opticalProperty.get("transmissionNIR").asText("0.2")
											: "0.2");

									String cropOpticalPropertyFilePath = (opticalProperty.path("opticalPropertyFile")
											.isNull() || opticalProperty.path("opticalPropertyFile").isMissingNode())
													? ""
													: opticalProperty.get("opticalPropertyFile").asText();

									cycleBedDetailsCmdArgs.add("\"" + cropOpticalPropertyFilePath + "\"");

									String cropTextureFilePath = (opticalProperty.path("linkToTexture").isNull()
											|| opticalProperty.path("linkToTexture").isMissingNode()) ? ""
													: opticalProperty.get("linkToTexture").asText();

									cycleBedDetailsCmdArgs.add("\"" + cropTextureFilePath + "\"");
								} else {
									cycleBedDetailsCmdArgs.add("0"); // crop reflection par
									cycleBedDetailsCmdArgs.add("0"); // crop relection NIR
									cycleBedDetailsCmdArgs.add("0"); // crop transmission PAR
									cycleBedDetailsCmdArgs.add("0"); // crop transmission NIR
									cycleBedDetailsCmdArgs.add("\"\""); // crop optical property file
									cycleBedDetailsCmdArgs.add("\"\""); // crop texture file
								}

								// crop stomatal parameter
								if (crop.has("stomatalParameter")) {
									JsonNode stomatalParameter = crop.get("stomatalParameter");

									cycleBedDetailsCmdArgs.add(
											stomatalParameter.has("em") ? stomatalParameter.get("em").asText("13.06")
													: "13.06");
									cycleBedDetailsCmdArgs.add(
											stomatalParameter.has("io") ? stomatalParameter.get("io").asText("167.89")
													: "167.89");
									cycleBedDetailsCmdArgs.add(
											stomatalParameter.has("k") ? stomatalParameter.get("k").asText("25926.4")
													: "25926.4");
									cycleBedDetailsCmdArgs
											.add(stomatalParameter.has("b") ? stomatalParameter.get("b").asText("9.81")
													: "9.81");
								} else {
									cycleBedDetailsCmdArgs.add("0"); // em
									cycleBedDetailsCmdArgs.add("0"); // io
									cycleBedDetailsCmdArgs.add("0"); // k
									cycleBedDetailsCmdArgs.add("0"); // b
								}

								// crop farquhar parameter
								if (crop.has("farquharParameter")) {
									JsonNode farquharParameter = crop.get("farquharParameter");

									cycleBedDetailsCmdArgs.add(farquharParameter.has("vcMax")
											? farquharParameter.get("vcMax").asText("107.69")
											: "107.69");
									cycleBedDetailsCmdArgs.add(farquharParameter.has("cjMax")
											? farquharParameter.get("cjMax").asText("18.82")
											: "18.82");
									cycleBedDetailsCmdArgs.add(farquharParameter.has("haJMax")
											? farquharParameter.get("haJMax").asText("46.04")
											: "46.04");
									cycleBedDetailsCmdArgs.add(farquharParameter.has("alpha")
											? farquharParameter.get("alpha").asText("0.274")
											: "0.274");
									cycleBedDetailsCmdArgs.add(farquharParameter.has("rd25")
											? farquharParameter.get("rd25").asText("1.510")
											: "1.510");
									cycleBedDetailsCmdArgs.add(farquharParameter.has("jmax")
											? farquharParameter.get("jmax").asText("176.71")
											: "176.71");
								} else {
									cycleBedDetailsCmdArgs.add("0"); // vcMax
									cycleBedDetailsCmdArgs.add("0"); // cjMax
									cycleBedDetailsCmdArgs.add("0"); // haJMax
									cycleBedDetailsCmdArgs.add("0"); // alpha
									cycleBedDetailsCmdArgs.add("0"); // rd25
									cycleBedDetailsCmdArgs.add("0"); // jmax
								}
							}
//							else
//							{
//								cycleBedDetailsCmdArgs.add("\"\""); // cropName
//								cycleBedDetailsCmdArgs.add("0"); // duration
//								cycleBedDetailsCmdArgs.add("0"); // o1
//								cycleBedDetailsCmdArgs.add("0"); // o2
//								cycleBedDetailsCmdArgs.add("0"); // s1
//								
//								cycleBedDetailsCmdArgs.add("0"); //crop reflection PAR
//								cycleBedDetailsCmdArgs.add("0"); //crop reflection NIR
//								cycleBedDetailsCmdArgs.add("0"); //crop transmission PAR
//								cycleBedDetailsCmdArgs.add("0"); //crop transmission NIR
//								cycleBedDetailsCmdArgs.add("\"\""); // crop optical property file
//								cycleBedDetailsCmdArgs.add("\"\""); // crop texture file
//								
//								cycleBedDetailsCmdArgs.add("0"); // em
//								cycleBedDetailsCmdArgs.add("0"); // io
//								cycleBedDetailsCmdArgs.add("0"); // k
//								cycleBedDetailsCmdArgs.add("0"); // b
//								
//								cycleBedDetailsCmdArgs.add("0"); // vcMax
//								cycleBedDetailsCmdArgs.add("0"); // cjMax
//								cycleBedDetailsCmdArgs.add("0"); // haJMax
//								cycleBedDetailsCmdArgs.add("0"); // alpha
//								cycleBedDetailsCmdArgs.add("0"); // rd25
//								cycleBedDetailsCmdArgs.add("0"); // jmax
//							}
						}
					}
				}
				commandLineArgs.put(counter++, cycleBedDetailsCmdArgs.stream().collect(Collectors.joining(" ")));
			}
		} else {
			commandLineArgs.put(counter++, "0 0 0 0 0 0 0 \"\" 0 0");
		}
		commandLineArgs.put(counter++, simulationTask.isFirst() ? 1 : 0);
		commandLineArgs.put(counter++, simulationTask.isHasHighestRadiation() ? 1 : 0);

		log.debug("Exit from createHeliosCommandLineArgs");
		log.info("End args");

		return "cd " + heliosLocation + " && ./simulation "
				+ commandLineArgs.values().stream().map(Object::toString).collect(Collectors.joining(" "));
	}

	private String createBifacialCommandLineArgs(Map<String, Object> runPayload, Map<String, String> weatherCondition,
			SimulationTask simulationTask) {
		Boolean withTracking = simulationTask.getSimulation().getWithTracking();

		LinkedHashMap<String, Object> cmdArgs = new LinkedHashMap<>();
		JsonNode payload = objectMapper.valueToTree(runPayload);

		cmdArgs.put("simulationName", payload.get("id").asText());
		cmdArgs.put("moduletype", "\"Custom Cell-Level Module\"");
		cmdArgs.put("albedo", 0.5);
		cmdArgs.put("lat", payload.get("latitude").asText());
		cmdArgs.put("lon", payload.get("longitude").asText());
		cmdArgs.put("row_length", payload.get("preProcessorToggles").get("lengthOfOneRow").asText());
		cmdArgs.put("nRows", 5);
		cmdArgs.put("hub_height", payload.get("pvParameters").get("height").asText());
		cmdArgs.put("pitch", payload.get("preProcessorToggles").get("pitchOfRows").asText());
		cmdArgs.put("cumulativesky", false);
		if (withTracking && payload.get("pvParameters").hasNonNull("maxAngleOfTracking"))
			cmdArgs.put("limit_angle", payload.get("pvParameters").get("maxAngleOfTracking").asInt());
		else
			cmdArgs.put("limit_angle", 0);
		cmdArgs.put("angledelta", 0.01);
		cmdArgs.put("backtrack", true);
		cmdArgs.put("xgap", payload.get("pvParameters").get("gapBetweenModules").asInt() / 1000.0);
		cmdArgs.put("ygap", payload.get("pvParameters").get("gapBetweenModules").asInt() / 1000.0);
		cmdArgs.put("zgap", 0.05);
		cmdArgs.put("numpanels",
				payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().charAt(0));
		cmdArgs.put("numcellsx", 6);
		cmdArgs.put("numcellsy", 12);
		cmdArgs.put("xcell", payload.get("pvParameters").get("pvModule").get("length").asDouble() / 1000.0);
		cmdArgs.put("ycell", payload.get("pvParameters").get("pvModule").get("width").asDouble() / 1000.0);
		cmdArgs.put("xcellgap", 0.02);
		cmdArgs.put("ycellgap", 0.02);
		cmdArgs.put("torquetube", true);
		cmdArgs.put("axisofrotation", true);
		cmdArgs.put("diameter", 0.1);
		cmdArgs.put("tubetype", "Oct");
		cmdArgs.put("material", "black");
		cmdArgs.put("startdate", simulationTask.getDate().minusHours(1l).toString());
		cmdArgs.put("enddate", simulationTask.getDate().minusHours(1l).toString());
//		cmdArgs.put("timeZone", "+05:30");
		cmdArgs.put("tracking", withTracking);
		cmdArgs.put("ew_sheds",
				payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().contains("-")
						? true
						: false);
		cmdArgs.put("coerce_year", simulationTask.getDate().getYear());

		if (!withTracking)
			cmdArgs.put("tilt", payload.get("pvParameters").get("tiltIfFt").asText());
		else
			cmdArgs.put("tilt", 0);
		cmdArgs.put("ew_gap", payload.get("pvParameters").get("gapBetweenModules").asText());
		cmdArgs.put("row_gap", payload.get("preProcessorToggles").get("pitchOfRows").asText());
		cmdArgs.put("clearance_height", 1.2);
		cmdArgs.put("module_config",
				payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().charAt(1));
		cmdArgs.put("azimuth", payload.get("preProcessorToggles").get("azimuth").asText());
		cmdArgs.put("epw_url", weatherCondition.get("dataSourceUrl"));

		return "cd " + bifacialLocation + " && python3 simulation.py "
				+ cmdArgs.entrySet().stream().map(e -> "--" + e.getKey() + " " + e.getValue())
						.collect(Collectors.joining(" "))
				+ " --timeZone="
				+ SimulationUtils.convertToTimeZoneString(Double.parseDouble(weatherCondition.get("timeZone")));

	}

	private void processResult(String result, SimulationTask simulationTask, SceneType sceneType,
			BaseServer baseServer, long taskExecutionTimeOnServer) {

		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("result", result);
		resultMap.put("host", baseServer.getHost());
		resultMap.put("serverName", baseServer.getServerName());
		resultMap.put("taskExecutionTimeOnServer", taskExecutionTimeOnServer);

		simulationTask.setCompletedAt(LocalDateTime.now());
		resultMap.put("id", simulationTask.getId());
		resultMap.put("type", sceneType);
		resultMap.put("completedAt", LocalDateTime.now());

		LogUtils.saveSimulationResult(resultMap);

		log.info("Pushing result into result queue");

		try {
			messageProducer.sendMessage(exchangeName, routingKey, objectMapper.writeValueAsString(resultMap));
		} catch (JsonProcessingException e) {
			log.debug("{}", e);
		}
	}

	public void acknowledge(Channel channel, long tag) {
		try {
			log.info("Sending acknowledgement for tag .... " + tag);
			channel.basicAck(tag, false);
		} catch (IOException e) {

			log.debug("{}", e);

			try {
				log.debug("UnAcknowledgement for tag .... " + tag);
				channel.basicNack(tag, false, false);

			} catch (IOException e1) {

				log.debug("{}", e1);
			}
		}
	}

	public boolean cancelSimulation(Long id) {
		Future<?> futureBifacial = tasksFuture.get(id + "_bifacial");
		Future<?> futureHelios = tasksFuture.get(id + "_helios");

		boolean isCancelledBifacial = true;
		boolean isCancelledHelios = true;

		if (futureBifacial != null) {
			isCancelledBifacial = futureBifacial.cancel(true);
			tasksFuture.remove(id + "_bifacial");
		}

		if (futureHelios != null) {
			isCancelledHelios = futureHelios.cancel(true);
			tasksFuture.remove(id + "_helios");
		}

//		return isCancelledBifacial || isCancelledHelios;
		return true;
	}

	public Boolean simulationTaskCancelledOrPausedOrSuccess(Long id, SceneType sceneType) {
		SimulationTask simulationTask = simulationTaskRepository.findById(id).orElse(null);

		if (simulationTask != null) {
			if (sceneType.equals(SceneType.BIFACIAL)) {
				if (simulationTask.getPvStatus().equals(Status.CANCELLED)
						|| simulationTask.getPvStatus().equals(Status.PAUSED)
						|| simulationTask.getPvStatus().equals(Status.SUCCESS))
					return true;
			} else if (sceneType.equals(SceneType.HELIOS)) {
				if (simulationTask.getAgriStatus().equals(Status.CANCELLED)
						|| simulationTask.getAgriStatus().equals(Status.PAUSED)
						|| simulationTask.getAgriStatus().equals(Status.SUCCESS))
					return true;
				if (simulationTask.getPvStatus().equals(Status.CANCELLED)
						|| simulationTask.getPvStatus().equals(Status.PAUSED)
						|| simulationTask.getPvStatus().equals(Status.SUCCESS))
					return true;
			}
		}

		return false;
	}
	
	public boolean isTaskRunnable(Long id) {
		Boolean result = simulationTaskRepository.isQueued(id,Status.QUEUED.toString());
		return result!=null?result:false;
	}
	
	public boolean isTaskUpdatedToRunning(Long id) {
		Boolean result = simulationTaskRepository.updateQueuedToRunning(id, Status.QUEUED.toString(),
				Status.RUNNING.toString());
		return result != null ? result : false;
	}

	private int getPlantStage(int duration, int minStage, int maxStage, long runningDay) {
		double stageFactor = (duration + 0.0) / (maxStage - (minStage - 1));

		double currentStage = ((runningDay + 1) / stageFactor) + (minStage - 1);

		return Math.min((int) Math.ceil(currentStage), maxStage);
	}

	// returns plant stage based on given parameters
	private int calculatePlantStage(JsonNode crop, LocalDate cycleStartDate, long runningDay) {
		int duration = crop.get("duration").asInt();
		int minStage = crop.get("minStage").asInt();
		int maxStage = crop.get("maxStage").asInt();
		boolean plantStartsWithActualDate = false;
		LocalDate actualStartDate = null;
		int plantMaxAge = maxStage;
		int plantCurrentStage;

		// if f1 is true then that means plant is starting with actual date
		if (crop.hasNonNull("hasPlantActualDate"))
			plantStartsWithActualDate = crop.get("hasPlantActualDate").booleanValue();

		// if f3 is given then it is max age of plant if it is not given then plant's
		// max stage is it's max age
		if (crop.hasNonNull("plantMaxAge") && crop.get("plantMaxAge").asInt() > 0)
			plantMaxAge = crop.get("plantMaxAge").asInt();

		// extracting actual startDate from f2 which is actually a Month day
		if (plantStartsWithActualDate == true && crop.hasNonNull("plantActualStartDate")) {
			String actualStartMonthDay = crop.get("plantActualStartDate").asText();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH);
			MonthDay monthDay = MonthDay.parse(actualStartMonthDay, formatter);

			int yearForActualStartDate = cycleStartDate.getYear();
			actualStartDate = LocalDate.of(yearForActualStartDate, monthDay.getMonth(), monthDay.getDayOfMonth());

			long diffOfActualStartDateAndCycleStartDate = ChronoUnit.DAYS.between(actualStartDate, cycleStartDate) + 1;
			minStage = minStage + (int) diffOfActualStartDateAndCycleStartDate;
			maxStage = maxStage + (int) diffOfActualStartDateAndCycleStartDate;
		}

		plantCurrentStage = getPlantStage(duration, minStage, maxStage, runningDay);
		if (plantCurrentStage > plantMaxAge)
			plantCurrentStage = plantCurrentStage - (maxStage - minStage);

		return plantCurrentStage;
	}
}