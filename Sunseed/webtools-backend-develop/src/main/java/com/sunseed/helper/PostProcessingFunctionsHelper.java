package com.sunseed.helper;

import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.Runs;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.repository.RunsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class PostProcessingFunctionsHelper {
    @Value("${crop.intervalType}")
    private Integer intervalType;
    private final RunsRepository runsRepository;

    private final JdbcTemplate jdbcTemplate;

    // carbon assimilation functions
    public List<Map<String, Object>> carbonAssimilationFunctionFromDB(String combinationJson) {
        System.out.println("combination json in carbon assim function :" + combinationJson);
        return jdbcTemplate.query(
                "SELECT combination_json, total_carbon_assimilation,per_plant,per_area ,hour FROM public.get_combination_union_hourly_carbon_assimilation_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("total_carbon_assimilation", rs.getDouble("total_carbon_assimilation"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );
    }

    // ********** avg leaf temperature function *********************
    public List<Map<String, Object>> avgLeafTemperatureFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json, avg_temperature ,hour FROM public.get_combination_union_hourly_temperature_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("avg_temperature",  rs.getDouble("avg_temperature") == 0.0 ? 0.0 : Math.round((rs.getDouble("avg_temperature") - 273.15) * 100.0) / 100.0);
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());

                    return row;
                },
                combinationJson// Directly pass the JSON string
        );
    }

    // ************** penetration efficiency metric graph hourly and with in run  ***********
    public List<Map<String, Object>> penetrationEfficiencyFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json, avg_penetration ,hour FROM public.get_combination_union_hourly_penetration_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("avg_penetration", rs.getDouble("avg_penetration"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());

                    return row;
                },
                combinationJson// Directly pass the JSON string
        );
    }
