import { ArrowBackIos, ExpandMore } from "@mui/icons-material";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  CircularProgress,
  Grid,
} from "@mui/material";

import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import LineGraph from "./LineGraph";

import CustomSelectMultipleAlternate from "../../../../components/graphs/CustomSelectMultipleAlternate";
import CustomSelectAlternate from "../../../../components/graphs/CustomSelectAlternate";
import CustomButton from "../../../../components/CustomButton";

import {
  agriSideHourlyRoute,
  postProcessorRoute,
} from "../../../../utils/constant";
import { getGraphData, getGraphFilters } from "../../../../api/graphs";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from "../../../../components/CustomLinearProgress";

const AgriSideHourly = ({ title = "Agri-Side Hourly Plots / Within Runs" }) => {
  const navigate = useNavigate();
  const theme = useTheme();

  // Post Processing Context
  const { 
    postProcessingRuns,
  } = usePostProcessing();

  // Query Params
  const { projectId, runId } = useParams();

  // Search Params
  const searchParams = new URLSearchParams(window.location.search);
  const graphType = searchParams.get("graphType");

  // State
  const [runName, setRunName] = useState("");
  const [isLoader,setIsLoader] = useState(false);
  const [cycles, setCycles] = useState();

  const [cropCycleOptions, setCropCycleOptions] = useState([]);
  const [weekOptions, setWeekOptions] = useState([]);
  const [bedOptions, setBedOptions] = useState([]);
  const [cropOptions, setCropOptions] = useState([]);
  const [lightConfigOptions, setLightConfigOptions] = useState([]);

  const [cropCycle, setCropCycle] = useState("");
  const [week, setWeek] = useState([]);
  const [weekLabel, setWeekLabel] = useState({});
  const [bed, setBed] = useState([]);
  const [crop, setCrop] = useState([]);
  const [lightConfig, setLightConfig] = useState([]);

  const [firstFetch, setFirstFetch] = useState(true);

  const quantityAvailableOptions = [
    {
      label: "Rate of Carbon Assim / Plant",
      value: "rate-of-carbon-assim-per-plant",
      unit: "MICRO MOLE / M² LEAF AREA"
    },
    {
      label: "Rate of Carbon Assim / Ground",
      value: "rate-of-carbon-assim-per-ground",
      unit: "MICRO MOLE / M² LEAF AREA / M² GROUND AREA"
    },
    {
      label: "Avg. Leaf Temperature",
      value: "average-leaf-temperature",
      unit: "DEGREE CELSIUS",
    },
    {
      label: "% Sunlit Leaves / Plant",
      value: "percent-sunlit-leaves-per-plant",
      unit: "",
    },
    {
      label: "% Sunlit Leaves / Ground Area",
      value: "percent-sunlit-leaves-per-ground-area",
      unit: "",
    },
    {
      label: "Light Absorbed / Plant",
      value: "light-absorbed-per-plant",
      unit: "JOULE / M²LEAF AREA"
    },
    {
      label: "Penetration Efficiency Metric",
      value: "penetration-efficiency-metric",
      unit: "",
    },
    {
      label: "% Of Sunlit Leaves Saturated / Plant",
      value: "percent-of-sunlit-leaves-sturated-per-plant",
      unit: "",
    },
    {
      label: "Saturation Extent / Plant",
      value: "saturation-extent-plant",
      unit: "WATT",
    },
    // {
    //   label: "Cumulative PPFD (DLI)",
    //   value: "cumulative-ppfd-dli",
    //   unit: "",
    // },
  ];

  // const getLabelForValue = (value) => {
  //   const option = quantityAvailableOptions.find((item) => item.value === value);
  //   return option ? `${option.label} (${option.unit})` : null; // Return null if no matching value is found
  // };

  const getLabelForValue = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    if (option) {
      // Convert unit to a string and trim whitespace to check if it's non-empty
      if (option.unit && option.unit.toString().trim() !== "") {
        return `${option.label} (${option.unit})`;
      }
      return option.label;
    }
    return null;
  };

  const [quantityAvailable, setQuantityAvailable] = useState(graphType);

  const [series, setSeries] = useState({});

  useEffect(() => {
    async function loadFilters() {
      const runIds = localStorage.getItem("post-processing-runs");
      if (JSON.parse(runIds)?.length) {
        const filters = await getGraphFilters(projectId, {
          runIds: [runId],
          dataType: "with in run",
          frequency: "hourly",
        });

        const cycles = filters?.data?.data?.runs?.[0]?.cycles;
        const weekMap = filters?.data?.data?.runs?.[0]?.weekIntervals.reduce((acc, week, index) => {
          acc[`W${index + 1}`] = `W${week}`;
          return acc;
        }, {});
        setWeekLabel(weekMap);

        setCycles(cycles);
        if (cycles?.length) {
          setCropCycleOptions(
            cycles.map((cycle) => ({ label: cycle.name, value: cycle.id }))
          );
          setCropCycle(cycles[0].id);
        }

        updateFiltersForCycle(cycles[0]?.name, cycles, weekMap);
      }
    }

    loadFilters();
  }, [postProcessingRuns]);

  const updateFiltersForCycle = (cycleName, allCycles, weekMap) => {
    allCycles.forEach((cycle) => {
      if (cycleName === cycle.id) {
        setWeekOptions(
          cycle.weeks.map((week)  => ({ label: weekMap[week], value: week }))
        );
        setWeek(cycle.weeks[0]);
        // setBedOptions([
        //   { label: "All", value: "all" },
        //   ...cycle.beds.map((bed) => ({ label: `Bed ${bed}`, value: bed})),
        // ]);
        // setBed(["all"]);

        const bedOptions = [
          ...(cycle.beds.length > 1 ? [{ label: "All", value: "all" }] : []),
          ...cycle.beds
            .map((bedArray, index) => ({
              label: `Bed ${index + 1}`, // Add 1 to index for human-readable labels
              value: (index + 1).toString(),  // Use index as value
            })),
        ];
        setBedOptions(bedOptions);

        // Set `bed` to 'all' if there are more than one bed, otherwise select the first bed.
        setBed(cycle.beds.length > 1 ? ["all"] : ["1"]);


        setCropOptions([
          ...(cycle.crops.length > 1 ? [{ label: "All", value: "all" }] : []),
          ...cycle.crops.map((crop) => ({ label: crop, value: crop })),
        ]);
        setCrop(cycle.crops.length > 1 ? ["all"] : [cycle.crops[0]]);
        setLightConfigOptions([
          { label: "All", value: "all" },
          { label: "sunlit", value: "sunlit" },
          { label: "sunshaded", value: "sunshaded" },
        ]);
        setLightConfig(["all"]);
      }
    });
  };

  const updateFiltersForBed = (cycleName, allCycles, weekMap) => {
    allCycles.forEach((cycle) => {
      if (cycleName === cycle.id) {
        let selectedCrops = [];

        if (bed.includes('all')) {
          // All beds selected
          selectedCrops = cycle.beds.flat();
        } else {
          // Specific beds selected
          selectedCrops = bed
            .map((bedIndex) => cycle.beds[parseInt(bedIndex) - 1]) // bed is 1-based
            .filter(Boolean) // ignore undefined in case of wrong index
            .flat();
        }

        // Remove duplicates
        const uniqueCrops = [...new Set(selectedCrops)];

        // Now set the cropOptions
        setCropOptions([
          ...(uniqueCrops.length > 1 ? [{ label: 'All', value: 'all' }] : []),
          ...uniqueCrops.map((crop) => ({ label: crop, value: crop }))
        ]);
        setCrop(uniqueCrops.length > 1 ? ["all"] : uniqueCrops);
        // setLightConfigOptions([
        //   { label: "All", value: "all" },
        //   { label: "sunlit", value: "sunlit" },
        //   { label: "sunshaded", value: "sunshaded" },
        // ]);
        // setLightConfig(["all"]);
      }
    });
  };

  useEffect(() => {
    if (cropCycle) updateFiltersForCycle(cropCycle, cycles, weekLabel);
  }, [cropCycle]);

  useEffect(() => {
    if (bed && cropCycle && cycles && weekLabel) {
      updateFiltersForBed(cropCycle, cycles, weekLabel);
    }
  }, [bed]);

  useEffect(() => {
    navigate(agriSideHourlyRoute(projectId, runId, quantityAvailable));
    applyFilters();
  }, [quantityAvailable]);
  
  const applyFilters = () => {
    if(cropCycle && week.length > 0 && crop.length> 0 && bed.length > 0 && lightConfig.length > 0){
      loadData();
    }
  }

  const getRunNames = (combination) => {
    const order = ["runName", "week", "bed_index", "crop_name", "leafType"];
    // return order.map(key => combination[key]).join(",");
    
    return order.map(key => {
      if (key === "bed_index") {
        return combination[key] === 'all' ? combination[key] : `Bed ${combination[key]}`;
      } else if(key === "week") {
        return weekLabel[`W${combination[key]}`];
      }
      return combination[key];
    }).join(", ");
  }

  const loadData = () => {
    setIsLoader(true);
    getGraphData(projectId, runId, {
      runIds: [runId],
      crops: crop,
      beds: bed,
      weeks: [week],
      configurations: lightConfig,
      dataType: "with in run",
      frequency: "hourly",
      quantityAvailable: encodeURIComponent(quantityAvailableOptions.filter(
        (obj) => obj.value === quantityAvailable
      )[0]?.label),
    }).then((res) => {
      const data = res?.data?.data;
      setIsLoader(false);

      // Generate Graph Series
      const tempSeries = [];
      data.forEach((ele) => {
        tempSeries.push({
          name: getRunNames(ele.combination),
          data: ele.response.map((res, i) => [i, res]),
        });
      });

      setSeries(tempSeries);
    });
  };

  useEffect(() => {
    if (
      cropCycle &&
      week?.length &&
      bed?.length &&
      crop?.length &&
      lightConfig?.length &&
      quantityAvailable &&
      firstFetch
    ) {
      applyFilters();
      setFirstFetch(false);
    }
  }, [cropCycle, week, bed, crop, lightConfig, quantityAvailable]);

  useEffect(() => {
    const currentRuns = JSON.parse(localStorage.getItem("current-runs"));
    setRunName((currentRuns &&  currentRuns.find(item => item.id == runId)?.name) || "");
  }, []);

  return (
    <Box
      sx={{
        width: "100%",
        minHeight: "calc(100vh - 6em)",
        display: "flex",
        flexDirection: "column",
        boxSizing: "border-box",
        paddingBottom: "calc(49px + 1em)",
        color: theme.palette.text.main,
        position: "relative",
      }}
    >
      {/* Header */}
      <Box
        sx={{
          width: "100%",
          display: "flex",
          background: "white",
        }}
      >
        <Button
          variant="text"
          size="large"
          sx={{ textTransform: "capitalize", color: theme.palette.text.main }}
          startIcon={<ArrowBackIos />}
          onClick={() => navigate(postProcessorRoute(projectId))}
        >
          Back
        </Button>
        <h3 style={{ fontSize: "18px", marginLeft: "1.25em" }}>{title}</h3>
        <Box sx={{ flexGrow: 1 }}></Box>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            boxSizing: "border-box",
            paddingRight: "1em",
          }}
        >
          <span style={{ fontSize: "1em" }}>Run: {runName}</span>
        </Box>
      </Box>

      {/* {loading && (
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          width="100%"
          height="100%"
          position="absolute"
          top={0}
          left={0}
          zIndex={99999}
          backgroundColor="rgba(255, 255, 255, 1)"
        >
          <CircularProgress />
        </Box>
      )} */}

      {/* Filters */}
      <Box
        sx={{
          width: "100%",
          display: "flex",
          boxSizing: "border-box",
          padding: "1.5em",
          marginTop: "1.5em",
          borderRadius: "1em",
          background: "white",
        }}
      >
        <Box sx={{ width: "100%" }}>
          <Accordion defaultExpanded sx={{ boxShadow: "none" }}>
            <AccordionSummary
              id="panel3-header"
              aria-controls="panel3-content"
              expandIcon={<ExpandMore />}
              sx={{
                padding: "0",
                margin: "0",
              }}
            >
              <Box
                sx={{
                  width: "100%",
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  boxSizing: "border-box",
                  paddingRight: "2em",
                }}
              >
                <h4
                  style={{
                    margin: 0,
                    fontWeight: "600",
                    color: theme.palette.text.main,
                  }}
                >
                  Filters
                </h4>
                <h4
                  style={{
                    margin: 0,
                    fontSize: "14px",
                    fontWeight: "500",
                    color: theme.palette.text.main,
                  }}
                >
                  {`${
                    week?.length +
                    bed?.length +
                    crop?.length +
                    lightConfig?.length
                  } Items Selected`}
                </h4>
              </Box>
            </AccordionSummary>
            <AccordionDetails>
              <Box sx={{ flexGrow: 1 }}>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectAlternate
                      id="quantity-available"
                      value={quantityAvailable}
                      options={quantityAvailableOptions}
                      onChange={(e) => setQuantityAvailable(e.target.value)}
                      width="100%"
                      placeholder="Select Quantity"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectAlternate
                      id="crop-cycle-dropdown"
                      value={cropCycle || cropCycleOptions?.[0]?.value || ""}
                      options={cropCycleOptions}
                      onChange={(e) => setCropCycle(e.target.value)}
                      width="100%"
                      placeholder="Select Crop Cycle"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectAlternate
                      id="week-dropdown"
                      value={week || weekOptions?.[0]?.value || ""}
                      options={weekOptions}
                      onChange={(e) => setWeek(e.target.value)}
                      width="100%"
                      placeholder="Select Week"
                    />
                    {/* <CustomSelectMultipleAlternate
                      id="week-dropdown"
                      name="Weeks"
                      value={week}
                      options={weekOptions}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setWeek(selectedValues.length === 0 ? [weekOptions?.[0].value] : selectedValues); // If no value selected, set 'All'
                      }}
                      width="100%"
                      subHeader="Select Weeks"
                    /> */}
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectMultipleAlternate
                      id="bed-dropdown"
                      name="Bed Indexes"
                      value={bed}
                      options={bedOptions}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setBed(selectedValues.length === 0 ? cropCycle?.beds?.length > 1 ? ["all"] : [cropCycle?.beds[0]] : selectedValues); // If no value selected, set 'All'
                      }}
                      width="100%"
                      subHeader="Select Bed Indexes"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectMultipleAlternate
                      id="crop-dropdown"
                      name="Crops"
                      value={crop}
                      options={cropOptions}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setCrop(selectedValues.length === 0 ? cropCycle?.crops?.length > 1 ? ["all"] : [cropCycle?.crops[0]] : selectedValues); // If no value selected, set 'All'
                      }}
                      width="100%"
                      subHeader="Select Crops"
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id"}>
                    <CustomSelectMultipleAlternate
                      id="light-config-dropdown"
                      name="Configurations"
                      value={lightConfig}
                      options={lightConfigOptions}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setLightConfig(selectedValues.length === 0 ? ['all'] : selectedValues); // If no value selected, set 'All'
                      }}
                      width="100%"
                      subHeader="Select Configurations"
                    />
                  </Grid>
                </Grid>
              </Box>
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  justifyContent: "flex-end",
                  boxSizing: "border-box",
                  paddingTop: "1em",
                }}
              >
                <CustomButton label="Apply" onClick={applyFilters} />
              </Box>
            </AccordionDetails>
            {
              isLoader && <CustomLinearProgress />
            }
          </Accordion>
        </Box>
      </Box>

      {/* Graph  Container */}
      <Box
        sx={{
          widht: "100%",
          flexGrow: 1,
          display: "flex",
          boxSizing: "border-box",
          marginTop: "1.5em",
          borderRadius: "1em",
          background: "white",
          position: "relative",
        }}
      >
        {/* Graph */}
        <Box
          sx={{
            width: "100%",
            display: "flex",
            flexDirection: "column",
            boxSizing: "border-box",
            padding: "1.5em",
            borderRight: `4px solid ${theme.palette.background.faded}`,
          }}
        >
          {/* <Box sx={{ width: "100%", display: "flex", alignItems: "center" }}>
            <h4
              style={{
                fontSize: "1em",
                fontWeight: "600",
                color: theme.palette.text.main,
              }}
            >
              {getLabelForValue(quantityAvailable)}
            </h4>

            <Box sx={{ flexGrow: 1 }}></Box>
          </Box> */}

          <Box sx={{ width: "100%", flexGrow: 1 }}>
            {series?.length && (
              <LineGraph title={getLabelForValue(quantityAvailable)} xTitle="Hours of the day" series={series} />
            )}
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default AgriSideHourly;
