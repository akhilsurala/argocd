package com.sunseed.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.sunseed.entity.*;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.mappers.EconomicParameterModelMapper;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.EconomicMultiCropRequestDto;
import com.sunseed.model.responseDTO.CropDto;
import com.sunseed.model.responseDTO.CurrencyResponse;
import com.sunseed.model.responseDTO.EconomicMultiCropResponse;
import com.sunseed.repository.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.EconomicParametersRequestDto;
import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
import com.sunseed.service.EconomicParametersService;
import com.sunseed.service.PreProcessorToggleService;
import com.sunseed.service.RunService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EconomicParametersServiceImpl implements EconomicParametersService {

    private final ProjectsRepository projectRepo;
    private final RunsRepository runsRepository;
    private final EconomicParameterRepository economicParameterRepo;
    private final CurrencyRepository currencyRepo;
    private final UserProfileRepository userProfileRepo;
    private final PvParameterRepository pvParameterRepository;
    private final CropParametersRepo cropParametersRepo;
    private final PreProcessorToggleRepository preProcessorToggleRepository;
    private final AgriGeneralParameterRepo agriGeneralParameterRepo;
    private final Validator validator;
    private final CropRepository cropRepository;
    private final EconomicMultiCropRepository economicMultiCropRepo;
    //    @Autowired
    private final RunService runService;
    //    @Autowired
    private final PreProcessorToggleService preProcessorToggleService;
    private final EconomicParameterModelMapper economicParameterModelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EconomicParametersResponseDto createEconomicParameters(EconomicParametersRequestDto request, Long projectId, Long userId, Long runId) {

        System.out.println("projectId :" + projectId + "user id :" + userId);
        for (double a : request.getHourlySellingRates())
            System.out.println("rate:" + a);
        Projects project = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        if (project.getUserProfile().getUserProfileId() != userProfile.getUserProfileId()) {
            throw new UnprocessableException("project.mismatch");
        }
        Runs run = null;
        PreProcessorToggle toggles = null;
        EconomicParameters economicParameters = null;
        CropParameters cropParameters = null;
        if (runId == null) {
            toggles = preProcessorToggleService.getPreProcessorToggles(projectId);
            System.out.println("toggles in economic parameter" + toggles.getToggle().name());
            Optional<EconomicParameters> getEconomicParameters = economicParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT);
            Optional<CropParameters> getCropParameters = cropParametersRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT);
            economicParameters = getEconomicParameters.isPresent() ? getEconomicParameters.get() : null;
            cropParameters = getCropParameters.isPresent() ? getCropParameters.get() : null;

            //    System.out.println("economic parameter id in draft : " + economicParameters.getEconomicId());

        } else {
            System.out.println(runId);
            run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }
            toggles = run.getPreProcessorToggle();
            economicParameters = run.getEconomicParameters();
            cropParameters = run.getCropParameters();
        }
        if (economicParameters != null) {
            throw new ConflictException("already.exist");
        }

        EconomicParametersResponseDto economicParametersResponseDto = null;
        if (request.isEconomicParameter() == true) {
            Currency currency = currencyRepo.findById(request.getCurrencyId()).orElseThrow(() -> new ResourceNotFoundException("currency.not.found"));


            //  EconomicParameters economicParameter = economicModelMapper.economicRequestToEconomic(request);
            // ******* save economic parameters in db ************
            EconomicParameters economicParameter = EconomicParameters.builder().currency(currency).project(project).economicParameter(request.isEconomicParameter()).hourlySellingRates(request.getHourlySellingRates()).build();
            System.out.println("economic parameter before save id :" + economicParameter.getEconomicId());

            EconomicParameters savedEconomicParameters = economicParameterRepo.save(economicParameter);
            CurrencyResponse currencyResponse = CurrencyResponse.builder().currencyId(currency.getCurrencyId()).currency(currency.getCurrency()).build();

            economicParametersResponseDto = EconomicParametersResponseDto.builder().economicId(savedEconomicParameters.getEconomicId())
                    .hourlySellingRates(savedEconomicParameters.getHourlySellingRates())
                    .currency(currencyResponse)
                    .createdAt(savedEconomicParameters.getCreatedAt())
                    .updatedAt(savedEconomicParameters.getUpdatedAt())
                    .economicParameter(savedEconomicParameters.isEconomicParameter())
                    .build();

            System.out.println("saved economic parameter id :" + savedEconomicParameters.getEconomicId());

            // ************* save economic MultiCrop in db  ****************
            if (toggles.getToggle() != Toggle.ONLY_PV) {

                List<CropDto> cropDtoList = null;
                // validating economic parameters based on group
                Set<ConstraintViolation<EconomicParametersRequestDto>> economicParametersViolations = validator
                        .validate(request, ValidationGroups.EconomicParameterGroup.class);

                if (!economicParametersViolations.isEmpty()) {
                    ConstraintViolation<EconomicParametersRequestDto> violation = economicParametersViolations.iterator()
                            .next();
                    throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
                }
                for (EconomicMultiCropRequestDto economicMultiCrop : request.getEconomicMultiCrop()) {
                    if (economicMultiCrop.getMinInputCostOfCrop() > economicMultiCrop.getMaxInputCostOfCrop() || economicMultiCrop.getMinSellingCostOfCrop() > economicMultiCrop.getMaxSellingCostOfCrop() || economicMultiCrop.getMinReferenceYieldCost() > economicMultiCrop.getMaxReferenceYieldCost()) {
                        throw new AgriGeneralParametersException("min.value.validation", HttpStatus.BAD_REQUEST);
                    }
                }

                Set<Crop> cropSet = cropParameters.getCycles()
                        .stream()
                        .flatMap(cycle -> cycle.getBeds().stream())
                        .flatMap(bed -> bed.getCropBed().stream())
                        .map(CropBedSection::getCrop)
                        .collect(Collectors.toSet());

                cropDtoList = cropSet.stream().map((crop) -> {
                    CropDto cropDto = CropDto.builder().name(crop.getName()).id(crop.getId()).createdAt(crop.getCreatedAt()).updatedAt(crop.getUpdatedAt()).build();
                    return cropDto;
                }).collect(Collectors.toList());

                List<EconomicMultiCrop> economicParameterMultiCropList = request.getEconomicMultiCrop().stream().map((economicMultiCropRequest) -> {
                    // check to crop is exist or not
                    Crop crop = cropRepository.findById(economicMultiCropRequest.getCropId()).orElseThrow(() -> new ResourceNotFoundException("crop.not.found"));
                    // 2nd check
                    EconomicMultiCrop economicMultiCrop = economicMultiCropRepo.getEconomicMultiCropByCropAndEconomicParameterId(crop.getId(), savedEconomicParameters.getEconomicId());
                    if (economicMultiCrop != null) {
                        throw new ConflictException("crop.already.exist");
                    }
                    // 3rd check crop is present in crop set or not
                    if (!cropSet.contains(crop)) {
                        throw new UnprocessableException("crop.invalid");
                    }
                    economicMultiCrop = EconomicMultiCrop.builder()
                            .crop(crop)
                            .minInputCostOfCrop(economicMultiCropRequest.getMinInputCostOfCrop())
                            .maxInputCostOfCrop(economicMultiCropRequest.getMaxInputCostOfCrop())
                            .cultivationArea(economicMultiCropRequest.getCultivationArea())
                            .minSellingCostOfCrop(economicMultiCropRequest.getMinSellingCostOfCrop())
                            .maxSellingCostOfCrop(economicMultiCropRequest.getMaxSellingCostOfCrop())
                            .minReferenceYieldCost(economicMultiCropRequest.getMinReferenceYieldCost())
                            .maxReferenceYieldCost(economicMultiCropRequest.getMaxReferenceYieldCost())
                            .economicParameters(savedEconomicParameters)
                            .build();
                    EconomicMultiCrop savedEconomicMultiCrop = economicMultiCropRepo.save(economicMultiCrop);
                    return savedEconomicMultiCrop;
                }).collect(Collectors.toList());


                // response for economic multicrop
                List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameterMultiCropList.stream().map((economicMultiCrop) -> {
                    EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
                            .cropId(economicMultiCrop.getCrop().getId())
                            .crop(economicMultiCrop.getCrop())
                            .id(economicMultiCrop.getId())
                            .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
                            .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
                            .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
                            .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
                            .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
                            .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
                            .cultivationArea(economicMultiCrop.getCultivationArea())
                            .createdAt(economicMultiCrop.getCreatedAt())
                            .updatedAt(economicMultiCrop.getUpdatedAt())
                            .build();
                    return economicMultiCropResponse;
                }).collect(Collectors.toList());

                economicParametersResponseDto = EconomicParametersResponseDto
                        .builder()
                        .economicId(savedEconomicParameters.getEconomicId())
                        .cropDtoSet(cropDtoList)
                        .economicMultiCropResponseList(economicMultiCropResponseList)
                        .economicParameter(savedEconomicParameters.isEconomicParameter())
                        .currency(currencyResponse)
                        .hourlySellingRates(savedEconomicParameters.getHourlySellingRates())
                        .createdAt(savedEconomicParameters.getCreatedAt())
                        .updatedAt(savedEconomicParameters.getUpdatedAt())
                        .build();
                //         EconomicParametersResponseDto economicParametersResponse= economicParameterModelMapper.getEconomicParameterResponseDto();
            }
            if (runId != null) {
                run.setEconomicParameters(savedEconomicParameters);
                runsRepository.save(run);
                savedEconomicParameters.setStatus(PreProcessorStatus.CREATED);
                economicParameterRepo.save(savedEconomicParameters);
                // set runId , is master, clone Id
                economicParametersResponseDto.setRunId(runId);
                economicParametersResponseDto.setIsMaster(run.isMaster());
                economicParametersResponseDto.setCloneId(run.getCloneId());


            }
        }
        if (runId != null) {
            run = runService.updateRun(runId, toggles);
        } else {
            System.out.println("Enter in Create run Method in Economic Parameters");
            runId = createRun(projectId, toggles);
            System.out.println("Exit from Create Run  Method ");
        }
        EconomicParametersResponseDto economicParametersResponseDto1 = economicParametersResponseDto != null ? economicParametersResponseDto : null;