// ************ radiation function call from db *************

    public List<Map<String, Object>> lightAbsorbedFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json, total_radiation ,hour,per_plant FROM public.get_combination_union_hourly_radiation_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("total_radiation", rs.getDouble("total_radiation"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    row.put("per_plant", rs.getDouble("per_plant"));

                    return row;
                },
                combinationJson// Directly pass the JSON string
        );
    }

    // ************latent flux  function call from db *************

    public List<Map<String, Object>> latentFluxFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json, total_latent_flux ,hour,per_plant,per_area FROM public.get_combination_union_hourly_latent_flux_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("total_latent_flux", rs.getDouble("total_latent_flux"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }
    // ************% Sunlit Leaves  function call from db *************

    public List<Map<String, Object>> sunlitLeavesFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json,hour,per_plant,per_area FROM public.get_combination_union_hourly_area_sunlit_percentage_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    // row.put("total_latent_flux", rs.getDouble("total_latent_flux"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }


    // ************ stauration function call from db *************

    public List<Map<String, Object>> saturationFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json,hour,avg_saturation FROM public.get_combination_union_hourly_saturation_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("avg_saturation", rs.getDouble("avg_saturation"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    //  row.put("per_plant", rs.getDouble("per_plant"));
                    //  row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }

    // ************ saturation extent per plant *********************
    public List<Map<String, Object>> saturationExtentFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json,hour,saturation_extent FROM public.get_combination_union_hourly_saturation_extent_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("saturation_extent", rs.getDouble("saturation_extent"));
                    row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    //  row.put("per_plant", rs.getDouble("per_plant"));
                    //  row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }


    // ******** Method for hourly bifacial gain and hourly dc power **********
    public List<Map<String, Object>> hourlyPvYields(Long runId, List<String> blockIndexes) {
        // Extract numeric parts from block indexes (e.g., "W01" -> 1, "W11" -> 11)
        int[] numericBlockIndexes = blockIndexes.stream()
                .map(index -> index.replaceAll("\\D", "")) // Remove non-digit characters
                .mapToInt(Integer::parseInt) // Convert to integer
                .toArray();

        // Construct the query
        String query = "SELECT hrs, bifacial_gain, total_pv_yields,week_index FROM get_hourly_data_pv_yield_v4(?, ?)";

        return jdbcTemplate.query(query,
                ps -> {
                    ps.setLong(1, runId); // Set the run ID

                    // Create SQL array for block indexes
                    java.sql.Array blockArray = ps.getConnection().createArrayOf("integer",
                            Arrays.stream(numericBlockIndexes).boxed().toArray(Integer[]::new));
                    ps.setArray(2, blockArray); // Set the array
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("hrs", rs.getInt("hrs")); // Hour of the day (0-23)
                    row.put("bifacial_gain", rs.getDouble("bifacial_gain")); // Bifacial gain as numeric
                    row.put("total_pv_yields", rs.getDouble("total_pv_yields")); // Total PV yield as numeric
                    row.put("week_index", rs.getInt("week_index"));
                    return row;
                });
    }

    // ************** hourly ppfd **************************
    public List<Map<String, Object>> hourlyPPFD(Long runId, List<String> blockIndexes) {
        // Extract numeric parts from block indexes (e.g., "W01" -> 1, "W11" -> 11)
        int[] numericBlockIndexes = blockIndexes.stream()
                .map(index -> index.replaceAll("\\D", "")) // Remove non-digit characters
                .mapToInt(Integer::parseInt) // Convert to integer
                .toArray();

        // Construct the query
        String query = "SELECT hrs, ppfd_pv_yields,week_index FROM get_hourly_data_pv_yield_ppfd_v2(?, ?)";

        return jdbcTemplate.query(query,
                ps -> {
                    ps.setLong(1, runId); // Set the run ID

                    // Create SQL array for block indexes
                    java.sql.Array blockArray = ps.getConnection().createArrayOf("integer",
                            Arrays.stream(numericBlockIndexes).boxed().toArray(Integer[]::new));
                    ps.setArray(2, blockArray); // Set the array
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("hrs", rs.getInt("hrs")); // Hour of the day (0-23)
                    row.put("ppfd_pv_yields", rs.getDouble("ppfd_pv_yields")); // ppfd pv yields
                    // row.put("total_pv_yields", rs.getDouble("total_pv_yields")); // Total PV yield as numeric
                    row.put("week_index", rs.getInt("week_index"));
                    return row;
                });
    }


    // ******** hourly pv yield with runId only for pv revenue****************
    public List<Map<String, Object>> getDcOutPutForPvRevenue(List<Long> runIds) {
        String query = "SELECT run_id,hrs, total_pv_yields_per_megawatt,per_area_acre FROM get_pv_yield_data_per_per_megawatt_per_acre_v2(?)";

        return jdbcTemplate.query(query,
                ps -> {
                    // Create SQL array for block indexes
                    // Create SQL array for run IDs as Long values
                    java.sql.Array runIdArray = ps.getConnection().createArrayOf("bigint", runIds.toArray(new Long[0]));
                    ps.setArray(1, runIdArray); // Set the array
                }, (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("hrs", rs.getInt("hrs"));
                    row.put("total_pv_yields_per_megawatt", rs.getDouble("total_pv_yields_per_megawatt"));
                    row.put("per_area_acre", rs.getDouble("per_area_acre"));

                    return row;
                }
        );

    }

    // *********** agri revenue function from db *********************
    public List<Map<String, Object>> getAgriRevenueFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT run_id,crop_name,yield_per_plant,yield_per_acre FROM public.get_yield_data_per_acre_revenue_v4(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("crop_name", rs.getString("crop_name"));
                    row.put("yield_per_acre", rs.getDouble("yield_per_acre"));
                    // row.put("hour", rs.getTimestamp("hour").toLocalDateTime().toLocalTime());
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }


    // *********** cumulative Carbon assim for bar chart *************
    public List<Map<String, Object>> getCumulativeCarbonAssimCrop(Long runId, String cropName, List<String> blockIndexes) {
        if (runId != null && cropName != null && !cropName.isEmpty() && blockIndexes != null && !blockIndexes.isEmpty()) {
            // Extract numeric parts from block indexes (e.g., "W01" -> 1, "W11" -> 11)
            int[] numericBlockIndexes = blockIndexes.stream()
                    .map(index -> index.replaceAll("\\D", "")) // Remove non-digit characters
                    .mapToInt(Integer::parseInt) // Convert to integer
                    .toArray();
// Construct the query
            String query = "SELECT  total_carbon_assimilation, per_plant,per_ground FROM public.get_biweekly_carbon_assimilation_crop_name_v3(?,?,?)";

            return jdbcTemplate.query(query,
                    ps -> {
                        ps.setLong(1, runId); // Set the run ID
                        ps.setString(2, cropName);

                        // Create SQL array for block indexes
                        java.sql.Array blockArray = ps.getConnection().createArrayOf("integer",
                                Arrays.stream(numericBlockIndexes).boxed().toArray(Integer[]::new));
                        ps.setArray(3, blockArray); // Set the array
                    },
                    (rs, rowNum) -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("total_carbon_assimilation", rs.getDouble("total_carbon_assimilation")); // Hour of the day (0-23)
                        row.put("per_plant", rs.getDouble("bifacial_gain")); // Bifacial gain as numeric
                        row.put("per_ground", rs.getDouble("total_pv_yields")); // Total PV yield as numeric
                        // row.put("week_index", rs.getInt("week_index"));
                        return row;
                    });
        } else {
            throw new UnprocessableException("some.field.empty");
        }

    }

    //cumulative carbon assimilation
    public List<Double> responseCumulativeCarbonAssim(List<Map<String, Object>> queryResult, String quantity) {
        Double data = null;
        for (Map<String, Object> row : queryResult) {
            if (quantity.equalsIgnoreCase("Cumulative Carbon Assim/Plant"))
                data = (Double) row.get("per_plant");
            else
                data = (Double) row.get("per_ground");


        }
        return List.of(data);
    }

    // ************ Get Tariff Data for PV Revenue ************
    public Map<Long, Map<Integer, Double>> getTariffData(List<Long> runIds) {
        Map<Long, Map<Integer, Double>> tariffMap = new HashMap<>();

        for (Long runId : runIds) {
            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

            if (run.getEconomicParameters() != null) {
                EconomicParameters economicParameters = run.getEconomicParameters();
                if (economicParameters.getHourlySellingRates() != null && economicParameters.getHourlySellingRates().length > 0) {
                    Map<Integer, Double> hourlyRates = new HashMap<>();
                    for (int i = 0; i < economicParameters.getHourlySellingRates().length; i++) {
                        hourlyRates.put(i, economicParameters.getHourlySellingRates()[i]);
                    }
                    tariffMap.put(runId, hourlyRates);
                }
            }
            else {
            	Map<Integer, Double> hourlyRates = new HashMap<>();
                for (int i = 0; i < 24; i++) {
                    hourlyRates.put(i, 0.0);
                }
                tariffMap.put(runId, hourlyRates);
            }
        }

        return tariffMap;
    }

    // *********** response for pv yield (hourly bifacial gain and dcpower ) **********
    public List<Map<String, Object>> responseForHourlyPvYield(
            List<Map<String, Object>> queryResults, String quantity, List<String> weekList, List<Long> runIdList) {

        // Map to store week-wise data
        Map<Integer, double[]> weekDataMap = new HashMap<>();
        Long runId = 0L;
        if (runIdList != null && !runIdList.isEmpty()) {
            runId = runIdList.get(0);
        }
        Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));


        // Populate the week data map based on query results
        queryResults.forEach(row -> {
            int weekIndex = (int) row.get("week_index");
            int hour = (int) row.get("hrs"); // Extract hour (0-23)
            double data;

            if (quantity.equalsIgnoreCase("Hourly DC Power")) {
                data = (double) row.get("total_pv_yields"); // Use DC Power
            } else if (quantity.equalsIgnoreCase("Hourly Bifacial Gain")) {
                data = (double) row.get("bifacial_gain"); // Use Bifacial Gain
            } else if (quantity.equalsIgnoreCase("PPFD")) {
                data = (double) row.get("ppfd_pv_yields"); // Use ppfd

            } else {
                throw new IllegalArgumentException("Unknown quantity type: " + quantity);
            }

            // Initialize the hourly array for the week if not already present
            weekDataMap.putIfAbsent(weekIndex, new double[24]);
            weekDataMap.get(weekIndex)[hour] = data; // Assign value to the corresponding hour
        });

        // Prepare the response list
        List<Map<String, Object>> responseList = new ArrayList<>();

        // Iterate over the given week indexes to ensure all are included
        for (String week : weekList) {
            int weekIndex = Integer.parseInt(week.replaceAll("\\D", "")); // Extract numeric part of the week
            double[] hourlyResponse = weekDataMap.getOrDefault(weekIndex, new double[24]); // Default to zero array if no data

            // Construct the combination object
            Map<String, Object> combination = new HashMap<>();
            combination.put("week", weekIndex);
            combination.put("runName", run.getRunName());

            // Construct the final response object
            Map<String, Object> weekResponse = new HashMap<>();
            weekResponse.put("response", Arrays.stream(hourlyResponse).boxed().collect(Collectors.toList())); // Convert array to list
            weekResponse.put("combination", combination);

            responseList.add(weekResponse);
        }

        return responseList;
    }

    // ************************* set response for post processing graphs for hourly and for with in run ***********************
    public List<Map<String, Object>> responseForPostProcessingGraph(List<Map<String, Object>> queryResults, String quantity) {

        List<LocalTime> hourRange = IntStream.range(0, 24)
                .mapToObj(i -> LocalTime.of(0, 0).plusHours(i))
                .collect(Collectors.toList());

        // Step 4: Aggregate results by unique combination, filling in hours with 0 when missing
        Map<String, List<Double>> groupedResults = new HashMap<>();


        for (Map<String, Object> queryRow : queryResults) {
            String combination = (String) queryRow.get("combination");
            LocalTime hour = (LocalTime) queryRow.get("hour");

            Double data = null;
            if (quantity.equalsIgnoreCase("Rate Of Carbon Assim / Plant")) {
                System.out.println("rate of carbon assimulation per plant response :");
                data = (Double) queryRow.get("per_plant");

            } else if (quantity.equalsIgnoreCase("Rate Of Carbon Assim / Ground")) {
                System.out.println("rate of carbon assimulation per ground response :");
                data = (Double) queryRow.get("per_area");

            } else if (quantity.equalsIgnoreCase("Avg. Leaf Temperature")) {
                System.out.println("Avg Leaf Temperature :");
                data = (Double) queryRow.get("avg_temperature");

            } else if (quantity.equalsIgnoreCase("Penetration Efficiency Metric")) {
                System.out.println("Penetration Efficiency Metric :");
                data = (Double) queryRow.get("avg_penetration");
            } else if (quantity.equalsIgnoreCase("Light Absorbed / Plant")) {
                System.out.println("light absorbed per plant :");
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("% Sunlit Leaves / Plant")) {
                System.out.println("Sunlit Leaves / Plant :");
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("% Sunlit Leaves / Ground Area")) {
                System.out.println("% Sunlit Leaves / Ground Area :");
                data = (Double) queryRow.get("per_area");
            } else if (quantity.equalsIgnoreCase("% Of Sunlit Leaves Saturated / Plant")) {
                System.out.println("Sunlit Leaves Saturated / Plant :");
                data = (Double) queryRow.get("avg_saturation");
            } else if (quantity.equalsIgnoreCase("Saturation Extent / Plant")) {
                System.out.println("Saturation Extent / Plant :");
                data = (Double) queryRow.get("saturation_extent");
            }
            // Initialize 24-hour list with 0s if the combination is new
            groupedResults.computeIfAbsent(combination, k -> new ArrayList<>(Collections.nCopies(24, 0.0)));

            // Set the total carbon assimilation at the correct hour index
            int hourIndex = hourRange.indexOf(hour);
            if (hourIndex != -1) {
                groupedResults.get(combination).set(hourIndex, data);
            }
        }

        // Step 5: Format the response
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : groupedResults.entrySet()) {
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("combination", parseCombination(entry.getKey(), "hourly"));
            resultEntry.put("response", entry.getValue());
            result.add(resultEntry);
        }

        return result;

    }


    // ************ biweekly methods *******************

    // ************** Bi-Weekly Cumulative Carbon Assim / Plant

    public List<Map<String, Object>> weeklyCarbonAssimulationFunctionFromDB(String combinationJson) {
        System.out.println("JSON Input: " + combinationJson);

        return jdbcTemplate.query(
                "SELECT combination_json, total_carbon_assimilation, week_index, total_per_plant, total_per_area " +
                        "FROM public.get_combination_union_weekly_carbon_assimilation_v3(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("total_carbon_assimilation", rs.getDouble("total_carbon_assimilation"));
                    row.put("week_index", rs.getInt("week_index"));
                    row.put("per_plant", rs.getDouble("total_per_plant"));
                    row.put("per_area", rs.getDouble("total_per_area"));
                    return row;
                },
                combinationJson
        );

    }

    //************ biweekly leaf temperature ****************

    public List<Map<String, Object>> weeklyAvgLeafTemperatureFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json, avg_temperature ,week_index FROM public.get_combination_union_weekly_temperature_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("avg_temperature",  rs.getDouble("avg_temperature") == 0.0 ? 0.0 : Math.round((rs.getDouble("avg_temperature") - 273.15) * 100.0) / 100.0);
                    row.put("week_index", rs.getInt("week_index"));
                    //  row.put("per_plant", rs.getDouble("per_plant"));
                    // row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }
