package com.sunseed.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.sunseed.enums.*;
import com.sunseed.exceptions.UnprocessableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Projects;
import com.sunseed.entity.PvModule;
import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.Runs;
import com.sunseed.entity.SoilType;
import com.sunseed.exceptions.PvParametersException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.PvParametersRequestDto;
import com.sunseed.model.responseDTO.PvParametersResponseDto;
import com.sunseed.model.responseDTO.agriGeneralParameters.SoilTypeResponse;
import com.sunseed.model.responseDTO.pvParameters.ModeOfPvOperationResponse;
import com.sunseed.model.responseDTO.pvParameters.PvModuleConfigurationResponse;
import com.sunseed.model.responseDTO.pvParameters.PvModuleResponse;
import com.sunseed.repository.AgriGeneralParameterRepo;
import com.sunseed.repository.CropParametersRepo;
import com.sunseed.repository.ModeOfPvOperationRepository;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.PvModuleConfigurationRepository;
import com.sunseed.repository.PvModuleRepository;
import com.sunseed.repository.PvParameterRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.SoilTypeRepo;
import com.sunseed.service.PvParametersService;
import com.sunseed.service.RunService;
import com.sunseed.service.SoilService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PvParametersServiceImpl implements PvParametersService {

    private final ProjectsRepository projectRepository;
    private final PreProcessorToggleRepository preProcessorToggleRepository;
    private final RunsRepository runRepository;
    private final Validator validator;
    private final PvParameterRepository pvParameterRepository;
    private final PvModuleRepository pvModuleRepository;
    private final PvModuleConfigurationRepository pvModuleConfigurationRepository;
    private final ModeOfPvOperationRepository modeOfPvOperationRepository;
    private final AgriGeneralParameterRepo agriGeneralParameterRepository;
    private final CropParametersRepo cropParametersRepository;
    private final RunService runService;
    private  final SoilService soilService;
    private final SoilTypeRepo soilTypeRepo;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public Map<String, Object> addOrUpdatePvParametersWithToggle(PvParametersRequestDto request, Long projectId,
                                                                 String toggle, Long runId, Long userId, String callFor, Long pvParameterId) {

    	Runs testRun = null;
    	String dbToggle = null;
    	if(runId != null) {
    		testRun = runRepository.findById(runId)
                .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));
    		dbToggle = testRun.getPreProcessorToggle().getToggle().toString();
    	}
    	Toggle toggleValueForClone = Toggle.fromValue(toggle);
