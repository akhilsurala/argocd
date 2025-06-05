package com.sunseed.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.*;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.Toggle;
import com.sunseed.exceptions.HiddenDataException;
import com.sunseed.exceptions.InvalidDataException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.PostprocessingHelper;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.requestDTO.*;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.service.SimulationService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {
    private final ProjectsRepository projectRepo;
    private final RunsRepository runRepo;
    private final WebClientHelper webClientHelper;
    private final ObjectMapper objectMapper;
    private final PostprocessingHelper postprocessingHelper;
//    private String simulationUrl = "http://localhost:8090/simtool/v1/simulation";
    @Value("${simulation.url}")
    private String simulationUrl;

    @Override
    public List<SimulationResponseDto> postSimulation(List<Long> runIds, Long projectId) {
        Optional<Projects> project = projectRepo.findById(projectId);
        if (project.isEmpty()) throw new ResourceNotFoundException("project.not.found");
        // get user profile id from project
        Long userProfileId = project.get().getUserProfile().getUserProfileId();
        SimulationRequest simulationRequest = new SimulationRequest();
        simulationRequest.setProjectId(projectId);
        simulationRequest.setUserProfileId(userProfileId);

        // get latitude and longitude from project
        String latitude = project.get().getLatitude();
        String longitude = project.get().getLongitude();
        System.out.println("longitude of project :" + longitude + " " + "latitude of project :" + longitude);

        List<RunSimulationRequestDto> runSimulationRequestDtos = new ArrayList<>();
        ArrayList<String> hideData = new ArrayList<>();

        // *********** start fetching run id using loop ******************
        System.out.println("start loop for create request dtos : ");
        for (Long runId : runIds) {
            Runs run = runRepo.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found.holding"));
            if (run.getRunStatus() != RunStatus.HOLDING) {
                throw new ResourceNotFoundException("run.not.found.holding");
            }
            if (!run.isCanSimulate() || !run.isActive() || run.isSimulated()) {
                throw new UnprocessableException("run.not.simulate");
            }


            //***********  preProcessor toggle request   *****************
            PreProcessorToggle preProcessorToggle = run.getPreProcessorToggle();
            // Specify the desired time zone
            ZoneId zoneId = ZoneId.systemDefault(); // Use system default time zone
            PreProcessorToggleRequestDto preProcessorToggleRequestDto = PreProcessorToggleRequestDto.builder().id(preProcessorToggle.getId()).azimuth(preProcessorToggle.getAzimuth()).status(preProcessorToggle.getPreProcessorStatus()).lengthOfOneRow(preProcessorToggle.getLengthOfOneRow()).toggle(preProcessorToggle.getToggle()).soilType(preProcessorToggle.getSoilType()).pitchOfRows(preProcessorToggle.getPitchOfRows()).createdAt(LocalDateTime.ofInstant(preProcessorToggle.getCreatedAt(), zoneId)).updatedAt(preProcessorToggle.getUpdatedAt() != null ? LocalDateTime.ofInstant(preProcessorToggle.getUpdatedAt(), zoneId) : null).build();
            System.out.println("preProcessorToggleRequestDto is :" + preProcessorToggleRequestDto);
            
            boolean hide = preProcessorToggle.getSoilType().getHide();
            System.out.println("Hide : " + hide);
            
            if(hide == true) {
//            	hideData.add(preProcessorToggle.getSoilType().getSoilName());
            	hideData.add("Soil");
            }

            // ********** run payload object  -- runPayload ****************
            RunSimulationRequestDto runSimulationRequestDto = new RunSimulationRequestDto();
            runSimulationRequestDto.setId(runId);
            runSimulationRequestDto.setLongitude(longitude);
            runSimulationRequestDto.setLatitude(latitude);
            runSimulationRequestDto.setPreProcessorToggles(preProcessorToggleRequestDto);
            runSimulationRequestDto.setRunStatus(run.getRunStatus());
            runSimulationRequestDto.setCreatedAt(LocalDateTime.ofInstant(run.getCreatedAt(), zoneId));

            runSimulationRequestDto.setUpdatedAt(run.getUpdatedAt() != null ? LocalDateTime.ofInstant(run.getUpdatedAt(), zoneId) : null);

            if (preProcessorToggle.getToggle() == Toggle.APV || preProcessorToggle.getToggle() == Toggle.ONLY_PV) {
                // ************ set pv parameters **************
                PvParameter pvParameter = run.getPvParameters();
                PvModule pvModule = run.getPvParameters().getPvModule();
                
                boolean pvModuleHide = run.getPvParameters().getPvModule().getHide();
                boolean modeOfPvOperationHide = run.getPvParameters().getModeOfOperationId().getHide();
                if(pvModuleHide == true) {
//                	hideData.add(run.getPvParameters().getPvModule().getModuleName());
                	hideData.add("Pv Module");
                }
                if(modeOfPvOperationHide == true) {
//                	hideData.add(run.getPvParameters().getModeOfOperationId().getModeOfOperation());
                	hideData.add("Mode Of Pv Operation");
                }
                PvParametersSimulationRequestDto pvParametersSimulationRequestDto = new PvParametersSimulationRequestDto();
                pvParametersSimulationRequestDto.setId(pvParameter.getId());
                pvParametersSimulationRequestDto.setProjectId(pvParameter.getProject().getProjectId());
                pvParametersSimulationRequestDto.setRunId(runId);
                pvParametersSimulationRequestDto.setStatus(pvParameter.getStatus());
                pvParametersSimulationRequestDto.setTiltIfFt(pvParameter.getTiltIfFt());
                pvParametersSimulationRequestDto.setGapBetweenModules(pvParameter.getGapBetweenModules());
                pvParametersSimulationRequestDto.setMaxAngleOfTracking(pvParameter.getMaxAngleOfTracking());
                pvParametersSimulationRequestDto.setModeOfOperationId(pvParameter.getModeOfOperationId());
                pvParametersSimulationRequestDto.setModuleMaskPattern(pvParameter.getModuleMaskPattern());
                pvParametersSimulationRequestDto.setHeight(pvParameter.getHeight());

                //******* set pv module ******************
                pvParametersSimulationRequestDto.setPvModule(pvParameter.getPvModule());
                //  PvModuleSimulationDto pvModuleSimulationDto = PvModuleSimulationDto.builder().moduleType(pvParameter.getPvModule().getModuleType()).id(pvParameter.getPvModule().getId()).pdc0(0.2).length(pvParameter.getPvModule().getLength()).width(pvParameter.getPvModule().getWidth()).gamma_pdc(0.5).temp_ref(25L).build();
                // pvParametersSimulationRequestDto.setPvModule(pvModuleSimulationDto);

                // ****** module configuration ****************
                List<PvModuleConfigurationRequestDto> moduleConfigList = pvParameter.getModuleConfig().stream().map((moduleConfigs) -> {
                    return PvModuleConfigurationRequestDto.builder().id(moduleConfigs.getId()).moduleConfig(moduleConfigs.getModuleConfig()).build();
                }).collect(Collectors.toList());
                
                List<String> moduleConfigListHide = pvParameter.getModuleConfig().stream()
                	    .filter(PvModuleConfiguration::getHide) // Only keep items where isHide is true
                	    .map(PvModuleConfiguration::getModuleConfig) // Extract moduleConfig names
                	    .collect(Collectors.toList()); // Collect into a List
                
                if((!moduleConfigListHide.isEmpty()) || moduleConfigListHide.size() != 0) {
                	hideData.add("Module COnfiguration");
                }


                pvParametersSimulationRequestDto.setModuleConfigs(moduleConfigList);
                runSimulationRequestDto.setPvParameters(pvParametersSimulationRequestDto);

                System.out.println("pvParamterSimulationReqestDto :" + pvParametersSimulationRequestDto);
            }
            if (preProcessorToggle.getToggle() == Toggle.APV || preProcessorToggle.getToggle() == Toggle.ONLY_AGRI) {
                // *************** crop Parameters ******************
                CropParameters cropParameters = run.getCropParameters();
                AgriGeneralParameter agriGeneralParameter = run.getAgriGeneralParameters();
                boolean irrigationHide = agriGeneralParameter.getIrrigationId().getHide();
                if(irrigationHide == true) {
//                	hideData.add(agriGeneralParameter.getIrrigationId().getIrrigationType());
                	hideData.add("Type Of Irrigation");
                }
                CropParametersSimulationDto cropParametersSimulationDto = new CropParametersSimulationDto();
                cropParametersSimulationDto.setId(cropParameters.getId());
                cropParametersSimulationDto.setRunId(runId);
//                cropParametersSimulationDto.setSoilType(agriGeneralParameter.getSoilType());
                cropParametersSimulationDto.setTypeOfIrrigation(agriGeneralParameter.getIrrigationId());
                cropParametersSimulationDto.setMulching(agriGeneralParameter.getIsMulching());

                //  ************** protection  layer and height list ***********
                List<ProtectionLayerSimulationRequestDto> protectionLayerList = agriGeneralParameter.getAgriPvProtectionHeight().stream().map((pvProtectionHeight) -> {
                    ProtectionLayerSimulationRequestDto protectionLayer = new ProtectionLayerSimulationRequestDto();
                    protectionLayer.setProtectionLayerType(pvProtectionHeight.getProtectionLayer().getProtectionLayerName());
                    protectionLayer.setHeight(pvProtectionHeight.getProtectionHeight());
                    protectionLayer.setF1(pvProtectionHeight.getProtectionLayer().getF1());
                    protectionLayer.setF2(pvProtectionHeight.getProtectionLayer().getF2());
                    protectionLayer.setF3(pvProtectionHeight.getProtectionLayer().getF3());
                    protectionLayer.setF4(pvProtectionHeight.getProtectionLayer().getF4());
                    protectionLayer.setHide(pvProtectionHeight.getProtectionLayer().getHide());
                    protectionLayer.setIsActive(pvProtectionHeight.getProtectionLayer().getIsActive());
                    protectionLayer.setPolysheets(pvProtectionHeight.getProtectionLayer().getPolysheets());
                    protectionLayer.setDiffusionFraction(pvProtectionHeight.getProtectionLayer().getDiffusionFraction());
                    protectionLayer.setLinkToTexture(pvProtectionHeight.getProtectionLayer().getLinkToTexture());
                    protectionLayer.setTransmissionPercentage(pvProtectionHeight.getProtectionLayer().getTransmissionPercentage());
                    protectionLayer.setVoidPercentage(pvProtectionHeight.getProtectionLayer().getVoidPercentage());
                    protectionLayer.setOpticalProperty(pvProtectionHeight.getProtectionLayer().getOpticalProperty());

                    return protectionLayer;
                }).collect(Collectors.toList());
                cropParametersSimulationDto.setProtectionLayer(protectionLayerList);
                
             // Collect ProtectionLayer names where isHide = true
                List<String> hiddenProtectionLayerNames = agriGeneralParameter.getAgriPvProtectionHeight().stream()
                    .map(pvProtectionHeight -> pvProtectionHeight.getProtectionLayer()) // Extract ProtectionLayer object
                    .filter(protectionLayer -> Boolean.TRUE.equals(protectionLayer.getHide())) // Filter where isHide = true
                    .map(ProtectionLayer::getProtectionLayerName) // Extract names
                    .collect(Collectors.toList());
                
                if((!hiddenProtectionLayerNames.isEmpty()) || hiddenProtectionLayerNames.size() != 0) {
                	hideData.add("Shade Net");
                }


                // ************* create list of cycle ***********
                List<CycleSimulationRequestDto> cyclesList = cropParameters.getCycles().stream().map((cycle) -> {
                    CycleSimulationRequestDto cycles = new CycleSimulationRequestDto();
                    //  cycles.setCycleStartDate(LocalDate.parse("2024-01-01"));
                    cycles.setCycleStartDate(cycle.getStartDate());
                    cycles.setCycleName(cycle.getName());
                    System.out.println("cycle start date :" + cycle.getStartDate());
                    Integer maxDuration = postprocessingHelper.findMaxDuration(cycle.getBeds());
                    cycles.setDuration(maxDuration);

                    // ************** if inter bed pattern is present then set  ********
                    if (cycle.getInterBedPattern() != null && cycle.getInterBedPattern().size() > 1) {
                        cycles.setInterBedPattern(cycle.getInterBedPattern());
                    } else {
                        cycles.setInterBedPattern(List.of(cycle.getBeds().get(0).getBedName()));
                    }
                    List<CycleBedDetailsSimulationDto> cycleBedDetailsSimulationDtos = cycle.getBeds().stream().map((bed) -> {
                        CycleBedDetailsSimulationDto cycleBedDetails = new CycleBedDetailsSimulationDto();
                        cycleBedDetails.setBedName(bed.getBedName());

                        List<CropDetailsSimulationRequestDto> cropDetails = bed.getCropBed().stream().map((bedSection) -> {
                            CropDetailsSimulationRequestDto cropDetailsSimulationRequestDto = new CropDetailsSimulationRequestDto();
                            cropDetailsSimulationRequestDto.setF1(bedSection.getCrop().getF1());
                            cropDetailsSimulationRequestDto.setF2(bedSection.getCrop().getF2());
                            cropDetailsSimulationRequestDto.setF3(bedSection.getCrop().getF3());
                            cropDetailsSimulationRequestDto.setF4(bedSection.getCrop().getF4());
                            cropDetailsSimulationRequestDto.setF5(bedSection.getCrop().getF5());
                            cropDetailsSimulationRequestDto.setO1(bedSection.getO1());
                            cropDetailsSimulationRequestDto.setS1(bedSection.getS1());
                            //   cropDetailsSimulationRequestDto.setS1(100L);
                            cropDetailsSimulationRequestDto.setO2(bedSection.getO2());
                            cropDetailsSimulationRequestDto.setCropName(bedSection.getCrop().getName());
                            double duration = bedSection.getCrop().getDuration(); // duration in days
                            double stretch = bedSection.getStretch(); // stretch in percentage
                            double effectiveDuration = duration * (1 + (stretch / 100));
                            cropDetailsSimulationRequestDto.setDuration((int) effectiveDuration);
                            cropDetailsSimulationRequestDto.setMaxStage(bedSection.getCrop().getMaxStage());
                            cropDetailsSimulationRequestDto.setMinStage(bedSection.getCrop().getMinStage());
                            cropDetailsSimulationRequestDto.setHarvestDays(bedSection.getCrop().getHarvestDays());
                            cropDetailsSimulationRequestDto.setRequiredDLI(bedSection.getCrop().getRequiredDLI());
                            cropDetailsSimulationRequestDto.setRequiredPPFD(bedSection.getCrop().getRequiredPPFD());
                            cropDetailsSimulationRequestDto.setOpticalProperty(bedSection.getCrop().getOpticalProperty());
                            cropDetailsSimulationRequestDto.setFarquharParameter(bedSection.getCrop().getFarquharParameter());
                            cropDetailsSimulationRequestDto.setStomatalParameter(bedSection.getCrop().getStomatalParameter());
                            cropDetailsSimulationRequestDto.setCropLabel(bedSection.getCrop().getCropLabel());
                            cropDetailsSimulationRequestDto.setHasPlantActualDate(bedSection.getCrop().getHasPlantActualDate());
                            cropDetailsSimulationRequestDto.setPlantActualStartDate(bedSection.getCrop().getPlantActualStartDate());
                            cropDetailsSimulationRequestDto.setPlantMaxAge(bedSection.getCrop().getPlantMaxAge());
                            cropDetailsSimulationRequestDto.setMaxPlantsPerBed(bedSection.getCrop().getMaxPlantsPerBed());
                            return cropDetailsSimulationRequestDto;
                        }).collect(Collectors.toList());

                        // ******** set crop details in bed details ************
                        cycleBedDetails.setCropDetails(cropDetails);
                        return cycleBedDetails;

                    }).collect(Collectors.toList());
                    
//                    List<String> hiddenCropNames = cycle.getBeds().stream()
//                    	    .flatMap(bed -> bed.getCropBed().stream()) // Flatten to stream all cropBed entries
//                    	    .map(bedSection -> bedSection.getCrop()) // Extract Crop object
//                    	    .filter(crop -> Boolean.TRUE.equals(crop.getHide())) // Filter crops where isHide = true
//                    	    .map(Crop::getName) // Extract crop names
//                    	    .collect(Collectors.toList());
//                    
//                    if((!hiddenCropNames.isEmpty()) || hiddenCropNames.size() != 0) {
//                    	hideData.add("Crop");
//                    }


                    // ****** add cyclebeddetailssimulationdtos list in cycle ********
                    cycles.setCycleBedDetails(cycleBedDetailsSimulationDtos);
                    return cycles;
                }).collect(Collectors.toList());
                cropParametersSimulationDto.setCycles(cyclesList);
                
             // Collect all hidden crop names before processing cycles
                List<String> hiddenCropNames = cropParameters.getCycles().stream()
                        .flatMap(cycle -> cycle.getBeds().stream()) // Flatten beds across cycles
                        .flatMap(bed -> bed.getCropBed().stream()) // Flatten crop beds across beds
                        .map(bedSection -> bedSection.getCrop()) // Extract Crop object
                        .filter(crop -> Boolean.TRUE.equals(crop.getHide())) // Filter crops where isHide = true
                        .map(Crop::getName) // Extract crop names
                        .collect(Collectors.toList());

                // If any hidden crops are found, add "Crop" to hideData
                if((!hiddenCropNames.isEmpty()) || hiddenCropNames.size() != 0) {
                	hideData.add("Crop");
                }



                cropParametersSimulationDto.setSimulationTimeOfYear(LocalDateTime.parse("2021-08-01T00:00:00"));
                //    cropParametersSimulationDto.setSimulationTimeOfYear(LocalDate("2021-08-01T00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

                //    cropParametersSimulationDto.setBedParameter(agriGeneralParameter.getBedParameter());

                //************** set  bed parameters  ********************//
                BedParametersSimulationRequestDto bedParametersSimulationRequestDto = BedParametersSimulationRequestDto.builder().bedAzimuth(agriGeneralParameter.getBedParameter().getBedAzimuth()).bedHeight(agriGeneralParameter.getBedParameter().getBedHeight()).bedWidth(agriGeneralParameter.getBedParameter().getBedWidth()).bedAngle(agriGeneralParameter.getBedParameter().getBedAngle()).noOfBeds(agriGeneralParameter.getBedParameter().getBedcc()).build();
                cropParametersSimulationDto.setBedParameter(bedParametersSimulationRequestDto);

//cropParametersSimulationDto.setBedSection();
                //   cropParametersSimulationDto.setProtectionLayer();
                cropParametersSimulationDto.setIrrigationType(agriGeneralParameter.getIrrigationId().getIrrigationType());
                cropParametersSimulationDto.setStatus(cropParameters.getStatus());
                cropParametersSimulationDto.setTempControl(agriGeneralParameter.getTempControl());
                cropParametersSimulationDto.setTrail(agriGeneralParameter.getTrail());
                cropParametersSimulationDto.setMinTemp(agriGeneralParameter.getMinTemp());
                cropParametersSimulationDto.setMaxTemp(agriGeneralParameter.getMaxTemp());
                cropParametersSimulationDto.setStartPointOffset(agriGeneralParameter.getBedParameter().getStartPointOffset());
                cropParametersSimulationDto.setCreatedAt(LocalDateTime.ofInstant(cropParameters.getCreatedAt(), zoneId));

                cropParametersSimulationDto.setUpdatedAt(cropParameters.getUpdatedAt() != null ? LocalDateTime.ofInstant(cropParameters.getUpdatedAt(), zoneId) : null);

                runSimulationRequestDto.setCropParameters(cropParametersSimulationDto);

            }


            //*************** add runPayload in list **********
            runSimulationRequestDtos.add(runSimulationRequestDto);

            // ************ end loop ********************
        }
        System.out.println("latitude in runSimulationRequestDtos" + runSimulationRequestDtos.get(0).getLatitude());
        //  Map<String, Object> map = new HashMap<>();

        //map.put("runPayLoad", runSimulationRequestDtos);
        //simulationRequest.setRunPayload(map);
        simulationRequest.setRunPayload(runSimulationRequestDtos);
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(simulationRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("simulationRequest payload :" + jsonString);
        
        if(!hideData.isEmpty() || hideData.size() != 0) {
        	throw new HiddenDataException("The following master data are disable from the admin side: " + String.join(", ", hideData));
        }

        // call webclient method
        List<SimulationResponseDto> simulationResponse = webClientHelper.startSimulation(simulationUrl + "/simtool/v1/simulation", simulationRequest);

//return null;
        return simulationResponse;
    }


    // ********* update simulated run status ****************
    @Override
    public Map<String, List<SimulationTaskStatusDto>> updateStatus(String status, Long simulatedId) {
        if (status == null || simulatedId == null) {
            throw new InvalidDataException("invalid.data");
        }
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("status", status);
        requestMap.put("simulationId", simulatedId);
        Map<String, List<SimulationTaskStatusDto>> res = webClientHelper.updateSimulateRun(simulationUrl + "/simtool/v1/simulation" + "/status", requestMap);
        System.out.println("data :" + res);
        System.out.println("update result :" + res);
        return res;
    }


}