//        if (runId != null) {
//            economicParametersResponseDto1.setRunId(runId);
//            economicParametersResponseDto1.setIsMaster(run.isMaster());
//            economicParametersResponseDto1.setCloneId(run.getCloneId());
//        }
        return economicParametersResponseDto1;
    }

    //    @Transactional
    private Long createRun(Long projectId, PreProcessorToggle toggle) {
        System.out.println("Project Id " + projectId);
        System.out.println("PreProcessorToggle" + toggle.getId());
        Runs run = runService.createRun(projectId, toggle);
        //   System.out.println("runId 241" + run.getId());
        Long runId = null;
        if (run != null) {
            runId = run.getRunId();

            if (toggle.getToggle().name().equals("APV")) {
                System.out.println("Enter in Apv");
                updatePvParameterStatus(run.getPvParameters());
                updateCropParameterStatus(run.getCropParameters());
                updateAgriGenralParameterStatus(run.getAgriGeneralParameters());

            } else if (toggle.getToggle().name().equals("ONLY_PV")) {
                updatePvParameterStatus(run.getPvParameters());

            } else if (toggle.getToggle().name().equals("ONLY_AGRI")) {
                updateAgriGenralParameterStatus(run.getAgriGeneralParameters());
                updateCropParameterStatus(run.getCropParameters());
            }

//            } else {
//                updateCropParameterStatus(run.getCropParameters());
//
//            }

            if (run.getEconomicParameters() != null) {
                updateEconomicParameterStatus(run.getEconomicParameters());
            }
            updatePreProcessorToggleStatus(run.getPreProcessorToggle());
        } else {
            throw new UnprocessableException("run.not.created");
        }
        return runId;

    }

    private void updatePvParameterStatus(PvParameter pvParameters) {
        pvParameters.setStatus(PreProcessorStatus.CREATED);
        pvParameterRepository.save(pvParameters);
    }

    private void updateEconomicParameterStatus(EconomicParameters economicParameters) {
        System.out.println("update economic parameter status is created :");
        economicParameters.setStatus(PreProcessorStatus.CREATED);
        economicParameterRepo.save(economicParameters);
    }

    private void updateCropParameterStatus(CropParameters cropParameters) {
        cropParameters.setStatus(PreProcessorStatus.CREATED);
        cropParametersRepo.save(cropParameters);
    }

    private void updatePreProcessorToggleStatus(PreProcessorToggle preProcessorToggles) {
        preProcessorToggles.setPreProcessorStatus(PreProcessorStatus.CREATED);
        preProcessorToggleRepository.save(preProcessorToggles);
    }

    private void updateAgriGenralParameterStatus(AgriGeneralParameter agriGeneralParameter) {
        agriGeneralParameter.setStatus(PreProcessorStatus.CREATED);
        agriGeneralParameterRepo.save(agriGeneralParameter);
    }


    @Override
    public EconomicParametersResponseDto getEconomicParameters(Long projectId, Long userId, Long runId) {
        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
        Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

        if (project.getUserProfile().getUserProfileId() != userProfile.getUserProfileId()) {
            throw new UnprocessableException("project.mismatch");
        }
        EconomicParameters economicParameters = null;
        CropParameters cropParameters = null;
        if (runId == null) {
            Optional<EconomicParameters> economicParameter = economicParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT);

            economicParameters = economicParameter.isPresent() ? economicParameter.get() : null;

            Optional<CropParameters> getCropParameters = cropParametersRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT);
            cropParameters = getCropParameters.isPresent() ? getCropParameters.get() : null;


        } else {
            Runs run = runsRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));
            economicParameters = run.getEconomicParameters();
            cropParameters = run.getCropParameters();
        }

        List<CropDto> cropDtoList = null;
        if (cropParameters != null) {
            System.out.println("crop parameters is not null");
            Set<Crop> cropSet = cropParameters.getCycles()
                    .stream()
                    .flatMap(cycle -> cycle.getBeds().stream())
                    .flatMap(bed -> bed.getCropBed().stream())
                    .map(CropBedSection::getCrop)
                    .collect(Collectors.toSet());

            cropDtoList = cropSet.stream().map((crop) -> {
                CropDto cropDto = CropDto.builder().name(crop.getName()).id(crop.getId()).crop(crop).createdAt(crop.getCreatedAt()).updatedAt(crop.getUpdatedAt()).build();
                return cropDto;
            }).collect(Collectors.toList());
            System.out.println("crop Dto list size is :" + cropDtoList.size());


        }
        if (economicParameters == null) {
        	List<EconomicMultiCropResponse> economicMultiCropResponseList = new ArrayList<>();
        	
			if (cropDtoList != null && !cropDtoList.isEmpty()) {
				for (CropDto crop : cropDtoList) {
					if (!crop.getCrop().getHide()) {
						EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
								.cropId(crop.getId()).crop(crop.getCrop()).build();
						economicMultiCropResponseList.add(economicMultiCropResponse);
					}
				}

			}
            return EconomicParametersResponseDto.builder().cropDtoSet(cropDtoList)
            		.economicMultiCropResponseList(economicMultiCropResponseList)
            		.build();
        }
        
     // Step 1: Extract crop details into a final cropDtoListCopy
        final List<CropDto> cropDtoListCopy; // Make it effectively final
        if (cropParameters != null) {
            System.out.println("Crop parameters are not null");

            Set<Crop> cropSet = cropParameters.getCycles()
                    .stream()
                    .flatMap(cycle -> cycle.getBeds().stream())
                    .flatMap(bed -> bed.getCropBed().stream())
                    .map(CropBedSection::getCrop)
                    .collect(Collectors.toSet());

            cropDtoListCopy = cropSet.stream()
                    .map(crop -> CropDto.builder()
                            .name(crop.getName())
                            .id(crop.getId())
                            .crop(crop)
                            .createdAt(crop.getCreatedAt())
                            .updatedAt(crop.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());

            System.out.println("Crop DTO list size: " + cropDtoListCopy.size());
        } else {
            cropDtoListCopy = Collections.emptyList(); // Ensure it's always initialized
        }

        // Step 2: Store cropDtoList IDs in a final set for efficient lookup
        final Set<Long> cropDtoIds = cropDtoListCopy.stream()
                .map(CropDto::getId)
                .collect(Collectors.toSet());

        // Step 3: Process economicMultiCrop and create EconomicMultiCropResponse list
        List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameters.getEconomicMultiCrop().stream()
        	    .filter(economicMultiCrop -> !economicMultiCrop.getCrop().getHide()) // Exclude hidden crops
        	    .map(economicMultiCrop -> {
        	        Long cropId = economicMultiCrop.getCrop().getId();

        	        if (cropDtoIds.contains(cropId)) {
        	            // ✅ If cropId exists in cropDtoIds, return full response
        	            return EconomicMultiCropResponse.builder()
        	                    .cropId(cropId)
        	                    .crop(economicMultiCrop.getCrop())
        	                    .id(economicMultiCrop.getId())
        	                    .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
        	                    .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
        	                    .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
        	                    .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
        	                    .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
        	                    .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
        	                    .cultivationArea(economicMultiCrop.getCultivationArea())
        	                    .createdAt(economicMultiCrop.getCreatedAt())
        	                    .updatedAt(economicMultiCrop.getUpdatedAt())
        	                    .build();
        	        } else {
//        	            //  If cropId is not found in cropDtoIds, check in cropDtoListCopy
//        	            Optional<CropDto> matchingCropDto = cropDtoListCopy.stream()
////        	                    .filter(cropDto -> cropDto.getId().equals(cropId))
//        	                    .findFirst();
//
//        	            return matchingCropDto.map(cropDto -> EconomicMultiCropResponse.builder()
//        	                    .cropId(cropDto.getId()) // Use ID from cropDtoListCopy
//        	                    .crop(cropDto.getCrop()) // Use crop details from cropDtoListCopy
//        	                    .build()).orElse(null);
        	        	return null;
        	        }
        	    })
        	    .filter(Objects::nonNull) // Remove null responses
        	    .collect(Collectors.toList());
        
        Set<Long> economicMultiCropIds = economicMultiCropResponseList.stream()
                .map(EconomicMultiCropResponse::getCropId)
                .collect(Collectors.toSet());

        for (CropDto crop : cropDtoListCopy) {
            if (!economicMultiCropIds.contains(crop.getId())) {
                // 🌱 New crop found in cropDtoSet but missing in economicMultiCropResponseList → Add it
                EconomicMultiCropResponse newCropResponse = EconomicMultiCropResponse.builder()
                        .cropId(crop.getId())
                        .crop(crop.getCrop())
                        .build();
                economicMultiCropResponseList.add(newCropResponse);
            }
        }

        	System.out.println("EconomicMultiCropResponse List Size: " + economicMultiCropResponseList.size());


        
//        // response for economic multicrop
//        List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameters.getEconomicMultiCrop().stream()
//        		.filter(economicMultiCrop -> !economicMultiCrop.getCrop().getHide()).map((economicMultiCrop) -> {
//            EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
//                    .cropId(economicMultiCrop.getCrop().getId())
//                    .crop(economicMultiCrop.getCrop())
//                    .id(economicMultiCrop.getId())
//                    .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
//                    .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
//                    .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
//                    .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
//                    .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
//                    .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
//                    .cultivationArea(economicMultiCrop.getCultivationArea())
//                    .createdAt(economicMultiCrop.getCreatedAt())
//                    .updatedAt(economicMultiCrop.getUpdatedAt())
//                    .build();
//            return economicMultiCropResponse;
//        }).collect(Collectors.toList());

        CurrencyResponse currencyResponse = CurrencyResponse.builder()
                .currencyId(economicParameters.getCurrency().getCurrencyId())
                .currency(economicParameters.getCurrency().getCurrency())
                .build();

        EconomicParametersResponseDto economicParametersResponseDto = EconomicParametersResponseDto
                .builder()
                .economicId(economicParameters.getEconomicId())
                .cropDtoSet(cropDtoList)
                .economicMultiCropResponseList(economicMultiCropResponseList)
                .economicParameter(economicParameters.isEconomicParameter())
                .currency(currencyResponse)
                .hourlySellingRates(economicParameters.getHourlySellingRates())
                .createdAt(economicParameters.getCreatedAt())
                .updatedAt(economicParameters.getUpdatedAt())
                .build();

        if (runId != null) {
            Runs run = runsRepository.findById(runId)
                    .orElseThrow(() -> new AgriGeneralParametersException("run.not.found", HttpStatus.NOT_FOUND));
            economicParametersResponseDto.setRunId(runId);
            economicParametersResponseDto.setIsMaster(run.isMaster());
            economicParametersResponseDto.setCloneId(run.getCloneId());

        }
        return economicParametersResponseDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EconomicParametersResponseDto updateEconomicParameters(Long projectId, EconomicParametersRequestDto
            request, Long economicParameterId, Long userId, Long runId) {

        Projects project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        UserProfile userProfile = userProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        if (project.getUserProfile().getUserProfileId() != userProfile.getUserProfileId()) {
            throw new UnprocessableException("project.mismatch");
        }
        EconomicParameters economicParameters = economicParameterRepo.findById(economicParameterId).orElseThrow(() -> new ResourceNotFoundException("economicParameter.not.found"));
        System.out.println("economic parameter project id" + economicParameters.getProject().getProjectId() + "project id :" + projectId);
        if (economicParameters.getProject().getProjectId() != projectId) {
            throw new ResourceNotFoundException("economicParameter.not.found");
        }


//      update or create run

        Runs run = null;
        PreProcessorToggle toggles = null;
        if (runId == null) {
            toggles = preProcessorToggleService.getPreProcessorToggles(projectId);
            System.out.println("toggles in crop parameter" + toggles.getToggle().name());
        } else {
            run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new UnprocessableException("run.not.holding");
            }

            toggles = run.getPreProcessorToggle();
        }

        if (runId != null) {
            run = runService.updateRun(runId, toggles);
            System.out.println("run id is :" + run.getRunId());
        } else {
            runId = createRun(projectId, toggles);
        }

        if (!request.isEconomicParameter()) {
            System.out.println("delete economic parameter " + economicParameterId);
            economicParameterRepo.deleteById(economicParameterId);
            run.setEconomicParameters(null);
            runsRepository.save(run);
            return null;
        }

        // validating economic parameters based on group
        Set<ConstraintViolation<EconomicParametersRequestDto>> economicParametersViolations = validator
                .validate(request, ValidationGroups.EconomicParameterGroup.class);

        if (!economicParametersViolations.isEmpty()) {
            ConstraintViolation<EconomicParametersRequestDto> violation = economicParametersViolations.iterator()
                    .next();
            throw new AgriGeneralParametersException(violation.getMessage(), HttpStatus.BAD_REQUEST);
        }

        for (EconomicMultiCropRequestDto economicMultiCrop : request.getEconomicMultiCrop()) {
            if (economicMultiCrop.getMinInputCostOfCrop() > economicMultiCrop.getMaxInputCostOfCrop() || economicMultiCrop.getMinSellingCostOfCrop() > economicMultiCrop.getMaxSellingCostOfCrop() || economicMultiCrop.getMinReferenceYieldCost() > economicMultiCrop.getMaxReferenceYieldCost()) {
                throw new AgriGeneralParametersException("min.value.validation", HttpStatus.BAD_REQUEST);
            }
        }
        Currency currency = currencyRepo.findById(request.getCurrencyId()).orElseThrow(() -> new ResourceNotFoundException("currency.not.found"));
        economicParameters.setCurrency(currency);
        economicParameters.setEconomicParameter(request.isEconomicParameter());
        economicParameters.setProject(project);
        economicParameters.setHourlySellingRates(request.getHourlySellingRates());
        EconomicParameters updatedEconomicParameters = economicParameterRepo.save(economicParameters);

        List<EconomicMultiCrop> economicParameterMultiCropList = request.getEconomicMultiCrop().stream().map((economicMultiCropRequest) -> {
            // check to crop is exist or not
            Crop crop = cropRepository.findById(economicMultiCropRequest.getCropId()).orElseThrow(() -> new ResourceNotFoundException("crop.not.found"));
//            // 2nd check
//            EconomicMultiCrop economicMultiCrop = economicMultiCropRepo.getEconomicMultiCropByCropAndEconomicParameterId(crop.getId(), savedEconomicParameters.getEconomicId());
//            if (economicMultiCrop != null) {
//                throw new ConflictException("crop.already.exist");
//            }
//            // 3rd check crop is present in crop set or not
//            if (!cropSet.contains(crop)) {
//                throw new UnprocessableException("crop.invalid");
//            }
            EconomicMultiCrop economicMultiCrop = EconomicMultiCrop.builder()
                    .id(economicMultiCropRequest.getId())
                    .crop(crop)
                    .minInputCostOfCrop(economicMultiCropRequest.getMinInputCostOfCrop())
                    .maxInputCostOfCrop(economicMultiCropRequest.getMaxInputCostOfCrop())
                    .cultivationArea(economicMultiCropRequest.getCultivationArea())
                    .minSellingCostOfCrop(economicMultiCropRequest.getMinSellingCostOfCrop())
                    .maxSellingCostOfCrop(economicMultiCropRequest.getMaxSellingCostOfCrop())
                    .minReferenceYieldCost(economicMultiCropRequest.getMinReferenceYieldCost())
                    .maxReferenceYieldCost(economicMultiCropRequest.getMaxReferenceYieldCost())
                    .economicParameters(updatedEconomicParameters)
                    .build();
            EconomicMultiCrop savedEconomicMultiCrop = economicMultiCropRepo.save(economicMultiCrop);
            return savedEconomicMultiCrop;
        }).collect(Collectors.toList());


        // response for economic multicrop
        List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameterMultiCropList.stream().map((economicMultiCrop) -> {
            EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
                    .cropId(economicMultiCrop.getCrop().getId())
                    .crop(economicMultiCrop.getCrop())     
                    .id(economicMultiCrop.getId())
                    .minInputCostOfCrop(economicMultiCrop.getMinInputCostOfCrop())
                    .maxInputCostOfCrop(economicMultiCrop.getMaxInputCostOfCrop())
                    .minSellingCostOfCrop(economicMultiCrop.getMinSellingCostOfCrop())
                    .maxSellingCostOfCrop(economicMultiCrop.getMaxSellingCostOfCrop())
                    .minReferenceYieldCost(economicMultiCrop.getMinReferenceYieldCost())
                    .maxReferenceYieldCost(economicMultiCrop.getMaxReferenceYieldCost())
                    .cultivationArea(economicMultiCrop.getCultivationArea())
                    .createdAt(economicMultiCrop.getCreatedAt())
                    .updatedAt(economicMultiCrop.getUpdatedAt())
                    .build();
            return economicMultiCropResponse;
        }).collect(Collectors.toList());
        CurrencyResponse currencyResponse = CurrencyResponse.builder().currencyId(currency.getCurrencyId()).currency(currency.getCurrency()).build();

        EconomicParametersResponseDto economicParametersResponseDto = EconomicParametersResponseDto
                .builder()
                .economicId(updatedEconomicParameters.getEconomicId())
                .economicMultiCropResponseList(economicMultiCropResponseList)
                .economicParameter(updatedEconomicParameters.isEconomicParameter())
                .currency(currencyResponse)
                .hourlySellingRates(updatedEconomicParameters.getHourlySellingRates())
                .createdAt(updatedEconomicParameters.getCreatedAt())
                .updatedAt(updatedEconomicParameters.getUpdatedAt())
                .build();

        if (runId != null) {
            economicParametersResponseDto.setRunId(runId);
            economicParametersResponseDto.setIsMaster(run.isMaster());
            economicParametersResponseDto.setCloneId(run.getCloneId());

        }
        return economicParametersResponseDto;
        //  return economicModelMapper.economicToEconomicResponse(updatedEconomicParameters);
    }
}