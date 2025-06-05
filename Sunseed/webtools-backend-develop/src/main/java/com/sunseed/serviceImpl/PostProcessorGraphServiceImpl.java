package com.sunseed.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunseed.entity.Crop;
import com.sunseed.entity.Projects;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.model.requestDTO.Simulation;
import com.sunseed.model.responseDTO.HeatMap;
import com.sunseed.model.responseDTO.RunResponseToggleDto;
import com.sunseed.repository.CropRepository;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.service.PostProcessorGraphService;

import jakarta.persistence.EntityManager;

@Service
public class PostProcessorGraphServiceImpl implements PostProcessorGraphService{
	
	@Autowired
    private ProjectsRepository userProjectsRepo;
	@Autowired
    private EntityManager entityManager;
	@Autowired
	private RunsRepository runsRepository;
	@Autowired
	private CropRepository cropRepository;

	@Override
	public HashMap<String, Object> getAllSimulationFromRunId(Long projectId, Long runId, Long userId) {
		if (userId == null || userId <= 0)
            throw new UnAuthorizedException(null, "user.not.found");

        Projects project = userProjectsRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        System.out.println("Project Id " + project.getProjectId());

        Long existingUserId = project.getUserProfile().getUserId();
        System.out.println("UserID" + userId);
        System.out.println("Existing user ID " + existingUserId);

        if (userId != existingUserId) {
            throw new UnAuthorizedException("user.not.authorized");
        }
        
        List<HeatMap> resultSimulation = findDataFromSimulationTaskAndCropYields(runId, projectId);
        HashMap<String, Object> response = new HashMap<>();
        List<HeatMap> runResponseDtoList = new ArrayList<>();
//        for(HeatMap h : resultSimulation) {
//        	HeatMap heat = new HeatMap();
//        	h.getDate().to;
//        }
        System.out.println(resultSimulation.size());
        runResponseDtoList.addAll(resultSimulation);
        response.put("resultSimulation", runResponseDtoList);
		return response;
	}

	private List<HeatMap> findDataFromSimulationTaskAndCropYields(Long runId, Long projectId) {
		// TODO Auto-generated method stub
//		String sql = "SELECT st.*, cy.* AS crop_temperature FROM simtool.simulation_tasks st JOIN simtool.simulations s ON st.simulation_id = s.id LEFT JOIN simtool.crop_yields cy ON st.id = cy.simulation_task_id WHERE s.run_id = :runId AND s.project_id = :projectId ORDER BY st.date ASC";
		String sql = "SELECT st.*,\n"
				+ "    COUNT(cy.id) AS crop_yield_count, \n"
				+ "    json_agg(cy) AS crop_yield_data, \n"
				+ "    AVG(COALESCE(cy.temperature, 0)) AS average_temperature \n"
				+ "    FROM simtool.simulation_tasks st \n"
				+ "    JOIN simtool.simulations s ON st.simulation_id = s.id \n"
				+ "    LEFT JOIN simtool.crop_yields cy ON st.id = cy.simulation_task_id \n"
				+ "	WHERE s.run_id = :runId AND s.project_id = :projectId\n"
				+ "    GROUP BY st.id \n"
				+ "    ORDER BY st.date ASC;";
		List<Object[]> results = entityManager.createNativeQuery(sql).setParameter("runId", runId).setParameter("projectId", projectId).getResultList();
//		return results.stream()
//			    .map(result -> new HeatMap(
//			    	result[3] != null ? (Date) result[3] : null,
//			    	result[3] != null ? (((Timestamp) result[3]).toInstant().atOffset(ZoneOffset.UTC)) : null,
//			    	result[13] != null ? ((Number) result[13]).floatValue() : null
//			    ))
//			    .collect(Collectors.toList());
		return results.stream()
	    .map(result -> {
	        LocalDate date = null;
	        LocalTime time = null;

	        if (result[3] != null) {
	            Timestamp timestamp = (Timestamp) result[3];
	            date = timestamp.toLocalDateTime().toLocalDate(); // Extract the date part
	            time = timestamp.toLocalDateTime().toLocalTime(); // Extract the time part
	        }

	        Float temperature = result[12] != null ? ((Number) result[12]).floatValue() : null;

	        return new HeatMap(date, time, temperature);
	    })
	    .collect(Collectors.toList());
		
	}

