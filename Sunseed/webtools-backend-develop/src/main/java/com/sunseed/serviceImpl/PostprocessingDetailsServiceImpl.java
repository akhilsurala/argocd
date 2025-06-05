package com.sunseed.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.sunseed.entity.*;
import com.sunseed.exceptions.EntityNotFoundException;
import com.sunseed.exceptions.InvalidDataException;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.PostProcessingFunctionsHelper;
import com.sunseed.helper.PostprocessingHelper;
import com.sunseed.model.requestDTO.HourlyDetailsPayload;
import com.sunseed.model.responseDTO.*;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.RunsRepository;
import com.sunseed.repository.CropRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.PostprocessingDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostprocessingDetailsServiceImpl implements PostprocessingDetailsService {
    //    private final HttpClient httpClient;
    private final RunsRepository runsRepository;
    private final UserProfileRepository userProfileRepository;
    private final JdbcTemplate jdbcTemplate;
    private final CropRepository cropRepository;
    private final ProjectsRepository userProjectsRepo;
    private final HttpClient httpClient;

    private final PostprocessingHelper postprocessingHelper;
    private final PostProcessingFunctionsHelper postProcessingFunctionsHelper;
    @Value("${crop.intervalType}")
    private Integer intervalType;

    @Override
    public List<?> getHourlyDetails(HourlyDetailsPayload request, String quantity, String dataType, String frequency, Long projectId, Long userId) throws Exception {

        if (userId == null || userId <= 0)
            throw new UnAuthorizedException(null, "user.not.found");
        System.out.println("project id is :" + projectId);
        Projects project = userProjectsRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        System.out.println("Project Id " + project.getProjectId());

        Long existingUserId = project.getUserProfile().getUserId();
        System.out.println("UserID" + userId);
        System.out.println("Existing user ID " + existingUserId);

//        if (!userId.equals(existingUserId)) {
//            throw new UnAuthorizedException("user.not.authorized");
//        }

        List<Map<String, Object>> result = new ArrayList<>();
        List<Long> runIdList = request.getRunIds();
//        List<Long> projectRuns = project.getRunIds();
//        if (!projectRuns.containsAll(runIdList)) {
//            throw new UnprocessableException("run.invalid");
//        }
        if (runIdList.isEmpty() || runIdList == null) {
            throw new InvalidDataException("runId.list");
        }
        List<String> cycleList = request.getCycles();
        List<String> bedList = request.getBeds();
        List<String> weekList = request.getWeeks();
        // List<String> cropList = request.getCrops();
        List<String> cropList = Optional.ofNullable(request.getCrops())
            .orElse(Collections.emptyList());
        List<String> leafConfigurationList = request.getConfigurations();

        // Fetch Crop entities for given crop names
        List<Crop> crops = cropRepository.findByNameIn(cropList);

        Map<String, String> nameToLabelMap = crops.stream()
            .collect(Collectors.toMap(Crop::getName, Crop::getCropLabel));


//        List<String> cropLabelList = cropList.stream()
//            .map(nameToLabelMap::get)
//            .filter(Objects::nonNull)
//            .collect(Collectors.toList());
        
        List<String> cropLabelList = cropList.stream()
        	    .map(name -> "all".equalsIgnoreCase(name) ? "all" : nameToLabelMap.get(name))
        	    .filter(Objects::nonNull)
        	    .collect(Collectors.toList());
        

        //frequency - weekly or hourly
        String givenFrequecy = frequency;
        //across run or withinrun
        String givenDataType = dataType;
        
        //Week cycle days
        Integer from = request.getFrom();
        Integer to = request.getTo();


        //same group - validations
        //  boolean isSameGroup=isSameGroup(runIdList);

        System.out.println("given data type is :" + givenDataType);
        if ("across".equalsIgnoreCase(givenDataType.trim())) {
            System.out.println("in across run :");
            switch (givenFrequecy) {
                case "weekly":
                    return getWeeklyAcrossRunGraph(quantity, runIdList, cycleList, bedList, weekList, cropLabelList, leafConfigurationList, from, to);
//                case "weekly":
//                    return getWeeklyAcrossRunGraph(quantity, runIdList);
                default:
                    throw new UnprocessableException("frequency.invalid");

            }

        } else if (givenDataType.trim().equalsIgnoreCase("with in run")) {
            System.out.println("with in run graphs :");
            switch (givenFrequecy) {
                case "hourly":
                    return getHourlyWithInRunGraph(quantity, runIdList, cycleList, bedList, weekList, cropLabelList, leafConfigurationList,false);
                case "weekly":
                    System.out.println("weekly with in run graphs :");
                    return getWeeklyWithInRunGraph(quantity, runIdList, cycleList, bedList, weekList, cropLabelList, leafConfigurationList);
                default:
                    throw new UnprocessableException("frequency.invalid");
            }
        } else {
            throw new UnprocessableException("given.data.type.invalid");
        }


    }

    //get hourly graph for across run
    public List<?> getWeeklyAcrossRunGraph(String quantity, List<Long> runIdList,
                                           List<String> cycleList,
                                           List<String> bedList,
                                           List<String> weekList,
                                           List<String> cropList,
                                           List<String> leafConfigurationList,
                                           Integer from, Integer to) throws JsonProcessingException {

        System.out.println("this case is  :" + quantity);
        List<Map<String, Object>> result = new ArrayList<>();
        // call makeCombinations method
        String combinationJson = makeCombinations(runIdList, cycleList, bedList, weekList, cropList, leafConfigurationList, "weekly", quantity);
        if(from == null) {
        	from = 1;
        }
        if(to == null) {
        	to = 5;
        }
        System.out.println("combination json :" + combinationJson);
        switch (quantity) {
            case "Carbon Assim / Plant":
                try {
                    System.out.println("Enter in Carbon Assim / Plant");

                    // String jsonCombination = this.jsonCombinationForTranspiration(runIdList, cropList,from,to);
                    // List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.cumulativeCarbonAssimPlantFunctionFromDB(jsonCombination);
                   List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    System.out.println("query result for " + queryResults);
                   result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                    // List<Map<String, Object>> processedResponse = processCarbonYieldData(queryResults,"per_plant");
                    // result = processedResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Carbon Assim / Ground":
                try {

//                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
//                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                	System.out.println("Enter in Carbon Assim / Ground");

                    // String jsonCombination = this.jsonCombinationForTranspiration(runIdList, cropList,from, to);
                    // List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.cumulativeCarbonAssimPlantFunctionFromDB(jsonCombination);
                   List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    System.out.println("query result for " + queryResults);
                   result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                    // List<Map<String, Object>> processedResponse = processCarbonYieldData(queryResults,"per_ground");
                    // result = processedResponse;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Cumulative Carbon Assim / Plant":
                try {
                    System.out.println("Enter in Cumulative Carbon Assim / Plant");

                    String jsonCombination = this.jsonCombinationForTranspiration(runIdList, cropList,from,to);
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.cumulativeCarbonAssimPlantFunctionFromDB(jsonCombination);
//                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    System.out.println("query result for " + queryResults);
//                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                    List<Map<String, Object>> processedResponse = processCarbonYieldData(queryResults,"per_plant");
                    result = processedResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Cumulative Carbon Assim / Ground":
                try {

//                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
//                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                	System.out.println("Enter in Cumulative Carbon Assim / Ground");

                    String jsonCombination = this.jsonCombinationForTranspiration(runIdList, cropList,from, to);
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.cumulativeCarbonAssimPlantFunctionFromDB(jsonCombination);
//                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    System.out.println("query result for " + queryResults);
//                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                    List<Map<String, Object>> processedResponse = processCarbonYieldData(queryResults,"per_ground");
                    result = processedResponse;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Avg. Leaf Temperature":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyAvgLeafTemperatureFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Light Absorbed / Plant":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyRadiationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Light Absorbed / M2 Ground":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyRadiationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Cumulative Transpiration / Plant":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyTranspirationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Total Transpiration":
                try {

                	System.out.println("Enter TotalTranspiration");
                    String jsonCombination = this.jsonCombinationForTranspiration(runIdList, cropList,from, to);
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.totalTranspirationFunctionFromDB(jsonCombination);
//                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);
                    System.out.println("out");
                    List<Map<String, Object>> processedResponse = processYieldData(queryResults,"total_transpiration");
                    result = processedResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Cumulative Transpiration / Ground":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyTranspirationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Cumulative Energy Generation":
                try {

                    result = postProcessingFunctionsHelper.weeklyPvYieldsResponse(runIdList, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Average Bifacial Gain":
                try {

                    result = postProcessingFunctionsHelper.weeklyPvYieldsResponse(runIdList, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

// *************** system economics graph **********************
            case "PV Revenue Per Mega Watt":
                try {
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getDcOutPutForPvRevenue(runIdList);
                    System.out.println("after getting query result");
                    Map<Long, Map<Integer, Double>> tariffData = postProcessingFunctionsHelper.getTariffData(runIdList);

                    result = this.getPvRevenue(queryResults, tariffData, quantity, runIdList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "PV Revenue Per Acre":
                try {
                    //   System.out.println("Enter PV revenue");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getDcOutPutForPvRevenue(runIdList);
                    Map<Long, Map<Integer, Double>> tariffData = postProcessingFunctionsHelper.getTariffData(runIdList);
                    result = this.getPvRevenue(queryResults, tariffData, quantity, runIdList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Agri Revenue":
                try {

                    System.out.println("Enter Agri revenue");
                    String jsonCombination = this.jsonCombinationForAgriRevenue(runIdList, cropList);
                    if(jsonCombination != null) {
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getAgriRevenueFunctionFromDB(jsonCombination);
                    result = this.getAgriRevenue(queryResults);
                    
                    List<Map<String, Object>> processedResponse = processYieldData(queryResults,"yield_per_acre");
                    result = processedResponse;
                    }else {
                    	throw new EntityNotFoundException("Economic parameters or Economic Multi-Crop data is missing for run: ");
                    }

                }
                catch (EntityNotFoundException e) {
                    // Handle EntityNotFoundException separately
                    throw e; 
                } 
                catch (UnprocessableException e) {
                    // Rethrow UnprocessableException to be handled by GlobalExceptionHandler
                	throw e;

                } catch (Exception e) {
                    // Wrap any other exceptions in UnprocessableException
                    throw new UnprocessableException("No control run exists. ");
                }
                break;
            case "Total Revenue":
                try {
                    // Calculate PV Revenue
                    List<Map<String, Object>> pvRevenueResults = postProcessingFunctionsHelper.getDcOutPutForPvRevenue(runIdList);
                    Map<Long, Map<Integer, Double>> tariffData = postProcessingFunctionsHelper.getTariffData(runIdList);
                    List<Map<String, Object>> pvRevenue;
                    if(pvRevenueResults.isEmpty()) {
                    	pvRevenue = null;
                    }
                    else {
                    	pvRevenue = this.getPvRevenue(pvRevenueResults, tariffData, quantity, runIdList);
                    }
                    
                    // Calculate Agri Revenue
                    String jsonCombination = this.jsonCombinationForAgriRevenue(runIdList, cropList);
                    List<Map<String, Object>> agriRevenueResults = postProcessingFunctionsHelper.getAgriRevenueFunctionFromDB(jsonCombination);
                    List<Map<String, Object>> agriRevenue = this.getAgriRevenue(agriRevenueResults);

                    // Calculate Total Revenue
                    result = this.getTotalRevenue(pvRevenue, agriRevenue);
                } catch (UnprocessableException e) {
                    // Rethrow UnprocessableException to be handled by GlobalExceptionHandler
                	throw e;

                } catch (Exception e) {
                    // Wrap any other exceptions in UnprocessableException
                    throw new EntityNotFoundException("No control run exists. ");
                }
                break;

            case "Profit":
                try {
                    // Calculate Total Revenue
                    List<Map<String, Object>> pvRevenueResults = postProcessingFunctionsHelper.getDcOutPutForPvRevenue(runIdList);
                    Map<Long, Map<Integer, Double>> tariffData = postProcessingFunctionsHelper.getTariffData(runIdList);
                    List<Map<String, Object>> pvRevenue;
                    if(pvRevenueResults.size() != 0) {
                    	pvRevenue = this.getPvRevenue(pvRevenueResults, tariffData, quantity, runIdList);
                    }
                    else {
                    	pvRevenue = null;
                    }

                    String jsonCombination = this.jsonCombinationForAgriRevenue(runIdList, cropList);
                    List<Map<String, Object>> agriRevenueResults = postProcessingFunctionsHelper.getAgriRevenueFunctionFromDB(jsonCombination);
                    List<Map<String, Object>> agriRevenue = this.getAgriRevenue(agriRevenueResults);

                    List<Map<String, Object>> totalRevenueResults = this.getTotalRevenue(pvRevenue, agriRevenue);

                    // Calculate Input Costs
                    Map<Long, Double> inputCosts = this.getInputCostForRuns(runIdList);

                    // Calculate Profit
                    result = this.getProfit(totalRevenueResults, inputCosts);
                } catch (UnprocessableException e) {
                    // Rethrow UnprocessableException to be handled by GlobalExceptionHandler
                	throw e;

                } catch (Exception e) {
                    // Wrap any other exceptions in UnprocessableException
                    throw new EntityNotFoundException("No control run exists. ");
                }
                break;


        }
        return result;
    }


    //    // hourly within runs graphs
    public List<?> getHourlyWithInRunGraph(String quantity, List<Long> runIdList,
                                           List<String> cycleList,
                                           List<String> bedList,
                                           List<String> weekList,
                                           List<String> cropList,
                                           List<String> leafConfigurationList,boolean isSkip) throws Exception {
        List<?> result = new ArrayList<>();
        Object[] resultSet = null;
        String filePath = null;
        Long simulationId = 0L;
//        List<Map<String, Object>> queryResults = new ArrayList<>();
        // call makeCombinations method
        String combinationJson = makeCombinations(runIdList, cycleList, bedList, weekList, cropList, leafConfigurationList, "hourly", quantity);
        //  Hardcoded JSON array formatted as a string
        String hardcodedJson = "[[298, 1, 1, \"sorghum\", \"sunlit\"], [298, 1, 1, \"sorghum\", \"sunshaded\"], [298, 1, 1, \"sorghum\", \"all\"]]";
        System.out.println("before switch case quantity is:" + quantity.trim());
        switch (quantity.trim()) {

            case "Rate of Carbon Assim / Plant":
                try {
                    System.out.println("Rate Of carbon Assim / Plant");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.carbonAssimilationFunctionFromDB(combinationJson);

                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case "Rate of Carbon Assim / Ground":
                try {
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.carbonAssimilationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Avg. Leaf Temperature":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.avgLeafTemperatureFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "PPFD":
                try {
                    System.out.println("hourly PPFD");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.hourlyPPFD(runIdList.get(0), weekList);
                    result = postProcessingFunctionsHelper.responseForHourlyPvYield(queryResults, quantity, weekList, runIdList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Cumulative PPFD ":
                result = forAllCasesSameMethod(runIdList, cycleList, bedList, weekList, cropList, leafConfigurationList, quantity);
                break;
            case "% Sunlit Leaves / Plant":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.sunlitLeavesFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "% Sunlit Leaves / Ground Area":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.sunlitLeavesFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Light Absorbed / Plant":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.lightAbsorbedFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Penetration Efficiency Metric":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.penetrationEfficiencyFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "% Of Sunlit Leaves Saturated / Plant":
                try {
                    System.out.println("sunlit leaves saturated per plant");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.saturationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Saturation Extent / Plant":
                try {
                    System.out.println("Saturation Extent / Plant");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.saturationExtentFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            // for pv
            case "Hourly Bifacial Gain":
                try {
                    System.out.println("hourly bifacial gain");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.hourlyPvYields(runIdList.get(0), weekList);
                    result = postProcessingFunctionsHelper.responseForHourlyPvYield(queryResults, quantity, weekList, runIdList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Hourly DC Power":
                try {
                    System.out.println("hourly dc power");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.hourlyPvYields(runIdList.get(0), weekList);
                    result = postProcessingFunctionsHelper.responseForHourlyPvYield(queryResults, quantity, weekList, runIdList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
// ************* 2 d graphs *********************
            case "Hourly Temperature Across The Year":
                try {
                    System.out.println("Hourly Temperature across the year");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.hourlyTemperatureAcrossYearDBCall(runIdList.get(0), leafConfigurationList.get(0), cropList.get(0), Long.parseLong(bedList.get(0)));
                    result = postProcessingFunctionsHelper.responseFor2DGraphsHeatMap(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Hourly Carbon Assimilation Across The Year":
                try {
                    System.out.println("Hourly Carbon Assimilation across the year");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.hourlyCarbonAssimilationacrosstheyearDBCall(runIdList.get(0), leafConfigurationList.get(0), cropList.get(0), Long.parseLong(bedList.get(0)));
                    result = postProcessingFunctionsHelper.responseFor2DGraphsHeatMap(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Bifacial Gain":
                try {
                    System.out.println("Yearly Bifacial Gain ");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.bifacialAcrosstheyearDBCall(runIdList.get(0));
                    result = postProcessingFunctionsHelper.responseFor2DGraphsHeatMap(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Daily Air Temp":
//                try {
                    System.out.println("Daily Air Temp ");
                    resultSet = getSimulationIdAndFilePathByRunId(runIdList.get(0));
                    filePath = resultSet[1] != null ? (String) resultSet[1] : "";
                    simulationId = resultSet[0] != null ? (Long) resultSet[0] : 0L;
                    if (simulationId.equals(0L)) {
                        throw new UnprocessableException("run.simulate");
                    }
                    if (filePath.isEmpty()) {
                        throw new UnprocessableException("file.not.found");
                    }


                    System.out.println("file path of epw file is :" + filePath);
                    result = fetchAndProcessEpwFile(filePath, 6,isSkip);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

            case "Humidity":
                System.out.println("Humidity");
                resultSet = getSimulationIdAndFilePathByRunId(runIdList.get(0));
                filePath = resultSet[1] != null ? (String) resultSet[1] : "";
                simulationId = resultSet[0] != null ? (Long) resultSet[0] : 0L;
                if (simulationId.equals(0L)) {
                    throw new UnprocessableException("run.simulate");
                }
                if (filePath.isEmpty()) {
                    throw new UnprocessableException("file.not.found");
                }
                System.out.println("file path of epw file is :" + filePath);
                result = fetchAndProcessEpwFile(filePath, 8,isSkip);
                break;
            case "Direct Normal Radiation":
                System.out.println("Direct Normal Radiation ");
                resultSet = getSimulationIdAndFilePathByRunId(runIdList.get(0));
                filePath = resultSet[1] != null ? (String) resultSet[1] : "";
                simulationId = resultSet[0] != null ? (Long) resultSet[0] : 0L;
                if (simulationId.equals(0L)) {
                    throw new UnprocessableException("run.simulate");
                }

                if (filePath.isEmpty()) {
                    throw new UnprocessableException("file.not.found");
                }
                System.out.println("file path of epw file is :" + filePath);
                result = fetchAndProcessEpwFile(filePath, 14,isSkip);
                break;
            case "Diffuse Horizontal Radiation":
                System.out.println("Diffuse Horizontal Radiation");
                resultSet = getSimulationIdAndFilePathByRunId(runIdList.get(0));
                filePath = resultSet[1] != null ? (String) resultSet[1] : "";
                simulationId = resultSet[0] != null ? (Long) resultSet[0] : 0L;
                if (simulationId.equals(0L)) {
                    throw new UnprocessableException("run.simulate");
                }

                if (filePath.isEmpty()) {
                    throw new UnprocessableException("file.not.found");
                }
                System.out.println("file path of epw file is :" + filePath);
                result = fetchAndProcessEpwFile(filePath, 15,isSkip);
                break;
            // **********End 2D Graphs *************

            // *********** Economics Graphs ***********

            case "Cumulative Carbon Assim/Plant":
                try {
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getCumulativeCarbonAssimCrop(runIdList.get(0), cropList.get(0), weekList);
                    result = postProcessingFunctionsHelper.responseCumulativeCarbonAssim(queryResults, quantity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case "Cumulative Carbon Assim/Ground":
                try {
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getCumulativeCarbonAssimCrop(runIdList.get(0), cropList.get(0), weekList);
                    result = postProcessingFunctionsHelper.responseCumulativeCarbonAssim(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            default:
                throw new UnprocessableException("valid.quantity");
        }


        return result;

    }


    public List<?> getWeeklyWithInRunGraph(String quantity, List<Long> runIdList,
                                           List<String> cycleList,
                                           List<String> bedList,
                                           List<String> weekList,
                                           List<String> cropList,
                                           List<String> leafConfigurationList) throws JsonProcessingException {
        List<Map<String, Object>> result = new ArrayList<>();
        // call makeCombinations method
        String combinationJson = makeCombinations(runIdList, cycleList, bedList, weekList, cropList, leafConfigurationList, "weekly", quantity);

        switch (quantity) {
            case "Bi-Weekly Cumulative Carbon Assim / Plant":
                try {
                    System.out.println("Bi-Weekly Cumulative Carbon Assim / Plant case");
                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Bi-Weekly Cumulative Carbon Assim / Ground Area":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyCarbonAssimulationFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Avg. Leaf Temperature":
                try {

                    List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.weeklyAvgLeafTemperatureFunctionFromDB(combinationJson);
                    result = postProcessingFunctionsHelper.responseForBiweeklyData(queryResults, quantity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // only pv case so only run id pass ,no filter apply
            case "Daily DLI":
                try {

                    return postProcessingFunctionsHelper.weeklyDLIFunctionFromDB(runIdList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new UnprocessableException("valid.quantity");

        }


        return result;
    }


    // ************  make combination json for post processing graphs ******

    public String makeCombinations(List<Long> runIdList, List<String> cycleList,
                                   List<String> bedList,
                                   List<String> weekList,
                                   List<String> cropList,
                                   List<String> leafConfigurationList, String type, String quantity) throws JsonProcessingException {


        System.out.println("enter for make combinations");
        // null checks
        List<String> hourlyWithInRunGraphs = List.of("Rate Of Carbon Assim / Plant", "Rate Of Carbon Assim / Ground", "Avg. Leaf Temperature", "% Sunlit Leaves / Plant", "% Sunlit Leaves / Ground Area", "Light Absorbed / Plant", "Penetration Efficiency Metric", "% Of Sunlit Leaves Saturated / Plant");


        // Initialize Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // ********** for now -- get only one week and one run for withinrun***********
        Integer weekInt = 0;
        if (weekList != null) {
            String week = weekList.get(0);
            weekInt = Integer.parseInt(week.substring(1));
        }
        //   Long runId = runIdList.get(0);
        // Create the outer array to hold all combinations
        ArrayNode combinationArray = objectMapper.createArrayNode();

        if (leafConfigurationList != null && !leafConfigurationList.isEmpty() && bedList != null && cropList != null && runIdList != null) {
            System.out.println("enter when all lists are not null");
            // Build combinations dynamically
            for (Long runId : runIdList) {
                for (String bedIndex : bedList) {
                    for (String crop : cropList) {
                        for (String leafConfiguration : leafConfigurationList) {
                            // Create an array for each combination
                            ArrayNode combination = objectMapper.createArrayNode();
                            combination.add(runId); // runid
                            //        combination.add(bedIndex); // bed index
                            // add week only for hourly case not for weekly
                            if (type.equalsIgnoreCase("hourly"))
                                combination.add(weekInt); // weeks
                            combination.add(bedIndex);
                            combination.add(crop);

                            // Add "sunlit" and "sunshade" as a list if "all" is specified
//                if ("all".equals(leafConfiguration)) {
//                    ArrayNode sunlitSunshade = objectMapper.createArrayNode();
//                    sunlitSunshade.add("sunlit");
//                    sunlitSunshade.add("sunshaded");
//                    combination.add(sunlitSunshade);
//                } else {
                            // Otherwise, add the single leaf configuration (e.g., "sunlit" or "sunshade")
                            combination.add(leafConfiguration);
                            // }

                            // Add each combination to the outer array
                            combinationArray.add(combination);
                        }
                    }
                }

            }
        } else if (bedList != null && runIdList != null && cropList != null) {
            System.out.println("enter when all lists are not null");
            for (Long runId : runIdList) {
                for (String bedIndex : bedList) {
                    for (String crop : cropList) {

                        // Create an array for each combination
                        ArrayNode combination = objectMapper.createArrayNode();
                        combination.add(runId); // runid
                        combination.add(bedIndex); // bed index
                        // add week only for hourly case not for weekly
                        if (type.equalsIgnoreCase("hourly"))
                            combination.add(weekInt); // weeks
                        combination.add(crop);

                        // Add each combination to the outer array
                        combinationArray.add(combination);

                    }
                }

            }
        }
        System.out.println("outside all loops");
        // Convert the combination array to a JSON string
        String combinationJson = objectMapper.writeValueAsString(combinationArray);
        return combinationJson;

    }


    //    // ***********  hourlywithinrun  *****************
//
    public List<Map<String, Object>> forAllCasesSameMethod(List<Long> runIdList,
                                                           List<String> cycleList,
                                                           List<String> bedList,
                                                           List<String> weekList,
                                                           List<String> cropList,
                                                           List<String> leafConfigurationList, String quantity) {


        try {
            // call makeCombinations method
            String combinationJson = makeCombinations(runIdList, cycleList, bedList, weekList, cropList, leafConfigurationList, "hourly", "quantity");

            //  Hardcoded JSON array formatted as a string
            String hardcodedJson = "[[298, 1, 1, \"sorghum\", \"sunlit\"], [298, 1, 1, \"sorghum\", \"sunshaded\"], [298, 1, 1, \"sorghum\", \"all\"]]";

//            List<Map<String, Object>> queryResults = callFunctionFromDB(combinationJson);
            List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.carbonAssimilationFunctionFromDB(combinationJson);

//            List<Map<String, Object>> result = responseForPostProcessingGraph(queryResults);
            List<Map<String, Object>> result = postProcessingFunctionsHelper.responseForPostProcessingGraph(queryResults, quantity);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // post processing details
    @Override
    public Map<String, List<PostProcessingDetailsResponseDto>> getPostprocessingDetails(List<Long> runIds, Long userId, Long projectId, String dataType, String frequency) {
// checks -- userId checks , runs belong with same group , null checks
        if (userId == null || userId <= 0)
            throw new UnAuthorizedException(null, "user.not.found");

        Projects project = userProjectsRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("project.not.found"));
        List<Long> projectRuns = project.getRunIds();
        if (!projectRuns.containsAll(runIds)) {
            throw new UnprocessableException("run.invalid");
        }
        System.out.println("Project Id " + project.getProjectId());

        Long existingUserId = project.getUserProfile().getUserId();
        System.out.println("UserID" + userId);
        System.out.println("Existing user ID " + existingUserId);
        Set<String> uniqueCropList = new HashSet<>();
        Set<String> uniqueControlCropNames = new HashSet<>();


        if (!userId.equals(existingUserId)) {
            throw new UnAuthorizedException("user.not.authorized");
        }

        //call validation function for -- runs belong same group
//logic

        Map<String, List<PostProcessingDetailsResponseDto>> responseMap = new HashMap<>();

        if (frequency.equalsIgnoreCase("weekly")) {
            List<PostProcessingDetailsResponseDto> postProcessingDetailsResponseDtoList = runIds.stream().map(runId -> {
                Runs run = runsRepository.findById(runId)
                        .orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
                String runName = run.getRunName();
                Double bedCount = 0.0;
                if(run.getAgriGeneralParameters() != null)
                	bedCount =  run.getAgriGeneralParameters().getBedParameter().getBedcc();

                // Initialize sets to store unique values
                Set<String> uniqueCropNames = new HashSet<>();
                Set<String> uniqueBeds = new HashSet<>();
                Set<String> uniqueWeeks = new HashSet<>();
//                Set<String> uniqueControlCropNames = new HashSet<>();

                if (run.getCropParameters() != null) {
                    List<Cycles> cycles = run.getCropParameters().getCycles();

                    cycles.forEach(cycle -> {
                        // Get all beds from the cycle
                    	List<List<String>> bedWithCrop = new ArrayList<>();
                        Map<String,List<String>> bedCropsList = new HashMap<>();
                        cycle.getBeds().stream().forEach(bed -> {
                        	bedCropsList.put(bed.getBedName(), bed.getCropBed().stream().map(t-> t.getCrop().getName()).collect(Collectors.toList()));
                        });
                    	
                        List<Bed> beds = cycle.getBeds();
                        beds.forEach(bed -> {
                            // Add bed names to the unique set
                            uniqueBeds.add(bed.getBedName().replaceAll("\\D", ""));

                            // Add crop names to the unique set
                            bed.getCropBed().forEach(cropBedSection -> {
                                uniqueCropNames.add(cropBedSection.getCrop().getName());
                            });
                        });

                        // Calculate weeks for each cycle and add them to the unique set
                        LocalDate cycleStartDate = cycle.getStartDate();
                        int maxDuration = postprocessingHelper.findMaxDuration(beds);
                        List<Integer> weeksList = postprocessingHelper.calculateIntervalsForCrop(cycleStartDate, maxDuration);
                        weeksList.forEach(week -> uniqueWeeks.add("W" + week));
                    });
                }
                
                Optional<Long> controlRunId = this.findControlRunIdForPostProcessing(runIds);
                if(controlRunId.isPresent()) {
                Runs controlRun = runsRepository.findById(controlRunId.get())
                        .orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
                
                if (controlRun.getCropParameters() != null) {
                    List<Cycles> cycles = controlRun.getCropParameters().getCycles();

                    cycles.forEach(cycle -> {
                        // Get all beds from the cycle
//                    	List<List<String>> bedWithCrop = new ArrayList<>();
//                        Map<String,List<String>> bedCropsList = new HashMap<>();
//                        cycle.getBeds().stream().forEach(bed -> {
//                        	bedCropsList.put(bed.getBedName(), bed.getCropBed().stream().map(t-> t.getCrop().getName()).collect(Collectors.toList()));
//                        });
                    	
                        List<Bed> beds = cycle.getBeds();
                        beds.forEach(bed -> {
                            // Add bed names to the unique set
//                            uniqueBeds.add(bed.getBedName().replaceAll("\\D", ""));

                            // Add crop names to the unique set
                            bed.getCropBed().forEach(cropBedSection -> {
                                uniqueControlCropNames.add(cropBedSection.getCrop().getName());
                            });
                        });

                        // Calculate weeks for each cycle and add them to the unique set
//                        LocalDate cycleStartDate = cycle.getStartDate();
//                        int maxDuration = postprocessingHelper.findMaxDuration(beds);
//                        List<Integer> weeksList = postprocessingHelper.calculateIntervalsForCrop(cycleStartDate, maxDuration);
//                        weeksList.forEach(week -> uniqueWeeks.add("W" + week));
                    });
                }
            }
                

                // Create the response DTO
                PostProcessingDetailsResponseDto responseDto = new PostProcessingDetailsResponseDto();
             // Generate bed list based on bedCount
                if (bedCount > 0) {
                    // Create a list of numbers as strings from 1 to bedCount
                    List<String> bedNumbers = IntStream.rangeClosed(1, bedCount.intValue())
                            .mapToObj(String::valueOf) // Convert each number to a string
                            .collect(Collectors.toList());
                    responseDto.setBeds(bedNumbers); // Set the list of bed numbers as strings
                } else {
                    responseDto.setBeds(Collections.emptyList()); // Set an empty list if no beds
                }
                responseDto.setRunName(runName);
                responseDto.setId(runId);
                responseDto.setCropName(uniqueCropNames);
                responseDto.setControlCropName(uniqueControlCropNames);
//                responseDto.setBeds(new ArrayList<>(uniqueBeds));
                responseDto.setWeeks(new ArrayList<>(uniqueWeeks));

                // Calculate and set pv weeks (1 Jan to 31 Dec)
                List<Integer> pvWeeksList = postprocessingHelper.calculateIntervalsForCrop(LocalDate.of(2023, 1, 1), 364);
                
                List<String> dynamicIntervals = generateIntervals(intervalType);
                responseDto.setWeekIntervals(dynamicIntervals);
                List<String> result = generateDynamicIntervals(pvWeeksList);
                
                
                List<String> pvWeeks = pvWeeksList.stream()
                        .map(week -> "W" + week)
                        .collect(Collectors.toList());
                responseDto.setPvWeeks(pvWeeks);

                return responseDto;
            }).collect(Collectors.toList());

// Prepare the final response map
            responseMap.put("runs", postProcessingDetailsResponseDtoList);
		} else {
			List<PostProcessingDetailsResponseDto> postProcessingDetailsResponseDtoList = runIds.stream()
					.map((runId) -> {
						Runs run = runsRepository.findById(runId)
								.orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
						String runName = run.getRunName();
						Double bedCount = null;
                        if(run.getAgriGeneralParameters() != null)
                        	bedCount = run.getAgriGeneralParameters().getBedParameter().getBedcc();

                        final Double finalBedCount = bedCount;
						// handle cycles
						List<PostCycleResponse> cyclesDtos = new ArrayList<>();
						if (run.getCropParameters() != null) {
							List<Cycles> cyclesUnsorted = run.getCropParameters().getCycles();
							
//							List<Cycles> cycles = cyclesUnsorted.stream()
//							        .sorted(Comparator.comparing(Cycles::getStartDate))
//							        .collect(Collectors.toList());
							
							List<Cycles> cycles = cyclesUnsorted.stream()
								    .sorted(Comparator.comparing(cycle -> 
								        cycle.getStartDate().getMonthValue() * 100 + cycle.getStartDate().getDayOfMonth()
								    ))
								    .collect(Collectors.toList());


							cyclesDtos = cycles.stream().map(cycle -> {
								PostCycleResponse postCycleResponse = new PostCycleResponse();
								postCycleResponse.setName(cycle.getName());
								postCycleResponse.setId(cycle.getId());
								List<Bed> beds = cycle.getBeds();
								List<List<String>> bedWithCrop = new ArrayList<>();
								Map<String, List<String>> bedCropsList = new HashMap<>();
								cycle.getBeds().stream().forEach(bed -> {
									bedCropsList.put(bed.getBedName(), bed.getCropBed().stream()
											.map(t -> t.getCrop().getName()).collect(Collectors.toList()));
								});

								List<String> interbeds = cycle.getInterBedPattern();
								int size = interbeds.size();
								ArrayList<String> arr = new ArrayList<>();

								if (size > 0) {
									for (int i = 0; i < finalBedCount; i++) {
										if (i == 0) {
											arr.add(interbeds.get(0));
										} else {
											arr.add(interbeds.get(i % size));
										}

										if (bedCropsList.containsKey(arr.get(i))) {
											bedWithCrop.add(bedCropsList.get(arr.get(i)));
										}
									}

								} else {
									if (!bedCropsList.isEmpty()) {
								        List<String> firstCropList = new ArrayList<>();

								        // Get crops from the first entry of bedCropsList
								        Map.Entry<String, List<String>> firstEntry = bedCropsList.entrySet().iterator().next();
								        firstCropList = firstEntry.getValue();

								        // Repeat crops from the first entry up to bedCount
								        for (int i = 0; i < finalBedCount; i++) {
								            bedWithCrop.add(firstCropList);
								        }
								    }
								}

								// logic calculate weeks
								LocalDate cycleStartDate = cycle.getStartDate();
								int maxDuration = postprocessingHelper.findMaxDuration(beds);
								System.out.println("max duration is :" + maxDuration);
//                            List<Integer> weeksList = calculateWeeksForCrop(cycleStartDate, maxDuration);
								List<Integer> weeksList = postprocessingHelper.calculateIntervalsForCrop(cycleStartDate,
										maxDuration);
								List<String> weeks = weeksList.stream().map(week -> "W" + week)
										.collect(Collectors.toList());

//                            postCycleResponse.setWeeks(List.of("W01", "W02", "W03", "W04", "W05"));
								postCycleResponse.setWeeks(weeks);

								postCycleResponse.setBeds(bedWithCrop);
								Set<String> cropName = new HashSet<>();

								beds.forEach((bed) -> {
									List<CropBedSection> cropBedSections = bed.getCropBed();
									cropName.add(cropBedSections.get(0).getCrop().getName());
									uniqueCropList.add(cropBedSections.get(0).getCrop().getName());
									if (cropBedSections.size() > 1) {
										cropName.add(cropBedSections.get(1).getCrop().getName());
										uniqueCropList.add(cropBedSections.get(1).getCrop().getName());

									}
								});
								postCycleResponse.setCrops(cropName);
								return postCycleResponse;

							}).collect(Collectors.toList());
						}
						
						Optional<Long> controlRunId = this.findControlRunIdForPostProcessing(runIds);
						
						if(controlRunId.isPresent()) {
		                Runs controlRun = runsRepository.findById(controlRunId.get())
		                        .orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
		                
		                if (controlRun.getCropParameters() != null) {
		                    List<Cycles> cycles = controlRun.getCropParameters().getCycles();

		                    cycles.forEach(cycle -> {
		                    	
		                        List<Bed> beds = cycle.getBeds();
		                        beds.forEach(bed -> {
		                            bed.getCropBed().forEach(cropBedSection -> {
		                                uniqueControlCropNames.add(cropBedSection.getCrop().getName());
		                            });
		                        });
		                    });
		                }
					}
		                
						
                        PostProcessingDetailsResponseDto responseDto = new PostProcessingDetailsResponseDto();
                        responseDto.setRunName(runName);
                        responseDto.setId(runId);
                        responseDto.setCropName(uniqueCropList);
                        responseDto.setControlCropName(uniqueControlCropNames);
                        responseDto.setCycles(cyclesDtos);

                        //*********  pv weeks - 1 jan to 31 dec - all block index
                        List<Integer> pvWeeksList = postprocessingHelper.calculateIntervalsForCrop(LocalDate.of(2023, 1, 1), 364);
                        List<String> pvWeeks = pvWeeksList.stream()
                                .map(week -> "W" + week)
                                .collect(Collectors.toList());
                        responseDto.setPvWeeks(pvWeeks);
                        List<String> result = generateDynamicIntervals(pvWeeksList);
                        List<String> dynamicIntervals = generateIntervals(intervalType);
                        responseDto.setWeekIntervals(dynamicIntervals);

                        return responseDto;

                    }).

                    collect(Collectors.toList());

            responseMap.put("runs", postProcessingDetailsResponseDtoList);
        }
        return responseMap;
    }
    
    public List<String> generateDynamicIntervals(List<Integer> weekIndexes) {
        int maxWeek = weekIndexes.stream().max(Integer::compare).orElse(14); // Default to 14 if empty
        return generateIntervals(maxWeek);
    }
    
    private List<String> generateIntervals(int intervalDays) {
        List<String> intervals = new ArrayList<>();
        int weeksInYear = 52;
        int intervalCount = intervalDays / 7; // Convert days to weekly intervals

        for (int i = 1; i <= weeksInYear; i += intervalCount) {
            int startWeek = i;
            int endWeek = i + intervalCount - 1;

            // Format the interval as a two-part string, e.g., "0102", "0304"
            String interval = String.format("%02d", startWeek) + "-" + String.format("%02d", endWeek);
            intervals.add(interval);
        }

        return intervals;
    }



    //validation
// to check all runs belong in same group
    public boolean isSameGroup(List<Runs> runs) {
        if (runs == null || runs.isEmpty()) {
            return true; // Consider an empty list as valid
        }

        // Find the master run (run with cloneId == null)
        Optional<Runs> masterRunOpt = runs.stream()
                .filter(run -> run.getCloneId() == null)
                .findFirst();

        if (!masterRunOpt.isPresent()) {
            throw new InvalidDataException("masterRun.not.found");
        }

        Runs masterRun = masterRunOpt.get();

        // Check that all other runs have cloneId matching the master run's runId
        Long masterRunId = masterRun.getRunId();
        boolean allMatch = runs.stream()
                .allMatch(run -> run.equals(masterRun) || masterRunId.equals(run.getCloneId()));

        if (!allMatch) {
            throw new InvalidDataException("list.contains");
        }

        return true; // All runs belong to the same group
    }


    // query for get epw file path
    public Object[] getSimulationIdAndFilePathByRunId(Long runId) {
        String query = "SELECT s.id, st.weather_condition->>'dataSourceUrl' AS file_url " +
                "FROM simtool.simulations s " +
                "JOIN simtool.simulation_tasks st ON s.id = st.simulation_id " +
                "WHERE s.run_id = ?"
                + "LIMIT 1";
        return jdbcTemplate.queryForObject(query, new Object[]{runId}, (rs, rowNum) -> {
            Long simulationId = rs.getLong("id");
            String fileUrl = rs.getString("file_url");
            return new Object[]{simulationId, fileUrl};
        });
    }


    // parse epw file
    public List<List<Object>> fetchAndProcessEpwFile(String fileUrl, Integer targetColumnIndex,boolean isSkip) throws Exception {
        // Download and process the EPW file directly from the URL
        //  HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() == 200) {
            System.out.println("response is getting correctly from ");
            try (InputStream inputStream = response.body();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

                return parseEPWFileFromStream(br, targetColumnIndex,isSkip);
            }
        } else {
            throw new RuntimeException("Failed to fetch file. HTTP status code: " + response.statusCode());
        }
    }

    public List<List<Object>> parseEPWFileFromStream(BufferedReader br, Integer targetColumnIndex,boolean isSkip) throws Exception {
        List<List<Object>> extractedData = new ArrayList<>();
        String line;
        int lineNumber = 0;

        //  System.out.println("lines of file of file is " + br.readLine());
//        while ((line = br.readLine()) != null) {
//            lineNumber++;
//
//            // Skip header lines (assuming first 8 lines are header)
//            if (lineNumber <= 8) continue;
//
//            String[] columns = line.split(",");
////            System.out.println("Columns :" + columns);
//
//            // Extract relevant fields
//            String date = String.format("%s-%s-%s", columns[0], columns[1], columns[2]); // Year-Month-Day
//            int hour = Integer.parseInt(columns[3].trim());
//            //String hour = columns[3].trim();
//            System.out.println("hour is " + hour);
//            double value = Double.parseDouble(columns[targetColumnIndex]);
//            if (value < 0) {
//                throw new UnprocessableException("data.negative");
//            }
//            //String value = columns[targetColumnIndex];
//            System.out.println("value is " + value);
//
//
//            //    System.out.println("getting value from file is " + value);
//            // Add data to the result
//            List<Object> row = new ArrayList<>();
//            row.add(date);
//            row.add(hour);
//            row.add(value);
//
//            extractedData.add(row);
//        }
//
//        return extractedData;
//    }

        int skipDays = 13; // Days to skip after processing 24 hours for one date

        String currentDate = null;
        int hoursCount = 0;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            // Skip header lines (assuming first 8 lines are header)
            if (lineNumber <= 8) continue;

            String[] columns = line.split(",");

            // Extract relevant fields
            String date = String.format("%s-%s-%s", columns[0], columns[1], columns[2]); // Year-Month-Day
            int hour = Integer.parseInt(columns[3].trim())-1;
            double value = Double.parseDouble(columns[targetColumnIndex]);

            // Check for negative values
//            if (value < 0) {
//                throw new UnprocessableException("data.negative");
//            }

            // If it's a new date, reset hoursCount
            if (currentDate == null || !currentDate.equals(date)) {
                currentDate = date;
                hoursCount = 0;
            }

            // Process the 24 hours of data for the current day
            if (hoursCount < 24) {
                List<Object> row = new ArrayList<>();
                row.add(date);
                row.add(hour);
                row.add(value);

                extractedData.add(row);
                hoursCount++;
            }

            // After processing 24 hours, skip the next 'skipDays' worth of data
            if (hoursCount == 24 && isSkip) {
                // Skip lines for the next 13 days (each day has 24 hours of data)
                for (int i = 0; i < skipDays * 24; i++) {
                    if ((line = br.readLine()) == null) break;
                    lineNumber++;
                }
                currentDate = null; // Reset currentDate to start with the next valid day
            }
        }
        return extractedData;
    }
    // ************ Get PV Revenue ************
    public List<Map<String, Object>> getPvRevenue(List<Map<String, Object>> resultSet,
                                                  Map<Long, Map<Integer, Double>> tariffs,
                                                  String quantity,
                                                  List<Long> runIds) {


        if (resultSet == null || resultSet.isEmpty()) {
            throw new UnprocessableException("dcoutput.result.null");
        }
        if (tariffs == null || tariffs.isEmpty()) {
            throw new UnprocessableException("tariff.empty");
        }
        //  System.out.println("enter to calculate pv revenue method");

        // Group the resultSet by runId
        Map<Long, List<Map<String, Object>>> groupedByRunId = resultSet.stream()
                .collect(Collectors.groupingBy(row -> (Long) row.get("run_id")));

        // Prepare the result list
        List<Map<String, Object>> revenues = new ArrayList<>();

        // Calculate revenue for each runId in the order of runIds list
        for (Long runId : runIds) {
            List<Map<String, Object>> rowsForRunId = groupedByRunId.get(runId);
            double totalRevenue = 0.0;
            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            String runName = run.getRunName();
            String control = null;
            
            if(run.isAgriControl() == true) {
            	control = "Agri";
            }
            else if(run.isPvControl() == true) {
            	control = "PV";
            }
            
            // Get the corresponding tariff map for the current runId
            Map<Integer, Double> hourlyTariffMap = tariffs.get(runId);
            if (hourlyTariffMap == null) {
                continue;
            }

            if (rowsForRunId != null) {
                for (Map<String, Object> row : rowsForRunId) {
                    Integer hour = (Integer) row.get("hrs");
                    Double dcOutput = null;

                    // Determine the correct field based on the quantity
                    if (quantity.equalsIgnoreCase("PV Revenue Per Mega Watt")) {
                        //  System.out.println("before calculate value of pv revenue ");
                        dcOutput = (Double) row.get("total_pv_yields_per_megawatt");
                        //    System.out.println("after calculate value of pv revenue value :"+dcOutput);

                    } else if (quantity.equalsIgnoreCase("PV Revenue Per Acre")) {
                        dcOutput = (Double) row.get("per_area_acre");
                    } else {
                    	dcOutput = (Double) row.get("per_area_acre");
                    }

                    Double hourlyTariff = hourlyTariffMap.get(hour);
                    if (hourlyTariff != null && dcOutput != null) {
                        totalRevenue += dcOutput * hourlyTariff;
                        //  System.out.println("total revenue :"+totalRevenue);

                    }
                }
            }

            // Create a map for the calculated revenue
            Map<String, Object> revenueMap = new HashMap<>();
            revenueMap.put("runId", runId);
            revenueMap.put("runName", runName);
            revenueMap.put("value", totalRevenue);
            revenueMap.put("control", control);

            // Add the map to the result list
            revenues.add(revenueMap);
        }

        return revenues;
    }

    // ********** json combination for agri revenue *****************
    public String jsonCombinationForAgriRevenue(List<Long> runIds, List<String> cropList) {
    	try {
        Optional<Long> controlRunId = this.findControlRunIdForPostProcessing(runIds);
        if (controlRunId.isEmpty()) {
            throw new EntityNotFoundException("No control run exists.");
        }
        
        List<List<Object>> combinationList = new ArrayList<>();
        for (String crop : cropList) {
// Fetch reference yield from EconomicParameter entity
        	Integer referenceYield ;
        	try {
        		referenceYield = this.getReferenceYield(controlRunId.get(), crop);
        	}catch(Exception e) {
        		return null;
        	}

            // Build the combination entry: [controlRunId, RunIds, crop, referenceYield]
            List<Object> combinationEntry = Arrays.asList(controlRunId.get(), runIds, crop, referenceYield);
            combinationList.add(combinationEntry);
        }
        // Convert the combination list to a JSON string
        String combination = new Gson().toJson(combinationList);
        System.out.println("combination for agri revenue:" + combination);
        return combination;
    	}catch (EntityNotFoundException e) {
            throw e; // Propagate UnprocessableException
        } catch (Exception e) {
            throw new EntityNotFoundException("Error generating JSON combination for agri revenue: " + e.getMessage());
        }
    }
    
    //***************json combination of Transpiration
    public String jsonCombinationForTranspiration(List<Long> runIds, List<String> cropList, Integer from, Integer to) {
        try {
            // Check if the range from and to is valid
            if (from > to) {
                throw new IllegalArgumentException("Invalid range: 'from' cannot be greater than 'to'");
            }
            
            // Generate the list of numbers between 'from' and 'to' (inclusive)
            List<Integer> range = new ArrayList<>();
            for (int i = from; i <= to; i++) {
                range.add(i);
            }

            // Initialize the combination list
            List<List<Object>> combinationList = new ArrayList<>();
            
            // Loop over each crop in the cropList
            for (String crop : cropList) {
                // Build the combination entry: [runIds, crop, range]
                // Combine each runId with the crop and range
                for (Long runId : runIds) {
                    List<Object> combinationEntry = Arrays.asList(runId, crop, range);
                    combinationList.add(combinationEntry);
                }
            }

            // Convert the combination list to a JSON string
            String combination = new Gson().toJson(combinationList);
            System.out.println("Combination for transpiration: " + combination);
            return combination;

        } catch (EntityNotFoundException e) {
            throw e; // Propagate UnprocessableException
        } catch (Exception e) {
            throw new EntityNotFoundException("Error generating JSON combination for transpiration: " + e.getMessage());
        }
    }


    

    public Long findControlRunId(List<Long> runIds) {
        return runsRepository.findAgriControlRunId(runIds).orElseThrow(() -> new ResourceNotFoundException("agri.control.not.found"));
    }
    
    public Optional<Long> findControlRunIdForPostProcessing(List<Long> runIds) {
        return runsRepository.findAgriControlRunId(runIds);
    }

    public Integer getReferenceYield(Long runId, String crop) {
        Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
        
        if (run.getEconomicParameters() == null || run.getEconomicParameters().getEconomicMultiCrop() == null) {
            throw new EntityNotFoundException("Economic parameters or Economic Multi-Crop data is missing for run: " + run.getRunName());
        }
      
        List<EconomicMultiCrop> economicMultiCrops = run.getEconomicParameters().getEconomicMultiCrop();
        if (!economicMultiCrops.isEmpty()) {
            for (EconomicMultiCrop economicParameterCrop : economicMultiCrops) {
                if (economicParameterCrop.getCrop().getName().equalsIgnoreCase(crop))
                    return economicParameterCrop.getMinReferenceYieldCost().intValue();
            }
        }
        return 0;
//        throw new UnprocessableException("No reference yield found for crop: " + crop + " in run: " + run.getRunName());

    }

    // ********* get agri revenue ***********
    public List<Map<String, Object>> getAgriRevenue(List<Map<String, Object>> queryResults) {
        if (queryResults == null || queryResults.isEmpty()) {
            return List.of();
        }

        List<Map<String, Object>> agriRevenueResults = new ArrayList<>();

        for (Map<String, Object> row : queryResults) {
            String cropName = (String) row.get("crop_name");
            Double yieldPerAcre = (Double) row.get("yield_per_acre");
            Long runId = (Long) row.get("run_id");

            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            String runName = run.getRunName();
            String control = null;
            
            if(run.isPvControl() == true) {
            	control = "PV";
            }else if(run.isAgriControl() == true) {
            	control = "Agri";
            }
            List<EconomicMultiCrop> economicMultiCrops = run.getEconomicParameters().getEconomicMultiCrop();
            Double sellingCost = 0.0;

            if (!economicMultiCrops.isEmpty()) {
                for (EconomicMultiCrop economicParameterCrop : economicMultiCrops) {
                    if (economicParameterCrop.getCrop().getName().equalsIgnoreCase(cropName)) {
                        sellingCost = economicParameterCrop.getMinSellingCostOfCrop() * 1000;
                    }
                }

                Double totalRevenue = sellingCost * yieldPerAcre;

                Map<String, Object> revenueRow = new HashMap<>();
                revenueRow.put("run_id", runId);
                revenueRow.put("run_name", runName);
                revenueRow.put("crop_name", cropName);
                revenueRow.put("value", totalRevenue);
                revenueRow.put("control", control);

                agriRevenueResults.add(revenueRow);
            }
        }

        return agriRevenueResults;
    }
    
    
    //*********get agregate value ****************
    public List<Map<String, Object>> processYieldData(List<Map<String, Object>> response, String val) {
        // Group by run_id and sum yield_per_acre for each run_id
    	Map<Number, Double> aggregatedData = response.stream()
                .collect(Collectors.groupingBy(
                        m -> {
                            Object runId = m.get("run_id");
                            if (runId instanceof Long) {
                                return (Long) runId;  // Safely cast to Long
                            } else if (runId instanceof Integer) {
                                return (Integer) runId;  // Safely cast to Integer
                            } else {
                                throw new IllegalArgumentException("Unexpected run_id type");
                            }
                        },
//                        "yield_per_acre"
                        Collectors.summingDouble(m -> {
                            Object yield = m.get(val);
                            return yield instanceof Double ? (Double) yield : 0.0;  // Safely cast yield_per_acre to Double
                        })
                ));
    	
    	// Fetch all run names in one query for efficiency
        Set<Long> runIds = aggregatedData.keySet().stream()
                .map(id -> ((Number) id).longValue()) // Convert Number to Long
                .collect(Collectors.toSet());

        Map<Long, Runs> runsMap = runsRepository.findAllById(runIds).stream()
                .collect(Collectors.toMap(Runs::getRunId, run -> run));

        // Convert map to a list of objects with aggregated data
        return aggregatedData.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("run_id", entry.getKey());
                    Long runId = entry.getKey().longValue();
                    Runs run = runsMap.get(runId);
                    
                 // Determine control value based on pvControl and agriControl
                    String control = null;
                    if (run != null) {
                        if (Boolean.TRUE.equals(run.isPvControl())) {
                            control = "PV";
                        } else if (Boolean.TRUE.equals(run.isAgriControl())) {
                            control = "Agri";
                        }
                    }
                    result.put("runName", run != null ? run.getRunName() : "Unknown Run"); // Get run name, default if not found
                    result.put("crop_name", "");
                    result.put("control", control);
                    result.put("value", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

  //*********get agregate value for Carbon Assimilation ****************
    public List<Map<String, Object>> processCarbonYieldData(List<Map<String, Object>> response, String val) {
        // Group by run_id and sum yield_per_acre for each run_id
    	Map<Number, BigDecimal> aggregatedData = response.stream()
                .collect(Collectors.groupingBy(
                        m -> {
                            Object runId = m.get("run_id");
                            if (runId instanceof Long) {
                                return (Long) runId;  // Safely cast to Long
                            } else if (runId instanceof Integer) {
                                return (Integer) runId;  // Safely cast to Integer
                            } else {
                                throw new IllegalArgumentException("Unexpected run_id type");
                            }
                        },
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                m -> {
                                    Object yield = m.get(val);
                                    return yield instanceof BigDecimal ? (BigDecimal) yield : BigDecimal.ZERO;
                                },
                                BigDecimal::add // Aggregate using BigDecimal addition
                        )
                ));
    	// Fetch all run names in one query for efficiency
        Set<Long> runIds = aggregatedData.keySet().stream()
                .map(id -> ((Number) id).longValue()) // Convert Number to Long
                .collect(Collectors.toSet());

        Map<Long, String> runNames = runsRepository.findAllById(runIds).stream()
                .collect(Collectors.toMap(Runs::getRunId, Runs::getRunName));

        // Convert map to a list of objects with aggregated data
        return aggregatedData.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("run_id", entry.getKey());
                    result.put("runName", runNames.getOrDefault(entry.getKey(), "Unknown Run")); // Get run name, default if not found
                    result.put("crop_name", "");
                    result.put("value", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }


    //*********** get total revenue ****************
    public List<Map<String, Object>> getTotalRevenue(List<Map<String, Object>> pvRevenueResults, List<Map<String, Object>> agriRevenueResults) {
    	Map<Long, Double> pvRevenueMap = new HashMap<>();
    	Map<Long, String> pvControlMap = new HashMap<>();
    	
    	if(pvRevenueResults != null) {
        	pvRevenueMap = pvRevenueResults.stream()
                    .collect(Collectors.toMap(row -> (Long) row.get("runId"), row -> (Double) row.get("value")));
        	
        	pvControlMap = pvRevenueResults.stream()
                    .filter(row -> row.get("control") != null)
                    .collect(Collectors.toMap(row -> (Long) row.get("runId"), row -> (String) row.get("control"), (c1, c2) -> c1));
        }else {
        	pvRevenueMap = null;
        	pvControlMap = null;
        }
    	Map<Long, Double> agriRevenueMap = new HashMap<>();
        Map<Long, String> agriControlMap = new HashMap<>();
    	if(agriRevenueResults != null) { 
    		agriRevenueMap = agriRevenueResults.stream()
                .collect(Collectors.toMap(row -> (Long) row.get("run_id"), row -> (Double) row.get("value")));
    		
    		agriControlMap = agriRevenueResults.stream()
                    .filter(row -> row.get("control") != null)
                    .collect(Collectors.toMap(row -> (Long) row.get("run_id"), row -> (String) row.get("control"), (c1, c2) -> c1));
    		
    	}else {
    		agriRevenueMap = null;
    		agriControlMap = null;
    	}

    		
//        List<Map<String, Object>> totalRevenueResults = new ArrayList<>();
//        
////        Set<Long> allRunIds = new HashSet<>();
////        allRunIds.addAll(pvRevenueMap.keySet());
////        allRunIds.addAll(agriRevenueMap.keySet());
//
//
//        // Merge both revenues and set to 0 if any is missing
//		if (pvRevenueResults != null && agriRevenueResults != null) {
//			for (Long runId : pvRevenueMap.keySet()) {
//				double pvRevenue = pvRevenueMap.getOrDefault(runId, 0.0);
//				double agriRevenue = agriRevenueMap.getOrDefault(runId, 0.0);
//				System.out.println("pvRevenue :" + pvRevenue + " agriRevenue :" + agriRevenue);
//				double totalRevenue = pvRevenue + agriRevenue;
//				System.out.println("total Revenue :" + totalRevenue);
//				Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
//	            String runName = run.getRunName();
//	            
//	            String controlPv = pvControlMap.get(runId);
//	            String controlAgri = agriControlMap.get(runId);
//	            String control = null;
//	            
//	            if(controlPv == null && controlAgri == null) {
//	            	control = null;
//	            }
//	            else if(controlPv != null) {
//	            	control = controlPv;
//	            }
//	            else if(controlAgri != null) {
//	            	control = controlAgri;
//	            }
//
//
//				Map<String, Object> revenueRow = new HashMap<>();
//				revenueRow.put("runId", runId);
//				revenueRow.put("value", totalRevenue);
//				revenueRow.put("runName", runName);
//				revenueRow.put("control", control); // Combined control value
//
//
//				totalRevenueResults.add(revenueRow);
//			}
//		}else if(pvRevenueResults == null) {
//			for (Long runId : agriRevenueMap.keySet()) {
//	            if (agriRevenueMap.containsKey(runId)) {
//	                double agriRevenue = agriRevenueMap.getOrDefault(runId, 0.0);
//	                Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
//		            String runName = run.getRunName();
//		            String control = agriControlMap.get(runId);
//
//	                Map<String, Object> revenueRow = new HashMap<>();
//	                revenueRow.put("runId", runId);
//	                revenueRow.put("runName", runName);
//	                revenueRow.put("value", agriRevenue);
//	                revenueRow.put("control", control);
//
//	                totalRevenueResults.add(revenueRow);
//	            }
//	        }
//		}else if(agriRevenueResults == null){
//
//        // Handle Agri Revenue-only runs (if not present in PV Revenue)
//        for (Long runId : pvRevenueMap.keySet()) {
//            if (!pvRevenueMap.containsKey(runId)) {
//                double pvRevenue = pvRevenueMap.getOrDefault(runId, 0.0);
//                Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
//	            String runName = run.getRunName();
//	            String control = pvControlMap.get(runId);
//
//                Map<String, Object> revenueRow = new HashMap<>();
//                revenueRow.put("runId", runId);
//                revenueRow.put("value", pvRevenue);
//                revenueRow.put("runName", runName);
//                revenueRow.put("control", control);
//
//                totalRevenueResults.add(revenueRow);
//            }
//        }
//		}
    	
    	// **Step 1: Get all unique runIds from both revenue maps**
        Set<Long> allRunIds = new HashSet<>();
        if(pvRevenueMap != null)
        	allRunIds.addAll(pvRevenueMap.keySet());
        if(agriRevenueMap != null)
        	allRunIds.addAll(agriRevenueMap.keySet());

        List<Map<String, Object>> totalRevenueResults = new ArrayList<>();

        // **Step 2: Iterate through all unique runIds and compute revenue**
        for (Long runId : allRunIds) {
        	double pvRevenue = 0.0;
        	double agriRevenue = 0.0;
        	if(pvRevenueMap != null)
        		pvRevenue = pvRevenueMap.getOrDefault(runId, 0.0);
        	if(agriRevenueMap != null)
        		agriRevenue = agriRevenueMap.getOrDefault(runId, 0.0);
            double totalRevenue = pvRevenue + agriRevenue;

            Runs run = runsRepository.findById(runId)
                    .orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            String runName = run.getRunName();

            // **Step 3: Determine control value**
            String controlPv = null;
            String controlAgri = null;
            if(pvControlMap != null)
            	controlPv = pvControlMap.get(runId);
            if(agriControlMap != null)
            	controlAgri = agriControlMap.get(runId);
            String control = (controlPv != null) ? controlPv : controlAgri; // Prioritize PV, fallback to Agri

            // **Step 4: Store result**
            Map<String, Object> revenueRow = new HashMap<>();
            revenueRow.put("runId", runId);
            revenueRow.put("value", totalRevenue);
            revenueRow.put("runName", runName);
            revenueRow.put("control", control); // Set control from either PV or Agri

            totalRevenueResults.add(revenueRow);
        }


        return totalRevenueResults;
    }

    //*********** get sum of input cost for that run ***********
    public Map<Long, Double> getInputCostForRuns(List<Long> runIds) {
        Map<Long, Double> inputCosts = new HashMap<>();

        for (Long runId : runIds) {
            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

            double totalInputCost = run.getEconomicParameters().getEconomicMultiCrop().stream()
                    .mapToDouble(EconomicMultiCrop::getMinInputCostOfCrop)
                    .sum();

            inputCosts.put(runId, totalInputCost);
        }

        return inputCosts;
    }

    // ********* get profit **************
    public List<Map<String, Object>> getProfit(List<Map<String, Object>> totalRevenueResults, Map<Long, Double> inputCosts) {
        List<Map<String, Object>> profitResults = new ArrayList<>();

        for (Map<String, Object> row : totalRevenueResults) {
            Long runId = (Long) row.get("runId");
            Double totalRevenue = (Double) row.get("value");
            Double inputCost = inputCosts.getOrDefault(runId, 0.0);
            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            String runName = run.getRunName();
            
            String control = (String) row.get("control");

            double profit = totalRevenue - inputCost;

            Map<String, Object> profitRow = new HashMap<>();
            profitRow.put("runId", runId);
            profitRow.put("value", profit);
            profitRow.put("runName", runName);
            profitRow.put("control", control);

            profitResults.add(profitRow);
        }

        return profitResults;
    }


}

