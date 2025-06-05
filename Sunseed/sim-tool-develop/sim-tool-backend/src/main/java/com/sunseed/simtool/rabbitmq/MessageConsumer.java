package com.sunseed.simtool.rabbitmq;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sunseed.simtool.bootup.AppStartupState;
import com.sunseed.simtool.constant.LeafType;
import com.sunseed.simtool.constant.SceneType;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.CropYield;
import com.sunseed.simtool.entity.PVYield;
import com.sunseed.simtool.entity.Scene;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.entity.TrackingTiltAngle;
import com.sunseed.simtool.exception.NoSuitableNodeFoundException;
import com.sunseed.simtool.helper.SimulationHelper;
import com.sunseed.simtool.model.BaseServer;
import com.sunseed.simtool.repository.CropYieldRepository;
import com.sunseed.simtool.repository.PVYieldRepository;
import com.sunseed.simtool.repository.SceneRepository;
import com.sunseed.simtool.repository.SimulationRepository;
import com.sunseed.simtool.repository.SimulationTaskRepository;
import com.sunseed.simtool.repository.TrackingTiltAngleRepository;
import com.sunseed.simtool.scheduler.DliProcessingScheduler;
import com.sunseed.simtool.service.E2EService;
import com.sunseed.simtool.service.ResourceReleaserService;
import com.sunseed.simtool.util.LogUtils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageConsumer {

	private final String queuePV;
	private final String queueAgri;
	private final String resultQueue;
	private final String exchangeName;
	private final String routingKeyAgri;
	private final SimulationHelper simulationHelper;
	private final SimulationTaskRepository simulationTaskRepository;
	private final ObjectMapper objectMapper;
	private final MessageProducer messageProducer;
	private final PVYieldRepository pvYieldRepository;
	private final TrackingTiltAngleRepository trackingTiltAngleRepository;
	private final SimulationRepository simulationRepository;
	private final DliProcessingScheduler dliProcessingScheduler;
	private final E2EService e2eService;
	private final ResourceReleaserService resourceReleaserService;

	@Value("${server.path:#{null}}")
	private List<String> serverPath;
	@Value("${simulation.server.host}")
	private List<String> serverHosts;

	@Value("${fixed.server.path}")
	private String fixedServerPath;

	@Autowired
	private AppStartupState appStartupState;

	public MessageConsumer(@Value("${rabbitmq.queue.pv}") String queuePV,
			@Value("${rabbitmq.queue.agri}") String queueAgri, @Value("${rabbitmq.queue.result}") String resultQueue,
			@Value("${rabbitmq.exchange}") String exchangeName,
			@Value("${rabbitmq.routingkey.agri}") String routingKeyAgri, SimulationHelper simulationHelper,
			SimulationTaskRepository simulationTaskRepository, ObjectMapper objectMapper,
			MessageProducer messageProducer, PVYieldRepository pvYieldRepository,
			CropYieldRepository cropYieldRepository, TrackingTiltAngleRepository trackingTiltAngleRepository,
			SceneRepository sceneRepository, SimulationRepository simulationRepository,
			DliProcessingScheduler dliProcessingScheduler, E2EService e2eService,ResourceReleaserService resourceReleaserService) {
		super();
		this.queuePV = queuePV;
		this.queueAgri = queueAgri;
		this.resultQueue = resultQueue;
		this.exchangeName = exchangeName;
		this.routingKeyAgri = routingKeyAgri;
		this.simulationHelper = simulationHelper;
		this.simulationTaskRepository = simulationTaskRepository;
		this.objectMapper = objectMapper;
		this.messageProducer = messageProducer;
		this.pvYieldRepository = pvYieldRepository;
		this.trackingTiltAngleRepository = trackingTiltAngleRepository;
		this.simulationRepository = simulationRepository;
		this.dliProcessingScheduler = dliProcessingScheduler;
		this.e2eService = e2eService;
		this.resourceReleaserService = resourceReleaserService;
	}

	@RabbitListener(queues = "${rabbitmq.queue.pv}", ackMode = "MANUAL")
	public void receiveMessageFromQueuePV(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
			throws InterruptedException, JsonMappingException, JsonProcessingException {
		MDC.put("uuid", UUID.randomUUID().toString());

//		appStartupState.awaitInitialization(); // wait for app initialization

		log.info("Consuming message {} from queue {}", message, queuePV);

		log.info("Checking for resource availability ....");
		while (!simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 1)) {
			Thread.sleep(10000);
			log.info("Waiting for resource ....");
		}

		log.info("Reading message .... ");
		SimulationTask simulationTask = objectMapper.readValue(message, SimulationTask.class);

		simulationHelper.runSimulation(simulationTask, null, channel, tag, SceneType.BIFACIAL);

	}

	@Async
	@RabbitListener(queues = "${rabbitmq.queue.agri}", ackMode = "MANUAL")
	public void receiveMessageFromQueueAgri(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
			throws InterruptedException, JsonMappingException, JsonProcessingException {
		MDC.put("uuid", UUID.randomUUID().toString());

		appStartupState.awaitInitialization(); // wait for app initialization

		log.info("Consuming message {} from queue {}", message, queueAgri);
		log.info("Reading message .... ");
		SimulationTask simulationTask = objectMapper.readValue(message, SimulationTask.class);
		
		// if task is not queued then it must acknowledge and return
		if(!simulationHelper.isTaskRunnable(simulationTask.getId())) {
			simulationHelper.acknowledge(channel, tag);
			return ;
		}

		// Task is queued so acquiring server for it
		BaseServer baseServer = null;
		boolean sessionAcquired = false;

		log.info("Waiting for a runnable server... for taskId: {}", simulationTask.getId());
		while (!sessionAcquired) {
			if (baseServer == null) {
				try {
					baseServer = e2eService.getRunnableServer(simulationTask, SceneType.HELIOS);
				} catch (NoSuitableNodeFoundException e) {
					try {
						simulationHelper.setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(simulationTask,
								SceneType.HELIOS.toString(), e.getMessage(), null);
					} catch (Exception ex) {
						log.error("Exception occurred during task handling: {}", ex.getMessage(), ex);
					} finally {
						log.info("Releasing the acquired resources... for taskId: {}", simulationTask.getId());
						resourceReleaserService.finallyReleasingServerResources(baseServer, simulationTask);
						try {
							channel.basicNack(tag, false, false);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (baseServer != null) {
					log.info("Trying to acquire session for server: {} , host: {} for taskId : {}",
							baseServer.getServerName(), baseServer.getHost(), simulationTask.getId());
					sessionAcquired = simulationHelper.acquireSession(baseServer, SceneType.HELIOS, simulationTask);
				}
			}

			if (!sessionAcquired) {
				baseServer = null;
				Thread.sleep(10000);
			}
		}

		simulationHelper.runSimulation(simulationTask, baseServer, channel, tag, SceneType.HELIOS);
	}

	@RabbitListener(queues = "${rabbitmq.queue.result}")
	@Transactional
	public void receiveMessageFromResultQueue(String message) throws JsonMappingException, JsonProcessingException {

		MDC.put("uuid", UUID.randomUUID().toString());

//		appStartupState.awaitInitialization(); // wait for app initialization

		log.info("Consuming message {} from queue {}", message, resultQueue);

		Map<String, String> map = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {
		});

		SimulationTask simulationTask = simulationTaskRepository.findById(Long.parseLong(map.get("id"))).orElse(null);

		if (simulationTask == null)
			return;

		log.info("Extracting result info ....");

		Map<String, String> resultMap = extractResult(simulationTask, map, map.get("type"),
				simulationTask.getSimulation().getWithTracking(), map.get("host"));

		if (resultMap == null || resultMap.isEmpty()) {
			log.info("Can't extract result, task marked as FAILED for taskId: " + simulationTask.getId() + " on host: "
					+ map.get("host"));
			String comment = "Can't extract result for taskId: " + simulationTask.getId() + " on host : "
					+ map.get("host");
			simulationHelper.setTaskStatusFailedAddCommentAndCancelAllQueuedTasks(simulationTask, map.get("type"),
					comment, map);
		} else {
			map.putAll(resultMap);
			log.info("Result info extracted, saving into the database for taskId: " + simulationTask.getId());
			log.info(resultMap.get("file_url"));
			saveSimulationResult(simulationTask, map);
		}

	}

	private Map<String, String> extractResult(SimulationTask simulationTask, Map<String, String> resultMap, String type,
			Boolean withTracking, String host) {

		Map<String, String> map = new HashMap<>();

		String result = resultMap.get("result");

		if (result != null) {
			if (type.equalsIgnoreCase(SceneType.BIFACIAL.toString())) {
				String serverPath = "";
				log.info("Host in extract result--------------->" + host);
				if (host != null) {
					serverPath = fixedServerPath;
					log.info("serverPath in extract result--------------->" + serverPath);
				}
				if (LogUtils.extractNumericalValue(result, "pv_yield") != null) {
					map.put("pv_yield", LogUtils.extractNumericalValue(result, "pv_yield"));
				} else {
					return null;
				}

				if (LogUtils.extractNumericalValue(result, "front_gain") != null) {
					map.put("front_gain", LogUtils.extractNumericalValue(result, "front_gain"));
				} else {
					map.put("front_gain", "0");
				}

				if (LogUtils.extractNumericalValue(result, "rear_gain") != null) {
					map.put("rear_gain", LogUtils.extractNumericalValue(result, "rear_gain"));
				} else {
					map.put("rear_gain", "0");
				}

				if (LogUtils.extractNumericalValue(result, "albedo") != null) {
					map.put("albedo", LogUtils.extractNumericalValue(result, "albedo"));
				} else {
					map.put("albedo", "0");
				}

				if (LogUtils.extractUrlValue(result, "file_url") != null) {
					map.put("file_url", serverPath + LogUtils.extractUrlValue(result, "file_url"));
				} else {
					map.put("file_url", "");
				}

				if (withTracking) {
					if (LogUtils.extractNumericalValue(result, "tilt_angle") != null) {
						map.put("tilt_angle", LogUtils.extractNumericalValue(result, "tilt_angle"));
					} else {
						map.put("tilt_angle", "0");
					}
				}
			} else if (type.equalsIgnoreCase(SceneType.HELIOS.toString())) {
				String serverPath = "";
				log.info("Host in extract result--------------->" + host);
				if (host != null) {
					serverPath = fixedServerPath;
					log.info("serverPath in extract result--------------->" + serverPath);
				}

				if (LogUtils.extractAgriSimulationValues(result) != null) {
					try {
						map.put("carbon_assimilation",
								objectMapper.writeValueAsString(LogUtils.extractAgriSimulationValues(result)));
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (simulationTask.getSimulationBlock().getBlockSimulationType() == SimulationType.APV
							|| simulationTask.getSimulationBlock().getBlockSimulationType() == SimulationType.ONLY_AGRI)
						return null;
				}

				if (LogUtils.extractNumericalValue(result, "pv_yield") != null) {
					map.put("pv_yield", LogUtils.extractNumericalValue(result, "pv_yield"));
				} else {
					if (simulationTask.getSimulationBlock().getBlockSimulationType() == SimulationType.APV
							|| simulationTask.getSimulationBlock().getBlockSimulationType() == SimulationType.ONLY_PV)
						return null;
				}

				// => ppfd
				if (LogUtils.extractNumericalValue(result, "ppfd") != null) {
					map.put("ppfd", LogUtils.extractNumericalValue(result, "ppfd"));
				} else {
					map.put("ppfd", null);
				}

				if (LogUtils.extractNumericalValue(result, "front_gain") != null) {
					map.put("front_gain", LogUtils.extractNumericalValue(result, "front_gain"));
				} else {
					map.put("front_gain", null);
				}
				if (LogUtils.extractNumericalValue(result, "rear_gain") != null) {
					map.put("rear_gain", LogUtils.extractNumericalValue(result, "rear_gain"));
				} else {
					map.put("rear_gain", null);
				}

				// isometric snaps numerical values will ask if it is necessary because they
				// will be files only
				if (LogUtils.extractNumericalValue(result, "isometric_snap_carbon_assimilation") != null) {
					map.put("isometric_snap_carbon_assimilation",
							LogUtils.extractNumericalValue(result, "isometric_snap_carbon_assimilation"));
				} else {
					map.put("isometric_snap_carbon_assimilation", null);
				}
				if (LogUtils.extractNumericalValue(result, "isometric_snap_temperature") != null) {
					map.put("isometric_snap_temperature",
							LogUtils.extractNumericalValue(result, "isometric_snap_temperature"));
				} else {
					map.put("isometric_snap_temperature", null);
				}
				if (LogUtils.extractNumericalValue(result, "isometric_snap_radiation") != null) {
					map.put("isometric_snap_radiation",
							LogUtils.extractNumericalValue(result, "isometric_snap_radiation"));
				} else {
					map.put("isometric_snap_radiation", null);
				}

				StringBuilder fileTypesBuilder = new StringBuilder();
				StringBuilder fileUrlsBuilder = new StringBuilder();
				StringBuilder fileMinBuilder = new StringBuilder();
				StringBuilder fileMaxBuilder = new StringBuilder();

				if (LogUtils.extractStringValue(result, "file_carbon_assimilation") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_carbon_assimilation");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "file_carbon_assimilation"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_carbon_assimilation"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_carbon_assimilation"));
				}

				if (LogUtils.extractStringValue(result, "file_temperature") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_temperature");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_temperature"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_temperature"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_temperature"));
				}

				if (LogUtils.extractStringValue(result, "file_radiation") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_radiation");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_radiation"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_radiation"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_radiation"));
				}

				if (LogUtils.extractStringValue(result, "file_pv_yield") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_pv_yield");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_pv_yield"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_pv_yield"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_pv_yield"));
				}

				if (LogUtils.extractStringValue(result, "file_material") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_material");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_material"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_material"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_material"));
				}

				if (LogUtils.extractStringValue(result, "file_dli") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_dli");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_dli"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_dli"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_dli"));
				}

				if (LogUtils.extractStringValue(result, "file_geometry") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("file_geometry");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "file_geometry"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_geometry"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_geometry"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_radiation") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_radiation");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_radiation"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_radiation_top") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_radiation_top");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_radiation_top"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_radiation_iso") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_radiation_iso");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_radiation_iso"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_radiation_right") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_radiation_right");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_radiation_right"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_radiation_left") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_radiation_left");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_radiation_left"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_carbon_assimilation");
					fileUrlsBuilder.append(
							serverPath + LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_top") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_carbon_assimilation_top");
					fileUrlsBuilder.append(
							serverPath + LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_top"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_iso") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_carbon_assimilation_iso");
					fileUrlsBuilder.append(
							serverPath + LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_iso"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_right") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_carbon_assimilation_right");
					fileUrlsBuilder.append(serverPath
							+ LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_right"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_left") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_carbon_assimilation_left");
					fileUrlsBuilder.append(serverPath
							+ LogUtils.extractStringValue(result, "isometric_snap_carbon_assimilation_left"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_temperature") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_temperature");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_temperature"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_temperature_top") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_temperature_top");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_temperature_top"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_temperature_iso") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_temperature_iso");
					fileUrlsBuilder
							.append(serverPath + LogUtils.extractStringValue(result, "isometric_snap_temperature_iso"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_temperature_right") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_temperature_right");
					fileUrlsBuilder.append(
							serverPath + LogUtils.extractStringValue(result, "isometric_snap_temperature_right"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "isometric_snap_temperature_left") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("isometric_snap_temperature_left");
					fileUrlsBuilder.append(
							serverPath + LogUtils.extractStringValue(result, "isometric_snap_temperature_left"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_isometric_snap"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_isometric_snap"));
				}

				if (LogUtils.extractStringValue(result, "visualization_left") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("visualization_left");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "visualization_left"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_visualization_left"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_visualization_left"));
				}

				if (LogUtils.extractStringValue(result, "visualization_top") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("visualization_top");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "visualization_top"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_visualization_top"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_visualization_top"));
				}

				if (LogUtils.extractStringValue(result, "visualization_right") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("visualization_right");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "visualization_right"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_visualization_right"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_visualization_right"));
				}

				if (LogUtils.extractStringValue(result, "visualization_iso") != null) {
					if (fileTypesBuilder.length() > 0) {
						fileTypesBuilder.append(",");
						fileUrlsBuilder.append(",");
						fileMinBuilder.append(",");
						fileMaxBuilder.append(",");
					}
					fileTypesBuilder.append("visualization_iso");
					fileUrlsBuilder.append(serverPath + LogUtils.extractStringValue(result, "visualization_iso"));
					fileMinBuilder.append(LogUtils.extractNumericalValue(result, "min_visualization_isometric"));
					fileMaxBuilder.append(LogUtils.extractNumericalValue(result, "max_visualization_isometric"));
				}

				// Put the comma-separated lists into resultMap
				map.put("file_types", fileTypesBuilder.toString());
				map.put("file_urls", fileUrlsBuilder.toString());
				map.put("file_min", fileMinBuilder.toString());
				map.put("file_max", fileMaxBuilder.toString());
			}
		}

		return map;
	}

	public String getServerPathForHost(String host) {
		for (int i = 0; i < serverHosts.size(); i++) {
			if (host.equals(serverHosts.get(i))) {
				if (serverPath != null)
					return serverPath.get(i); // Return the matching server path
				else {
					return "";
				}

			}
		}
		return ""; // Return null or handle the case where no matching host is found
	}