	@Override
	public HashMap<String, Object> getAllCropData(Long projectId, List<Long> runId, Long userId, List<Long> cropId, String typeGraph) {
		// TODO Auto-generated method stub
		if (userId == null || userId <= 0)
            throw new UnAuthorizedException(null, "user.not.found");

        Projects project = userProjectsRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        System.out.println("Project Id " + project.getProjectId());

        Long existingUserId = project.getUserProfile().getUserId();
        System.out.println("UserID" + userId);
        System.out.println("Existing user ID " + existingUserId);

        if (userId != existingUserId) {
            throw new UnAuthorizedException("user.not.authorized");
        }
        
        HashMap<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        
//        List<Object[]> results = fetchDataFromDb(projectId, runId, cropId);
        
        switch (typeGraph) {
        case "PV Yield":
            // Process data for temperature graph
            response.put("graphType", "PV Yield");
			List<Object[]> results = fetchPvYieldData(runId, projectId);

			// Calculate min and max PV yield values
			Optional<Double> minPvYieldOpt = results.stream().map(result -> ((Number) result[2]).doubleValue())
					.min(Double::compareTo);
			Optional<Double> maxPvYieldOpt = results.stream().map(result -> ((Number) result[3]).doubleValue())
					.max(Double::compareTo);

			// Process data to include only runName and pvYield
			List<Map<String, Object>> dataPv = results.stream().map(result -> {
				Map<String, Object> map = new HashMap<>();
				map.put("runName", result[0]);
				map.put("pvYield", result[1] != null ? ((Number) result[1]).doubleValue() : 0.0);
				return map;
			}).collect(Collectors.toList());

			// Prepare the response
			response.put("graphType", "PV Yield");
			response.put("data", dataPv);
			response.put("minPvYield", minPvYieldOpt.orElse(null));
			response.put("maxPvYield", maxPvYieldOpt.orElse(null));
			break;
//            break;
//        case "CA Yield":
            // Process data for CA Yield graph
//            response.put("graphType", "CA Yield");
//            
//            List<Crop> crops = cropRepository.findAllById(cropId);
//            List<Map<String, Object>> allCropData = new ArrayList<>();
//            
//            // Fetch CV yield data
//            List<Object[]> resultCv = fetchCVYieldData(runId, projectId);
//            
//            // Calculate min and max carbon assimilation
//            Optional<Double> minYieldOptCv = resultCv.stream()
//                    .map(result -> ((Number) result[3]).doubleValue())
//                    .min(Double::compareTo);
//            Optional<Double> maxYieldOptCv = resultCv.stream()
//                    .map(result -> ((Number) result[4]).doubleValue())
//                    .max(Double::compareTo);
//            
//            for (Crop e : crops) {
//                for (Object[] row : resultCv) {
//                    String cropNameFromQuery = (String) row[1]; // Assuming cropName is the second column in the result
//
//                    if (cropNameFromQuery.equals(e.getName())) {
//                        List<Map<String, Object>> dataCv = resultCv.stream()
//                                .filter(result -> ((String) result[1]).equals(e.getName()))
//                                .map(result -> {
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("runName", result[0]);
//                                    map.put("carbonAssimilation", result[2] != null ? ((Number) result[2]).doubleValue() : 0.0);
//                                    return map;
//                                })
//                                .collect(Collectors.toList());
//
//                        Map<String, Object> cropData = new HashMap<>();
//                        cropData.put("cropName", e.getName());
//                        cropData.put("data", dataCv);
//
//                        allCropData.add(cropData); // Add crop-specific data to the list
//                    }
//                }
//            }
//            
//            response.put("cropData", allCropData); // Store all crops' data in the response
//            response.put("minCAYield", minYieldOptCv.orElse(null)); // Add min yield only once
//            response.put("maxCAYield", maxYieldOptCv.orElse(null)); // Add max yield only once
//            break;

        case "CA Yield":
            // Process data for CA Yield graph
            response.put("graphType", "CA Yield");
            
            List<Crop> crops = cropRepository.findAllById(cropId);
            List<Map<String, Object>> allCropData = new ArrayList<>();
            
            // Fetch CV yield data
            List<Object[]> resultCv = fetchCVYieldData(runId, projectId);
            
            // Calculate min and max carbon assimilation
            Optional<Double> minYieldOptCv = resultCv.stream()
                    .map(result -> ((Number) result[3]).doubleValue())
                    .min(Double::compareTo);
            Optional<Double> maxYieldOptCv = resultCv.stream()
                    .map(result -> ((Number) result[4]).doubleValue())
                    .max(Double::compareTo);
            
            for (Crop e : crops) {
                // Collect data for the current crop only where crop name matches
                List<Map<String, Object>> dataCv = resultCv.stream()
                        .filter(result -> {
                            String cropNameFromQuery = (String) result[1]; // Assuming cropName is the second column in the result
                            return cropNameFromQuery.equals(e.getName());  // Validation check for matching cropName
                        })
                        .map(result -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("runName", result[0]);
                            map.put("carbonAssimilation", result[2] != null ? ((Number) result[2]).doubleValue() : 0.0);
                            return map;
                        })
                        .collect(Collectors.toList());

                // Add crop-specific data only if it exists
                if (!dataCv.isEmpty()) {
                    Map<String, Object> cropData = new HashMap<>();
                    cropData.put("cropName", e.getName());
                    cropData.put("data", dataCv);

                    allCropData.add(cropData); // Add crop-specific data to the list
                }
            }
            
            response.put("cropData", allCropData); // Store all crops' data in the response
            response.put("minCAYield", minYieldOptCv.orElse(null)); // Add min yield only once
            response.put("maxCAYield", maxYieldOptCv.orElse(null)); // Add max yield only once
            break;


            
//            List<Object[]> resultCv = fetchCVYieldData(runId, projectId);

			// Calculate min and max PV yield values
			
            
//            response.put("graphType", "Yield");
//            response.put("data", processYieldData(results));
//            break;
            
        case "Bifacial Delta":
            // Process data for growth graph
            response.put("graphType", "Growth");
//            response.put("data", processGrowthData(results));
        case "Albedo":
            // Process data for growth graph
            response.put("graphType", "Albedo");
            data = fetchAlbedoData(runId, projectId);
            response.put("data", data);
            break;
//            response.put("data", processGrowthData(results));
        default:
            // Handle unsupported graph types
            throw new IllegalArgumentException("Invalid graph type: " + typeGraph);
    }