//************ biweekly radiation ****************

    public List<Map<String, Object>> weeklyRadiationFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json,week_index,per_plant,per_area FROM public.get_combination_union_weekly_radiation_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("week_index", rs.getInt("week_index"));
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }

    // ************* biweekly transpiration function ****************
    public List<Map<String, Object>> weeklyTranspirationFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT combination_json,week_index,per_plant,per_area FROM public.get_combination_union_weekly_latent_flux_v2(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("combination", rs.getString("combination_json"));
                    row.put("week_index", rs.getInt("week_index"));
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_area", rs.getDouble("per_area"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }
    //***********Total Transpiration***********************
    public List<Map<String, Object>> totalTranspirationFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT run_id,total_transpiration,per_plant,per_ground FROM public.get_biweekly_transpiration_crop_name_v1(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("total_transpiration", rs.getDouble("total_transpiration"));
                    row.put("per_plant", rs.getDouble("per_plant"));
                    row.put("per_ground", rs.getDouble("per_ground"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }
    
  //***********Cumulative Carbon Assim / Plant***********************
    public List<Map<String, Object>> cumulativeCarbonAssimPlantFunctionFromDB(String combinationJson) {

        return jdbcTemplate.query(
                "SELECT run_id,total_carbon_assimilation,per_plant,per_ground FROM public.get_biweekly_carbon_assimilation_crop_name_v4(?::jsonb)",
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("total_carbon_assimilation", rs.getBigDecimal("total_carbon_assimilation"));
//                    Math.round((rs.getDouble("avg_temperature") - 273.15) * 100.0) / 100.0
                    row.put("per_plant", rs.getBigDecimal("per_plant"));
                    row.put("per_ground", rs.getBigDecimal("per_ground"));
                    return row;
                },
                combinationJson// Directly pass the JSON string
        );

    }


    // ************* biweekly DLI ****************
    public List<Map<String, Object>> weeklyDLIFunctionFromDB(List<Long> runIds) {
        String query = "SELECT run_id, week_index, total_ppfd_pv_yields FROM public.get_weekly_data_ppfd_pv_yield_v3(?)";

        // Query the database
        List<Map<String, Object>> queryResults = jdbcTemplate.query(query,
                ps -> {
                    java.sql.Array runIdArray = ps.getConnection().createArrayOf("bigint", runIds.toArray(new Long[0]));
                    ps.setArray(1, runIdArray);
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("week_index", rs.getInt("week_index"));
                    row.put("total_ppfd_pv_yields", rs.getDouble("total_ppfd_pv_yields"));
                    return row;
                }
        );

        // Group the query results by run_id and week_index
        Map<Long, Map<Integer, Double>> runIdWeekMap = queryResults.stream()
                .collect(Collectors.groupingBy(
                        result -> (Long) result.get("run_id"),
                        Collectors.toMap(
                                result -> (Integer) result.get("week_index"),
                                result -> (Double) result.get("total_ppfd_pv_yields")
                        )
                ));

        // Prepare the final response list
        List<Map<String, Object>> finalResults = new ArrayList<>();
        List<String> dynamicInterval = generateIntervals(intervalType);
        for (Long runId : runIds) {
            // Create the combination map for each run_id
            Map<String, Object> combination = new HashMap<>();
            combination.put("runId", runId);
            Runs runs = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            String runName = runs.getRunName();
            combination.put("runName", runName);  // Replace this with actual runName if needed

            // Create the response array with default 0 values
            List<Double> response = new ArrayList<>(Collections.nCopies(26, 0.0));

            // Fill in the actual values from the query results
            Map<Integer, Double> weekMap = runIdWeekMap.getOrDefault(runId, new HashMap<>());
            weekMap.forEach((weekIndex, value) -> response.set(weekIndex - 1, value));

            // Create the final result map
            Map<String, Object> result = new HashMap<>();
            result.put("combination", combination);
            result.put("response", response);
            result.put("week_intervals", dynamicInterval);

            finalResults.add(result);
        }

        return finalResults;
    }


    //********** avg bifacial gains *****************
    public List<Map<String, Object>> weeklyPvYieldsResponse(List<Long> runIds, String quantity) {
        // SQL query to call the function
    	String query = "SELECT run_id, week_index, total_pv_yields, avg_bifacial_gain FROM get_weekly_data_pv_yield_v4(?)";

        // Execute the query and map the results
        List<Map<String, Object>> queryResults = jdbcTemplate.query(query,
                ps -> {
                    // Create SQL array for run IDs as Long values
                    java.sql.Array runIdArray = ps.getConnection().createArrayOf("bigint", runIds.toArray(new Long[0]));
                    ps.setArray(1, runIdArray); // Set the array
                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("week_index", rs.getInt("week_index")); // Block index (1-26)
                    row.put("total_pv_yields", rs.getDouble("total_pv_yields")); // Total PV yield
                    row.put("avg_bifacial_gain", rs.getDouble("avg_bifacial_gain")); // Average bifacial gains
                    return row;
                });

        // Group data by runId and construct the response
        Map<Long, Map<String, Object>> responseMap = new LinkedHashMap<>();
        List<String> dynamicIntervals = generateIntervals(intervalType);

        for (Long runId : runIds) {
            // Filter data for the current runId
            List<Map<String, Object>> blocks = queryResults.stream()
                    .filter(row -> row.get("run_id").equals(runId))
                    .collect(Collectors.toList());

            // Prepare the response for this runId
            double[] data = new double[26]; // Initialize array for 26 blocks with default values 0.0

            if (quantity.equalsIgnoreCase("Cumulative Energy Generation")) {
                // Populate the array based on blockIndex
                for (Map<String, Object> block : blocks) {
                    int blockIndex = (int) block.get("week_index");
                    data[blockIndex - 1] = (double) block.get("total_pv_yields");
                }
            } else if (quantity.equalsIgnoreCase("Average Bifacial Gain")) {
                for (Map<String, Object> block : blocks) {
                    int blockIndex = (int) block.get("week_index");
                    data[blockIndex - 1] = (double) block.get("avg_bifacial_gain");
                }
            }
            Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));
            Map<String, Object> combination = new HashMap<>();
            combination.put("runName", run.getRunName());

            Map<String, Object> result = new HashMap<>();
            result.put("combination", combination);
            result.put("response", data); // Add the array to the response
            result.put("week_intervals", dynamicIntervals);
            responseMap.put(runId, result);

        }

        return new ArrayList<>(responseMap.values());
    }

    // ***************** response for biweekly data **************
