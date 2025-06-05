import { ArrowBackIos, ExpandMore } from "@mui/icons-material";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  CircularProgress,
  Grid,
  LinearProgress,
} from "@mui/material";

import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import LineGraph from "./LineGraph";

import CustomSelectMultipleAlternate from "../../../../components/graphs/CustomSelectMultipleAlternate";
import CustomSelectAlternate from "../../../../components/graphs/CustomSelectAlternate";
import CustomButton from "../../../../components/CustomButton";

import {
  postProcessorRoute,
  pvSideHourlyRoute,
} from "../../../../utils/constant";
import { getGraphData, getGraphFilters } from "../../../../api/graphs";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from "../../../../components/CustomLinearProgress";

const HourlyPlotsPV = ({ title = "PV-Side Hourly Plots / Within Runs" }) => {
  const navigate = useNavigate();
  const theme = useTheme();

  // Post Processing Context
  const { 
    postProcessingRuns,
    runsOptions,
  } = usePostProcessing();

  // Query Params
  const { projectId, runId } = useParams();

  // Search Params
  const searchParams = new URLSearchParams(window.location.search);
  const graphType = searchParams.get("graphType");

  // State
  const [runName, setRunName] = useState("");
  const [isLoader,setIsLoader] = useState(false);

  const [weekOptions, setWeekOptions] = useState(
    ["W01", "W02", "W03", "W04"]
      .map((week) => ({ label: week, value: week }))
  );

  const [week, setWeek] = useState([]);

  const [firstFetch, setFirstFetch] = useState(true);

  const SampleuantityAvailableOptions = [
    {
      label: "Hourly Bifacial Gain",
      value: "hourly-bifacial-gain",
      unit: "KWATT.HR/MW",
    },
    {
      label: "Hourly DC Power",
      value: "hourly-dc-power",
      unit: "WATTS/MODULE",
    },
    {
      label: "PPFD",
      value: "ppfd",
      unit: "WATT", // or include any Unicode superscript if needed, e.g. "WATT²"
    },
  ];

  let quantityAvailableOptions = [];
  const runSelected = runsOptions.find((item) => item.id == runId);
  if (runSelected?.toggle?.toUpperCase() === "ONLY PV") {
    // Include all options for APV
    quantityAvailableOptions = [...SampleuantityAvailableOptions];
  } else if (runSelected?.toggle?.toUpperCase() === "APV") {
    quantityAvailableOptions = SampleuantityAvailableOptions.filter(
      (option) => 
        ![
          "ppfd",
        ].includes(option.value)
    );
  }

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
      const runIds = localStorage.getItem("post-processing-runs");
      if (JSON.parse(runIds)?.length) {
        const filters = await getGraphFilters(projectId, {
          runIds: [runId],
          dataType: "with in run",
          frequency: "hourly",
        });
        
        const weeks = filters.data.data.runs[0].weekIntervals
        setWeekOptions(
          weeks.map((week, index) => ({ label: `W${week}`, value: `W${index + 1}` }))
        );
        setWeek(weeks.map((_, index) => `W${index + 1}`));
      }
    }

    loadFilters();
  }, [postProcessingRuns]);
  
  useEffect(() => {
    if(quantityAvailable){
      navigate(pvSideHourlyRoute(projectId, runId, quantityAvailable));
      applyFilters();
    }
  }, [quantityAvailable]);
  
  const applyFilters = () => {
    if(week.length > 0){
      loadData();
    }
  }
  
  const loadData = () => {
    setIsLoader(true);
    getGraphData(projectId, runId, {
      runIds: [runId],
      weeks: week,
      dataType: "with in run",
      frequency: "hourly",
      quantityAvailable: SampleuantityAvailableOptions.filter(
        (obj) => obj.value === quantityAvailable
      )[0]?.label,
    }).then((res) => {
      const data = res?.data?.data;
      setIsLoader(false);

      // Generate Graph Series
      const tempSeries = [];
      data.forEach((ele) => {
        tempSeries.push({
          name: weekOptions[ele.combination.week - 1]?.label,
          data: ele.response.map((res, i) => [i, res]),
          week: ele.combination.week, // Add the week to sort later
          weekLabel: weekOptions[ele.combination.week]?.label
        });
      });
      tempSeries.sort((a, b) => a.week - b.week);
      setSeries(tempSeries);
    });
  };

  useEffect(() => {
    if (week?.length && quantityAvailable && firstFetch) {
      loadData();
      setFirstFetch(false);
    }
  }, [week, quantityAvailable]);

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
                    color: "theme.palette.text.main",
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
                  {`${week?.length} Items Selected`}
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

export default HourlyPlotsPV;
