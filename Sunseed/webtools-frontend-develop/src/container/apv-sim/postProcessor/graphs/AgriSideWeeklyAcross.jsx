import React from 'react';
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
  agriSideWeeklyAcrossRoute,
  postProcessorRoute,
} from "../../../../utils/constant";
import { getGraphData, getGraphFilters } from "../../../../api/graphs";
import { useSelector } from "react-redux";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from '../../../../components/CustomLinearProgress';

const AgriSideWeeklyAcross = ({
  title = "Agri-Side Weekly Plots / Across Run",
}) => {
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
  const [graphType, setGraphType] = useState(searchParams.get("graphType"));

  const { runOptions } = useSelector((state) => state.postProcessor);
  
  const apvAgriRuns = JSON.parse(localStorage.getItem("apv_agri_runs"));
  const [runs, setRuns] = useState([apvAgriRuns?.[0]?.value]);

  // State
  const [isLoader,setIsLoader] = useState(false);
  const [cycles, setCycles] = useState();

  const [cropCycleOptions, setCropCycleOptions] = useState([]);
  const [cropOptions, setCropOptions] = useState([]);
  const [lightConfigOptions, setLightConfigOptions] = useState([]);

  const [cropCycle, setCropCycle] = useState("");
  const [crop, setCrop] = useState([]);
  const [lightConfig, setLightConfig] = useState([]);

  const [firstFetch, setFirstFetch] = useState(true);

  const quantityAvailableOptions = [
    {
      label: "Cumulative Carbon Assim / Plant",
      payloadLabel: "Carbon Assim / Plant",
      value: "cumulative-carbon-assim-per-plant",
      unit: "MICRO MOLE / M² LEAF AREA",
    },
    {
      label: "Cumulative Carbon Assim / Ground",
      payloadLabel: "Carbon Assim / Ground",
      value: "cumulative-carbon-assim-per-ground",
      unit: " MICRO MOLE / M² LEAF AREA / M² GROUND AREA",
    },
    {
      label: "Avg. Leaf Temperature",
      payloadLabel: "Avg. Leaf Temperature",
      value: "average-leaf-temperature",
      unit: "DEGREE CELSIUS",
    },
    {
      label: "Light Absorbed / Plant",
      payloadLabel: "Light Absorbed / Plant",
      value: "light-absorbed-per-plant",
      unit: "JOULE / M² LEAF AREA",
    },
    {
      label: "Light Absorbed / M2 Ground",
      payloadLabel: "Light Absorbed / M2 Ground",
      value: "light-absorbed-per-m2-ground",
      unit: "JOULE / M² LEAF AREA / M² GROUND AREA",
    },
    {
      label: "Cumulative Transpiration / Plant",
      payloadLabel: "Cumulative Transpiration / Plant",
      value: "cumulative-transpiration-per-plant",
      unit: "LITRES/TREE",
    },
    {
      label: "Cumulative Transpiration / Ground",
      payloadLabel: "Cumulative Transpiration / Ground",
      value: "cumulative-transpiration-per-ground",
      unit: "LITRES / M² GROUND AREA",
    },
  ];

  // const getLabelForValue = (value) => {
  //   const option = quantityAvailableOptions.find((item) => item.value === value);
  //   return option ? `${option.label} (${option.unit})` : null; // Return null if no matching value is found
  // };

  const getLabelForValue = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    return option ? `${option.label} (${option.unit})` : null;
  };

  const [quantityAvailable, setQuantityAvailable] = useState(graphType);

  const [series, setSeries] = useState({});

  useEffect(() => {
    async function loadFilters() {
      if (runs?.length) {
        const filters = await getGraphFilters(projectId, {
          runIds: apvAgriRuns.map(item => item.value),
          dataType: "with in run",
          frequency: "weekly",
        });

        // const crops = filters?.data?.data?.runs?.[0]?.cropName;
        // Extract and flatten crop names
        const cropList = filters?.data.data.runs.flatMap(run => run.cropName);

        // Get unique crop names
        const crops = [...new Set(cropList)]

        // setCycles(cycles);
        // if (cycles?.length) {
        //   setCropCycleOptions(
        //     cycles.map((cycle) => ({ label: cycle.name, value: cycle.id }))
        //   );
        //   setCropCycle(cycles[0].id);
        // }

        // updateFiltersForCycle(cycles[0]?.id, cycles);
        setCropOptions([
          ...(crops.length > 1 ? [{ label: "All", value: "all" }] : []),
          ...crops.map((crop) => ({ label: crop, value: crop })),
        ]);
        setCrop(crops.length > 1 ? ["all"] : [crops[0]]);

        setLightConfigOptions([
          { label: "All", value: "all" },
          { label: "sunlit", value: "sunlit" },
          { label: "sunshaded", value: "sunshaded" },
        ]);
        setLightConfig(["all"]);
      }
    }

    loadFilters();
  }, [postProcessingRuns]);

  const updateFiltersForCycle = (cycleName, allCycles) => {

    allCycles.forEach((cycle) => {
      if (cycleName === cycle.id) {
        setCropOptions([
          { label: "All", value: "all" },
          ...cycle.crops.map((crop) => ({ label: crop, value: crop })),
        ]);

        setCrop(["all"]);
        setLightConfigOptions([
          { label: "All", value: "all" },
          { label: "sunlit", value: "sunlit" },
          { label: "sunshaded", value: "sunshaded" },
        ]);
        setLightConfig(["all"]);
      }
    });
  };

  useEffect(() => {
    navigate(agriSideWeeklyAcrossRoute(projectId, quantityAvailable));
    applyFilters();
  }, [quantityAvailable]);
  
  const applyFilters = () => {
    if(crop.length> 0 && lightConfig.length > 0){
      loadData();
    }
  }

  const loadData = () => {
    setIsLoader(true);
    getGraphData(projectId, null, {
      runIds: runs,
      crops: crop,
      beds: ["all"],
      configurations: lightConfig,
      dataType: "across",
      frequency: "weekly",
      quantityAvailable: quantityAvailableOptions.filter(
        (obj) => obj.value === quantityAvailable
      )[0]?.payloadLabel,
    }).then((res) => {
      const data = res?.data?.data;
      setIsLoader(false);

      // Generate Graph Series
      const tempSeries = [];
      
      data.forEach((ele) => {
        tempSeries.push({
          name: Object.keys(ele.combination).filter(key => key !== "bed_index").map(key => ele.combination[key]).join(", "),
          data: ele.response.map((res, i) => [`W${ele.week_intervals[i]}`, res]),
        });
      });

      setSeries(tempSeries);
    });
  };

  useEffect(() => {
    if (
      crop?.length &&
      lightConfig?.length &&
      quantityAvailable &&
      firstFetch
    ) {
      applyFilters();
      setFirstFetch(false);
    }
  }, [cropCycle, crop, lightConfig, quantityAvailable]);

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
        {/* <Box
          sx={{
            display: "flex",
            alignItems: "center",
            boxSizing: "border-box",
            paddingRight: "1em",
          }}
        >
          <span style={{ fontSize: "1em" }}>Run Id: {runId}</span>
        </Box> */}
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
                    runs?.length + crop?.length + lightConfig?.length
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
                    <CustomSelectMultipleAlternate
                      id="runs-dropdown"
                      name="Runs"
                      value={runs || apvAgriRuns?.[0]?.value || []}
                      options={apvAgriRuns}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setRuns(selectedValues.length === 0 ? [apvAgriRuns?.[0]?.value] : selectedValues); // If no value selected, set first run
                      }}
                      width="100%"
                      subHeader="Select Runs"
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
                        setCrop(selectedValues.length === 0 ? cropOptions.length > 1 ? ["all"] : [cropOptions[0].value] : selectedValues); // If no value selected, set 'All'
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
              <LineGraph title={getLabelForValue(quantityAvailable)} xTitle="Weeks of the year" series={series} />
            )}
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default AgriSideWeeklyAcross;