//	@Transactional
	private void saveSimulationResult(SimulationTask simulationTask, Map<String, String> resultMap)
			throws JsonProcessingException {

		if (resultMap.get("type").equalsIgnoreCase(SceneType.BIFACIAL.toString())) {
			PVYield pvYield = new PVYield();
			pvYield.setPvYield(Float.parseFloat(resultMap.get("pv_yield")));
			pvYield.setFrontGain(Float.parseFloat(resultMap.get("front_gain")));
			pvYield.setRearGain(Float.parseFloat(resultMap.get("rear_gain")));
			pvYield.setAlbedo(Float.parseFloat(resultMap.get("albedo")));
			pvYield.setSimulationTask(simulationTask);
			pvYield = pvYieldRepository.save(pvYield);
			simulationTask.setPvYields(pvYield);

			Scene scene = new Scene();
			scene.setType(SceneType.BIFACIAL);
			scene.setUrl(resultMap.get("file_url"));
			scene.setSimulationTask(simulationTask);
			simulationTask.getScenes().add(scene);

			if (simulationTask.getSimulation().getWithTracking()) {
				TrackingTiltAngle trackingTiltAngle = new TrackingTiltAngle();
				trackingTiltAngle.setTiltAngle(Float.parseFloat(resultMap.get("tilt_angle")));
				trackingTiltAngle.setSimulationTask(simulationTask);
				trackingTiltAngle = trackingTiltAngleRepository.save(trackingTiltAngle);
				simulationTask.setTrackingTiltAngles(trackingTiltAngle);
			}

			simulationTask.setPvStatus(Status.SUCCESS);
		} else if (resultMap.get("type").equalsIgnoreCase(SceneType.HELIOS.toString())) {

			SimulationType simulationBlockType = simulationTask.getSimulationBlock().getBlockSimulationType();

			if (resultMap.get("carbon_assimilation") == null && resultMap.get("pv_yield") == null) {
				if (simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.APV)) {
					simulationTask.setPvStatus(Status.FAILED);
					simulationTask.setAgriStatus(Status.FAILED);
				} else if (simulationTask.getSimulationBlock().getBlockSimulationType()
						.equals(SimulationType.ONLY_PV)) {
					simulationTask.setPvStatus(Status.FAILED);
				} else if (simulationTask.getSimulationBlock().getBlockSimulationType()
						.equals(SimulationType.ONLY_AGRI)) {
					simulationTask.setAgriStatus(Status.FAILED);
				}
			}

			if ((simulationBlockType == SimulationType.APV || simulationBlockType == SimulationType.ONLY_AGRI)
					&& resultMap.get("carbon_assimilation") != null) {
				// Iterate through the resultMap (bedIndex -> cropName -> lightCondition ->
				// parameter -> value)
				List<CropYield> cropYields = new ArrayList<>();

				Map<Integer, Map<String, Map<String, Map<String, Double>>>> carbonAssimilationMap = objectMapper
						.readValue(resultMap.get("carbon_assimilation"),
								new TypeReference<Map<Integer, Map<String, Map<String, Map<String, Double>>>>>() {
								});
				carbonAssimilationMap.forEach((bedIndex, cropMap) -> {
					cropMap.forEach((cropName, lightConditionMap) -> {
						lightConditionMap.forEach((lightCondition, parametersMap) -> {
							// Create a CropYield for each lightCondition (sunlit/sunshaded)
							CropYield cropYield = new CropYield();
							cropYield.setBedIndex(bedIndex);
							cropYield.setCropName(cropName);
							cropYield.setSimulationTask(simulationTask);

							// Optionally, set the LeafType based on lightCondition (sunlit/sunshaded)
							cropYield.setLeafType(
									lightCondition.equals("sunlit") ? LeafType.sunlit : LeafType.sunshaded);

							// Set values for the parameters (carbonAssimilation, temperature, etc.)
							parametersMap.forEach((parameter, value) -> {
								switch (parameter) {
								case "carbon_assimilation":
									cropYield.setCarbonAssimilation(value.floatValue());
									break;
								case "temperature":
									cropYield.setTemperature(value.floatValue());
									break;
								case "radiation":
									cropYield.setRadiation(value.floatValue());
									break;
								case "saturation":
									cropYield.setSaturation(value.floatValue());
									break;
								case "penetration":
									cropYield.setPenetration(value.floatValue());
									break;
								case "saturation_extent":
									cropYield.setSaturationExtent(value.floatValue());
									break;
								case "latent_flux": // Assuming this is the log field name for latentFlux
									cropYield.setLatentFlux(value.floatValue());
									break;
								case "leaves_area": // Assuming this is the log field name for leavesArea
									cropYield.setLeavesArea(value.floatValue());
									break;
								case "crop_count": // Assuming this is the log field name for leavesArea
									cropYield.setCropCount(Math.round(value.floatValue()));
									break;

								// Add more cases for other parameters as needed
								}
							});

							// Add the populated CropYield to the list
							cropYields.add(cropYield);
						});
					});
				});

				// Clear existing crop yields and add the new ones
				simulationTask.getCropYields().clear();
				simulationTask.getCropYields().addAll(cropYields);

				// Set the status of the simulationTask
				simulationTask.setAgriStatus(Status.SUCCESS);
			}

			if ((simulationBlockType == SimulationType.APV || simulationBlockType == SimulationType.ONLY_PV)
					&& resultMap.get("pv_yield") != null) {
				PVYield pvYield = new PVYield();
				pvYield.setPvYield(
						resultMap.get("pv_yield") != null ? Float.parseFloat(resultMap.get("pv_yield")) : null);
				pvYield.setFrontGain(
						resultMap.get("front_gain") != null ? Float.parseFloat(resultMap.get("front_gain")) : null);
				pvYield.setRearGain(
						resultMap.get("rear_gain") != null ? Float.parseFloat(resultMap.get("rear_gain")) : null);
				pvYield.setAlbedo(pvYield.getRearGain() != null && pvYield.getFrontGain() != null
						? pvYield.getRearGain() / pvYield.getFrontGain()
						: null);
				pvYield.setPpfd(resultMap.get("ppfd") != null ? Float.parseFloat(resultMap.get("ppfd")) : null);
				pvYield.setSimulationTask(simulationTask);
				pvYield = pvYieldRepository.save(pvYield);
				simulationTask.setPvYields(pvYield);
				simulationTask.setPvStatus(Status.SUCCESS);
			}

			List<BigDecimal> minValues = new ArrayList<>();
			List<BigDecimal> maxValues = new ArrayList<>();

			String[] fileTypes = resultMap.get("file_types").split(","); // Assuming types are comma-separated
			String[] fileUrls = resultMap.get("file_urls").split(","); // Assuming URLs are comma-separated
			String[] filemin = resultMap.get("file_min").split(","); // Assuming URLs are comma-separated
			String[] filemax = resultMap.get("file_max").split(","); // Assuming URLs are comma-separated

			if (fileTypes.length != fileUrls.length) {
				throw new IllegalArgumentException("Mismatch between number of file types and URLs");
			}

			for (int i = 0; i < fileTypes.length; i++) {
				String fileType = fileTypes[i].trim();
				String fileUrl = fileUrls[i].trim();
				String fileMin = filemin[i].trim();
				String fileMax = filemax[i].trim();

				BigDecimal roundedMinValue = roundToTwoDecimals(parseFloatSafely(fileMin));
				BigDecimal roundedMaxValue = roundToTwoDecimals(parseFloatSafely(fileMax));

				minValues.add(roundedMinValue);
				maxValues.add(roundedMaxValue);

				Scene scene = new Scene();

				switch (fileType) {
				case "file_carbon_assimilation":
					scene.setType(SceneType.carbon_assimilation);
					break;
				case "file_temperature":
					scene.setType(SceneType.temperature);
					break;
				case "file_radiation":
					scene.setType(SceneType.radiation);
					break;
				case "file_pv_yield":
					scene.setType(SceneType.pv_yield);
					break;
				case "file_material":
					scene.setType(SceneType.material);
					break;
				case "isometric_snap_radiation":
					scene.setType(SceneType.isometric_snap_radiation);
					break;
				case "isometric_snap_radiation_top":
					scene.setType(SceneType.isometric_snap_radiation_top);
					break;
				case "isometric_snap_radiation_iso":
					scene.setType(SceneType.isometric_snap_radiation_iso);
					break;
				case "isometric_snap_radiation_right":
					scene.setType(SceneType.isometric_snap_radiation_right);
					break;
				case "isometric_snap_radiation_left":
					scene.setType(SceneType.isometric_snap_radiation_left);
					break;
				case "isometric_snap_carbon_assimilation":
					scene.setType(SceneType.isometric_snap_carbon_assimilation);
					break;
				case "isometric_snap_carbon_assimilation_top":
					scene.setType(SceneType.isometric_snap_carbon_assimilation_top);
					break;
				case "isometric_snap_carbon_assimilation_iso":
					scene.setType(SceneType.isometric_snap_carbon_assimilation_iso);
					break;
				case "isometric_snap_carbon_assimilation_right":
					scene.setType(SceneType.isometric_snap_carbon_assimilation_right);
					break;
				case "isometric_snap_carbon_assimilation_left":
					scene.setType(SceneType.isometric_snap_carbon_assimilation_left);
					break;
				case "isometric_snap_temperature":
					scene.setType(SceneType.isometric_snap_temperature);
					break;
				case "isometric_snap_temperature_top":
					scene.setType(SceneType.isometric_snap_temperature_top);
					break;
				case "isometric_snap_temperature_iso":
					scene.setType(SceneType.isometric_snap_temperature_iso);
					break;
				case "isometric_snap_temperature_right":
					scene.setType(SceneType.isometric_snap_temperature_right);
					break;
				case "isometric_snap_temperature_left":
					scene.setType(SceneType.isometric_snap_temperature_left);
					break;
				case "visualization_left":
					scene.setType(SceneType.visualization_left);
					break;
				case "visualization_top":
					scene.setType(SceneType.visualization_top);
					break;
				case "visualization_right":
					scene.setType(SceneType.visualization_right);
					break;
				case "visualization_iso":
					scene.setType(SceneType.visualization_iso);
					break;
				case "file_geometry":
					scene.setType(SceneType.geometry);
					break;
				case "file_dli":
					scene.setType(SceneType.dli);
					break;
				}

				scene.setUrl(fileUrl); // Set URL dynamically
				if (roundedMinValue != null) {
					scene.setMinimum(roundedMinValue);
				}
				if (roundedMaxValue != null) {
					scene.setMaximum(roundedMaxValue);
				}
				scene.setSimulationTask(simulationTask);
				simulationTask.getScenes().add(scene);
			}

		}

		simulationTask.setCompletedAt(
				LocalDateTime.parse(resultMap.get("completedAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

		simulationTask.setServerName(resultMap.get("serverName") != null ? resultMap.get("serverName") : null);
		simulationTask.setTaskExecutionTimeOnServer(resultMap.get("taskExecutionTimeOnServer") != null
				? Long.parseLong(resultMap.get("taskExecutionTimeOnServer"))
				: null);

		SimulationTask savedSimulationTask = simulationTaskRepository.save(simulationTask);

		if (simulationTask.getSimulationBlock().getBlockSimulationType().equals(SimulationType.APV)
				&& simulationTask.getSimulation().getWithTracking()
				&& resultMap.get("type").equalsIgnoreCase(SceneType.BIFACIAL.toString())) {
			savedSimulationTask.setAgriStatus(Status.QUEUED);
			savedSimulationTask = simulationTaskRepository.save(savedSimulationTask);

			messageProducer.sendMessage(exchangeName, routingKeyAgri,
					objectMapper.writeValueAsString(savedSimulationTask));
		}

		updateCompletedTaskCount(savedSimulationTask);
	}

	private BigDecimal roundToTwoDecimals(Float value) {
		if (value != null) {
			return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
		}
		return null; // or handle as needed
	}

	private Float parseFloatSafely(String value) {
		if (value != null && !value.isEmpty()) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				System.err.println("Invalid float value: " + value);
			}
		}
		return null; // or handle as needed
	}

	private void updateCompletedTaskCount(SimulationTask savedSimulationTask) {
		SimulationType simulationType = savedSimulationTask.getSimulationBlock().getBlockSimulationType();

		Simulation simulation = savedSimulationTask.getSimulation();

		if (simulationType.equals(SimulationType.ONLY_PV) && savedSimulationTask.getPvStatus().equals(Status.SUCCESS))
			simulation.setCompletedTaskCount(simulation.getCompletedTaskCount() + 1);
		else if (simulationType.equals(SimulationType.ONLY_AGRI)
				&& savedSimulationTask.getAgriStatus().equals(Status.SUCCESS))
			simulation.setCompletedTaskCount(simulation.getCompletedTaskCount() + 1);
		else if (simulationType.equals(SimulationType.APV) && savedSimulationTask.getPvStatus().equals(Status.SUCCESS)
				&& savedSimulationTask.getAgriStatus().equals(Status.SUCCESS))
			simulation.setCompletedTaskCount(simulation.getCompletedTaskCount() + 1);

		if (simulation.getTaskCount().equals(simulation.getCompletedTaskCount())) {
			simulation.setStatus(Status.SUCCESS);
		}

		Simulation updatedSimulation = simulationRepository.save(simulation);

		// adding dli output in a file
		if (updatedSimulation.getStatus() == Status.SUCCESS && simulationType.equals(SimulationType.ONLY_PV)) {
			dliProcessingScheduler.scheduleDliProcessing(updatedSimulation);
		}
	}
}
