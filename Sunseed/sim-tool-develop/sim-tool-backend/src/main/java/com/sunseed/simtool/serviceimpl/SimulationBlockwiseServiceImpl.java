package com.sunseed.simtool.serviceimpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.AgriBlockSimulationDetails;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationBlock;
import com.sunseed.simtool.entity.SimulationGroundArea;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.helper.SimulationBlockWiseHelper;
import com.sunseed.simtool.helper.PayloadExtractor;
import com.sunseed.simtool.helper.PayloadExtractor.CropValues;
import com.sunseed.simtool.helper.PayloadExtractor.CycleData;
import com.sunseed.simtool.model.response.SimulationResponseDto;
import com.sunseed.simtool.rabbitmq.MessageProducer;
import com.sunseed.simtool.repository.SimulationGroundAreaRepository;
import com.sunseed.simtool.repository.SimulationRepository;
import com.sunseed.simtool.repository.SimulationTaskRepository;
import com.sunseed.simtool.service.EPWService;
import com.sunseed.simtool.service.SimulationBlockwiseService;
import com.sunseed.simtool.serviceimpl.EPWServiceImpl.WeatherData;
import com.sunseed.simtool.util.SimulationUtils;
import com.sunseed.simtool.validation.SimulationValidator;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SimulationBlockwiseServiceImpl implements SimulationBlockwiseService {

	private final ModelMapper modelMapper;
	private final SimulationRepository simulationRepository;
	private final SimulationTaskRepository simulationTaskRepository;
	private final MessageProducer messageProducer;
	private final String exchangeName;
	private final String routingKeyPV;
	private final String routingKeyAgri;
	private final ObjectMapper objectMapper;
	private final String simulationRunPolicy;
	private final Integer simulationDailyRunHours;
	private final EPWService epwService;
	private final SimulationValidator simulationValidator;
	private final Integer simulationDurationInDays;
	private final SimulationBlockWiseHelper simulationBlockwiseHelper;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${minimum.length}")
	private Double minLength;

	@Value("${number.pitch}")
	private Integer numberPitch;

	@Value("${minimum.modules}")
	private Double minModules;

	@Value("${minimum.ground.length}")
	private Double minimumGroundLength;

	@Value("${minimum.number.of.crops}")
	private Integer minimumNumberOfCrops;

	@Value("${reference.void.ratio}")
	private Double referenceVoidRatio;

	public SimulationBlockwiseServiceImpl(ModelMapper modelMapper, SimulationRepository simulationRepository,
			SimulationTaskRepository simulationTaskRepository, MessageProducer messageProducer,
			@Value("${rabbitmq.exchange}") String exchangeName, @Value("${rabbitmq.routingkey.pv}") String routingKeyPV,
			@Value("${rabbitmq.routingkey.agri}") String routingKeyAgri, ObjectMapper objectMapper,
			@Value("${simulation.run.policy}") String simulationRunPolicy,
			@Value("${simulation.daily.run.hours}") Integer simulationDailyRunHours, EPWService epwService,
			SimulationValidator simulationValidator,
			@Value("${simulation.duration.days}") Integer simulationDurationInDays,
			SimulationBlockWiseHelper simulationBlockwiseHelper) {
		super();
		this.modelMapper = modelMapper;
		this.simulationRepository = simulationRepository;
		this.simulationTaskRepository = simulationTaskRepository;
		this.messageProducer = messageProducer;
		this.exchangeName = exchangeName;
		this.routingKeyPV = routingKeyPV;
		this.routingKeyAgri = routingKeyAgri;
		this.objectMapper = objectMapper;
		this.simulationRunPolicy = simulationRunPolicy;
		this.simulationDailyRunHours = simulationDailyRunHours;
		this.epwService = epwService;
		this.simulationValidator = simulationValidator;
		this.simulationDurationInDays = simulationDurationInDays;
		this.simulationBlockwiseHelper = simulationBlockwiseHelper;
	}

	@Override
	@Transactional
	public SimulationResponseDto createSimulationBlockwise(Map<String, Object> runPayload, Long projectId,
			Long userProfileId) throws NumberFormatException, IOException {
		log.debug("Enter into createSimulationBlockwise Service");

		log.debug("Simulation DTO: " + runPayload);

		log.info("Validating simulation inputs");

		Long runId = null;
		SimulationType simulationType = null;
		Boolean withTracking = Boolean.FALSE;

		if (runPayload != null) {
			if (runPayload.get("id") == null)
				throw new InvalidRequestBodyArgumentException("Run Id can't be null");
			runId = Long.parseLong((String) runPayload.get("id").toString());
		} else
			throw new InvalidRequestBodyArgumentException("Run payload can't be null");

		if (runPayload.get("preProcessorToggles") != null
				&& ((Map<String, String>) runPayload.get("preProcessorToggles")).get("toggle") != null) {
			simulationType = SimulationType
					.fromString(((Map<String, String>) runPayload.get("preProcessorToggles")).get("toggle"));
		} else
			throw new InvalidRequestBodyArgumentException("Simulation type can't be null");

		if (runPayload.get("pvParameters") != null
				&& ((Map<String, String>) runPayload.get("pvParameters")).get("withTracking") != null) {
			withTracking = Boolean.valueOf(((Map<String, String>) runPayload.get("pvParameters")).get("withTracking"));
		}

		if (simulationRepository.findByRunId(runId).isPresent())
			throw new InvalidRequestBodyArgumentException("Simulation for runId: " + runId + " already exists");

		simulationValidator.validatePayload(runPayload, simulationType, withTracking);
		log.info("Validated simulation inputs");

		log.info("Simulation Run Policy: " + simulationRunPolicy);
		log.info("Simulation Daily Hour Run Interval: " + simulationDailyRunHours);

		Simulation simulation = new Simulation();

		simulation.setProjectId(projectId);
		simulation.setUserProfileId(userProfileId);
		simulation.setRunId(runId);
		simulation.setWithTracking(withTracking);
		simulation.setSimulationType(simulationType);
		simulation.setRunPayload(runPayload);
		simulation.setStatus(Status.QUEUED);
		simulation.setSimulationTasks(new ArrayList<>());
		simulation.setSimulationBlock(new ArrayList<>());

		Long taskCount = 0l;

		log.info("Fetching weather data ....");
		List<WeatherData> weatherData = epwService.getWeatherInfoForDateTime(
				Double.parseDouble((String) simulation.getRunPayload().get("longitude")),
				Double.parseDouble((String) simulation.getRunPayload().get("latitude")));

		List<SimulationBlock> simulationBlocksData = simulationBlockwiseHelper.getSimulationBlocksData(runPayload,
				simulationType, simulationDurationInDays, simulationRunPolicy);

		if (simulationBlocksData == null || simulationBlocksData.isEmpty())
			throw new RuntimeException("Failed to simulate data in blocks");

		// sorting the list for getting the order based on simulation block start date
		simulationBlocksData.sort(Comparator.comparing(SimulationBlock::getBlockStartDate));

		simulation.setStartDate(simulationBlocksData.get(0).getBlockStartDate());
		simulation.setEndDate(simulation.getStartDate().plusDays(simulationDurationInDays - 1));
		log.info("Creating simulation tasks .... ");

		// with epw timezone offset we will calculate taskTimezone and save it in
		// simulation tasks later
		double epwOffset = weatherData.get(0).getTimeZone();
		String taskTimeZone = SimulationUtils.convertToTimeZoneString(epwOffset);

		// Map for storing tasks which are having highest radiation for the blocks
		Map<LocalDateTime, String> highestRadiationTasksMap = new HashMap<>();

		for (SimulationBlock simBlock : simulationBlocksData) {

			log.debug("Simulation date : " + simBlock.getBlockSimulationDate() + " for block index: "
					+ simBlock.getBlockIndex());

			simBlock.setSimulationTasks(new ArrayList<>());
			LocalDate blockSimulationDate = simBlock.getBlockSimulationDate();
			LocalDateTime startTime = blockSimulationDate.atStartOfDay();
			LocalDateTime endTime = blockSimulationDate.atTime(23, 0);

			log.debug("Start Time: " + startTime + " End Time: " + endTime);

			// Track highest radiation value and the corresponding task
			SimulationTask highestRadiationTask = null;
			double highestRadiation = Double.MIN_VALUE;

			while (startTime.isBefore(endTime) || startTime.isEqual(endTime)) {
				LocalDate blockStartDate = simBlock.getBlockStartDate();
				LocalDate blockEndDate = simBlock.getBlockEndDate();
				WeatherData blockAvgWeatherData = getAverageWeatherDataOfBlock(weatherData, startTime, blockStartDate,
						blockEndDate);
				blockAvgWeatherData.setDateTime(startTime);

				log.debug("Weather Data: " + blockAvgWeatherData);
				simulationValidator.validateWeatherData(blockAvgWeatherData, simulationType);

//				if (!(blockAvgWeatherData.getDiffuseHorizRad() > 0.0 || blockAvgWeatherData.getDirectNormalRad() > 0.0
//						|| blockAvgWeatherData.getGlobalHorizRad() > 0.0)) {
				if(!(blockAvgWeatherData.getDirectNormalRad() > 0.0)) {
				} else {
					// firstly regulating air Temperature based on given Temp control
					double regulatedAirTemperature = regulateAirTempBasedOnGivenTempControl(runPayload,
							blockAvgWeatherData.getAirTemperature(), simulationType);
					blockAvgWeatherData.setAirTemperature(regulatedAirTemperature);

					taskCount++;
					SimulationTask simulationTask = new SimulationTask();
					simulationTask.setDate(startTime);
					simulationTask.setSimulation(simulation);
					simulationTask.setSimulationBlock(simBlock);
					simulationTask.setTaskTimezone(taskTimeZone);
					simulationTask.setScenes(new ArrayList<>());

					simulationTask.setWeatherCondition(
							objectMapper.convertValue(blockAvgWeatherData, new TypeReference<Map<String, String>>() {
							}));

					// calculate radiation sum
					double currentRadiation = blockAvgWeatherData.getDirectNormalRad()
							+ blockAvgWeatherData.getDiffuseHorizRad();

					// update highest radiation tracking
					if (currentRadiation > highestRadiation) {
						highestRadiation = currentRadiation;
						highestRadiationTask = simulationTask;
					}

					simulation.getSimulationTasks().add(simulationTask);
					simBlock.getSimulationTasks().add(simulationTask);

					log.debug("Simulation Task #{}: {}", taskCount, simulationTask);
				}

				startTime = SimulationUtils.nextSimulationTaskTime(simulationDailyRunHours, startTime);
			}

			simBlock.setSimulation(simulation);
			simulation.getSimulationBlock().add(simBlock);

			// => creating and adding simulation ground area to the block
			SimulationGroundArea simGroundArea = unitAreaCalculationBlockwise(runPayload, simBlock);
			simGroundArea.setSimulationBlock(simBlock);
			simBlock.setSimulationGroundArea(simGroundArea);

			// adding the highest radiation date to the map with just a value true
			if (highestRadiationTask != null)
				highestRadiationTasksMap.putIfAbsent(highestRadiationTask.getDate(), "true");
		}

		simulation.setTaskCount(taskCount);

		// now adding the value of hasHighestRadiation = true for the tasks having
		// highest radiation in the block
		// and to be done for all the blocks of the simulation
		for (SimulationTask simTask : simulation.getSimulationTasks()) {
			// check if the date exists as a key in the map
			if (highestRadiationTasksMap.containsKey(simTask.getDate()))
				simTask.setHasHighestRadiation(true);
		}

		// ===========> updating the memory values for apv and only agri simulations
		if (!simulation.getSimulationType().equals(SimulationType.ONLY_PV)) {
			Map<String, PayloadExtractor.CycleData> cycleDataFromPayload = PayloadExtractor
					.extractCycleDataFromPayload(objectMapper.valueToTree(runPayload));

			for (SimulationBlock simBlock : simulation.getSimulationBlock()) {
				if (simBlock.getBlockSimulationType().equals(SimulationType.ONLY_PV))
					continue;

				double unitXLength = simBlock.getSimulationGroundArea().getUnitXLength().doubleValue();
				
				PayloadExtractor.CycleData cycleData = cycleDataFromPayload
						.get(simBlock.getCycleStartDate().toString());
				if (cycleData == null)
					continue;
				Map<String, Map<String, PayloadExtractor.CropValues>> cropValuesMap = cycleData.getCropValues();
				Map<String, Double> bedMemorySum = new HashMap<>();

				for (AgriBlockSimulationDetails agriDetail : simBlock.getAgriBlockSimulationDetails()) {
					String bedName = agriDetail.getBedName();
					String cropName = agriDetail.getCropName();

					if (!cropValuesMap.containsKey(bedName))
						continue;
					Map<String, PayloadExtractor.CropValues> cropMap = cropValuesMap.get(bedName);
					if (!cropMap.containsKey(cropName))
						continue;

					PayloadExtractor.CropValues cropVal = cropMap.get(cropName);
					if (cropVal.getS1() == 0)
						continue; // prevent divide by zero

					double memory = (unitXLength / (cropVal.getS1() / 1000));
					if (cropVal.getO1() > 50) {
						memory *= 2;
					}
					
					memory /= 5.5;

					bedMemorySum.merge(bedName, memory, Double::sum);
				}

				// Count bed occurrences based on pattern
				List<String> bedPattern = cycleData.getInterBedPattern();
				int bedCount = cycleData.getBedCount();
				Map<String, Integer> bedOccurrences = new HashMap<>();

				for (int i = 0; i < bedCount; i++) {
					String bed = bedPattern.get(i % bedPattern.size());
					bedOccurrences.merge(bed, 1, Integer::sum);
				}

				double totalMemoryValue = 0;
				for (Map.Entry<String, Double> bedEntry : bedMemorySum.entrySet()) {
					String bed = bedEntry.getKey();
					double value = bedEntry.getValue();
					int occurrences = bedOccurrences.getOrDefault(bed, 0);
					totalMemoryValue += value * occurrences;
				}

				for (SimulationTask simTask : simBlock.getSimulationTasks()) {
					simTask.setRamRequired(totalMemoryValue < 1.0 ? 1.0 : totalMemoryValue);
				}
			}
		}

		Simulation dbSimulation = simulationRepository.saveAndFlush(simulation);

		// updating isFirst field in each block's first task
		dbSimulation.getSimulationBlock().forEach(block -> {
			List<SimulationTask> tasks = block.getSimulationTasks();

			if (tasks != null && !tasks.isEmpty()) {
				tasks.sort(
						Comparator.comparing(SimulationTask::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
				tasks.get(0).setFirst(true); // Mark first task in each block
			}
		});

		List<SimulationTask> tasks = dbSimulation.getSimulationTasks();
		tasks.sort(Comparator.comparing(SimulationTask::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

		log.info("Created simulation with id: " + dbSimulation.getId());
		log.info("Created {} simulation tasks", dbSimulation.getTaskCount());
		log.debug("Saved Simulation: " + dbSimulation);

		pushIntoQueue(tasks);

		log.debug("Exit from createSimulationBlockwise Service");

		return modelMapper.map(dbSimulation, SimulationResponseDto.class);
	}

	private double regulateAirTempBasedOnGivenTempControl(Map<String, Object> runPayload, double actualTemperature,
			SimulationType simulationType) {
		JsonNode payload = objectMapper.valueToTree(runPayload);
		if (simulationType.equals(SimulationType.APV) || simulationType.equals(SimulationType.ONLY_AGRI)) {
			if (payload.has("cropParameters")) {
				JsonNode cropParameters = payload.get("cropParameters");
				if (cropParameters.has("tempControl")
						&& cropParameters.get("tempControl").asText().equalsIgnoreCase("NONE"))
					return actualTemperature;
				else if (cropParameters.has("tempControl")
						&& cropParameters.get("tempControl").asText().equalsIgnoreCase("ABSOLUTE_MIN_MAX")) {
					double maxTemp = cropParameters.get("maxTemp").doubleValue();
					double minTemp = cropParameters.get("minTemp").doubleValue();

					if (actualTemperature > maxTemp)
						return maxTemp;
					else if (actualTemperature < minTemp)
						return minTemp;
					else
						return actualTemperature;
				}

				else if (cropParameters.has("tempControl")
						&& cropParameters.get("tempControl").asText().equalsIgnoreCase("TRAIL_MIN_MAX")) {
					double maxTemp = cropParameters.get("maxTemp").doubleValue();
					double minTemp = cropParameters.get("minTemp").doubleValue();
					double trail = cropParameters.get("trail").doubleValue();

					if (actualTemperature >= minTemp || actualTemperature <= maxTemp)
						return actualTemperature;
					else if (actualTemperature > maxTemp && actualTemperature <= maxTemp + trail)
						return maxTemp;
					else if (actualTemperature > maxTemp + trail)
						return actualTemperature - trail;
					else if (actualTemperature < minTemp && actualTemperature >= minTemp - trail)
						return minTemp;
					else
						return actualTemperature + trail;
				} else
					return actualTemperature;
			}
		}
		return actualTemperature;
	}

	// @Transactional
	private void pushIntoQueue(List<SimulationTask> simulationTasks) {
		log.info("Pushing tasks into the task queue ....");

		for (SimulationTask task : simulationTasks) {

			if (task.getSimulationBlock().getBlockSimulationType().equals(SimulationType.ONLY_PV)) {

				task.setPvStatus(Status.QUEUED);
				task.setAgriStatus(Status.NA);
				task.setEnqueuedAt(LocalDateTime.now());

				task = simulationTaskRepository.saveAndFlush(task);
				registerTransactionSynchronization(task, exchangeName, routingKeyAgri);
			} else if (task.getSimulationBlock().getBlockSimulationType().equals(SimulationType.ONLY_AGRI)) {

				task.setPvStatus(Status.NA);
				task.setAgriStatus(Status.QUEUED);
				task.setEnqueuedAt(LocalDateTime.now());

				task = simulationTaskRepository.saveAndFlush(task);

				registerTransactionSynchronization(task, exchangeName, routingKeyAgri);
			} else if (task.getSimulationBlock().getBlockSimulationType()
					.equals(SimulationType.APV) /*
												 * && task.getSimulation().getWithTracking().equals(Boolean.FALSE)
												 */) {

				task.setPvStatus(Status.QUEUED);
				task.setAgriStatus(Status.QUEUED);
				task.setEnqueuedAt(LocalDateTime.now());

				task = simulationTaskRepository.saveAndFlush(task);
				registerTransactionSynchronization(task, exchangeName, routingKeyAgri);

			}
		}

		log.info("Successfully pushed tasks into the task queue");
	}

	private void registerTransactionSynchronization(SimulationTask task, String exchangeName, String routingKey) {

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				try {
					String taskMessage = objectMapper.writeValueAsString(task);
					messageProducer.sendMessage(exchangeName, routingKey, taskMessage);
				} catch (JsonProcessingException e) {
					log.error("{}", e);
				}
			}
		});
	}

	private WeatherData getAverageWeatherDataOfBlock(List<WeatherData> weatherData, LocalDateTime startTime,
			LocalDate blockStartDate, LocalDate blockEndDate) {
		List<WeatherData> weatherDataForBlock = new ArrayList<>();
		long numberOfDaysInBlock = ChronoUnit.DAYS.between(blockStartDate, blockEndDate) + 1;

		// for simulation block date we are getting weather data for the same time in
		// all the days in the block
		while (blockStartDate.isBefore(blockEndDate) || blockStartDate.isEqual(blockEndDate)) {

			LocalDateTime blockDateStartTime = blockStartDate.atTime(startTime.getHour(), 0);
			for (WeatherData d : weatherData) {
				if (d.getDateTime().getMonth().equals(blockDateStartTime.getMonth())
						&& d.getDateTime().getDayOfMonth() == blockDateStartTime.getDayOfMonth()
						&& d.getDateTime().getHour() == blockDateStartTime.getHour()) {
					weatherDataForBlock.add(d);
					break;
				}
			}
			blockStartDate = blockStartDate.plusDays(1);
		}

		// now averaging the weather data for the particular hour
		WeatherData averageWeatherDataForBlock = calculateAverage(weatherDataForBlock, numberOfDaysInBlock);
		return averageWeatherDataForBlock;
	}

	private WeatherData calculateAverage(List<WeatherData> weatherDataForBlock, long numberOfDaysInBlock) {
		WeatherData averageData = new WeatherData();
		averageData.setTimeZone(weatherDataForBlock.get(0).getTimeZone());
		averageData.setDataSourceUrl(weatherDataForBlock.get(0).getDataSourceUrl());
		averageData.setDataSourceUncertainty(weatherDataForBlock.get(0).getDataSourceUncertainty());
		averageData.setPresentWeatherCodes(weatherDataForBlock.get(0).getPresentWeatherCodes());
		averageData.setPresentWeatherObservation(weatherDataForBlock.get(0).getPresentWeatherObservation());
		averageData.setDaysSinceLastSnow(weatherDataForBlock.get(0).getDaysSinceLastSnow());

		try {
			Field[] fields = WeatherData.class.getDeclaredFields();

			for (Field field : fields) {
				// Skip fields we want to exclude from averaging
				if (shouldExcludeField(field))
					continue;

				field.setAccessible(true); // Allow access to private fields

				double sum = 0.0;
				for (WeatherData weatherData : weatherDataForBlock) {
					Object value = field.get(weatherData);
					if (value instanceof Number) {
						sum += ((Number) value).doubleValue();
					}
				}

				double average = sum / numberOfDaysInBlock;
				field.set(averageData, average);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return averageData;
	}

	private boolean shouldExcludeField(Field field) {
		String fieldName = field.getName();
		return fieldName.equals("presentWeatherObservation") || fieldName.equals("presentWeatherCodes")
				|| fieldName.equals("timeZone") || fieldName.equals("dataSourceUrl") || fieldName.equals("dateTime")
				|| fieldName.equals("dataSourceUncertainty") || fieldName.equals("daysSinceLastSnow");
	}

	private SimulationGroundArea unitAreaCalculationBlockwise(Map<String, Object> runPayload,
			SimulationBlock simBlock) {
		Double moduleConfigScale = 0.0;
		Integer numberOfPitch;
		Double minimumModules = 0.0;
		Double lengthOfOneRow = 0.0;
		Double unitXLength;
		Integer xRepetition = 0;
		Double minGroundLength = 0.0;
		Double pitchOfRows = 0.0;
		Double pvLength = 0.0;
		Double agriLength = 0.0;
		Double cropSpacing = 0.0;
		int numberOfCrops;
		String moduleConfig = "";
		Double pvModuleLength = 0.0;
		Double pvModuleWidth = 0.0;
		Double gapBetweenModules = 0.0;
		int moduleIncrement = 0;
		Double moduleIncrementFactor = 0.0;

		JsonNode payload = objectMapper.valueToTree(runPayload);
		SimulationType blockSimulationType = simBlock.getBlockSimulationType();

		// obtaining length of one row from payload
		if (payload.get("preProcessorToggles").hasNonNull("lengthOfOneRow")) {
			lengthOfOneRow = payload.get("preProcessorToggles").get("lengthOfOneRow").asDouble();
		}

		// pitch of rows
		if (payload.get("preProcessorToggles").hasNonNull("pitchOfRows"))
			pitchOfRows = payload.get("preProcessorToggles").get("pitchOfRows").asDouble();

		numberOfPitch = numberPitch;

		// calculating agri length and in case of only agri agri length is
		// minGroundLength
		if (blockSimulationType.equals(SimulationType.ONLY_AGRI)) {
			minimumModules = minModules;
			Map<String, Double> initialAgriResultMap = getAgriLengthAndCropCountAndCropSpacing(payload,
					simBlock.getCycleStartDate(), simBlock.getCycleEndDate());

			cropSpacing = initialAgriResultMap.get("cropSpacing");
			numberOfCrops = initialAgriResultMap.get("numberOfCrops").intValue();
			agriLength = initialAgriResultMap.get("agriLength");
			minGroundLength = agriLength;
		} else {

			// if module mask pattern is given
			// setting minModules and moduleIncrement as same to module mask pattern length
			// else taking minimumModules as given in propFile and module increment factor
			// as 1
			if (payload.get("pvParameters").hasNonNull("moduleMaskPattern")) {
				String moduleMaskPattern = payload.get("pvParameters").get("moduleMaskPattern").asText().trim();
				minimumModules = (double) moduleMaskPattern.length() == 0 ? minModules
						: (double) moduleMaskPattern.length();
				moduleIncrement = moduleMaskPattern.length() == 0 ? 1 : moduleMaskPattern.length();
			} else {
				minimumModules = minModules;
				moduleIncrement = 1;
			}

			// getting module config
			if (payload.has("pvParameters") && payload.get("pvParameters").has("moduleConfigs")
					&& !payload.get("pvParameters").get("moduleConfigs").isEmpty()
					&& payload.get("pvParameters").get("moduleConfigs").get(0).hasNonNull("moduleConfig")) {

				moduleConfig = payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText()
						.trim();
			}

			// getting pvModuleLength and pvModuleWidth
			if (payload.has("pvParameters")) {
				if (payload.get("pvParameters").has("pvModule")) {
					if (payload.get("pvParameters").get("pvModule").hasNonNull("length")) {
						pvModuleLength = payload.get("pvParameters").get("pvModule").get("length").asDouble() / 1000.0; // in
																														// mm
					}
					if (payload.get("pvParameters").get("pvModule").hasNonNull("width")) {
						pvModuleWidth = payload.get("pvParameters").get("pvModule").get("width").asDouble() / 1000.0; // in
																														// mm
					}

				}
			}

			// getting moduleConfigScale
			if (moduleConfig.charAt(1) == 'P') {
				moduleConfigScale = pvModuleWidth;
			} else if (moduleConfig.charAt(1) == 'L') {
				moduleConfigScale = pvModuleLength;
			}

			gapBetweenModules = payload.get("pvParameters").get("gapBetweenModules").asDouble() / 1000.0;
			pvLength = minimumModules * (moduleConfigScale + gapBetweenModules);

			// if case is only pv then minGroundLength will be pv length only
			minGroundLength = pvLength;

			if (blockSimulationType.equals(SimulationType.APV)) {

				Map<String, Double> initialAgriResultMap = getAgriLengthAndCropCountAndCropSpacing(payload,
						simBlock.getCycleStartDate(), simBlock.getCycleEndDate());

				cropSpacing = initialAgriResultMap.get("cropSpacing");
				numberOfCrops = initialAgriResultMap.get("numberOfCrops").intValue();
				agriLength = initialAgriResultMap.get("agriLength");

				if (agriLength >= lengthOfOneRow || pvLength >= lengthOfOneRow) {
					minGroundLength = lengthOfOneRow;
				} else {
					// for handling large difference of cropSpacing and pvLength
					if (pvLength > agriLength + cropSpacing) {
						numberOfCrops = (int) Math.ceil(pvLength / cropSpacing);
						agriLength = cropSpacing.doubleValue() * (numberOfCrops);
					}

					Double refVoidRatio = referenceVoidRatio;
					int maxNumberOfCrops = 2 * numberOfCrops;
					Double currentVoidRatio = 1.0;
					Double finalAgriLength = agriLength;
					Integer finalNumberOfCrops = numberOfCrops;
					moduleIncrementFactor = (moduleConfigScale + gapBetweenModules) * moduleIncrement;

					// here taking 1 as replacement for 100%
					Double finalVoidRatio = Math.abs(1 - (pvLength / agriLength));

					while (numberOfCrops <= maxNumberOfCrops) {

						// if agriLength or pvLength matches or crosses lengthOfOneRow
						// then finalAgriLength will become lengthOfOneRow
						// and still best void ratio not found
						if (agriLength >= lengthOfOneRow || pvLength >= lengthOfOneRow) {
							finalAgriLength = lengthOfOneRow;
							break;
						}

						// calculating void ratio for current pv & agri lengths
						currentVoidRatio = Math.abs(1 - (pvLength / agriLength));

						// best case if void ratio comes closest to refVoidRatio
						// then pvLength,agriLength,cropCount gets tracked
						if (currentVoidRatio <= refVoidRatio) {
							finalAgriLength = agriLength;
							finalNumberOfCrops = numberOfCrops;
							break;
						}

						// if given void ratio comes to be better than previous
						// then update final pv & agri length
						if (currentVoidRatio < finalVoidRatio) {
							finalAgriLength = agriLength;
							finalNumberOfCrops = numberOfCrops;
							finalVoidRatio = currentVoidRatio;
						}

						// now loop increment factors for which loop will end
						// not checking for equal case as for that currentVoidRatio will be 0
						// no need to check
						if (pvLength < agriLength) {
							pvLength += moduleIncrementFactor;
						} else if (agriLength < pvLength) {
							numberOfCrops += 1;
							agriLength += cropSpacing;
						}
					}

					// as we cannot break cropSpacing hence agriLength will be final ground length
					minGroundLength = finalAgriLength;
				}
			}
		}

		if (lengthOfOneRow <= minGroundLength) {
			xRepetition = 1;
			unitXLength = lengthOfOneRow;
		} else {
			xRepetition = (int) (lengthOfOneRow / minGroundLength);
			unitXLength = minGroundLength;
		}

		// saving simulation ground area
		SimulationGroundArea simulationGroundArea = new SimulationGroundArea();
		simulationGroundArea.setUnitXLength(unitXLength);
		simulationGroundArea.setUnitYLength(pitchOfRows);
		simulationGroundArea.setXRepetition(xRepetition);
		simulationGroundArea.setYRepetition(numberOfPitch);
		simulationGroundArea.setXLength(lengthOfOneRow);
		simulationGroundArea.setYLength(pitchOfRows * numberOfPitch);
		return simulationGroundArea;
	}

	private Map<String, Double> getAgriLengthAndCropCountAndCropSpacing(JsonNode payload, LocalDate cycleStartDate,
			LocalDate cycleEndDate) {
		Map<String, Double> resultMap = new HashMap<>();
		// Assuming the length of one row is provided in meters
		double lengthOfOneRow = payload.get("preProcessorToggles").get("lengthOfOneRow").asDouble();

		// Initialize minLength to lengthOfOneRow
		Double minLength = Double.MIN_VALUE;

		if (payload.has("cropParameters") && payload.get("cropParameters").has("cycles")) {
			JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
			if (!cyclesNode.isNull() && cyclesNode.isArray()) {
				for (JsonNode cycle : cyclesNode) {
					LocalDate existingCycleStartDate = LocalDate.parse(cycle.get("cycleStartDate").asText());
					Integer cycleDurationInDays = cycle.get("duration").asInt();
					LocalDate existingCycleEndDate = existingCycleStartDate.plusDays(cycleDurationInDays - 1);

					if (existingCycleStartDate.equals(cycleStartDate) && existingCycleEndDate.equals(cycleEndDate)) {

						// Iterate over beds within the cycle
						if (cycle.has("cycleBedDetails") && cycle.get("cycleBedDetails").isArray()) {
							for (JsonNode bed : cycle.get("cycleBedDetails")) {
								// Iterate over crops within the bed
								if (bed.has("cropDetails") && bed.get("cropDetails").isArray()) {
									for (JsonNode crop : bed.get("cropDetails")) {
										if (crop.has("s1")) {

											// by default take given minimum no of crops
											int cropMultiplicationFactor = minimumNumberOfCrops;
											if (crop.hasNonNull("maxPlantsPerBed"))
												cropMultiplicationFactor = crop.get("maxPlantsPerBed").asInt();
											else
												cropMultiplicationFactor = minimumNumberOfCrops;

											double s1InMeters = (crop.get("s1").asDouble() / 1000.0)
													* cropMultiplicationFactor;
											if (s1InMeters >= lengthOfOneRow) {
												resultMap.put("cropSpacing", crop.get("s1").asDouble() / 1000.0);
												resultMap.put("agriLength", lengthOfOneRow);
												resultMap.put("numberOfCrops",
														Double.valueOf(cropMultiplicationFactor));
												return resultMap; // End all loops and return lengthOfOneRow
											} else if (s1InMeters >= minLength) {
												minLength = Math.max(minLength, s1InMeters);
												resultMap.put("cropSpacing", crop.get("s1").asDouble() / 1000.0);
												resultMap.put("agriLength", minLength);
												resultMap.put("numberOfCrops",
														Double.valueOf(cropMultiplicationFactor));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return resultMap;
	}
}