		return response;
	}
	
	
	private List<Object[]> fetchPvYieldData(List<Long> runId, Long projectId) {
		String sql = "WITH YieldData AS (" +
                "    SELECT ur.run_name, SUM(py.pv_yield) AS totalPvYield " +
                "    FROM public.user_run ur " +
                "    JOIN simtool.simulations s ON ur.run_id = s.run_id " +
                "    JOIN simtool.pv_yields py ON s.id = py.simulation_task_id " +
                "    WHERE ur.run_id IN :runId AND ur.project_id = :projectId " +
                "    GROUP BY ur.run_name" +
                ") " +
                "SELECT yd.run_name, yd.totalPvYield, " +
                "       (SELECT MIN(totalPvYield) FROM YieldData) AS minPvYield, " +
                "       (SELECT MAX(totalPvYield) FROM YieldData) AS maxPvYield " +
                "FROM YieldData yd";
		
		return entityManager.createNativeQuery(sql)
		        .setParameter("runId", runId)
		        .setParameter("projectId", projectId)
		        .getResultList();



	}
	
	private List<Object[]> fetchCVYieldData(List<Long> runId, Long projectId) {
		
		String sql = "WITH YieldData AS (" +
	             "    SELECT " +
	             "        ur.run_name, " +
	             "        cy.crop_name, " +  // Include cropName in the selection
	             "        SUM(cy.carbon_assimilation) AS totalCarbonAssimilation " +
	             "    FROM " +
	             "        public.user_run ur " +
	             "    JOIN " +
	             "        simtool.simulations s ON ur.run_id = s.run_id AND ur.project_id = s.project_id " +
	             "    JOIN " +
	             "        simtool.simulation_tasks st ON s.id = st.simulation_id " +
	             "    JOIN " +
	             "        simtool.crop_yields cy ON st.id = cy.simulation_task_id " +
	             "    WHERE " +
	             "        ur.run_id IN :runId AND ur.status = 'COMPLETED' AND ur.project_id = :projectId " +
	             "    GROUP BY " +
	             "        ur.run_name, cy.crop_name " +  // Group by run_name and crop_name
	             ") " +
	             "SELECT " +
	             "    yd.run_name, " +
	             "    yd.crop_name, " +  // Select cropName in the final result
	             "    yd.totalCarbonAssimilation, " +
	             "    MIN(yd.totalCarbonAssimilation) OVER () AS minCarbonAssimilation, " +
	             "    MAX(yd.totalCarbonAssimilation) OVER () AS maxCarbonAssimilation " +
	             "FROM " +
	             "    YieldData yd";

	return entityManager.createNativeQuery(sql)
	        .setParameter("runId", runId)
	        .setParameter("projectId", projectId)
	        .getResultList();



	}
	
	private List<Map<String, Object>> fetchAlbedoData(List<Long> runId, Long projectId) {
	    String sql = "SELECT ur.run_name, SUM(py.albedo) AS albedo " +
	                 "FROM public.user_run ur " +
	                 "JOIN simtool.simulations s ON ur.run_id = s.run_id " +
	                 "JOIN simtool.pv_yields py ON s.id = py.simulation_task_id " +
	                 "WHERE ur.run_id IN :runId AND ur.project_id = :projectId " +
	                 "GROUP BY ur.run_name";

	    List<Object[]> results = entityManager.createNativeQuery(sql)
	        .setParameter("runId", runId)
	        .setParameter("projectId", projectId)
	        .getResultList();

	    return results.stream()
	        .map(result -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put("runName", result[0]);
	            map.put("albedo", result[1] != null ? ((Number) result[1]).doubleValue() : 0.0);
	            return map;
	        })
	        .collect(Collectors.toList());
	}





	// 2d heat map hourly
}
