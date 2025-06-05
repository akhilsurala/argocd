package com.sunseed.serviceImpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.Bed;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.Cycles;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Projects;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.Runs;
import com.sunseed.entity.SimulatedRun;
import com.sunseed.entity.UserProfile;
import com.sunseed.enums.RunBayStatus;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.exceptions.AuthenticationException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.InvalidDataException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.exceptions.WebclientException;
import com.sunseed.helper.PostprocessingHelper;
import com.sunseed.helper.PostProcessingFunctionsHelper;
import com.sunseed.mappers.AgriGeneralModelMapper;
import com.sunseed.mappers.CropParameterModelMapper;
import com.sunseed.mappers.EconomicParameterModelMapper;
import com.sunseed.mappers.PvParameterModelMapper;
import com.sunseed.model.requestDTO.Simulation;
import com.sunseed.model.responseDTO.MasterRunResponse;
import com.sunseed.model.responseDTO.RunNameListResponseDto;
import com.sunseed.model.responseDTO.RunResponseToggleDto;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;
import com.sunseed.projection.ControlPanel;
import com.sunseed.projection.DeleteRunProjection;
import com.sunseed.projection.RunProjectionRecord;
import com.sunseed.projection.SceneDetails;
import com.sunseed.projection.SceneList;
import com.sunseed.projection.SceneResponse;
import com.sunseed.projection.SceneType;
import com.sunseed.repository.AgriGeneralParameterRepo;
import com.sunseed.repository.CropParametersRepo;
import com.sunseed.repository.EconomicParameterRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.PvParameterRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.SimulatedRunRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.NotificationService;
import com.sunseed.service.RunService;
import com.sunseed.service.SimulationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunServiceImpl implements RunService {

	@Autowired
	private ProjectsRepository userProjectsRepo;
	@Autowired
	private CropParametersRepo cropParametersRepo;
	@Autowired
	private PvParameterRepository pvParameterRepository;
	@Autowired
	private RunsRepository runsRepository;
	@Autowired
	private AgriGeneralParameterRepo agriGeneralParameterRepo;
	@Autowired
	private EconomicParameterRepository economicParameterRepository;
	@Autowired
	private PvParameterModelMapper pvParameterModelMapper;
	@Autowired
	private CropParameterModelMapper cropParameterModelMapper;
	@Autowired
	private EconomicParameterModelMapper economicParameterModelMapper;
	@Autowired
	private AgriGeneralModelMapper agriGeneralModelMapper;
	@Autowired
	private EntityManager entityManager;

	@Value("${crop.intervalType}")
	private Integer intervalType;

	private final NotificationService notificationService;
	private final PostprocessingHelper postprocessingHelper;
	private final PostProcessingFunctionsHelper postProcessingFunctionsHelper;

	private final SimulationService simulationService;
	private final SimulatedRunRepository simulatedRunRepo;
	@Autowired
	private UserProfileRepository userProfileRepository;

	@Override
	public Runs createRun(Long projectId, PreProcessorToggle toggles) {
		// TODO Auto-generated method stub

		System.out.println("Enter 1");
		Optional<Projects> project = userProjectsRepo.findById(projectId);
		if (project.isEmpty())
			return null;

		String toggle = toggles.getToggle().name();
		System.out.println("toggle in create run" + toggle);

		if (!toggle.equalsIgnoreCase("APV") && !toggle.equalsIgnoreCase("ONLY_PV")
				&& !toggle.equalsIgnoreCase("ONLY_AGRI"))
			return null;

		List<CropParameters> cropParameters;
		List<PvParameter> pvParameters;
		List<AgriGeneralParameter> agriGeneralParameters;
		List<EconomicParameters> economicParameters;
		Runs run = new Runs();

		switch (toggle.toUpperCase()) {
		case "APV":
			cropParameters = cropParametersRepo.getCropParametersWithDraft(projectId);
			System.out.println(cropParameters.size() + ": crop parameter with draft in create run method");
			pvParameters = pvParameterRepository.getPvParameterWithStatusDraft(projectId);
			System.out.println(pvParameters.size() + ": pv parameter with draft in create run method");
			agriGeneralParameters = agriGeneralParameterRepo.getAgriGeneralParametersWithDraft(projectId);
			System.out
					.println(agriGeneralParameters.size() + ": agri general parameter with draft in create run method");

			if (cropParameters.isEmpty() || pvParameters.isEmpty()) {
				return null;
			}

			run.setPvParameters(pvParameters.get(0));
			run.setCropParameters(cropParameters.get(0));
			run.setAgriGeneralParameters(agriGeneralParameters.get(0));
			break;

		case "ONLY_PV":
			pvParameters = pvParameterRepository.getPvParameterWithStatusDraft(projectId);
			if (pvParameters.isEmpty()) {
				return null;
			}

			run.setPvParameters(pvParameters.get(0));
			run.setCropParameters(null);
			run.setAgriGeneralParameters(null);
			break;

		case "ONLY_AGRI":
			cropParameters = cropParametersRepo.getCropParametersWithDraft(projectId);
			agriGeneralParameters = agriGeneralParameterRepo.getAgriGeneralParametersWithDraft(projectId);
			if (cropParameters.isEmpty() || agriGeneralParameters.isEmpty()) {
				return null;
			}

			run.setPvParameters(null);
			run.setCropParameters(cropParameters.get(0));
			run.setAgriGeneralParameters(agriGeneralParameters.get(0));
			break;

		default:
			// Optionally handle unexpected values of 'toggle'
			break;
		}
		economicParameters = economicParameterRepository.getEconomicParametersWithDraft(projectId);
		if (economicParameters.isEmpty() || economicParameters == null) {
			run.setEconomicParameters(null);
		} else {
			run.setEconomicParameters(economicParameters.get(0));
		}

		run.setRunName(toggles.getRunName());
		run.setInProject(project.get());
//        run.setRunStatus(RunStatus.HOLDING);
		run.setPreProcessorToggle(toggles);
		run.setCanSimulate(true);
		run.setRunStatus(RunStatus.HOLDING);
//        run.setUpdatedAt(LocalDateTime.now().toInstant(null));

		runsRepository.save(run);
		return run;
	}

	@Override
	public Runs updateRun(Long runId, PreProcessorToggle toggles) {

		/*
		 * This method updates canSimulate to true after validating Run based on
		 * PreProcessorToggles
		 */

		Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

		String toggle = toggles.getToggle().name();

		PvParameter pvParameters = run.getPvParameters();
		AgriGeneralParameter agriGeneralParameters = run.getAgriGeneralParameters();
		CropParameters cropParameters = run.getCropParameters();

		switch (toggle.toUpperCase()) {
		case "APV":
			if ((pvParameters == null) || (agriGeneralParameters == null) || (cropParameters == null)) {
				run.setCanSimulate(false);
				runsRepository.save(run);
				return null;
			}
			break;

		case "ONLY_PV":
			if (pvParameters == null || cropParameters != null || agriGeneralParameters != null) {
				run.setCropParameters(null);
				run.setAgriGeneralParameters(null);
				run.setCanSimulate(false);
				runsRepository.save(run);
				return null;
			}
			break;

		case "ONLY_AGRI":
			if (agriGeneralParameters == null || cropParameters == null || pvParameters != null) {
				run.setPvParameters(null);
				run.setCanSimulate(false);
				runsRepository.save(run);
				return null;
			}
			break;

		default:
			// Handle the case where 'toggle' doesn't match any of the cases
			break;
		}

		run.setCanSimulate(true);

		runsRepository.save(run);
		return run;

	}

	public List<Simulation> findDataFromSimtool(ArrayList<Long> id) {
		String sql = "Select * from simtool.simulations r where r.id IN :id";

		List<Object[]> results = entityManager.createNativeQuery(sql).setParameter("id", id).getResultList();
		return results.stream()
				.map(result -> new Simulation(((Number) result[0]).longValue(), ((Number) result[1]).longValue(),
						((Number) result[2]).longValue(), ((Number) result[3]).longValue(),
						((Number) result[4]).longValue(), ((Long) result[12]).longValue(), (String) result[13]))
				.collect(Collectors.toList());
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

	@Override
	public Map<String, Object> getAllRuns(Long projectId, String bay, Long userId, Long masterRunId,
			String searchText) {

		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.FORBIDDEN);

		Projects project = userProjectsRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
		System.out.println("Project Id " + project.getProjectId());

		Long existingUserId = project.getUserProfile().getUserId();
		System.out.println("UserID" + userId);
		System.out.println("Existing user ID " + existingUserId);

		if (userId != existingUserId) {
			throw new AuthenticationException(null, "user.access", HttpStatus.FORBIDDEN);
		}

		List<RunStatus> runStatusList = RunBayStatus.getListOfRunStatus(bay);
		if (runStatusList == null || runStatusList.isEmpty())
			throw new ResourceNotFoundException("run.status.invalid");
		List<RunProjectionRecord> runsWithVarExist = new ArrayList<>();
		List<MasterRunResponse> masterRunResponseList = null;
		// fetching runs from holding bay
		if (RunBayStatus.HOLDING.getValue().equalsIgnoreCase(bay)) {

			if (searchText == null || searchText.isBlank()) {
				runsWithVarExist = runsRepository.findMasterRunsWithVariantExist(project.getProjectId(), runStatusList);

			} else {
				runsWithVarExist = runsRepository.findMasterRunsWithVariantExistAndSearchText(project.getProjectId(),
						runStatusList, searchText);
			}
		} else if (RunBayStatus.RUNNING.getValue().equalsIgnoreCase(bay)) {
			// running bay master response list(only id and name)
			List<Runs> masterRunListForRunningBay = runsRepository
					.getAllRunsForRunningWithMasterTrueAndVarientMaster(project.getProjectId(), runStatusList);


			masterRunResponseList = masterRunListForRunningBay.stream().map((masterRun) -> {
				MasterRunResponse masterRunResponse = MasterRunResponse.builder().id(masterRun.getRunId())
						.runName(masterRun.getRunName()).build();
				return masterRunResponse;
			}).collect(Collectors.toList());
			if (masterRunId == null) {
				runsWithVarExist = runsRepository.findAllRunsForRunningBay(project.getProjectId(), runStatusList);
			} else
				runsWithVarExist = runsRepository.findMasterAndVariantsRuns(project.getProjectId(), masterRunId,
						runStatusList);
		}

		System.out.println("runs size variant :" + runsWithVarExist.size());

		List<Long> simulatedIds;

		// setting simulatedIds
		simulatedIds = runsWithVarExist.stream().filter(t -> t.simulatedRun() != null).map(t -> {
			if (t.simulatedRun() != null && t.simulatedRun().getSimulatedId() == null)
				throw new ResourceNotFoundException("simulation.id.not.matched");
			return t.simulatedRun().getSimulatedId();
		}).collect(Collectors.toList());

		// now getting simulation data from simtool
		List<Simulation> simulationResult = findDataFromSimtool(simulatedIds);

		// creating map for faster retrieval
		Map<Long, Simulation> simulationResultMap = new HashMap<>();

		simulationResult.forEach(t -> simulationResultMap.put(t.getRunId().longValue(), t));

		// now setting up response
		Map<String, Object> response = new HashMap<>();
		List<RunResponseToggleDto> runResponseDtoList = new ArrayList<>();

		for (RunProjectionRecord currentRun : runsWithVarExist) {

			RunResponseToggleDto runResponseDto = new RunResponseToggleDto();

			long runId = currentRun.id();
			runResponseDto.setId(currentRun.id());
			runResponseDto.setRunName(currentRun.runName());
			runResponseDto.setProjectId(currentRun.projectId());
			runResponseDto.setRunStatus(currentRun.runStatus().getValue());
			runResponseDto.setCreatedAt(currentRun.createdAt());
			runResponseDto.setUpdatedAt(currentRun.updatedAt());
			runResponseDto.setCloneId(currentRun.cloneId());
			runResponseDto.setIsMaster(currentRun.isMaster());
			runResponseDto.setVariantExist(currentRun.variantExist());
			runResponseDto.setAgriControl(currentRun.agriControl());
			runResponseDto.setPvControl(currentRun.pvControl());

			if (currentRun.simulatedRun() != null)
				runResponseDto.setSimulatedId(currentRun.simulatedRun().getSimulatedId());

			PreProcessorToggle toggles = currentRun.preProcessorToggle();
			runResponseDto.setPreProcessorToggle(toggles);

			if (currentRun.pvParameters() != null) {
				runResponseDto.setPvParameters(pvParameterModelMapper
						.entityToPvParameterResponseDto(currentRun.pvParameters(), toggles, runId));
			}

			if (currentRun.cropParameters() != null) {
				runResponseDto.setCropParameters(cropParameterModelMapper
						.entityToCropParametersResponseDto(currentRun.cropParameters(), runResponseDto.getId()));
			}

			if (currentRun.agriGeneralParameters() != null) {
				runResponseDto.setAgriGeneralParameters(agriGeneralModelMapper
						.entityToAgriGeneralParameterResponseDto(currentRun.agriGeneralParameters()));
			}

			if (currentRun.economicParameters() != null) {
				runResponseDto.setEconomicParameters(
						economicParameterModelMapper.getEconomicParameterResponseDto(currentRun.economicParameters()));
			}

			long progress = 0;
			System.out.println("RunId r : " + runId);

			if (simulationResultMap.containsKey(runId)) {
				Simulation s = simulationResultMap.get(runId);
				Runs run = runsRepository.getReferenceById(runId);

				System.out.println("Completed task : " + s.getCompletedTaskCount());
				System.out.println(" task count : " + s.getTaskCount());
				if (s.getTaskCount() == 0) {
					progress = 0;
				} else {

					System.out.println("comp : " + s.getCompletedTaskCount());
					System.out.println("task : " + s.getTaskCount());

					progress = (s.getCompletedTaskCount() * 100) / s.getTaskCount();

					System.out.println("Progress : " + progress);
				}

				// getting runStatus from simtool and then setting the status to the db and
				// response as well
				if (s.getStatus().equals("SUCCESS")) {
					if (!run.getRunStatus().equals(RunStatus.COMPLETED))
						updateRunStatusOnly(run, RunStatus.COMPLETED);
					runResponseDto.setRunStatus(RunStatus.COMPLETED.getValue());
				} else if (s.getStatus().equals("FAILED")) {
					if (!run.getRunStatus().equals(RunStatus.FAILED))
						updateRunStatusOnly(run, RunStatus.FAILED);
					runResponseDto.setRunStatus(RunStatus.FAILED.getValue());
				} else if (s.getStatus().equals("QUEUED") && (s.getCompletedTaskCount() > 0)) {
					if (!run.getRunStatus().equals(RunStatus.RUNNING))
						updateRunStatusOnly(run, RunStatus.RUNNING);
					runResponseDto.setRunStatus(RunStatus.RUNNING.getValue());
				}
			}
			runResponseDto.setProgress(progress);
			runResponseDtoList.add(runResponseDto);
		}

		response.put("runs", runResponseDtoList);
		response.put("masterRuns", masterRunResponseList);
		return response;
	}

	@Transactional
	public void updateRunStatusOnly(Runs run, RunStatus runStatus) {
		run.setRunStatus(runStatus);
		runsRepository.save(run);

		// Trigger notification and save it only once
		switch (runStatus) {

		case COMPLETED:
			notificationService.saveNotification(run.getRunName() + " run simulation is completed",
					run.getInProject().getUserProfile().getUserProfileId(), 1L, Boolean.TRUE);
			break;

		case FAILED:
			notificationService.saveNotification(run.getRunName() + "run simulation is failed",
					run.getInProject().getUserProfile().getUserProfileId(), 1L, Boolean.FALSE);
			break;

		case RUNNING:
			notificationService.saveNotification(run.getRunName() + "run simulation is running",
					run.getInProject().getUserProfile().getUserProfileId(), 1L, Boolean.TRUE);
			break;

		}
	}

	@Override
	public Runs getRunById(Long runId) {

		if (runId == null || runId <= 0)
			return null;

		Runs run = runsRepository.getRunInHoldingByRunId(runId);

		if (run == null)
			return null;

		return run;
	}

	@Override
	@Transactional
	public List<SimulationResponseDto> postSimulation(List<Long> runId, Long projectId, Long userId) {
		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.BAD_REQUEST);

		UserProfile userProfile = userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

		if (runId.size() <= 0 || projectId <= 0) {
			throw new UnprocessableException("id.invalid");
		}
		Optional<Projects> project = userProjectsRepo.findById(projectId);
		if (project.isEmpty())
			throw new ResourceNotFoundException("project.not.found");

		if (project.get().getUserProfile().getUserProfileId() != userProfile.getUserProfileId()) {
			throw new UnprocessableException("mismatch.project");
		}
// ******* simulation service call *************
		List<SimulationResponseDto> simulationResponseDtos = simulationService.postSimulation(runId, projectId);

		if (simulationResponseDtos.size() == 0) {
			throw new UnprocessableException("simulation.empty");

		}
		for (SimulationResponseDto simulation : simulationResponseDtos) {
			SimulatedRun simulatedRun = new SimulatedRun();
			if (simulation.getId() == null) {
				String error = simulation.getErrorMessage();
				System.out.println("Error Message :" + error);
				throw new WebclientException(error, HttpStatus.UNPROCESSABLE_ENTITY);
			}
			SimulatedRun existingSimulatedRun = simulatedRunRepo.getSimulatedRunByRunId(simulation.getRunId());
			if (existingSimulatedRun != null) {
				throw new ConflictException("simulation.already.exist");
			}
			simulatedRun.setSimulatedId(simulation.getId());
			simulatedRun.setProject(project.get());
			Runs run = runsRepository.findById(simulation.getRunId())
					.orElseThrow(() -> new ResourceNotFoundException("run.not.found.holding"));
			run.setSimulated(true);
			run.setRunStatus(RunStatus.QUEUED);
			simulatedRun.setRun(run);
			SimulatedRun savedSimulatedRun = simulatedRunRepo.save(simulatedRun);
			System.out.println("saved Simulation successfully :" + savedSimulatedRun);

		}

		return simulationResponseDtos;

	}

	// ************* simulated_run method **************
	@Override
	public Map<String, List<SimulationTaskStatusDto>> updateSimulatedRunStatus(String status, Long runId) {
		if (status == null || runId == null) {
			throw new InvalidDataException("invalid.data");
		}
		Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
		SimulatedRun simulatedRun = simulatedRunRepo.findByRun(run)
				.orElseThrow(() -> new ResourceNotFoundException("simulatedrun.not.found"));
		Long simulatedId = simulatedRun.getSimulatedId();
		// call simulated service
		Map<String, List<SimulationTaskStatusDto>> data = simulationService.updateStatus(status, simulatedId);
		RunStatus runStatus = null;
		if (status.equalsIgnoreCase("Resume")) {
			runStatus = RunStatus.QUEUED;
		} else if (status.equalsIgnoreCase("cancel")) {
			runStatus = RunStatus.CANCELLED;
			run.setActive(false);
		} else {
			runStatus = RunStatus.fromValue(status);
		}
		run.setRunStatus(runStatus);
		runsRepository.save(run);
		return data;
	}

	@Override
	public void deleteRun(Long userId, Long projectId, Long runId) {

		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.BAD_REQUEST);

		Projects project = userProjectsRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
		System.out.println("Project Id " + project.getProjectId());

		Long existingUserId = project.getUserProfile().getUserId();
		System.out.println("UserID" + userId);
		System.out.println("Existing user ID " + existingUserId);

		if (userId != existingUserId) {
			throw new AuthenticationException(null, "user.access", HttpStatus.FORBIDDEN);
		}

		DeleteRunProjection existingRun = runsRepository.findRunForDelete(runId)
				.orElseThrow(() -> new UnprocessableException(null, "run.not.found"));

		if (existingRun.runStatus() != RunStatus.HOLDING)
			throw new UnprocessableException(null, "run.status.not.holding");

		if (existingRun.isMaster() == true && existingRun.variantExist() == true)
			throw new UnprocessableException(null, "master.has.variants");

		deleteRunFromId(existingRun.id());

	}

	@Transactional
	private void deleteRunFromId(Long runId) {
		runsRepository.deleteById(runId);
	}

	@Override
	@Transactional
	public Map<String, Object> updateAgriPvControlStatusOfRun(Long projectId, Long runId, Long userId,
			Boolean agriControl, Boolean pvControl) {

		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.FORBIDDEN);

		Projects project = userProjectsRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
		System.out.println("Project Id " + project.getProjectId());

		Long existingUserId = project.getUserProfile().getUserId();
		System.out.println("UserID" + userId);
		System.out.println("Existing user ID " + existingUserId);

		if (userId != existingUserId) {
			throw new AuthenticationException(null, "user.access", HttpStatus.FORBIDDEN);
		}

		Map<String, Object> serviceResponse = new HashMap<>();

		// finding run and it's toggle value
		Runs existingRun = runsRepository.findById(runId)
				.orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

		if (agriControl != null && agriControl == true && pvControl != null && pvControl == true) {
			serviceResponse.put("run", null);
			serviceResponse.put("message", "cant.make.run.control.both");
			return serviceResponse;
		}

		// where run can be made pvControl if toggle is only pv
		else if (agriControl == null && pvControl != null && pvControl == true
				|| agriControl != null && agriControl == false && pvControl != null && pvControl == true) {

			String toggleValue = existingRun.getPreProcessorToggle() != null
					? existingRun.getPreProcessorToggle().getToggle().name()
					: null;
			if (toggleValue == null || !(toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())))
				throw new AgriGeneralParametersException(null, "cant.make.run.pv.control",
						HttpStatus.UNPROCESSABLE_ENTITY);

			else if (existingRun.isPvControl() == true)
				throw new AgriGeneralParametersException(null, "run.already.pv.control", HttpStatus.CONFLICT);

			Long masterRunId = null;
			if (existingRun.isMaster() == true)
				masterRunId = existingRun.getRunId();
			else
				masterRunId = existingRun.getCloneId();

			// first setting previous pvControl to false if present in the group
			runsRepository.updatePvControlInGroupToFalse(masterRunId);

			existingRun.setPvControl(true);
			Runs updatedRun = runsRepository.save(existingRun);
			serviceResponse.put("run", updatedRun);
			serviceResponse.put("message", "run.set.to.pv.control");
			return serviceResponse;

		}

		// where run can be made agriControl if toggle is only agri
		else if (agriControl != null && agriControl == true && pvControl == null
				|| agriControl != null && agriControl == true && pvControl != null && pvControl == false) {

			String toggleValue = existingRun.getPreProcessorToggle() != null
					? existingRun.getPreProcessorToggle().getToggle().name()
					: null;

			if (toggleValue == null || !(toggleValue.equalsIgnoreCase(Toggle.ONLY_AGRI.name())))
				throw new AgriGeneralParametersException(null, "cant.make.run.agri.control",
						HttpStatus.UNPROCESSABLE_ENTITY);

			else if (existingRun.isAgriControl() == true)
				throw new AgriGeneralParametersException(null, "run.already.agri.control", HttpStatus.CONFLICT);

			Long masterRunId = null;
			if (existingRun.isMaster() == true)
				masterRunId = existingRun.getRunId();
			else
				masterRunId = existingRun.getCloneId();

			// first setting previous agriControl to false if present in the group
			runsRepository.updateAgriControlInGroupToFalse(masterRunId);

			existingRun.setAgriControl(true);
			Runs updatedRun = runsRepository.save(existingRun);
			serviceResponse.put("run", updatedRun);
			serviceResponse.put("message", "run.set.to.agri.control");
			return serviceResponse;
		}

		// else where no update being done
		serviceResponse.put("run", null);
		serviceResponse.put("message", "nothing.to.update.for.control");
		return serviceResponse;

	}

	@Override
	public Map<String, Object> getRunNames(Long projectId, Long userId, List<Long> runIdList) {

		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.FORBIDDEN);

		Projects project = userProjectsRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

		Long existingUserId = project.getUserProfile().getUserId();

		if (!userId.equals(existingUserId)) {
			throw new AuthenticationException(null, "user.access", HttpStatus.FORBIDDEN);
		}

		// now checking for null list or any null entry in list
		if (runIdList.isEmpty() || runIdList.stream().anyMatch(Objects::isNull))
			throw new UnprocessableException("runIdList.cant.be.empty");

		List<RunNameListResponseDto> existingRunsList = runsRepository.getRunNames(projectId, runIdList);

		Map<String, Object> response = new HashMap<>();
		response.put("runs", existingRunsList);
		return response;
	}

	// scenes logic
	@Override
	public SceneResponse getAllScenesForRun(Long projectId, Long runId, Long userId) {
		if (userId == null || userId <= 0)
			throw new AuthenticationException(null, "user.not.found", HttpStatus.FORBIDDEN);

		Projects project = userProjectsRepo.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

		Long existingUserId = project.getUserProfile().getUserId();
		System.out.println("UserID" + userId);
		System.out.println("Existing user ID " + existingUserId);

		if (!userId.equals(existingUserId)) {
			System.out.println("user is not authorized");
			throw new AuthenticationException(null, "user.access", HttpStatus.FORBIDDEN);
		}
		Runs existingRun = runsRepository.findById(runId)
				.orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

		SimulatedRun simulatedRun = existingRun.getSimulatedRun();

		if (simulatedRun == null || simulatedRun.getSimulatedId() == null)
			throw new UnprocessableException("run.not.simulated");
		// *************** simulation ground area **********************
		List<Map<String, Object>> simulationGroundArea = findSimulationGroundAreas(simulatedRun.getSimulatedId());

		List<SceneList> sceneList = findScenesFromSimtool(existingRun.getRunId(), simulatedRun.getSimulatedId());

		SceneResponse scenes = transform(sceneList);

		// new fields added in responses
		scenes.setMonths(postprocessingHelper.generateMonths());
		List<String> dynamicInterval = postProcessingFunctionsHelper.generateIntervals(intervalType);
		scenes.setWeekIntervals(dynamicInterval);

		if (existingRun.getPvParameters() != null && existingRun.getPvParameters().getModeOfOperationId()
				.getModeOfOperation().equalsIgnoreCase("Single Axis Tracking")) {
			scenes.setIsTracking(Boolean.TRUE);
		}

//        // cycles with weeks added in control panel
		List<ControlPanel> controlPanelList = new ArrayList<>();
		System.out.println("existing crop parameter in scenes api " + existingRun.getCropParameters());
		System.out.println(existingRun.getPreProcessorToggle().getToggle());
		if (existingRun.getCropParameters() != null) {
			List<Cycles> cycles = existingRun.getCropParameters().getCycles();
			cycles.sort(Comparator.comparing(Cycles::getStartDate));

			for (Cycles cycle : cycles) {
				System.out.println("enter in cycles");
				ControlPanel controlPanel = new ControlPanel();
				// controlPanel.setCycleName(cycle.getName());
				int startBlockNumber = postprocessingHelper.calculateIntervalNumberForDate(cycle.getStartDate());
				// System.out.println("start block number is :" + startBlockNumber);
				List<Bed> beds = cycle.getBeds();
				int maxDuration = postprocessingHelper.findMaxDuration(beds);
				System.out.println("in scenes api max duration is :" + maxDuration);
				int endBlockNumber = postprocessingHelper
						.calculateIntervalNumberForDate(cycle.getStartDate().plusDays(maxDuration));
				// System.out.println("end block number is :" + endBlockNumber);
				if (startBlockNumber > endBlockNumber) {
					System.out.println("enter for  duplicatie cycle ");
					controlPanel.setCycleName(cycle.getName());
					List<Integer> startBlockList = IntStream.rangeClosed(startBlockNumber, 26).boxed().sorted()
							.collect(Collectors.toList());
					Map<String, Object> weeksMap = new TreeMap<>(
							Comparator.comparingInt(k -> Integer.parseInt(k.substring(1))));
					// System.out.println();
					// System.out.println("start block list :" + startBlockList.toString());

					for (Integer blockNumber : startBlockList) {
						String week = "W" + blockNumber;

						// Find corresponding ground area for the block
						Map<String, Object> groundArea = simulationGroundArea.stream()
								.filter(area -> ((Number) area.get("block_index")).intValue() == blockNumber)
								.findFirst().orElse(null);

						// Add to weeks map
						weeksMap.put(week, groundArea);
					}
					// System.out.println("week map for start block :" + weeksMap.toString());
					// List<String> startWeeks =
					// postprocessingHelper.convertWeeksIntoStringForm(startBlockList);
					controlPanel.setWeeks(weeksMap);
					controlPanelList.add(controlPanel);
					ControlPanel controlPanel1 = new ControlPanel();
					controlPanel1.setCycleName(cycle.getName());
					List<Integer> endBlockList = IntStream.rangeClosed(1, endBlockNumber).boxed()
							.collect(Collectors.toList());
					weeksMap = new TreeMap<>(Comparator.comparingInt(k -> Integer.parseInt(k.substring(1))));
					for (Integer blockNumber : endBlockList) {
						String week = "W" + blockNumber;

						// Find corresponding ground area for the block
						Map<String, Object> groundArea = simulationGroundArea.stream()
								.filter(area -> ((Number) area.get("block_index")).intValue() == blockNumber)
								.findFirst().orElse(null);

						// Add to weeks map
						weeksMap.put(week, groundArea);
					}
					// List<String> endWeeks =
					// postprocessingHelper.convertWeeksIntoStringForm(endBlockList);
					controlPanel1.setWeeks(weeksMap);
					controlPanelList.add(controlPanel1);

				} else {
					System.out.println("enter for single cycle");
					controlPanel.setCycleName(cycle.getName());
					List<Integer> blockList;
					if (startBlockNumber == endBlockNumber && maxDuration > intervalType) {
						// Full year, take all blocks
						blockList = IntStream.rangeClosed(startBlockNumber, 365 / intervalType).boxed()
								.collect(Collectors.toList());
					} else {
						// Normal case
						blockList = IntStream.rangeClosed(startBlockNumber, endBlockNumber).boxed()
								.collect(Collectors.toList());
					}
					Map<String, Object> weeksMap = new TreeMap<>(
							Comparator.comparingInt(k -> Integer.parseInt(k.substring(1))));
					// System.out.println(" block list :" + blockList.toString());

					for (Integer blockNumber : blockList) {
						String week = "W" + blockNumber;
						// System.out.println("simulation ground area :" +
						// simulationGroundArea.toString());

						// Find corresponding ground area for the block
						Map<String, Object> groundArea = simulationGroundArea.stream()
								.filter(area -> ((Number) area.get("block_index")).intValue() == blockNumber)
								.findFirst().orElse(null);

						// Add to weeks map
						weeksMap.put(week, groundArea);
					}
					// System.out.println("single cycle start block list :" + weeksMap.toString());

					// List<String> weeks =
					// postprocessingHelper.convertWeeksIntoStringForm(blockList);
					controlPanel.setWeeks(weeksMap);
					controlPanelList.add(controlPanel);
				}

			}
		}

		else if (existingRun.getPreProcessorToggle().getToggle().toString().equals("ONLY_PV")) {
			Map<String, Object> groundArea = simulationGroundArea.stream()
					.filter(area -> ((Number) area.get("block_index")).intValue() == 1).findFirst().orElse(null);
			scenes.setSimulationGroundArea(groundArea);
		}

		scenes.setControlPanel(controlPanelList);
		// scenes.setSimulationGrounArea(simulationGroundArea);

		return scenes;

	}

	// find simulation ground area
