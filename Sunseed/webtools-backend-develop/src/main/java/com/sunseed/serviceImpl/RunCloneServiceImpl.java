package com.sunseed.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.AgriPvProtectionHeight;
import com.sunseed.entity.Bed;
import com.sunseed.entity.BedParameter;
import com.sunseed.entity.CropBedSection;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.Cycles;
import com.sunseed.entity.EconomicMultiCrop;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Projects;
import com.sunseed.entity.PvModule;
import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.Runs;
import com.sunseed.entity.SimulatedRun;
import com.sunseed.entity.SoilType;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.exceptions.PvParametersException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.mappers.AgriGeneralModelMapper;
import com.sunseed.mappers.CropParameterModelMapper;
import com.sunseed.mappers.EconomicParameterModelMapper;
import com.sunseed.mappers.PvParameterModelMapper;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.PvParametersRequestDto;
import com.sunseed.model.requestDTO.Simulation;
import com.sunseed.model.responseDTO.PvParametersResponseDto;
import com.sunseed.model.responseDTO.RunResponseToggleDto;
import com.sunseed.repository.ModeOfPvOperationRepository;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.PvModuleConfigurationRepository;
import com.sunseed.repository.PvModuleRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.SimulatedRunRepository;
import com.sunseed.repository.SoilTypeRepo;
import com.sunseed.service.RunCloneService;
import com.sunseed.service.SoilService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunCloneServiceImpl implements RunCloneService {

    private final ProjectsRepository projectRepository;
    private final RunsRepository runRepository;
    private final Validator validator;
    private final PvModuleRepository pvModuleRepository;
    private final PvParameterModelMapper pvParameterModelMapper;
    private final CropParameterModelMapper cropParameterModelMapper;
    private final AgriGeneralModelMapper agriGeneralModelMapper;
    private final PvModuleConfigurationRepository pvModuleConfigurationRepository;
    private final SimulatedRunRepository simulatedRunRepo;
    private final ModeOfPvOperationRepository modeOfPvOperationRepository;
    private final RunServiceImpl runServiceImpl;
    private final PreProcessorToggleRepository preProcessorToggleRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final EconomicParameterModelMapper economicParameterModelMapper;
    private final SoilTypeRepo soilTypeRepo;

    @Override
    public Map<String, Object> createCloneForGivenRun(PvParametersRequestDto request, Long projectId, String toggle,
                                                      Long runId, Long userId, Boolean isMaster) {

        if (userId == null || userId <= 0)
            throw new PvParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new PvParametersException("project.not.found", HttpStatus.NOT_FOUND);

        Toggle toggleValue = Toggle.fromValue(toggle);
        if (toggleValue == null)
            throw new PvParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);


        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new PvParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new PvParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        if(isMaster!=null && isMaster==true && toggleValue!=Toggle.APV){
            throw new UnprocessableException("toggle.APV");
        }
        // validating run
        Runs existingRun = runRepository.findById(runId)
                .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));

        // check toggle must be APV to clone the run
        if (existingRun.isMaster() == true && existingRun.getPreProcessorToggle().getToggle() != Toggle.APV) {
            throw new UnprocessableException("toggle.APV");
        }

        if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
            throw new PvParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

        Long masterCloneId = null;

        // getting and setting cloneId before hand to throw exception when clone
        // is made from variant and has cloneId of a run which is not a master run
        // only when requested clone is going to be a variant and not master
        if (isMaster == null || isMaster == false) {
            if (existingRun.isMaster() == true)
                masterCloneId = existingRun.getRunId();
            else {
                Runs possibleMasterRun = runRepository.findById(existingRun.getCloneId()).orElseThrow(
                        () -> new PvParametersException("cant.make.clone", HttpStatus.UNPROCESSABLE_ENTITY));
                if (possibleMasterRun.isMaster() == false)
                    throw new PvParametersException("cant.make.clone", HttpStatus.UNPROCESSABLE_ENTITY);
                masterCloneId = possibleMasterRun.getRunId();
            }
        }

        EconomicParameters newEconomicParameters;
        PvParameter newPvParameter = null;
        AgriGeneralParameter newAgriGeneralParameter = null;
        CropParameters newCropParameter = null;

        // creating pre processor toggle
        boolean runNameExists = preProcessorToggleRepository
                .findByProjectProjectIdAndRunName(existingProject.getProjectId(), request.getRunName());
        if (runNameExists)
            throw new PvParametersException("run.name.already.exists", HttpStatus.UNPROCESSABLE_ENTITY);
        Optional<SoilType> soil = soilTypeRepo.findById(request.getSoilId());
        PreProcessorToggle newPreProcessorToggle = PreProcessorToggle.builder().runName(request.getRunName())
                .lengthOfOneRow(request.getLengthOfOneRow()).pitchOfRows(request.getPitchOfRows())
                .azimuth(request.getAzimuth()).project(existingProject).preProcessorStatus(PreProcessorStatus.CREATED)
                .soilType(soil.get())
                .toggle(toggleValue).build();

        EconomicParameters existingEconomicParameters = existingRun.getEconomicParameters();
       // existingEconomicParameters.getEconomicMultiCrop().forEach(economicMultiCrop -> System.out.println("economic multi crop : " + economicMultiCrop.getId()));

        // creating new economic parameters
        if (existingEconomicParameters != null) {
            List<EconomicMultiCrop> economicMultiCropList=null;
            if(toggleValue==Toggle.APV || toggleValue==Toggle.ONLY_AGRI) {

                economicMultiCropList = existingEconomicParameters.getEconomicMultiCrop().stream()
                        .map((economicMultiCrop) -> {
                            EconomicMultiCrop newEconomicMultiCrop = EconomicMultiCrop.builder()
                                    .crop(economicMultiCrop.getCrop())
                                    .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
                                    .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
                                    .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
                                    .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
                                    .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
                                    .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
                                    .cultivationArea(economicMultiCrop.getCultivationArea()).build();

                            return newEconomicMultiCrop;
                        }).collect(Collectors.toList());

            }
            newEconomicParameters = EconomicParameters.builder().currency(existingEconomicParameters.getCurrency())
                    .economicParameter(existingEconomicParameters.isEconomicParameter())
                    .hourlySellingRates(toggleValue==Toggle.ONLY_AGRI?null: existingEconomicParameters.getHourlySellingRates()).project(existingProject)
                    .economicMultiCrop(economicMultiCropList).status(PreProcessorStatus.CREATED).build();

            if(economicMultiCropList!=null) {
                economicMultiCropList.forEach(t -> t.setEconomicParameters(newEconomicParameters));
            }
        } else {
            newEconomicParameters = null;
        }

        // now the clone logic begins
        if (toggleValue == Toggle.ONLY_PV) {

            // validating pv parameters and fetching master data
            Map<String, Object> masterData = validatePvParametersAndFetchMasterData(request);

            PvModule pvModule = (PvModule) masterData.get("pvModule");
            ModeOfPvOperation modeOfPvOperation = (ModeOfPvOperation) masterData.get("modeOfPvOperation");
            List<PvModuleConfiguration> moduleConfigs = objectMapper.convertValue(masterData.get("moduleConfigs"),
                    new TypeReference<List<PvModuleConfiguration>>() {
                    });

            // now creating new pv parameters
            newPvParameter = PvParameter.builder().gapBetweenModules(request.getGapBetweenModules())
                    .height(request.getHeight()).maxAngleOfTracking(request.getMaxAngleOfTracking())
                    .modeOfOperationId(modeOfPvOperation).moduleConfig(moduleConfigs)
                    .xCoordinate(request.getXCoordinate()).yCoordinate(request.getYCoordinate())
                    .moduleMaskPattern(request.getModuleMaskPattern()).pvModule(pvModule).project(existingProject)
                    .status(PreProcessorStatus.CREATED).tiltIfFt(request.getTiltIfFt()).build();
        } else if (toggleValue == Toggle.APV) {

            // validating pv parameters and fetching master data
            Map<String, Object> masterData = validatePvParametersAndFetchMasterData(request);

            PvModule pvModule = (PvModule) masterData.get("pvModule");
            ModeOfPvOperation modeOfPvOperation = (ModeOfPvOperation) masterData.get("modeOfPvOperation");
            List<PvModuleConfiguration> moduleConfigs = objectMapper.convertValue(masterData.get("moduleConfigs"),
                    new TypeReference<List<PvModuleConfiguration>>() {
                    });

            // now creating new pv parameters
            newPvParameter = PvParameter.builder().gapBetweenModules(request.getGapBetweenModules())
                    .height(request.getHeight()).maxAngleOfTracking(request.getMaxAngleOfTracking())
                    .modeOfOperationId(modeOfPvOperation).moduleConfig(moduleConfigs)
                    .xCoordinate(request.getXCoordinate()).yCoordinate(request.getYCoordinate())
                    .moduleMaskPattern(request.getModuleMaskPattern()).pvModule(pvModule).project(existingProject)
                    .status(PreProcessorStatus.CREATED).tiltIfFt(request.getTiltIfFt()).build();

            // copying agri general parameter from master run to clone run
            AgriGeneralParameter existingAgriGeneralParameter = existingRun.getAgriGeneralParameters();

            if (existingAgriGeneralParameter != null) {
                newAgriGeneralParameter = copyExistingAgriGeneralParameter(existingAgriGeneralParameter,
                        existingProject);
            }

            // copying crop parameter from master run to clone run
            CropParameters existingCropParameters = existingRun.getCropParameters();

            if (existingCropParameters != null) {
                newCropParameter = copyExistingCropParameter(existingCropParameters, existingProject);
            }

        } else if (toggleValue == Toggle.ONLY_AGRI) {
            // copying agri general parameter from master run to clone run
            AgriGeneralParameter existingAgriGeneralParameter = existingRun.getAgriGeneralParameters();

            if (existingAgriGeneralParameter != null) {
                newAgriGeneralParameter = copyExistingAgriGeneralParameter(existingAgriGeneralParameter,
                        existingProject);
            }

            // copying crop parameter from master run to clone run
            CropParameters existingCropParameters = existingRun.getCropParameters();

            if (existingCropParameters != null) {
                newCropParameter = copyExistingCropParameter(existingCropParameters, existingProject);
            }
        }

        // now creating clone
        Runs newRun = Runs.builder().build();
        newRun.setRunName(newPreProcessorToggle.getRunName());
        newRun.setRunStatus(RunStatus.HOLDING);
        newRun.setInProject(existingProject);
        newRun.setPreProcessorToggle(newPreProcessorToggle);
        newRun.setPvParameters(newPvParameter);
        newRun.setAgriGeneralParameters(newAgriGeneralParameter);
        newRun.setCropParameters(newCropParameter);
        newRun.setEconomicParameters(newEconomicParameters);
        newRun.setCloneId(masterCloneId);

        if (isMaster == null || isMaster == false) {
            newRun.setMaster(false);
        } else
            newRun.setMaster(true);

        Runs clonedRun = savingRun(newRun);

        //
        PvParametersResponseDto response = new PvParametersResponseDto();
        response = populateServiceResponse(clonedRun.getPreProcessorToggle(), clonedRun.getPvParameters(),
                existingProject, clonedRun);
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("response", response);
        String message = "pvWithToggle.created";
        serviceResponse.put("message", message);
        serviceResponse.put("httpStatus", HttpStatus.OK);
        return serviceResponse;
    }

    @Override
    public Map<String, Object> getPvParametersWithToggle(Long userId, Long projectId, Long runId) {

        if (userId == null || userId <= 0)
            throw new PvParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new PvParametersException("project.not.found", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new PvParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new PvParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        Runs existingRun = runRepository.findById(runId)
                .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));

        if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
            throw new PvParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

        // getting toggle from existing run and just emptying run name
        PreProcessorToggle preProcessorToggle = existingRun.getPreProcessorToggle();
        if (preProcessorToggle == null)
            throw new PvParametersException("toggle.not.found", HttpStatus.UNPROCESSABLE_ENTITY);

        preProcessorToggle.setRunName(null);

        // getting pv parameters from existing run
        PvParameter pvParameter = existingRun.getPvParameters();

        if (pvParameter == null)
            pvParameter = new PvParameter();

        // setting the response
        PvParametersResponseDto response = populateServiceResponse(preProcessorToggle, pvParameter, existingProject,
                existingRun);
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("response", response);
        serviceResponse.put("message", "pvWithToggle.fetched");
        serviceResponse.put("httpStatus", HttpStatus.OK);
        return serviceResponse;
    }

    @Override
    public Map<String, Object> getAllVariantRuns(Long projectId, Long runId, Long userId) {

        if (userId == null || userId <= 0)
            throw new UnAuthorizedException(null, "user.not.found");

        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

        Long existingUserId = existingProject.getUserProfile().getUserId();
        if (userId != existingUserId)
            throw new UnAuthorizedException("user.not.authorized");

        if (runId == null || runId < 0)
            throw new UnprocessableException("run.not.found");

        // validating run
        Runs masterRun = runRepository.findById(runId).orElseThrow(() -> new UnprocessableException("run.not.found"));

        if (existingProject.getProjectId() != masterRun.getInProject().getProjectId())
            throw new UnprocessableException("run.not.of.project");

        if (masterRun.isMaster() == false)
            throw new UnprocessableException("not.master.run");

        List<Runs> existingRuns = runRepository.getVariantRuns(runId);
        System.out.println("runs size " + existingRuns.size());

        List<SimulatedRun> simulated = new ArrayList<>();
        Map<Long, Long> runsKey = new HashMap<>();
        List<Long> runIds = new ArrayList<>();
        List<Long> simulatedId = new ArrayList<>();

        existingRuns.stream().forEach(t -> {
            runIds.add(t.getRunId());
            System.out.println("Run ID : " + t.getRunId() + ",");
            System.out.println("Run Status : " + t.getRunStatus());
        });

        simulated = simulatedRunRepo.getAllSimulatedByRunId(runIds);

        System.out.println("Simulated repo size " + simulated.size());

        for (SimulatedRun s : simulated) {
            if (s.getSimulatedId() == null) {
                System.out.println("Inside exception");
                throw new ResourceNotFoundException("simulation.id.not.matched");
            }
            simulatedId.add(s.getSimulatedId());
            runsKey.put(s.getRun().getRunId(), s.getSimulatedId());
            System.out.println("s get Id " + s.getSimulatedId());
        }

        List<Simulation> resultSimulation = runServiceImpl.findDataFromSimtool(simulatedId);

        Map<String, Object> response = new HashMap<>();
        List<RunResponseToggleDto> runResponseDtoList = new ArrayList<>();

        for (Runs currentRun : existingRuns) {

            RunResponseToggleDto runResponseDto = new RunResponseToggleDto();

            runResponseDto.setProjectId(projectId);
            long currentRunId = currentRun.getRunId();
            PreProcessorToggle toggles = currentRun.getPreProcessorToggle();
            runResponseDto.setId(currentRun.getRunId());
            runResponseDto.setRunName(currentRun.getRunName());
            runResponseDto.setPreProcessorToggle(toggles);
            runResponseDto.setAgriControl(currentRun.isAgriControl());
            runResponseDto.setPvControl(currentRun.isPvControl());

            Optional<SimulatedRun> simulatedRun = simulatedRunRepo.findByRun(currentRun);
            if (simulatedRun.isPresent()) {
                System.out.println(" simulated Id with optional:" + currentRun.getSimulatedRun().getSimulatedId());
                runResponseDto.setSimulatedId(simulatedRun.get().getSimulatedId());
                System.out.println("simulation id with Long value :" + simulatedRun.get().getSimulatedId());
            }

            if (currentRun.getPvParameters() != null) {
                runResponseDto.setPvParameters(pvParameterModelMapper
                        .entityToPvParameterResponseDto(currentRun.getPvParameters(), toggles, currentRunId));
            }
            runResponseDto.setCropParameters(cropParameterModelMapper
                    .entityToCropParametersResponseDto(currentRun.getCropParameters(), runResponseDto.getId()));
            runResponseDto.setRunStatus(currentRun.getRunStatus().getValue());
            runResponseDto.setCreatedAt(currentRun.getCreatedAt());
            runResponseDto.setUpdatedAt(currentRun.getUpdatedAt());
            if (currentRun.getEconomicParameters() != null) {

                runResponseDto.setEconomicParameters(economicParameterModelMapper
                        .getEconomicParameterResponseDto(currentRun.getEconomicParameters()));
            }
            if (currentRun.getAgriGeneralParameters() != null) {
                runResponseDto.setAgriGeneralParameters(agriGeneralModelMapper
                        .entityToAgriGeneralParameterResponseDto(currentRun.getAgriGeneralParameters()));
            }

            long progress = 0;
            for (Simulation s : resultSimulation) {
                System.out.println("RunId s : " + s.getRunId());
                System.out.println("RunId r : " + currentRunId);

                if (s.getRunId().equals(currentRunId)) {
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

                    if (s.getStatus().equals("SUCCESS")) {
                        // Update the run status in the database
                        currentRun.setRunStatus(RunStatus.COMPLETED);
                        runRepository.save(currentRun);
                        // Also update the run status in the response
                        runResponseDto.setRunStatus(RunStatus.COMPLETED.getValue());
                    } else if (s.getStatus().equals("FAILED")) {
                        currentRun.setRunStatus(RunStatus.FAILED);
                        runRepository.save(currentRun);
                        // Also update the run status in the response
                        runResponseDto.setRunStatus(RunStatus.FAILED.getValue());
                    } else if (s.getStatus().equals("QUEUED") && (s.getCompletedTaskCount() > 0)) {
                        currentRun.setRunStatus(RunStatus.RUNNING);
                        runRepository.save(currentRun);
                        // Also update the run status in the response
                        runResponseDto.setRunStatus(RunStatus.RUNNING.getValue());

                    }

                }
            }
            runResponseDto.setProgress(progress);
            runResponseDtoList.add(runResponseDto);

        }

        response.put("runs", runResponseDtoList);
        return response;
    }

    private PvParametersResponseDto populateServiceResponse(PreProcessorToggle preProcessorToggle,
                                                            PvParameter pvParameter, Projects existingProject, Runs existingRun) {

        PvParametersResponseDto response = new PvParametersResponseDto();
        response.setPreProcessorToggle(preProcessorToggle);
        response.setGapBetweenModules(pvParameter != null ? pvParameter.getGapBetweenModules() : null);
        response.setHeight(pvParameter != null ? pvParameter.getHeight() : null);
        response.setId(pvParameter != null ? pvParameter.getId() : null);
        response.setMaxAngleOfTracking(pvParameter != null ? pvParameter.getMaxAngleOfTracking() : null);
        response.setModeOfOperationId(pvParameter != null ? pvParameter.getModeOfOperationId() : null);
        response.setModuleConfigs(pvParameter != null ? pvParameter.getModuleConfig() : null);
        response.setModuleMaskPattern(pvParameter != null ? pvParameter.getModuleMaskPattern() : null);
        response.setProjectId(existingProject != null ? existingProject.getProjectId() : null);
        response.setPvModule(pvParameter != null ? pvParameter.getPvModule() : null);
        response.setRunId(existingRun != null ? existingRun.getRunId() : null);
        response.setStatus(pvParameter != null ? pvParameter.getId() == null ? null : pvParameter.getStatus() : null);
        response.setTiltIfFt(pvParameter != null ? pvParameter.getTiltIfFt() : null);
        response.setXCoordinate(pvParameter != null ? pvParameter.getXCoordinate() : null);
        response.setYCoordinate(pvParameter != null ? pvParameter.getYCoordinate() : null);
        response.setRunId(existingRun != null ? existingRun.getRunId() : null);
        response.setCloneId(existingRun != null ? existingRun.getCloneId() : null);
        response.setIsMaster(existingRun != null ? existingRun.isMaster() : null);
        response.setSoilType(preProcessorToggle.getSoilType() != null ? preProcessorToggle.getSoilType() : null);
        return response;
    }

    private AgriGeneralParameter copyExistingAgriGeneralParameter(AgriGeneralParameter existingAgriGeneralParameter,
                                                                  Projects existingProject) {

        AgriGeneralParameter newAgriGeneralParameter = new AgriGeneralParameter();
        newAgriGeneralParameter.setTempControl(existingAgriGeneralParameter.getTempControl());
        newAgriGeneralParameter.setTrail(existingAgriGeneralParameter.getTrail());
        newAgriGeneralParameter.setMinTemp(existingAgriGeneralParameter.getMinTemp());
        newAgriGeneralParameter.setMaxTemp(existingAgriGeneralParameter.getMaxTemp());
        newAgriGeneralParameter.setIsMulching(existingAgriGeneralParameter.getIsMulching());
        newAgriGeneralParameter.setStatus(PreProcessorStatus.CREATED);
        newAgriGeneralParameter.setIrrigationId(existingAgriGeneralParameter.getIrrigationId());
//        newAgriGeneralParameter.setSoilType(existingAgriGeneralParameter.getSoilType());
        newAgriGeneralParameter.setProject(existingProject);

        // setting bed parameter in agri general parameter
        BedParameter newBedParameter = null;

        if (existingAgriGeneralParameter.getBedParameter() != null) {
            newBedParameter = BedParameter.builder()
                    .bedWidth(existingAgriGeneralParameter.getBedParameter().getBedWidth())
                    .bedHeight(existingAgriGeneralParameter.getBedParameter().getBedHeight())
                    .bedAngle(existingAgriGeneralParameter.getBedParameter().getBedAngle())
                    .bedAzimuth(existingAgriGeneralParameter.getBedParameter().getBedAzimuth())
                    .bedcc(existingAgriGeneralParameter.getBedParameter().getBedcc())
                    .startPointOffset(existingAgriGeneralParameter.getBedParameter().getStartPointOffset()).build();
            newBedParameter.setAgriGeneralParameter(newAgriGeneralParameter);
        }

        newAgriGeneralParameter.setBedParameter(newBedParameter);

        // setting agri pv protection height in agri general parameter
        List<AgriPvProtectionHeight> agriPvProtectionHeights = null;

        if (existingAgriGeneralParameter.getAgriPvProtectionHeight() != null) {
            agriPvProtectionHeights = existingAgriGeneralParameter.getAgriPvProtectionHeight().stream().map(t -> {
                return AgriPvProtectionHeight.builder().protectionHeight(t.getProtectionHeight())
                        .protectionLayer(t.getProtectionLayer()).build();
            }).collect(Collectors.toList());

            agriPvProtectionHeights.forEach(t -> t.setAgriGeneralParameter(newAgriGeneralParameter));
        }

        newAgriGeneralParameter.setAgriPvProtectionHeight(agriPvProtectionHeights);

        return newAgriGeneralParameter;
    }

    private CropParameters copyExistingCropParameter(CropParameters existingCropParameters, Projects existingProject) {

        CropParameters newCropParameters = new CropParameters();
        newCropParameters.setProject(existingProject);
        newCropParameters.setStatus(PreProcessorStatus.CREATED);

        List<Cycles> newCycles = null;
        // setting cycles in new crop parameter from master run
        if (existingCropParameters.getCycles() != null) {
            newCycles = new ArrayList<>();
            for (Cycles existingCycle : existingCropParameters.getCycles()) {
                Cycles newCycle = new Cycles();
                newCycle.setName(existingCycle.getName());
                newCycle.setStartDate(existingCycle.getStartDate());
                newCycle.setInterBedPattern(existingCycle.getInterBedPattern());
                if (existingCycle.getBeds() != null) {
                    List<Bed> newBeds = new ArrayList<>();
                    for (Bed existingBed : existingCycle.getBeds()) {
                        Bed newBed = new Bed();
                        newBed.setBedName(existingBed.getBedName());

                        if (existingBed.getCropBed() != null) {
                            List<CropBedSection> newCropBedSections = new ArrayList<>();
                            for (CropBedSection cropBedSection : existingBed.getCropBed()) {
                                CropBedSection newCropBedSection = new CropBedSection();
                                newCropBedSection.setCrop(cropBedSection.getCrop());
                                newCropBedSection.setO1(cropBedSection.getO1());
                                newCropBedSection.setS1(cropBedSection.getS1());
                                newCropBedSection.setO2(cropBedSection.getO2());
                                newCropBedSection.setStretch(cropBedSection.getStretch());
                                newCropBedSection.setBed(newBed);
                                newCropBedSections.add(newCropBedSection);
                            }
                            newBed.setCropBed(newCropBedSections);
                        }
                        newBed.setCycle(newCycle);
                        newBeds.add(newBed);
                    }
                    newCycle.setBeds(newBeds);
                }
                newCycle.setCropParameters(newCropParameters);
                newCycles.add(newCycle);
            }
        }

        newCropParameters.setCycles(newCycles);
        return newCropParameters;
    }

    private Map<String, Object> validatePvParametersAndFetchMasterData(PvParametersRequestDto request) {

        // first validating pv parameters
        validatePvParameters(request);

        // now fetching master data and sending back to owner methods
        Map<String, Object> masterData = new HashMap<>();

        PvModule pvModule = pvModuleRepository.findById(request.getPvModuleId())
                .orElseThrow(() -> new PvParametersException("pvModule.notFound", HttpStatus.NOT_FOUND));
        ModeOfPvOperation modeOfPvOperation = modeOfPvOperationRepository.findById(request.getModeOfOperationId())
                .orElseThrow(() -> new PvParametersException("modeOfPvOperation.notFound", HttpStatus.NOT_FOUND));

        List<PvModuleConfiguration> moduleConfigs = new ArrayList<>();
        List<PvModuleConfiguration> values = pvModuleConfigurationRepository.findAllById(request.getModuleConfigId());
        if (values.isEmpty()) {
            throw new PvParametersException("moduleConfig.notFound", HttpStatus.NOT_FOUND);
        }
        for (PvModuleConfiguration config : values) {
            PvModuleConfiguration moduleConfig = new PvModuleConfiguration();
            moduleConfig.setId(config.getId());
            moduleConfig.setModuleConfig(config.getModuleConfig());
            moduleConfig.setIsActive(config.getIsActive());
            moduleConfig.setHide(config.getHide());
            moduleConfig.setNumberOfModules(config.getNumberOfModules());
            moduleConfig.setTypeOfModule(config.getTypeOfModule());

            moduleConfigs.add(moduleConfig);
        }

        masterData.put("pvModule", pvModule);
        masterData.put("modeOfPvOperation", modeOfPvOperation);
        masterData.put("moduleConfigs", moduleConfigs);
        return masterData;

    }

    @Transactional
    private Runs savingRun(Runs newRun) {
        return runRepository.save(newRun);
    }

    private void validatePvParameters(PvParametersRequestDto request) {

        Set<ConstraintViolation<PvParametersRequestDto>> pvParametersViolations = validator.validate(request,
                ValidationGroups.PvParametersGroup.class);

        if (!pvParametersViolations.isEmpty()) {
            ConstraintViolation<PvParametersRequestDto> violation = pvParametersViolations.iterator().next();
            throw new PvParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // validating mode of pv operation
        validateModeOfOperation(request);

        // validating module mask pattern
        if (!validateModuleMaskPattern(request.getModuleMaskPattern()))
            throw new PvParametersException("moduleMaskPattern.invalidCombination", HttpStatus.BAD_REQUEST);
    }

    private void validateModeOfOperation(PvParametersRequestDto request) {
        // When Mode of operation is 'FT', then TiltIfFt is enabled
        if (request.getModeOfOperationId() == 1) {
            Double tiltIfFt = request.getTiltIfFt();
            if (tiltIfFt == null)
                throw new PvParametersException("pvParameter.tiltIfFtRequired", HttpStatus.BAD_REQUEST);

            if (tiltIfFt < -90)
                throw new PvParametersException("tiltIfFt.negative", HttpStatus.BAD_REQUEST);

            if (tiltIfFt > 90)
                throw new PvParametersException("tiltIfFt.greater", HttpStatus.BAD_REQUEST);
        }
        // When Mode of operation is 'Single Axis', then MaxAngleOfTracking is enabled
        else if (request.getModeOfOperationId() == 2) {
            Double maxAngleOfTracking = request.getMaxAngleOfTracking();

            if (maxAngleOfTracking == null)
                throw new PvParametersException("pvParameter.maxAngleOfTrackingRequired", HttpStatus.BAD_REQUEST);

            if (maxAngleOfTracking < 30)
                throw new PvParametersException("maxAngleOfTracking.smaller", HttpStatus.BAD_REQUEST);

            if (maxAngleOfTracking > 90)
                throw new PvParametersException("maxAngleOfTracking.greater", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean validateModuleMaskPattern(String moduleMaskPattern) {
        if (moduleMaskPattern == null || moduleMaskPattern.isEmpty())
            return true;

        if (moduleMaskPattern.length() > 10)
            throw new PvParametersException("moduleMaskPattern.lengthGreater", HttpStatus.BAD_REQUEST);

        boolean zeroes = false;
        boolean ones = false;
        for (int i = 0; i < moduleMaskPattern.length(); i++) {
            if (moduleMaskPattern.charAt(i) == '0') {
                zeroes = true; // moduleMaskPattern contains '0'
            }
            if (moduleMaskPattern.charAt(i) == '1') {
                ones = true; // moduleMaskPattern contains '1'
            }
            if (zeroes && ones)
                break; // break when moduleMaskPattern has combination of 0's and 1's
        }

        // moduleMaskPattern should be in combination of 0's and 1's
        return (zeroes && ones);
    }

    @Override
    public Runs updateRunMasterStatus(Long projectId, Long runId, Long userId) {

        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId.longValue() != existingProject.getUserProfile().getUserId())
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        // validating run
        Runs existingRun = runRepository.findById(runId)
                .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));

        if (existingProject.getProjectId().longValue() != existingRun.getInProject().getProjectId())
            throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

        if (existingRun.isMaster() == true)
            throw new AgriGeneralParametersException("run.already.master", HttpStatus.CONFLICT);

        existingRun.setCloneId(null);
        existingRun.setMaster(true);
        Runs updatedRun = runRepository.save(existingRun);

        return updatedRun;
    }

    // ******* update varient-run **********


}