//    public List<Map<String, Object>> responseForBiweeklyData(List<Map<String, Object>> queryResults, String quantity) {
//        System.out.println("calling response method ........" + quantity);
//        // Initialize a map to store data for each combination
//        Map<String, List<Double>> groupedResults = new HashMap<>();
//
//
//        // Iterate over query results
//        for (Map<String, Object> queryRow : queryResults) {
//            String combination = (String) queryRow.get("combination");
//            int weekIndex = (int) queryRow.get("week_index"); // Week index is 1-based
//
//            Double data = null;
//            if (quantity.equalsIgnoreCase("Bi-Weekly Cumulative Carbon Assim / Plant")) {
//                System.out.println("Bi-Weekly Cumulative Carbon Assim / Plant response:");
//                data = (Double) queryRow.get("per_plant");
//            } else if (quantity.equalsIgnoreCase("Bi-Weekly Cumulative Carbon Assim / Ground Area") || quantity.equalsIgnoreCase("Cumulative Carbon Assim / Ground")) {
//                System.out.println("Bi-Weekly Cumulative Carbon Assim / Ground Area response:");
//                data = (Double) queryRow.get("per_area");
//            } else if (quantity.equalsIgnoreCase("Avg. Leaf Temperature")) {
//                System.out.println("Avg Leaf Temperature response:");
//                data = (Double) queryRow.get("avg_temperature");
//            } else if (quantity.equalsIgnoreCase("Cumulative Carbon Assim / Plant")) {
//                System.out.println(" Cumulative Carbon Assim / Plant response:");
//                data = (Double) queryRow.get("per_plant");
//            } else if (quantity.equalsIgnoreCase("Light Absorbed / Plant")) {
//                System.out.println(" Light Absorbed / Plant response:");
//                data = (Double) queryRow.get("per_plant");
//            } else if (quantity.equalsIgnoreCase("Light Absorbed / M2 Ground")) {
//                System.out.println(" Light Absorbed / M2 Ground response:");
//                data = (Double) queryRow.get("per_area");
//            } else if (quantity.equalsIgnoreCase("Cumulative Transpiration / Plant")) {
//                System.out.println(" Transpiration / Plant response:");
//                data = (Double) queryRow.get("per_plant");
//            } else if (quantity.equalsIgnoreCase("Cumulative Transpiration / Ground")) {
//                System.out.println(" Transpiration / Ground response:");
//                data = (Double) queryRow.get("per_area");
//            } else if (quantity.equalsIgnoreCase("Daily DLI")) {
//                System.out.println(" Daily DLI:");
//                data=(Double) queryRow.get("total_ppfd_pv_yields");
//            }
//
//            // Initialize a list of size 26 with 0s for new combinations
//            groupedResults.computeIfAbsent(combination, k -> new ArrayList<>(Collections.nCopies(26, 0.0)));
//
//            // Set the value at the correct index
//            if (weekIndex >= 1 && weekIndex <= 26) {
//                groupedResults.get(combination).set(weekIndex - 1, data);
//            }
//        }
//        System.out.println("outside the response loop ");
//        // Format the response
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (Map.Entry<String, List<Double>> entry : groupedResults.entrySet()) {
//            Map<String, Object> resultEntry = new HashMap<>();
//            resultEntry.put("combination", parseCombination(entry.getKey(), "weekly")); // Parse combination if needed
//            resultEntry.put("response", entry.getValue());
//            result.add(resultEntry);
//        }
//
//        return result;
//    }

    // add with week indexes
    public List<Map<String, Object>> responseForBiweeklyData(List<Map<String, Object>> queryResults, String quantity) {
        System.out.println("calling response method ........" + quantity);

        // Initialize a map to store data for each combination
        Map<String, List<Double>> groupedResults = new HashMap<>();

        // Generate dynamic intervals
        List<String> dynamicIntervals = generateIntervals(intervalType);

        // Iterate over query results
        for (Map<String, Object> queryRow : queryResults) {
            String combination = (String) queryRow.get("combination");
            int weekIndex = (int) queryRow.get("week_index"); // Week index is 1-based

            Double data = null;
            if (quantity.equalsIgnoreCase("Bi-Weekly Cumulative Carbon Assim / Plant")) {
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("Bi-Weekly Cumulative Carbon Assim / Ground Area") || quantity.equalsIgnoreCase("Carbon Assim / Ground")) {
                data = (Double) queryRow.get("per_area");
            } else if (quantity.equalsIgnoreCase("Avg. Leaf Temperature")) {
                data = (Double) queryRow.get("avg_temperature");
            } else if (quantity.equalsIgnoreCase("Carbon Assim / Plant")) {
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("Light Absorbed / Plant")) {
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("Light Absorbed / M2 Ground")) {
                data = (Double) queryRow.get("per_area");
            } else if (quantity.equalsIgnoreCase("Cumulative Transpiration / Plant")) {
                data = (Double) queryRow.get("per_plant");
            } else if (quantity.equalsIgnoreCase("Cumulative Transpiration / Ground")) {
                data = (Double) queryRow.get("per_area");
            }

            // Initialize a list of size based on dynamic intervals
            groupedResults.computeIfAbsent(combination, k -> new ArrayList<>(Collections.nCopies(dynamicIntervals.size(), 0.0)));

            // Set the value at the correct index
            if (weekIndex >= 1 && weekIndex <= dynamicIntervals.size()) {
                groupedResults.get(combination).set(weekIndex - 1, data);
            }
        }

        // Format the response
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : groupedResults.entrySet()) {
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("combination", parseCombination(entry.getKey(), "weekly")); // Parse combination if needed
            resultEntry.put("response", entry.getValue());
            resultEntry.put("week_intervals", dynamicIntervals); // Add the dynamic intervals
            result.add(resultEntry);
        }

        return result;
    }

    public List<String> generateIntervals(int intervalDays) {
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


    // Helper method to parse combination JSON string into a Map
    private Map<String, Object> parseCombination(String combinationJson, String type) {
        // Assuming the combination format is always as per your example: "[[298, 1, 1, \"sorghum\", \"sunlit\"]]"
        String[] parts = combinationJson.replaceAll("[\\[\\]\"]", "").split(",");
        Map<String, Object> combinationMap = new HashMap<>();
        long runId = Long.parseLong(parts[0].trim());
        Runs run = runsRepository.findById(runId).orElseThrow(() -> new ResourceNotFoundException("run.not.found"));

//        combinationMap.put("runId", Long.parseLong(parts[0].trim()));
        combinationMap.put("runName", run.getRunName());
        if (type.equalsIgnoreCase("hourly")) {
            combinationMap.put("week", Integer.parseInt(parts[1].trim()));
            combinationMap.put("bed_index", (parts[2].trim()));
            combinationMap.put("crop_name", parts[3].trim());
            if (parts.length == 5)
                combinationMap.put("leafType", parts[4].trim());
        } else {
            combinationMap.put("bed_index", (parts[1].trim()));
            combinationMap.put("crop_name", parts[2].trim());
            combinationMap.put("leafType", parts[3].trim());
        }
        return combinationMap;
    }


    // *********** 2d graphs -- heat map
    //******** Hourly Temperature across the year *************************


    public List<Map<String, Object>> hourlyTemperatureAcrossYearDBCall(Long runId, String leaf_type, String cropName, Long bed_index) {


        // Construct the query
        String query = "SELECT run_id,date,hrs,avg_temperature FROM get_hourly_data_temperature_v2(?, ?, ?, ?)";

        return jdbcTemplate.query(query,
                ps -> {
                    ps.setLong(1, runId); // Set the run ID
                    ps.setString(2, leaf_type);
                    ps.setString(3, cropName);
                    ps.setLong(4, bed_index);

                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("date", rs.getDate("date"));
                    row.put("hrs", rs.getInt("hrs")); // Hour of the day (0-23)
                    row.put("avg_temperature", rs.getDouble("avg_temperature") == 0.0 ? 0.0 : Math.round((rs.getDouble("avg_temperature") - 273.15) * 100.0) / 100.0);
                    return row;
                });
    }

    // ********************** 2D graph - Hourly Carbon Assimilation across the year *******************
    public List<Map<String, Object>> hourlyCarbonAssimilationacrosstheyearDBCall(Long runId, String leaf_type, String cropName, Long bed_index) {


        // Construct the query
        String query = "SELECT run_id,date,hrs,total_carbon_assimilation FROM get_hourly_data_carbon_assimilation_v2(?, ?, ?, ?)";

        return jdbcTemplate.query(query,
                ps -> {
                    ps.setLong(1, runId); // Set the run ID
                    ps.setString(2, leaf_type);
                    ps.setString(3, cropName);
                    ps.setLong(4, bed_index);

                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("date", rs.getDate("date"));
                    row.put("hrs", rs.getInt("hrs")); // Hour of the day (0-23)
                    row.put("total_carbon_assimilation", rs.getDouble("total_carbon_assimilation"));
                    return row;
                });
    }

    // **************** bifacial gain for 2 d **********************
    public List<Map<String, Object>> bifacialAcrosstheyearDBCall(Long runId) {


        // Construct the query
        String query = "SELECT run_id,date,hrs,bifacial_gain,week_index FROM public.get_all_hourly_data_pv_yield_v2(?)";

        return jdbcTemplate.query(query,
                ps -> {
                    ps.setLong(1, runId); // Set the run ID
//                    ps.setString(2, leaf_type);
//                    ps.setString(3, cropName);
//                    ps.setLong(4, bed_index);

                },
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("run_id", rs.getLong("run_id"));
                    row.put("date", rs.getDate("date"));
                    row.put("hrs", rs.getInt("hrs")); // Hour of the day (0-23)
                    row.put("bifacial_gain", rs.getDouble("bifacial_gain"));
                    return row;
                });
    }


    // *********** response for 2d graphs-- heat map
     public List<List<String>> responseFor2DGraphsHeatMap(List<Map<String, Object>> queryResult, String quantity) {
        List<List<String>> response = new ArrayList<>();
        List<String> dynamicIntervals = generateIntervals(intervalType);
        Set<String> usedWeekLabels = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Map<String, Object> row : queryResult) {
            List<String> record = new ArrayList<>();

            // Convert and add elements to the inner list
            String dateString = row.get("date").toString();
            record.add(dateString);
            LocalDate currentDate = LocalDate.parse(dateString, formatter);


//            String dateString = row.get("date").toString(); // Original date as String
//            LocalDate localDate = LocalDate.parse(dateString); // Parse to LocalDate
//
//            // Convert to ZonedDateTime in UTC
//            ZonedDateTime utcDateTime = localDate.atStartOfDay(ZoneId.of("UTC"));
//
//            // Format to ISO-8601 with "Z"
//            String formattedDate = utcDateTime.format(DateTimeFormatter.ISO_INSTANT);
//
//            // Add to record or use as needed
//            System.out.println(formattedDate);
            record.add(row.get("hrs").toString()); // Convert hour to String
            //   record.add(formattedDate);
            if (quantity.equalsIgnoreCase("Hourly Temperature across the year"))
                record.add(row.get("avg_temperature").toString()); // Convert temperature to String
            else if (quantity.equalsIgnoreCase("Hourly Carbon Assimilation across the year"))
                record.add(row.get("total_carbon_assimilation").toString()); // Convert carbon assimilation to String
            else if (quantity.equalsIgnoreCase("Bifacial Gain"))
                record.add(row.get("bifacial_gain").toString()); //convert bifacial gain to string

            // ✅ Compute dynamic BASE_DATE using year from current date
            LocalDate baseDate = LocalDate.of(currentDate.getYear(), 1, 1);
            long daysBetween = ChronoUnit.DAYS.between(baseDate, currentDate);
            int intervalIndex = (int) (daysBetween / intervalType);
            int cappedIndex = Math.min(intervalIndex, dynamicIntervals.size() - 1);
//            System.out.println(intervalIndex);
            String weekLabel = dynamicIntervals.get(cappedIndex);
            usedWeekLabels.add(weekLabel); // Mark as covered

            // Add the computed week interval to the record
            record.add(weekLabel);
            // Add the inner list to the response list
            response.add(record);
        }
        
        // ✅ Fill missing week intervals with dummy data
        int year = 2025;

        for (String weekLabel : dynamicIntervals) {
            if (!usedWeekLabels.contains(weekLabel)) {
                String[] parts = weekLabel.split("-");
                int startWeek = Integer.parseInt(parts[0]);

                // Calculate representative date as the first day of that interval (startWeek - 1) * 7 + 1
                int startDayOfYear = (startWeek - 1) * 7 + 1;

                // Guard against invalid day of year
                if (startDayOfYear > Year.of(year).length()) {
                    continue; // skip if the calculated day exceeds the year length (leap year safe)
                }

                LocalDate representativeDate = LocalDate.ofYearDay(year, startDayOfYear);

                for (int hour = 0; hour < 24; hour++) {
                    List<String> dummy = new ArrayList<>();
                    dummy.add(representativeDate.format(formatter)); // e.g., "2025-01-15"
                    dummy.add(String.format("%02d", hour));          // "00" to "23"
                    dummy.add("0.0");
                    dummy.add(weekLabel);                            // e.g., "03-04"
                    response.add(dummy);
                }
            }
        }
        // Optional: sort by weekLabel then hour
        response.sort(Comparator.comparing((List<String> row) -> row.get(3))  // weekLabel
                                .thenComparing(row -> Integer.parseInt(row.get(1)))); // hour
        return response;
    }

}