//    private Map<String, Object> findSimulationGroundArea(Long simulationId) {
//        String sql = "SELECT g.simulation_id, g.unit_x_length, g.unit_y_length, g.x_repetition, g.y_repetition, "
//                + "g.x_length, g.y_length, g.created_at, g.updated_at "
//                + "FROM simtool.simulation_ground_area g WHERE g.simulation_id = :simulatedId";
//
//        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter("simulatedId", simulationId);
//
//        List<Object[]> results = (List<Object[]>) query.getResultList();
//        Map<String, Object> simulationGroundArea = new HashMap<>();
//
//        if (!results.isEmpty()) {
//            Object[] result = results.get(0); // First record
//            simulationGroundArea.put("simulation_id", result[0]);
//            simulationGroundArea.put("unit_x_length", result[1]);
//            simulationGroundArea.put("unit_y_length", result[2]);
//            simulationGroundArea.put("x_repetition", result[3]);
//            simulationGroundArea.put("y_repetition", result[4]);
//            simulationGroundArea.put("x_length", result[5]);
//            simulationGroundArea.put("y_length", result[6]);
//            simulationGroundArea.put("created_at", result[7]);
//            simulationGroundArea.put("updated_at", result[8]);
//        }
//
//        return simulationGroundArea;
//
//    }
	private List<ControlPanel> handleSplitCycle(Cycles cycle, int startBlockNumber, int endBlockNumber,
			List<Map<String, Object>> simulationGroundArea) {
		List<ControlPanel> splitControlPanels = new ArrayList<>();

		// First part: From startBlockNumber to 26
		splitControlPanels.add(createControlPanelForRange(cycle.getName(), startBlockNumber, 26, simulationGroundArea));

		// Second part: From 1 to endBlockNumber
		splitControlPanels.add(createControlPanelForRange(cycle.getName(), 1, endBlockNumber, simulationGroundArea));

		return splitControlPanels;
	}

	private ControlPanel createControlPanelForRange(String cycleName, int startBlock, int endBlock,
			List<Map<String, Object>> simulationGroundArea) {
		ControlPanel controlPanel = new ControlPanel();
		controlPanel.setCycleName(cycleName);

		Map<String, Object> weeksMap = new HashMap<>();
		for (int blockNumber = startBlock; blockNumber <= endBlock; blockNumber++) {
			String week = "W" + String.format("%02d", blockNumber);

			// Find corresponding ground area for the block
			int finalBlockNumber = blockNumber;
			Map<String, Object> groundArea = simulationGroundArea.stream()
					.filter(area -> ((Number) area.get("block_id")).intValue() == finalBlockNumber).findFirst()
					.orElse(null);

			weeksMap.put(week, groundArea);
		}

		controlPanel.setWeeks(weeksMap);
		return controlPanel;
	}

	private List<Map<String, Object>> findSimulationGroundAreas(Long simulationId) {
		// SQL to get the block IDs for the given simulation ID
		String blockSql = "SELECT id as block_id, block_index FROM simtool.simulation_blocks WHERE simulation_id = :simulationId";

		Query blockQuery = entityManager.createNativeQuery(blockSql);
		blockQuery.setParameter("simulationId", simulationId);

		List<Object[]> blockResults = (List<Object[]>) blockQuery.getResultList();
		List<Map<String, Object>> groundAreas = new ArrayList<>();

		if (!blockResults.isEmpty()) {
			// Iterate over each block and fetch its ground area details
			for (Object[] block : blockResults) {
				Long blockId = ((Number) block[0]).longValue();
				Integer blockIndex = (Integer) block[1];

				// SQL to get the ground area details for the block ID
				String groundAreaSql = """
						    SELECT g.simulation_block_id, g.unit_x_length, g.unit_y_length, g.x_repetition, g.y_repetition,
						           g.x_length, g.y_length, g.created_at, g.updated_at
						    FROM simtool.simulation_ground_area g
						    WHERE g.simulation_block_id = :blockId
						""";

				Query groundAreaQuery = entityManager.createNativeQuery(groundAreaSql);
				groundAreaQuery.setParameter("blockId", blockId);

				List<Object[]> groundAreaResults = (List<Object[]>) groundAreaQuery.getResultList();

				if (!groundAreaResults.isEmpty()) {
					Object[] groundArea = groundAreaResults.get(0); // Get first record for the block
					Map<String, Object> groundAreaMap = new HashMap<>();
					groundAreaMap.put("block_id", blockId);
					groundAreaMap.put("block_index", blockIndex);
					groundAreaMap.put("unit_x_length", groundArea[1]);
					groundAreaMap.put("unit_y_length", groundArea[2]);
					groundAreaMap.put("x_repetition", groundArea[3]);
					groundAreaMap.put("y_repetition", groundArea[4]);
					groundAreaMap.put("x_length", groundArea[5]);
					groundAreaMap.put("y_length", groundArea[6]);
					groundAreaMap.put("created_at", groundArea[7]);
					groundAreaMap.put("updated_at", groundArea[8]);

					groundAreas.add(groundAreaMap);
				}
			}
		}

		return groundAreas;
	}

	// find scenes from simtool
	private List<SceneList> findScenesFromSimtool(Long runId, Long simulatedId) {
// minimum and maximum value will be added later

		String sql = "SELECT s.type,s.minimum,s.maximum,s.url,st.date,s.simulation_task_id " + "FROM simtool.scenes s "
				+ "JOIN simtool.simulation_tasks st ON s.simulation_task_id = st.id "
				+ "JOIN simtool.simulations sim ON st.simulation_id = sim.id "
				+ "WHERE sim.id =:simulatedId AND sim.run_id =:runId " + "AND s.type IN :sceneTypes "
				+ "GROUP BY s.minimum,s.maximum,st.date,s.simulation_task_id,s.type,s.url " + "ORDER BY st.date ASC ";

//        String sql = "SELECT s.type,s.url,st.date,s.simulation_task_id " + "FROM simtool.scenes s "
//                + "JOIN simtool.simulation_tasks st ON s.simulation_task_id = st.id "
//                + "JOIN simtool.simulations sim ON st.simulation_id = sim.id "
//                + "WHERE sim.id =:simulatedId AND sim.run_id =:runId " + "AND s.type IN :sceneTypes "
//                + "GROUP BY st.date,s.simulation_task_id,s.type,s.url " + "ORDER BY st.date ASC ";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("simulatedId", simulatedId);
		query.setParameter("runId", runId);
		query.setParameter("sceneTypes",
				Arrays.asList("geometry", "material", "carbon_assimilation", "temperature", "radiation", "dli_output"));

		List<Object[]> results = (List<Object[]>) query.getResultList();
		return results
				.stream().map(result -> new SceneList((String) result[0], ((BigDecimal) result[1]),
						((BigDecimal) result[2]), (String) result[3], (Timestamp) result[4], ((Long) result[5])))
				.collect(Collectors.toList());

		// only for now minumum and maximum value is 0 , minumum and maximum value will
		// be added later
//        return results
//                .stream().map(result -> new SceneList((String) result[0], null,
//                        null, (String) result[1], (Timestamp) result[2], ((Long) result[3])))
//                .collect(Collectors.toList());

	}

	// transform
	private SceneResponse transform(List<SceneList> sceneRows) {
		SceneResponse response = new SceneResponse();
		List<SceneType> sceneTypeList = new ArrayList<>();
		Map<String, Set<String>> weekDateMap = new HashMap<>();

		for (SceneList row : sceneRows) {

			Timestamp timestamp = row.date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
			String formatted = sdf.format(timestamp);

			// Extract date from formatted timestamp
			String datePart = formatted.substring(0, 10);

			// Convert into local date and calculate week number
			LocalDate startDate = timestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
			Integer weekNumber = postprocessingHelper.calculateIntervalNumberForDate(startDate);
			String weekNum = "W" + weekNumber;

			// Validation: Check if the week number already has a different date
			weekDateMap.computeIfAbsent(weekNum, k -> new HashSet<>());
			if (!weekDateMap.get(weekNum).contains(datePart)) {
				if (!weekDateMap.get(weekNum).isEmpty()) {
					System.err.println("Warning: Week " + weekNum + " has multiple dates: " + weekDateMap.get(weekNum)
							+ " and " + datePart);
					continue;
				}
				weekDateMap.get(weekNum).add(datePart);
			}

			SceneType sceneType = sceneTypeList.stream().filter(scene -> formatted.equals(scene.getStartTime()))
					.findFirst().orElse(null);

			// If no existing SceneType with the same startTime, create a new one
			if (sceneType == null) {
				sceneType = new SceneType();
				sceneType.setStartTime(formatted);

				// convert into local date
				// Define the formatter for the input date string
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

				// Parse the string to a ZonedDateTime
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(formatted, formatter);

				// Extract and return the LocalDate
//                LocalDate startDate = zonedDateTime.toLocalDate();
//                Integer weekNumber = postprocessingHelper.calculateIntervalNumberForDate(startDate);
//                String weekNum = "W" + weekNumber.toString();
				sceneType.setWeek(weekNum);
				sceneTypeList.add(sceneType); // Add the new SceneType to the list
			}

			// Update the SceneType with new details from the current row
			updateSceneType(sceneType, row);
		}

		response.setScenes(sceneTypeList);
		return response;
	}

	// update scene type method
	private void updateSceneType(SceneType sceneType, SceneList row) {
		SceneDetails details = getOrCreateSceneDetails(sceneType, row.sceneType());
		if (row.min() == null)
			details.setMin(BigDecimal.valueOf(0));
		else
			details.setMin(row.min()); // row.min()
		if (row.max() == null)
			details.setMax(BigDecimal.valueOf(0));
		else
			details.setMax(row.max()); // row.max()
		details.setUrl(row.url());
	}

	// getOrCreateSceneDetails method
	private SceneDetails getOrCreateSceneDetails(SceneType sceneType, String sceneTypeName) {
		switch (sceneTypeName) {
		case "geometry":
			if (sceneType.getGeometry() == null)
				sceneType.setGeometry(new SceneDetails());
			return sceneType.getGeometry();
		case "material":
			if (sceneType.getMaterial() == null)
				sceneType.setMaterial(new SceneDetails());
			return sceneType.getMaterial();
		case "carbon_assimilation":
			if (sceneType.getCarbonAssimilation() == null)
				sceneType.setCarbonAssimilation(new SceneDetails());
			return sceneType.getCarbonAssimilation();
		case "temperature":
			if (sceneType.getTemperature() == null)
				sceneType.setTemperature(new SceneDetails());
			return sceneType.getTemperature();
		case "radiation":
			if (sceneType.getRadiation() == null)
				sceneType.setRadiation(new SceneDetails());
			return sceneType.getRadiation();
		case "dli_output":
			if (sceneType.getDliOutput() == null)
				sceneType.setDliOutput(new SceneDetails());
			return sceneType.getDliOutput();
//            case "HELIOS":
//                if (sceneType.getHelios() == null)
//                    sceneType.setHelios(new SceneDetails());
//                return sceneType.getHelios();
		}

		return new SceneDetails();
	}

}