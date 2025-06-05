package com.sunseed.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.sunseed.enums.RunStatus;
import com.sunseed.exceptions.UnprocessableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.Cycles;
import com.sunseed.entity.AgriPvProtectionHeight;
import com.sunseed.entity.BedParameter;
import com.sunseed.entity.Irrigation;
import com.sunseed.entity.Projects;
import com.sunseed.entity.ProtectionLayer;
import com.sunseed.entity.Runs;
import com.sunseed.entity.SoilType;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.TempControl;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.AgriGeneralParametersRequestDto;
import com.sunseed.model.requestDTO.AgriPvProtectionHeightRequestDto;
import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
import com.sunseed.model.responseDTO.AgriPvProtectionHeightResponseDto;
import com.sunseed.model.responseDTO.BedParameterResponseDto;
import com.sunseed.model.responseDTO.agriGeneralParameters.IrrigationResponse;
import com.sunseed.model.responseDTO.agriGeneralParameters.ProtectionLayerResponse;
import com.sunseed.model.responseDTO.agriGeneralParameters.SoilTypeResponse;
import com.sunseed.repository.AgriGeneralParameterRepo;
import com.sunseed.repository.AgriPvProtectionHeightRepo;
import com.sunseed.repository.CropParametersRepo;
import com.sunseed.repository.IrrigationRepo;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.ProtectionLayerRepo;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.SoilTypeRepo;
import com.sunseed.service.AgriGeneralParametersService;
import com.sunseed.service.RunService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgriGeneralParametersServiceImpl implements AgriGeneralParametersService {

    private final ProjectsRepository projectRepository;
    private final PreProcessorToggleRepository preProcessorToggleRepository;
    private final AgriGeneralParameterRepo agriGeneralParameterRepository;
    private final CropParametersRepo cropParametersRepository;
    private final Validator validator;
    private final IrrigationRepo irrigationRepository;
    private final SoilTypeRepo soilTypeRepository;
    private final ProtectionLayerRepo protectionLayerRepository;
    private final AgriPvProtectionHeightRepo agriPvProtectionHeightRepository;
    private final RunsRepository runRepository;
    private final RunService runService;

    @Override
    @Transactional
    public Map<String, Object> addAgriGeneralParameters(AgriGeneralParametersRequestDto request, Long projectId,
                                                        Long runId, Long userId) {

        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        Irrigation irrigation = irrigationRepository.findById(request.getIrrigationTypeId())
                .orElseThrow(() -> new AgriGeneralParametersException("Irrigation.not.found", HttpStatus.NOT_FOUND));

//        SoilType soilType = soilTypeRepository.findById(request.getSoilId())
//                .orElseThrow(() -> new AgriGeneralParametersException("Soil.not.found", HttpStatus.NOT_FOUND));

        Optional<AgriGeneralParameter> optionalExistingAgriGeneralParameter = null;
        AgriGeneralParametersResponseDto response = null;
        Map<String, Object> serviceResponse = null;

        /*
         * adding agri general parameters in draft
         */
        if (runId == null) {
            String toggleValue = preProcessorToggleRepository
                    .findToggleByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                agriGeneralParameterRepository
                        .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(agriGeneralParameterRepository::delete);
                cropParametersRepository
                        .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(cropParametersRepository::delete);
                throw new AgriGeneralParametersException("agri.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // checking if already exists
            optionalExistingAgriGeneralParameter = agriGeneralParameterRepository
                    .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
            if (optionalExistingAgriGeneralParameter.isPresent()) {
                response = populateServiceResponse(optionalExistingAgriGeneralParameter.get(), existingProject, null);
                throw new AgriGeneralParametersException(response, "agri.exists", HttpStatus.CONFLICT);
            }

            // now validating agriGeneralParameters
            validateAgriGeneralParameters(request, toggleValue);

            // now saving agri general parameter
            AgriGeneralParameter newAgriGeneralParameter = saveOrUpdateAgriGeneralParameter(request,
                    optionalExistingAgriGeneralParameter, irrigation,/* soilType,*/ existingProject, null);

            // now returning response
            response = populateServiceResponse(newAgriGeneralParameter, existingProject, null);
            serviceResponse = new HashMap<>();
            serviceResponse.put("response", response);
            serviceResponse.put("message", "agri.general.added");
            return serviceResponse;

        }

        /*
         * adding agri general parameters in existing run
         */
        else {
            // validating run
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));

            // run status check
            if (existingRun.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            String toggleValue = null;
            if (existingRun.getPreProcessorToggle() != null && existingRun.getPreProcessorToggle().getToggle() != null)
                toggleValue = existingRun.getPreProcessorToggle().getToggle().name();

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                Optional.ofNullable(existingRun.getAgriGeneralParameters()).ifPresent(agriGeneralParameterRepository::delete);
                Optional.ofNullable(existingRun.getCropParameters()).ifPresent(cropParametersRepository::delete);
                existingRun.setAgriGeneralParameters(null);
                existingRun.setCropParameters(null);
                Runs updatedRun = runRepository.save(existingRun);

                // updating canSimulate of updated run
                runService.updateRun(updatedRun.getRunId(), updatedRun.getPreProcessorToggle());
                throw new AgriGeneralParametersException("agri.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // checking if already exists
            optionalExistingAgriGeneralParameter = Optional.ofNullable(existingRun.getAgriGeneralParameters());
            if (optionalExistingAgriGeneralParameter.isPresent()) {
                response = populateServiceResponse(optionalExistingAgriGeneralParameter.get(), existingProject,
                        existingRun);
                throw new AgriGeneralParametersException(response, "agri.exists", HttpStatus.CONFLICT);
            }

            // now validating agriGeneralParameters
            validateAgriGeneralParameters(request, toggleValue);

            // now saving agri general parameter
            AgriGeneralParameter newAgriGeneralParameter = saveOrUpdateAgriGeneralParameter(request,
                    optionalExistingAgriGeneralParameter, irrigation,/* soilType,*/ existingProject, existingRun);

            // now updating run
            existingRun.setAgriGeneralParameters(newAgriGeneralParameter);
            Runs updatedRun = runRepository.save(existingRun);

            // update canSimulate of updatedRun
            runService.updateRun(updatedRun.getRunId(), updatedRun.getPreProcessorToggle());

            // now returning response
            response = populateServiceResponse(newAgriGeneralParameter, existingProject, updatedRun);
            serviceResponse = new HashMap<>();
            serviceResponse.put("response", response);
            serviceResponse.put("message", "agri.general.added");
            return serviceResponse;

        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateAgriGeneralParameters(AgriGeneralParametersRequestDto request, Long projectId,
                                                           Long agriGeneralParameterId, Long runId, Long userId) {

        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);
        if (agriGeneralParameterId == null || agriGeneralParameterId <= 0)
            throw new AgriGeneralParametersException("agri.not.null", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        Irrigation irrigation = irrigationRepository.findById(request.getIrrigationTypeId())
                .orElseThrow(() -> new AgriGeneralParametersException("Irrigation.not.found", HttpStatus.NOT_FOUND));
        System.out.println("irrigation is :" + irrigation.getIrrigationType());
//        SoilType soilType = soilTypeRepository.findById(request.getSoilId())
//                .orElseThrow(() -> new AgriGeneralParametersException("Soil.not.found", HttpStatus.NOT_FOUND));

        Optional<AgriGeneralParameter> optionalExistingAgriGeneralParameter = null;
        Optional<CropParameters> optionalCropParameter = null;
        AgriGeneralParametersResponseDto response = null;
        Map<String, Object> serviceResponse = null;

        /*
         * updating agri general parameters in draft
         */
        if (runId == null) {
            optionalExistingAgriGeneralParameter = agriGeneralParameterRepository
                    .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
            optionalCropParameter = cropParametersRepository
            .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
//            .ifPresent(cropParametersRepository::delete);
            if (optionalExistingAgriGeneralParameter.isEmpty())
                throw new AgriGeneralParametersException("agri.not.found", HttpStatus.NOT_FOUND);

            else if (!agriGeneralParameterId.equals(optionalExistingAgriGeneralParameter.get().getId()))
                throw new AgriGeneralParametersException("agri.id.mismatch", HttpStatus.UNPROCESSABLE_ENTITY);
            
            Double existingBedCC = optionalExistingAgriGeneralParameter.get().getBedParameter().getBedcc();

            if (!existingBedCC.equals(request.getBedcc())) {
                optionalCropParameter.ifPresent(cropParameter -> {
                    if (!cropParameter.getCycles().isEmpty()) {
                    	Cycles cycle = cropParameter.getCycles().get(0); // Get the first cycle

                        if (cycle.getInterBedPattern() != null && !cycle.getInterBedPattern().isEmpty()) {
                            // Set interBedPattern to an empty list []
                            cycle.setInterBedPattern(new ArrayList<>());

                            // Save the updated crop parameter in the database
                            cropParametersRepository.save(cropParameter);
                        }
                    }
                });
            }

            // finding the toggle and updating acc.
            String toggleValue = preProcessorToggleRepository
                    .findToggleByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                agriGeneralParameterRepository
                        .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(agriGeneralParameterRepository::delete);
                cropParametersRepository
                        .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT)
                        .ifPresent(cropParametersRepository::delete);
                throw new AgriGeneralParametersException("agri.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // now validating agri general parameters
            validateAgriGeneralParameters(request, toggleValue);

            // now updating agri general parameters
            AgriGeneralParameter updatedAgriGeneralParameter = saveOrUpdateAgriGeneralParameter(request,
                    optionalExistingAgriGeneralParameter, irrigation,/* soilType,*/ existingProject, null);

            // now returning response
            response = populateServiceResponse(updatedAgriGeneralParameter, existingProject, null);
            serviceResponse = new HashMap<>();
            serviceResponse.put("response", response);
            serviceResponse.put("message", "agri.general.updated");
            return serviceResponse;
        }

        /*
         * updating agri general parameters in existing run
         */
        else {
            // validating run
            Runs existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));
// run status check
            if (existingRun.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            optionalExistingAgriGeneralParameter = Optional.ofNullable(existingRun.getAgriGeneralParameters());
            optionalCropParameter = Optional.ofNullable(existingRun.getCropParameters());
            if (optionalExistingAgriGeneralParameter.isEmpty())
                throw new AgriGeneralParametersException("agri.not.found", HttpStatus.NOT_FOUND);
            else if (!agriGeneralParameterId.equals(optionalExistingAgriGeneralParameter.get().getId())) {
                System.out.println("agrigeneral parameter id :" + agriGeneralParameterId + "agri general parameter id from run : " + optionalExistingAgriGeneralParameter.get().getId());
                throw new AgriGeneralParametersException("agri.id.mismatch", HttpStatus.UNPROCESSABLE_ENTITY);
            }
            String toggleValue = null;
            if (existingRun.getPreProcessorToggle() != null && existingRun.getPreProcessorToggle().getToggle() != null)
                toggleValue = existingRun.getPreProcessorToggle().getToggle().name();

            if (toggleValue == null)
                throw new AgriGeneralParametersException("toggle.not.exists", HttpStatus.BAD_REQUEST);
            else if (toggleValue.equalsIgnoreCase(Toggle.ONLY_PV.name())) {
                Optional.ofNullable(existingRun.getAgriGeneralParameters()).ifPresent(agriGeneralParameterRepository::delete);
                Optional.ofNullable(existingRun.getCropParameters()).ifPresent(cropParametersRepository::delete);
                existingRun.setAgriGeneralParameters(null);
                existingRun.setCropParameters(null);
                Runs updatedRun = runRepository.save(existingRun);

                // updating canSimulate of updated run
                runService.updateRun(updatedRun.getRunId(), updatedRun.getPreProcessorToggle());
                throw new AgriGeneralParametersException("agri.cant.only.pv", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            // now validating agriGeneralParameters
            validateAgriGeneralParameters(request, toggleValue);
            
            Double existingBedCC = optionalExistingAgriGeneralParameter.get().getBedParameter().getBedcc();
            
            if (!existingBedCC.equals(request.getBedcc())) {
                optionalCropParameter.ifPresent(cropParameter -> {
                    if (!cropParameter.getCycles().isEmpty()) {
                    	Cycles cycle = cropParameter.getCycles().get(0); // Get the first cycle

                        if (cycle.getInterBedPattern() != null && !cycle.getInterBedPattern().isEmpty()) {
                            // Set interBedPattern to an empty list []
                            cycle.setInterBedPattern(new ArrayList<>());

                            // Save the updated crop parameter in the database
                            cropParametersRepository.save(cropParameter);
                        }
                    }
                });
            }

            // now saving agri general parameter
            AgriGeneralParameter updatedAgriGeneralParameter = saveOrUpdateAgriGeneralParameter(request,
                    optionalExistingAgriGeneralParameter, irrigation,/* soilType,*/ existingProject, existingRun);

            // now updating run
            existingRun.setAgriGeneralParameters(updatedAgriGeneralParameter);
            Runs updatedRun = runRepository.save(existingRun);

            // update canSimulate of updatedRun
            runService.updateRun(updatedRun.getRunId(), updatedRun.getPreProcessorToggle());

            // now returning response
            response = populateServiceResponse(updatedAgriGeneralParameter, existingProject, updatedRun);
            serviceResponse = new HashMap<>();
            serviceResponse.put("response", response);
            serviceResponse.put("message", "agri.general.updated");
            return serviceResponse;

        }
    }

    @Override
    public Map<String, Object> getAgriGeneralParameters(Long projectId, Long runId, Long userId) {

        if (userId == null || userId <= 0)
            throw new AgriGeneralParametersException("user.not.found", HttpStatus.NOT_FOUND);
        if (projectId == null || projectId <= 0)
            throw new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND);

        // project existance and userId match check
        Projects existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new AgriGeneralParametersException("project.not.found", HttpStatus.NOT_FOUND));

        if (userId != existingProject.getUserProfile().getUserId())
            throw new AgriGeneralParametersException("unauthorized.user", HttpStatus.UNAUTHORIZED);

        Optional<AgriGeneralParameter> optionalExistingAgriGeneralParameter = null;
        AgriGeneralParametersResponseDto response = null;
        Map<String, Object> serviceResponse = null;
        Runs existingRun = null;

        // getting agri general parameters from draft
        if (runId == null) {
            optionalExistingAgriGeneralParameter = agriGeneralParameterRepository
                    .findByProjectProjectIdAndStatus(existingProject.getProjectId(), PreProcessorStatus.DRAFT);
        } else {
            existingRun = runRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));

            if (existingProject.getProjectId() != existingRun.getInProject().getProjectId())
                throw new AgriGeneralParametersException("run.not.of.project", HttpStatus.UNPROCESSABLE_ENTITY);

            optionalExistingAgriGeneralParameter = Optional.ofNullable(existingRun.getAgriGeneralParameters());
        }

        if (optionalExistingAgriGeneralParameter.isPresent())
            response = populateServiceResponse(optionalExistingAgriGeneralParameter.get(), existingProject,
                    existingRun);
        else {
            response = null;
        }
        serviceResponse = new HashMap<>();
        serviceResponse.put("response", response);
        serviceResponse.put("message", "agri.fetched");
        return serviceResponse;
    }

    @Override
    public Map<String, Object> getMasterData() {

        List<SoilType> existingSoils = soilTypeRepository.findByIsActiveTrueAndHideFalseOrderBySoilNameAsc();
        List<SoilTypeResponse> soils = existingSoils.stream().map(soil -> SoilTypeResponse.builder().id(soil.getId())
                        .name(soil.getSoilName()).soilPicturePath(soil.getSoilPicturePath()).build())
                .collect(Collectors.toList());

        List<Irrigation> existingTypeOfIrrigations = irrigationRepository.findByIsActiveTrueAndHideFalseOrderByIrrigationTypeAsc();
        List<IrrigationResponse> typeOfIrrigations = existingTypeOfIrrigations.stream()
                .map(irrigationType -> IrrigationResponse.builder().id(irrigationType.getId())
                        .name(irrigationType.getIrrigationType()).build())
                .collect(Collectors.toList());

        List<ProtectionLayer> existingProtectionLayers = protectionLayerRepository.findByIsActiveTrueAndHideFalseOrderByProtectionLayerNameAsc();
        List<ProtectionLayerResponse> protectionLayers = existingProtectionLayers.stream()
                .map(protectionLayer -> ProtectionLayerResponse.builder().id(protectionLayer.getProtectionLayerId())
                        .name(protectionLayer.getProtectionLayerName()).build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("soils", soils);
        response.put("typeOfIrrigations", typeOfIrrigations);
        response.put("protectionLayers", protectionLayers);

        return response;

    }

    private AgriGeneralParameter saveOrUpdateAgriGeneralParameter(AgriGeneralParametersRequestDto request,
                                                                  Optional<AgriGeneralParameter> optionalExistingAgriGeneralParameter, Irrigation irrigation,
                                                                  /*SoilType soilType,*/ Projects existingProject, Runs existingRun) {

        // ignore status on updation as already status is resolved just need to check
        // while creating
        if (optionalExistingAgriGeneralParameter.isEmpty()) {

            // setting up agri general parameter object
            AgriGeneralParameter agriGeneralParameter = new AgriGeneralParameter();
            agriGeneralParameter.setIrrigationId(irrigation);
//            agriGeneralParameter.setSoilType(soilType);
            agriGeneralParameter.setIsMulching(request.getIsMulching());
            agriGeneralParameter.setProject(existingProject);
            if (TempControl.TRAIL_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
                agriGeneralParameter.setTempControl(TempControl.TRAIL_MIN_MAX);
                agriGeneralParameter.setTrail(request.getTrail());
                agriGeneralParameter.setMinTemp(request.getMinTemp());
                agriGeneralParameter.setMaxTemp(request.getMaxTemp());
            } else if (TempControl.ABSOLUTE_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
                agriGeneralParameter.setTempControl(TempControl.ABSOLUTE_MIN_MAX);
                agriGeneralParameter.setMinTemp(request.getMinTemp());
                agriGeneralParameter.setMaxTemp(request.getMaxTemp());
            } else {
                System.out.println("enter in none case in agri general parameters");
                agriGeneralParameter.setTempControl(TempControl.NONE);
                agriGeneralParameter.setTrail(null);
                agriGeneralParameter.setMinTemp(null);
                agriGeneralParameter.setMaxTemp(null);

            }

            if (existingRun != null)
                agriGeneralParameter.setStatus(PreProcessorStatus.CREATED);

            // code for setting agri pv protection height object
            if (request.getAgriPvProtectionHeight() != null) {
                List<Long> protectionIdList = request.getAgriPvProtectionHeight().stream().map(t -> t.getProtectionId())
                        .collect(Collectors.toList());
                List<ProtectionLayer> protectionLayerList = protectionLayerRepository
                        .findAllByProtectionLayerIdIn(protectionIdList);

                List<Long> entityProtectionIds = protectionLayerList.stream().map(ProtectionLayer::getProtectionLayerId)
                        .collect(Collectors.toList());

                if (protectionLayerList.isEmpty() || !protectionIdList.stream().allMatch(entityProtectionIds::contains))
                    throw new AgriGeneralParametersException("ProtectionLayer.not.found", HttpStatus.NOT_FOUND);

                Map<Long, ProtectionLayer> protectionLayerMap = protectionLayerList.stream().collect(
                        Collectors.toMap(ProtectionLayer::getProtectionLayerId, protectionLayer -> protectionLayer));

                List<AgriPvProtectionHeight> agriPvProtectionHeights = request.getAgriPvProtectionHeight().stream()
                        .map(t -> {
                            return AgriPvProtectionHeight.builder().protectionHeight(t.getHeight())
                                    .protectionLayer(protectionLayerMap.get(t.getProtectionId())).build();
                        }).collect(Collectors.toList());

                agriPvProtectionHeights.forEach(t -> t.setAgriGeneralParameter(agriGeneralParameter));
                agriGeneralParameter.setAgriPvProtectionHeight(agriPvProtectionHeights);
            }
            // code for bed parameter object creation
            BedParameter bedParameter = BedParameter.builder().bedAngle(request.getBedAngle())
                    .bedAzimuth(request.getBedAzimuth()).bedcc(request.getBedcc()).bedHeight(request.getBedHeight())
                    .bedWidth(request.getBedWidth()).startPointOffset(request.getStartPointOffset()).build();

            bedParameter.setAgriGeneralParameter(agriGeneralParameter);
            agriGeneralParameter.setBedParameter(bedParameter);

            // now saving agri general parameter no need to save child just saving parent it
            // will save child as well
            AgriGeneralParameter savedAgriGeneralParameter = agriGeneralParameterRepository.save(agriGeneralParameter);
            return savedAgriGeneralParameter;
        }

        // updating already existing agri general parameters

        // setting up agrigeneral parameter object for updating later
        AgriGeneralParameter existingAgriGeneralParameter = optionalExistingAgriGeneralParameter.get();
        existingAgriGeneralParameter.setIrrigationId(irrigation);
//        existingAgriGeneralParameter.setSoilType(soilType);
        existingAgriGeneralParameter.setIsMulching(request.getIsMulching());
        existingAgriGeneralParameter.setProject(existingProject);
        if (TempControl.TRAIL_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
            existingAgriGeneralParameter.setTempControl(TempControl.TRAIL_MIN_MAX);
            existingAgriGeneralParameter.setTrail(request.getTrail());
            existingAgriGeneralParameter.setMinTemp(request.getMinTemp());
            existingAgriGeneralParameter.setMaxTemp(request.getMaxTemp());
        } else if (TempControl.ABSOLUTE_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
            existingAgriGeneralParameter.setTempControl(TempControl.ABSOLUTE_MIN_MAX);
            existingAgriGeneralParameter.setMinTemp(request.getMinTemp());
            existingAgriGeneralParameter.setMaxTemp(request.getMaxTemp());
        } else {
            existingAgriGeneralParameter.setTempControl(TempControl.NONE);
            existingAgriGeneralParameter.setTrail(null);
            existingAgriGeneralParameter.setMinTemp(null);
            existingAgriGeneralParameter.setMaxTemp(null);
        }

        // code for setting agri pv protection height object

        // Check if agriPvProtectionHeight is empty and clear if needed
        if (request.getAgriPvProtectionHeight() != null && !request.getAgriPvProtectionHeight().isEmpty()) {
            // existing logic for handling agriPvProtectionHeight when there are entries
            List<Long> protectionIdList = request.getAgriPvProtectionHeight().stream().map(t -> t.getProtectionId())
                    .collect(Collectors.toList());
            List<ProtectionLayer> protectionLayerList = protectionLayerRepository
                    .findAllByProtectionLayerIdIn(protectionIdList);

            List<Long> entityProtectionIds = protectionLayerList.stream().map(ProtectionLayer::getProtectionLayerId)
                    .collect(Collectors.toList());

            if (protectionLayerList.isEmpty() || !protectionIdList.stream().allMatch(entityProtectionIds::contains))
                throw new AgriGeneralParametersException("ProtectionLayer.not.found", HttpStatus.NOT_FOUND);

            Map<Long, ProtectionLayer> protectionLayerMap = protectionLayerList.stream()
                    .collect(Collectors.toMap(ProtectionLayer::getProtectionLayerId, protectionLayer -> protectionLayer));

            List<AgriPvProtectionHeight> agriPvProtectionHeights = new ArrayList<>();
            for (AgriPvProtectionHeightRequestDto t : request.getAgriPvProtectionHeight()) {
                if (t.getAgriPvProtectionHeightId() != null) {
                    AgriPvProtectionHeight agriPvProtectionHeight = agriPvProtectionHeightRepository
                            .findById(t.getAgriPvProtectionHeightId())
                            .orElseThrow(() -> new AgriGeneralParametersException("AgriPvProtectionHeight.not.found",
                                    HttpStatus.NOT_FOUND));
                    agriPvProtectionHeight.setProtectionHeight(t.getHeight());
                    agriPvProtectionHeight.setProtectionLayer(protectionLayerMap.get(t.getProtectionId()));
                    agriPvProtectionHeights.add(agriPvProtectionHeight);
                } else {
                    AgriPvProtectionHeight agriPvProtectionHeight = new AgriPvProtectionHeight();
                    agriPvProtectionHeight.setProtectionHeight(t.getHeight());
                    agriPvProtectionHeight.setProtectionLayer(protectionLayerMap.get(t.getProtectionId()));
                    agriPvProtectionHeights.add(agriPvProtectionHeight);
                }
            }
            agriPvProtectionHeights.forEach(t -> t.setAgriGeneralParameter(existingAgriGeneralParameter));

            existingAgriGeneralParameter.getAgriPvProtectionHeight().clear();
            existingAgriGeneralParameter.getAgriPvProtectionHeight().addAll(agriPvProtectionHeights);
        } else {
            // If agriPvProtectionHeight is an empty array, clear the existing collection
            existingAgriGeneralParameter.getAgriPvProtectionHeight().clear();
        }

        // code for bed parameter object creation
        BedParameter existingBedParameter = existingAgriGeneralParameter.getBedParameter();
        if (existingBedParameter == null)
            throw new AgriGeneralParametersException("BedParameter.not.found", HttpStatus.NOT_FOUND);
        existingBedParameter.setBedAngle(request.getBedAngle());
        existingBedParameter.setBedAzimuth(request.getBedAzimuth());
        existingBedParameter.setBedcc(request.getBedcc());
        existingBedParameter.setBedHeight(request.getBedHeight());
        existingBedParameter.setBedWidth(request.getBedWidth());
        existingBedParameter.setStartPointOffset(request.getStartPointOffset());

        existingAgriGeneralParameter.setBedParameter(existingBedParameter);
        existingBedParameter.setAgriGeneralParameter(existingAgriGeneralParameter);


        // now updating agri general parameter
        AgriGeneralParameter updatedAgriGeneralParameter = agriGeneralParameterRepository
                .save(existingAgriGeneralParameter);

        // deleting given protection layer id's which were given to delete
        if (request.getProtectionDelete() != null && !request.getProtectionDelete().isEmpty()) {
            System.out.println("delete list is given");

            //  request.getProtectionDelete().forEach((id) -> agriPvProtectionHeightRepository.deleteById(id));
            agriPvProtectionHeightRepository.deleteAllById(request.getProtectionDelete());

        }
        return updatedAgriGeneralParameter;
    }

    private void validateAgriGeneralParameters(AgriGeneralParametersRequestDto request, String toggleValue) {

        // validating agri general parameters based on group
        Set<ConstraintViolation<AgriGeneralParametersRequestDto>> agriGeneralParametersViolations = validator
                .validate(request, ValidationGroups.AgriGeneralParametersGroup.class);

        if (!agriGeneralParametersViolations.isEmpty()) {
            ConstraintViolation<AgriGeneralParametersRequestDto> violation = agriGeneralParametersViolations.iterator()
                    .next();
            throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (!TempControl.NONE.getValue().equalsIgnoreCase(request.getTempControl())) {

            // Validate using group validation for null checks and range checks
            Set<ConstraintViolation<AgriGeneralParametersRequestDto>> tempControlViolations = new HashSet<>();

            if (TempControl.TRAIL_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
                tempControlViolations = validator.validate(request, ValidationGroups.TrailMinMaxGroup.class);
            } else if (TempControl.ABSOLUTE_MIN_MAX.getValue().equalsIgnoreCase(request.getTempControl())) {
                tempControlViolations = validator.validate(request, ValidationGroups.AbsoluteMinMaxGroup.class);
            }

            if (!tempControlViolations.isEmpty()) {
                ConstraintViolation<AgriGeneralParametersRequestDto> violation = tempControlViolations.iterator()
                        .next();
                throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
            }

            // Additional logical validation
            if (request.getMinTemp() > request.getMaxTemp()) {
                throw new AgriGeneralParametersException("min.greater.max.temp", HttpStatus.BAD_REQUEST);
            }
        }

        if (toggleValue != null && toggleValue.equalsIgnoreCase(Toggle.APV.name())) {

            // validating offset group
            Set<ConstraintViolation<AgriGeneralParametersRequestDto>> offsetViolations = validator
                    .validate(request, ValidationGroups.OffsetGroup.class);

            if (!offsetViolations.isEmpty()) {
                ConstraintViolation<AgriGeneralParametersRequestDto> violation = offsetViolations.iterator()
                        .next();
                throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private AgriGeneralParametersResponseDto populateServiceResponse(AgriGeneralParameter agriGeneralParameter,
                                                                     Projects existingProject, Runs existingRun) {
        List<AgriPvProtectionHeightResponseDto> agriPvProtectionHeightResponse = null;
        if (agriGeneralParameter.getAgriPvProtectionHeight() != null) {
            agriPvProtectionHeightResponse = agriGeneralParameter
                    .getAgriPvProtectionHeight().stream().map(t -> {
                        return AgriPvProtectionHeightResponseDto.builder().agriPvProtectionHeightId(t.getId())
                                .height(t.getProtectionHeight()).protectionId(t.getProtectionLayer().getProtectionLayerId())
                                .protectionLayerName(t.getProtectionLayer().getProtectionLayerName()).build();
                    }).collect(Collectors.toList());
        }
        BedParameter existingBedParameter = agriGeneralParameter.getBedParameter();
        BedParameterResponseDto bedParameterResponse = BedParameterResponseDto.builder()
                .id(existingBedParameter.getId()).bedAngle(existingBedParameter.getBedAngle())
                .bedAzimuth(existingBedParameter.getBedAzimuth()).bedcc(existingBedParameter.getBedcc())
                .bedHeight(existingBedParameter.getBedHeight()).bedWidth(existingBedParameter.getBedWidth())
                .startPointOffset(existingBedParameter.getStartPointOffset()).build();

        AgriGeneralParametersResponseDto response = AgriGeneralParametersResponseDto.builder()
                .id(agriGeneralParameter.getId()).projectId(existingProject.getProjectId())
                .runId(existingRun != null ? existingRun.getRunId() : null)
                .irrigationType(agriGeneralParameter.getIrrigationId().getId())
//                .soilId(agriGeneralParameter.getSoilType().getId())
                .tempControl(
                        agriGeneralParameter.getTempControl() != null ? agriGeneralParameter.getTempControl().getValue()
                                : null)
                .trail(agriGeneralParameter.getTrail()).minTemp(agriGeneralParameter.getMinTemp())
                .maxTemp(agriGeneralParameter.getMaxTemp()).isMulching(agriGeneralParameter.getIsMulching())
                .status(agriGeneralParameter.getStatus() != null ? agriGeneralParameter.getStatus().getValue() : null)
                .agriPvProtectionHeight(agriPvProtectionHeightResponse).bedParameter(bedParameterResponse)
                .isMaster(existingRun != null ? existingRun.isMaster() : null)
                .cloneId(existingRun != null ? existingRun.getCloneId() : null)
                .build();
        return response;
    }

}
