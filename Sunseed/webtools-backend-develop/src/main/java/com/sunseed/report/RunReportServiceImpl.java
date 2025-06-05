package com.sunseed.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sunseed.entity.EconomicParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.Crop;
import com.sunseed.entity.CropBedSection;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.entity.PvParameter;
import com.sunseed.enums.RunBayStatus;
import com.sunseed.enums.RunStatus;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
import com.sunseed.model.responseDTO.CropDto;
import com.sunseed.model.responseDTO.CropParametersResponseDto;
//import com.sunseed.model.responseDTO.EconomicMultiCropResponse;
import com.sunseed.model.responseDTO.PvParametersResponseDto;
import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
import com.sunseed.model.responseDTO.pvParameters.PvModuleResponse;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.serviceImpl.PostprocessingDetailsServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunReportServiceImpl implements RunReportService {

    private final ProjectsRepository projectRepository;
    private final RunsRepository runRepository;
    private final EntityManager entityManager;
    private final PostprocessingDetailsServiceImpl postprocessingDetailsServiceImpl;

	@Override
	public Map<String, Object> getRunReport(Long projectId, List<Long> runIds, Long userId) {

		if (userId == null || userId <= 0)
			throw new UnAuthorizedException("user.not.found");
		
		if (runIds == null || runIds.isEmpty()) {
	        throw new IllegalArgumentException("runIds cannot be null or empty");
	    }

		// getting list of runStatus for holding and running bay
		List<RunStatus> holdingBayRunStatusList = RunBayStatus.getListOfRunStatus(RunBayStatus.HOLDING.getValue());
		List<RunStatus> runningBayRunStatusList = RunBayStatus.getListOfRunStatus(RunBayStatus.RUNNING.getValue());

		// getting the projectDetail
		ProjectDetail projectDetail = projectRepository
				.getProjectDetailWithNoOfRun(projectId, holdingBayRunStatusList, runningBayRunStatusList)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

		List<RunProjectionForReport> runProjections = runRepository.getRunProjectionsForReport(projectId, runIds);

		if (runProjections == null || runProjections.isEmpty()) {
			throw new ResourceNotFoundException("No runs found for the given projectId and runIds.");

		}
//
		// now setting runDetail from existing run
		List<RunDetail> runDetail = populateRunDetails(runProjections);

		// now constructing the response back
		Map<String, Object> response = new HashMap<>();
		response.put("project-detail", projectDetail);
		response.put("run-detail", runDetail);

//        Long simulationId = runDetail.getSimulatedId();
		List<Long> simulationIds = runDetail.stream().map(RunDetail::getSimulatedId).collect(Collectors.toList());
//
		List<ThreeDViewResponse> threeDViewResponses = new ArrayList<>();

		for (RunDetail run : runDetail) {
			Long simulationId = run.getSimulatedId(); // Get simulationId
			String runName = run.getRunName(); // Get runName

			// Call get3DViews method
			String viewUrl = get3DViews(simulationId);

			// Create a response object and add it to the list
			ThreeDViewResponse threeDViewResponse = new ThreeDViewResponse(runName, viewUrl);
			threeDViewResponses.add(threeDViewResponse);
		}

		response.put("3d-views", threeDViewResponses);

		// List of quantities with their corresponding units
		List<Map<String, String>> quantities = List.of(Map.of("quantity", "Daily Air Temp", "unit", "W/m²"),
				Map.of("quantity", "Humidity", "unit", "0C"),
				Map.of("quantity", "Direct Normal Radiation", "unit", "%"),
				Map.of("quantity", "Diffuse Horizontal Radiation", "unit", "m/s")

		);

		// Prepare the outputs list
		List<Map<String, Object>> outputs = new ArrayList<>();

		// Simulating dynamic data generation
		for (Map<String, String> quantityInfo : quantities) {
			List<?> dataList = null;
			try {
				dataList = postprocessingDetailsServiceImpl.getHourlyWithInRunGraph(quantityInfo.get("quantity"),
						runIds, null, null, null, null, null,false);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Create an output entry for the current quantity
			Map<String, Object> outputEntry = new HashMap<>();
			outputEntry.put("quantity", quantityInfo.get("quantity"));
			outputEntry.put("unit", quantityInfo.get("unit"));
			outputEntry.put("data", dataList);

			// Add the entry to the outputs list
			outputs.add(outputEntry);
		}

		// Set the "outputs" key in the response
		response.put("outputs", outputs);

		return response;
	}

    private List<RunDetail> populateRunDetails(List<RunProjectionForReport> runProjections) {
        if (runProjections == null || runProjections.isEmpty()) {
            return Collections.emptyList();
        }

        return runProjections.stream()
                .map((RunProjectionForReport runProjection) -> {
                    // Map PvParametersDetail
                    PvParametersResponseDto pvParametersDetail = mapToPvParametersDetail(
                            runProjection.getPvParameters(),
                            runProjection.getProjectId(),
                            runProjection.getId(),
                            runProjection.getPreProcessorToggles()
                    );

                    // Map CropParametersDetail
                    CropResponse cropParametersDetail = mapToCropParametersDetail(
                            runProjection.getCropParameters(),
                            runProjection.getProjectId(),
                            runProjection.getId(),
                            runProjection.getPreProcessorToggles()
                    );
                    
                    AgriGeneralResponseDto agriGeneralResponseDto = mapToAgriGeneralParametersDetail(
                            runProjection.getAgriGeneralParameter(),
                            runProjection.getProjectId(),
                            runProjection.getId(),
                            runProjection.getPreProcessorToggles()
                    );
                    
                    EconomicParameterResponse economicParameterResponse = mapToEconomicParametersDetail(
                            runProjection.getEconomicParameters(),runProjection.getCropParameters(),
                            runProjection.getProjectId(),
                            runProjection.getId(),
                            runProjection.getPreProcessorToggles()
                    );

                    // Map SimulatedRun ID if it exists
                    Long simulatedId = (runProjection.getSimulatedRun() != null)
                            ? runProjection.getSimulatedRun().getSimulatedId()
                            : null;

                    // Build and return RunDetail object
                    return RunDetail.builder()
                            .id(runProjection.getId())
                            .projectId(runProjection.getProjectId())
                            .runName(runProjection.getRunName())
                            .preProcessorToggles(runProjection.getPreProcessorToggles())
                            .pvParameters(pvParametersDetail)
                            .cropParameters(cropParametersDetail)
                            .agriGeneralParameter(agriGeneralResponseDto)
                            .economicParameters(economicParameterResponse)
                            .runStatus(runProjection.getRunStatus())
                            .simulatedId(simulatedId)
                            .createdAt(runProjection.getCreatedAt())
                            .updatedAt(runProjection.getUpdatedAt())
                            .progress(runProjection.getProgress())
                            
                            .build();
                })
                .collect(Collectors.toList());
    }
    
	private PvParametersResponseDto mapToPvParametersDetail(PvParameter pvParameter, Long projectId, Long runId,
			PreProcessorToggle preProcessorToggles) {
		if (pvParameter == null) {
			return null;
		}

// Map module configurations
		List<PvModuleConfiguration> moduleConfigs = pvParameter.getModuleConfig().stream()
				.map(moduleConfig -> PvModuleConfiguration.builder().id(moduleConfig.getId())
						.moduleConfig(moduleConfig.getModuleConfig()).ordering(moduleConfig.getOrdering())
						.numberOfModules(moduleConfig.getNumberOfModules()).typeOfModule(moduleConfig.getTypeOfModule())
						.createdAt(moduleConfig.getCreatedAt()).updatedAt(moduleConfig.getUpdatedAt()).build())
				.toList();

// Build PvParametersDetail object
		return PvParametersResponseDto.builder().id(pvParameter.getId()).runId(runId).projectId(projectId)
				.tiltIfFt(pvParameter.getTiltIfFt()).maxAngleOfTracking(pvParameter.getMaxAngleOfTracking())
				.moduleMaskPattern(pvParameter.getModuleMaskPattern())
				.gapBetweenModules(pvParameter.getGapBetweenModules()).height(pvParameter.getHeight())
				.status(pvParameter.getStatus()).pvModule(pvParameter.getPvModule())
				.modeOfOperationId(pvParameter.getModeOfOperationId()).moduleConfigs(moduleConfigs)
				.preProcessorToggle(preProcessorToggles).build();
	}

	private CropResponse mapToCropParametersDetail(CropParameters cropParameters, Long projectId, Long runId,
			PreProcessorToggle preProcessorToggles) {
		if (cropParameters == null) {
			return null;
		}

// Build CropParametersDetail object
		return CropResponse.builder().id(cropParameters.getId()).runId(runId).projectId(projectId)
				.cycles(cropParameters.getCycles())
				.build();
	}
	
	private AgriGeneralResponseDto mapToAgriGeneralParametersDetail(AgriGeneralParameter agriGeneralParameter, Long projectId, Long runId,
			PreProcessorToggle preProcessorToggles) {
		if (agriGeneralParameter == null) {
			return null;
		}

// Build AgriGeneral object
		return AgriGeneralResponseDto.builder().id(agriGeneralParameter.getId()).runId(runId).projectId(projectId)
				.agriPvProtectionHeight(agriGeneralParameter.getAgriPvProtectionHeight())
				.irrigationType(agriGeneralParameter.getIrrigationId())
				.soilType(null)
				.tempControl(agriGeneralParameter.getTempControl())
				.trail(agriGeneralParameter.getTrail()).minTemp(agriGeneralParameter.getMinTemp())
				.maxTemp(agriGeneralParameter.getMaxTemp()).isMulching(agriGeneralParameter.getIsMulching())
				.bedParameter(agriGeneralParameter.getBedParameter())
				.status(agriGeneralParameter.getStatus())
				.build();
	}
	
	private EconomicParameterResponse mapToEconomicParametersDetail(EconomicParameters economicParameters, CropParameters cropParameters, Long projectId, Long runId,
			PreProcessorToggle preProcessorToggles) {
		if (economicParameters == null) {
			return null;
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
                CropDto cropDto = CropDto.builder().name(crop.getName()).id(crop.getId()).createdAt(crop.getCreatedAt()).updatedAt(crop.getUpdatedAt()).build();
                return cropDto;
            }).collect(Collectors.toList());
            System.out.println("crop Dto list size is :" + cropDtoList.size());


        }
        
        List<EconomicMultiCropResponse> economicMultiCropResponseList = economicParameters.getEconomicMultiCrop().stream().map((economicMultiCrop) -> {
            EconomicMultiCropResponse economicMultiCropResponse = EconomicMultiCropResponse.builder()
                    .cropId(economicMultiCrop.getCrop().getId())
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

// Build CropParametersDetail object
		return EconomicParameterResponse.builder().economicId(economicParameters.getEconomicId()).runId(runId)
				.cropDtoSet(cropDtoList)
				.currency(economicParameters.getCurrency())
				.economicMultiCropResponseList(economicMultiCropResponseList)
				.hourlySellingRates(economicParameters.getHourlySellingRates())
				.economicParameter(economicParameters.isEconomicParameter())
				.createdAt(economicParameters.getCreatedAt()).updatedAt(economicParameters.getUpdatedAt())
				.build();
	}

	private String get3DViews(Long simulationId) {
        String sql = """
                WITH ranked_scenes AS (
                    SELECT DISTINCT ON (sc.type)
                        sc.type,
                        sc.url
                    FROM
                        simtool.simulations s
                    JOIN
                        simtool.simulation_tasks st ON s.id = st.simulation_id
                    JOIN
                        simtool.scenes sc ON st.id = sc.simulation_task_id
                    WHERE
                        s.id = :simulationId
                        AND sc.type IN ('visualization_iso')
                    ORDER BY
                        sc.type, sc.id
                )
                SELECT
                    MAX(CASE WHEN type = 'visualization_iso' THEN url END) AS isometricUrl
                FROM ranked_scenes
                """;

        String isometricUrl = null;
        try {
        	isometricUrl = (String) entityManager.createNativeQuery(sql)
                    .setParameter("simulationId", simulationId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // Log if necessary and handle case where no result is found
            System.out.println("No result found for simulationId: " + simulationId);
        }

        // Check if result is null or empty
        if (isometricUrl == null ) {
            return null; // Default fallback value
        }

        // Extract the isometric URL
//        String isometricUrl = (String) result[0];

        return isometricUrl;
    }
}