//    	String dbToggle = testRun.getPreProcessorToggle().getToggle().toString();
    	if(runId != null && testRun.getCloneId() != null && toggleValueForClone.toString() != dbToggle) {
    		throw new UnprocessableException("clones.not.changed");
    	}
    	
        if (userId == null || userId <= 0)
            throw new PvParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new PvParametersException("project.not.found", HttpStatus.NOT_FOUND);
        if (callFor.trim().equalsIgnoreCase("update")) {
            if (pvParameterId == null || pvParameterId <= 0)
                throw new PvParametersException("pvParameter.not.found", HttpStatus.NOT_FOUND);
        }

        Toggle toggleValue = Toggle.fromValue(toggle);

        if (toggleValue == null)
            throw new PvParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);

        if (callFor.equalsIgnoreCase("update") && runId != null) {
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));
            if (existingRun.isMaster() == Boolean.TRUE && toggleValue != Toggle.APV) {
                throw new UnprocessableException("toggle.APV");
            }
        } else if((callFor.equalsIgnoreCase("create") || callFor.equalsIgnoreCase("update")) && runId == null){
            if (toggleValue != Toggle.APV) {
                throw new UnprocessableException("toggle.APV");
            }
       }

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new PvParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new PvParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        PreProcessorToggle preProcessorToggle = null;
        Optional<PreProcessorToggle> optionalExistingPreProcessorToggle = null;
        Optional<PvParameter> optionalExistingPvParameter = null;
        PvParametersResponseDto response = new PvParametersResponseDto();
        Map<String, Object> serviceResponse = new HashMap<>();

        /*
         * creating or updating pv parameters and pre processor toggle in draft
         */
        if (runId == null) {

            optionalExistingPvParameter = pvParameterRepository
                    .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
            if (callFor.trim().equalsIgnoreCase("update")) {
                if (optionalExistingPvParameter.isEmpty())
                    throw new PvParametersException("pvParameter.not.found", HttpStatus.NOT_FOUND);
                else if (!pvParameterId.equals(optionalExistingPvParameter.get().getId()))
                    throw new PvParametersException("pvParameterId.mismatch", HttpStatus.UNAUTHORIZED);
            } else if (callFor.trim().equalsIgnoreCase("create") && (optionalExistingPvParameter.isPresent())
                    && (toggleValue == Toggle.APV || toggleValue == Toggle.ONLY_PV)) {
                optionalExistingPreProcessorToggle = preProcessorToggleRepository
                        .findByProjectProjectIdAndPreProcessorStatus(existingProject.getProjectId(),
                                PreProcessorStatus.DRAFT);
                response = populateServiceResponse(optionalExistingPreProcessorToggle.get(),
                        optionalExistingPvParameter.get(), existingProject, null);
                serviceResponse.put("response", response);
                serviceResponse.put("message", "pvParameter.already.exists");
                serviceResponse.put("httpStatus", HttpStatus.CONFLICT);
                return serviceResponse;

            }

            // when toggle is only_agri
            if (toggleValue == Toggle.ONLY_AGRI) {
                optionalExistingPreProcessorToggle = preProcessorToggleRepository
                        .findByProjectProjectIdAndPreProcessorStatus(existingProject.getProjectId(),
                                PreProcessorStatus.DRAFT);
                preProcessorToggle = addOrUpdatePreProcessorToggle(request, toggleValue,
                        optionalExistingPreProcessorToggle, existingProject, null);

                optionalExistingPvParameter.ifPresent(pvParameterRepository::delete);
                response.setPreProcessorToggle(preProcessorToggle);
                response.setProjectId(existingProject.getProjectId());
                serviceResponse.put("response", response);
                serviceResponse.put("message", "onlyAgri.created");
                serviceResponse.put("httpStatus", HttpStatus.OK);
                return serviceResponse;
            }
            // when toggle is apv or pv
            else {

                // validating pv parameters and fetching master data
                Map<String, Object> masterData = validatePvParametersAndFetchMasterData(request);

                PvModule pvModule = (PvModule) masterData.get("pvModule");
                ModeOfPvOperation modeOfPvOperation = (ModeOfPvOperation) masterData.get("modeOfPvOperation");
                List<PvModuleConfiguration> moduleConfigs = objectMapper.convertValue(masterData.get("moduleConfigs"),
                        new TypeReference<List<PvModuleConfiguration>>() {
                        });

                // creating or updating preProcessorToggle
                optionalExistingPreProcessorToggle = preProcessorToggleRepository
                        .findByProjectProjectIdAndPreProcessorStatus(existingProject.getProjectId(),
                                PreProcessorStatus.DRAFT);
                preProcessorToggle = addOrUpdatePreProcessorToggle(request, toggleValue,
                        optionalExistingPreProcessorToggle, existingProject, null);

                // creating or updating pvParameters
                PvParameter savedPvParameter = addOrUpdatePvParameters(optionalExistingPvParameter, request,
                        existingProject, null, pvModule, modeOfPvOperation, moduleConfigs);

                // deleting agri general parameters and crop parameters when toggle is only_pv
                if (toggleValue == Toggle.ONLY_PV) {

                    agriGeneralParameterRepository.findByProjectProjectIdAndStatus(projectId, PreProcessorStatus.DRAFT)
                            .ifPresent(agriGeneralParameterRepository::delete);

                    cropParametersRepository.findByProjectProjectIdAndStatus(projectId, PreProcessorStatus.DRAFT)
                            .ifPresent(cropParametersRepository::delete);
                }

                // setting up service response and then sending back to controller
                response = populateServiceResponse(preProcessorToggle, savedPvParameter, existingProject, null);
                serviceResponse.put("response", response);
                String message = callFor.trim().equalsIgnoreCase("update") ? "pvWithToggle.updated"
                        : "pvWithToggle.created";
                serviceResponse.put("message", message);
                serviceResponse.put("httpStatus", HttpStatus.OK);
                return serviceResponse;
            }
        }

        /*
         * creating or updating pv parameters and toggle in existing run
         */

        else {

            // validating run
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));

            if (existingRun.isMaster()==true && toggleValue != Toggle.APV) {
                throw new UnprocessableException("toggle.APV");
            }
            // check run is in holding state or not
            if (existingRun.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }

            if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
                throw new PvParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            optionalExistingPvParameter = Optional.ofNullable(existingRun.getPvParameters());
            if (callFor.trim().equalsIgnoreCase("update")) {
                if (optionalExistingPvParameter.isEmpty())
                    throw new PvParametersException("pvParameter.not.found", HttpStatus.NOT_FOUND);
                else if (pvParameterId.longValue() != optionalExistingPvParameter.get().getId())
                    throw new PvParametersException("pvParameterId.mismatch", HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (callFor.trim().equalsIgnoreCase("create") && (optionalExistingPvParameter.isPresent())
                    && (toggleValue == Toggle.APV || toggleValue == Toggle.ONLY_PV)) {
                optionalExistingPreProcessorToggle = Optional.ofNullable(existingRun.getPreProcessorToggle());
                response = populateServiceResponse(optionalExistingPreProcessorToggle.get(),
                        optionalExistingPvParameter.get(), existingProject, existingRun);
                serviceResponse.put("response", response);
                serviceResponse.put("message", "pvParameter.already.exists");
                serviceResponse.put("httpStatus", HttpStatus.CONFLICT);
                return serviceResponse;

            }

            // when toggle is only_agri
            if (toggleValue == Toggle.ONLY_AGRI) {
                optionalExistingPreProcessorToggle = Optional.ofNullable(existingRun.getPreProcessorToggle());
                preProcessorToggle = addOrUpdatePreProcessorToggle(request, toggleValue,
                        optionalExistingPreProcessorToggle, existingProject, existingRun);

                optionalExistingPvParameter.ifPresent(pvParameterRepository::delete);
                existingRun.setPreProcessorToggle(preProcessorToggle);
                existingRun.setPvParameters(null);
                existingRun.setRunName(preProcessorToggle.getRunName());
                Runs updatedRun = runRepository.save(existingRun);

                // updating the canSimulate of run
                runService.updateRun(updatedRun.getRunId(), preProcessorToggle);

                // setting the response
                response.setPreProcessorToggle(preProcessorToggle);
                response.setProjectId(existingProject.getProjectId());
                response.setRunId(updatedRun.getRunId());
                serviceResponse.put("response", response);
                serviceResponse.put("message", "pvWithToggle.added");
                serviceResponse.put("httpStatus", HttpStatus.OK);
                return serviceResponse;
            }

            // when toggle is apv or pv
            else {

                // validating pv parameters and fetching master data
                Map<String, Object> masterData = validatePvParametersAndFetchMasterData(request);

                Runs updatedRun = null;

                PvModule pvModule = (PvModule) masterData.get("pvModule");
                ModeOfPvOperation modeOfPvOperation = (ModeOfPvOperation) masterData.get("modeOfPvOperation");
                List<PvModuleConfiguration> moduleConfigs = objectMapper.convertValue(masterData.get("moduleConfigs"),
                        new TypeReference<List<PvModuleConfiguration>>() {
                        });

                // creating or updating preProcessorToggle
                optionalExistingPreProcessorToggle = Optional.ofNullable(existingRun.getPreProcessorToggle());
                preProcessorToggle = addOrUpdatePreProcessorToggle(request, toggleValue,
                        optionalExistingPreProcessorToggle, existingProject, existingRun);

                // creating or updating pvParameters
                PvParameter savedPvParameter = addOrUpdatePvParameters(optionalExistingPvParameter, request,
                        existingProject, existingRun, pvModule, modeOfPvOperation, moduleConfigs);

                // deleting agri general parameters and crop parameters when toggle is only_pv
                if (toggleValue == Toggle.ONLY_PV) {

                    AgriGeneralParameter existingAgriGeneralParameters = existingRun.getAgriGeneralParameters();
                    if (existingAgriGeneralParameters != null)
                        agriGeneralParameterRepository.delete(existingAgriGeneralParameters);
                    CropParameters existingCropParameters = existingRun.getCropParameters();
                    if (existingCropParameters != null)
                        cropParametersRepository.delete(existingCropParameters);

                    existingRun.setAgriGeneralParameters(null);
                    existingRun.setCropParameters(null);
                }

                existingRun.setPreProcessorToggle(preProcessorToggle);
                existingRun.setPvParameters(savedPvParameter);
                existingRun.setRunName(preProcessorToggle.getRunName());
                updatedRun = runRepository.save(existingRun);

                // updating canSimulate for existingRun
                runService.updateRun(updatedRun.getRunId(), preProcessorToggle);

                // setting up response
                response = populateServiceResponse(preProcessorToggle, savedPvParameter, existingProject, updatedRun);
                serviceResponse.put("response", response);
                serviceResponse.put("message", "pvWithToggle.added");
                serviceResponse.put("httpStatus", HttpStatus.OK);
                return serviceResponse;
            }
        }
    }

    public Map<String, Object> getPvParametersWithToggle(Long userId, Long projectId, Long runId) {

        if (userId == null || userId <= 0)
            throw new PvParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new PvParametersException("project.not.found", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new PvParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new PvParametersException("user.access", HttpStatus.FORBIDDEN);

        PreProcessorToggle preProcessorToggle = null;
        Optional<PreProcessorToggle> optionalExistingPreProcessorToggle = null;
        Optional<PvParameter> optionalExistingPvParameter = null;
        PvParameter pvParameter = null;
        Runs foundRun = null;
        PvParametersResponseDto response = new PvParametersResponseDto();
        Map<String, Object> serviceResponse = new HashMap<>();

        // when in draft
        if (runId == null) {

            // creating or updating preprocessor toggle in draft
            optionalExistingPreProcessorToggle = preProcessorToggleRepository
                    .findByProjectProjectIdAndPreProcessorStatus(existingProject.getProjectId(),
                            PreProcessorStatus.DRAFT);

            preProcessorToggle = optionalExistingPreProcessorToggle.map(existingPreProcessorToggle -> {
                return existingPreProcessorToggle;
            }).orElseGet(() -> {
                PreProcessorToggle newPreProcessorToggle = PreProcessorToggle.builder().project(existingProject)
                        .toggle(Toggle.APV).build();
                return preProcessorToggleRepository.save(newPreProcessorToggle);
            });

            // getting existing pv parameters
//            optionalExistingPvParameter = pvParameterRepository
//                    .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
            List<PvParameter> result = pvParameterRepository.findAllByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
            if (result.size() > 1) {
                throw new IllegalStateException("Expected a unique result but found multiple.");
            }
            optionalExistingPvParameter = result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));

        }

        // when run exists
        else {
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new PvParametersException("run.not.found", HttpStatus.NOT_FOUND));

            foundRun = existingRun;

            if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
                throw new PvParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            // creating or updating preprocessor toggle in run
            optionalExistingPreProcessorToggle = Optional.ofNullable(existingRun.getPreProcessorToggle());

            if (optionalExistingPreProcessorToggle.isPresent())
                preProcessorToggle = optionalExistingPreProcessorToggle.get();
            else {
                PreProcessorToggle newPreProcessorToggle = PreProcessorToggle.builder().project(existingProject)
                        .toggle(Toggle.APV).preProcessorStatus(PreProcessorStatus.CREATED).build();
                PreProcessorToggle savedPreProcessorToggle = preProcessorToggleRepository.save(newPreProcessorToggle);
                existingRun.setPreProcessorToggle(savedPreProcessorToggle);
                foundRun = runRepository.save(existingRun);

                // updating the canSimulate of existing run
                runService.updateRun(foundRun.getRunId(), savedPreProcessorToggle);
            }

            // getting existing pv parameters
            optionalExistingPvParameter = Optional.ofNullable(existingRun.getPvParameters());

        }

        pvParameter = optionalExistingPvParameter.map(existingPvParameter -> {
            return existingPvParameter;
        }).orElseGet(() -> {
            return new PvParameter();
        });

        // setting up response
        response = populateServiceResponse(preProcessorToggle, pvParameter, existingProject, foundRun);
        serviceResponse.put("response", response);
        serviceResponse.put("message", "pvWithToggle.fetched");
        serviceResponse.put("httpStatus", HttpStatus.OK);
        return serviceResponse;
    }

    @Override
    public Map<String, Object> getMasterData(String mode) {
    	
    	List<SoilType> activeSoils = soilService.getActiveSoilDetails();
        List<SoilTypeResponse> soils = activeSoils.stream().map(soil -> SoilTypeResponse.builder().id(soil.getId())
                        .name(soil.getSoilName())
//                        .soilPicturePath(soil.getSoilPicturePath())
                        .build())
                .collect(Collectors.toList());

        List<PvModule> existingPvModules = pvModuleRepository.findByIsActiveTrueAndHideFalseOrderByModuleTypeAsc();
        List<PvModuleResponse> pvModules = existingPvModules.stream()
                .map(pvModule -> PvModuleResponse.builder().id(pvModule.getId()).name(pvModule.getModuleType())
                        .length(pvModule.getLength()).width(pvModule.getWidth()).build())
                .collect(Collectors.toList());

        List<ModeOfPvOperation> existingModeOfOperations = modeOfPvOperationRepository
                .findByIsActiveTrueAndHideFalseOrderByModeOfOperationAsc();
        List<ModeOfPvOperationResponse> modeOfOperations = existingModeOfOperations.stream()
                .map(modeOfOperation -> ModeOfPvOperationResponse.builder().id(modeOfOperation.getId())
                        .name(modeOfOperation.getModeOfOperation()).build())
                .collect(Collectors.toList());

        List<PvModuleConfiguration> existingModuleConfigurations = pvModuleConfigurationRepository
                .findByIsActiveTrueAndHideFalseOrderByOrderingAsc();
        List<PvModuleConfigurationResponse> moduleConfigurations = existingModuleConfigurations.stream()
                .map(moduleConfiguration -> PvModuleConfigurationResponse.builder().id(moduleConfiguration.getId())
                        .name(moduleConfiguration.getModuleConfig())
                        .numberOfModules(moduleConfiguration.getNumberOfModules())
                        .typeOfModule(moduleConfiguration.getTypeOfModule().getValue())
                        .ordering(moduleConfiguration.getOrdering()).build())
                .sorted(Comparator.comparing(PvModuleConfigurationResponse::getOrdering)).collect(Collectors.toList());

        if (mode != null && ModeOfOperations.fromString(mode) == ModeOfOperations.SINGLE_AXIS_TRACKING) {

            moduleConfigurations = existingModuleConfigurations.stream().filter(moduleConfiguration -> {
                        PVModuleConfigType type = moduleConfiguration.getTypeOfModule();
                        return type == PVModuleConfigType.P || type == PVModuleConfigType.L;
                    }).map(moduleConfiguration -> PvModuleConfigurationResponse.builder().id(moduleConfiguration.getId())
                            .name(moduleConfiguration.getModuleConfig())
                            .numberOfModules(moduleConfiguration.getNumberOfModules())
                            .typeOfModule(moduleConfiguration.getTypeOfModule().getValue())
                            .ordering(moduleConfiguration.getOrdering()).build())
                    .sorted(Comparator.comparing(PvModuleConfigurationResponse::getOrdering))
                    .collect(Collectors.toList());

        }

        Map<String, Object> response = new HashMap<>();
        response.put("pvModules", pvModules);
        response.put("soils", soils);
        response.put("modeOfOperations", modeOfOperations);
        response.put("moduleConfigurations", moduleConfigurations);

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
        response.setIsMaster(existingRun != null ? existingRun.isMaster() : null);
        response.setCloneId(existingRun != null ? existingRun.getCloneId() : null);
        response.setRunId(existingRun != null ? existingRun.getRunId() : null);
        response.setSoilType(preProcessorToggle.getSoilType() != null ? preProcessorToggle.getSoilType() : null);
        return response;
    }

    private PreProcessorToggle addOrUpdatePreProcessorToggle(PvParametersRequestDto request, Toggle toggleValue,
                                                             Optional<PreProcessorToggle> optionalExistingPreProcessorToggle, Projects existingProject,
                                                             Runs existingRun) {

        PreProcessorToggle updatedPreProcessorToggle = optionalExistingPreProcessorToggle
                .map(existingPreProcessorToggle -> {

                    Long preProcessorToggleId = preProcessorToggleRepository
                            .findExistingToggleId(existingProject.getProjectId(), request.getRunName());
                    if (preProcessorToggleId != null && preProcessorToggleId.longValue() != existingPreProcessorToggle.getId())
                        throw new PvParametersException("run.name.already.exists", HttpStatus.UNPROCESSABLE_ENTITY);
                    SoilType soil = soilTypeRepo.findById(request.getSoilId())
                            .orElseThrow(() -> new ResourceNotFoundException(null, "soil.not.found"));

                    existingPreProcessorToggle.setRunName(request.getRunName());
                    existingPreProcessorToggle.setLengthOfOneRow(request.getLengthOfOneRow());
                    existingPreProcessorToggle.setPitchOfRows(request.getPitchOfRows());
                    existingPreProcessorToggle.setAzimuth(request.getAzimuth());
                    existingPreProcessorToggle.setToggle(toggleValue);
                    existingPreProcessorToggle.setSoilType(soil);
                    return existingPreProcessorToggle;
                }).orElseGet(() -> {

                    boolean runNameExists = preProcessorToggleRepository
                            .findByProjectProjectIdAndRunName(existingProject.getProjectId(), request.getRunName());
                    if (runNameExists)
                        throw new PvParametersException("run.name.already.exists", HttpStatus.UNPROCESSABLE_ENTITY);
                    SoilType soil = soilTypeRepo.findById(request.getSoilId())
                            .orElseThrow(() -> new ResourceNotFoundException(null, "soil.not.found"));
                    PreProcessorToggle newPreProcessorToggle = PreProcessorToggle.builder()
                            .runName(request.getRunName()).lengthOfOneRow(request.getLengthOfOneRow())
                            .pitchOfRows(request.getPitchOfRows()).azimuth(request.getAzimuth())
                            .soilType(soil)
                            .project(existingProject).run(existingRun).toggle(toggleValue).build();

                    return newPreProcessorToggle;
                });
        return preProcessorToggleRepository.save(updatedPreProcessorToggle);
    }

    private PvParameter addOrUpdatePvParameters(Optional<PvParameter> optionalExistingPvParameter,
                                                PvParametersRequestDto request, Projects existingProject, Runs existingRun, PvModule pvModule,
                                                ModeOfPvOperation modeOfPvOperation, List<PvModuleConfiguration> moduleConfigs) {

        // ignore status on updation as already status is resolved just need to check
        // while creating
        PvParameter updatedPvParameter = optionalExistingPvParameter.map(existingPvParameter -> {
            existingPvParameter.setGapBetweenModules(request.getGapBetweenModules());
            existingPvParameter.setHeight(request.getHeight());
            existingPvParameter.setMaxAngleOfTracking(request.getMaxAngleOfTracking());
            existingPvParameter.setModeOfOperationId(modeOfPvOperation);
            existingPvParameter.setModuleConfig(moduleConfigs);
            existingPvParameter.setModuleMaskPattern(request.getModuleMaskPattern());
            existingPvParameter.setProject(existingProject);
            existingPvParameter.setPvModule(pvModule);
            existingPvParameter.setRun(existingRun);
            existingPvParameter.setTiltIfFt(request.getTiltIfFt());
            existingPvParameter.setXCoordinate(request.getXCoordinate());
            existingPvParameter.setYCoordinate(request.getYCoordinate());
            return existingPvParameter;
        }).orElseGet(() -> {
            PvParameter newPvParameter = PvParameter.builder().gapBetweenModules(request.getGapBetweenModules())
                    .height(request.getHeight()).maxAngleOfTracking(request.getMaxAngleOfTracking())
                    .modeOfOperationId(modeOfPvOperation).moduleConfig(moduleConfigs)
                    .xCoordinate(request.getXCoordinate()).yCoordinate(request.getYCoordinate())
                    .moduleMaskPattern(request.getModuleMaskPattern()).pvModule(pvModule).project(existingProject)
                    .run(existingRun).tiltIfFt(request.getTiltIfFt()).build();
            if (existingRun != null)
                newPvParameter.setStatus(PreProcessorStatus.CREATED);
            return newPvParameter;
        });
        return pvParameterRepository.save(updatedPvParameter);
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

}