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
  postProcessorRoute,
  pvSideWeeklyAcrossRoute,
} from "../../../../utils/constant";
import { getGraphData } from "../../../../api/graphs";
import { useSelector } from "react-redux";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from "../../../../components/CustomLinearProgress";

const PVSideWeeklyAcross = ({
  title = "PV Side Weekly Plots / Across Run",
}) => {
  const navigate = useNavigate();
  const theme = useTheme();

  // Post Processing Context
  const { 
    postProcessingRuns,
  } = usePostProcessing();

  // Query Params
  const { projectId } = useParams();

  // Search Params
  const searchParams = new URLSearchParams(window.location.search);
  const graphType = searchParams.get("graphType");

  // State
  const [isLoader,setIsLoader] = useState(false);

  const { runOptions } = useSelector((state) => state.postProcessor);
  const pvRuns = JSON.parse(localStorage.getItem("pv_runs"));
  const [runs, setRuns] = useState([pvRuns?.[0]?.value]);

  const quantityAvailableOptions = [
    {
      label: "Cumulative Energy Generation",
      value: "cumulative-energy-generation",
      unit: "KWATT.HR/MW",
    },
    {
      label: "Average Bifacial Gain",
      value: "average-bifacial-gain",
      unit: "BACKSIDE RAD/FRONTSIDE RAD",
    },
  ];

  const getLabelForValue = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    return option ? `${option.label} (${option.unit})` : null; // Return null if no matching value is found
  };

  const [quantityAvailable, setQuantityAvailable] = useState(graphType);
  const [series, setSeries] = useState({});

  const loadData = () => {
    setIsLoader(true);
    getGraphData(projectId, null, {
      runIds: runs,
      dataType: "across",
      frequency: "weekly",
      quantityAvailable: quantityAvailableOptions.filter(
        (obj) => obj.value === quantityAvailable
      )[0]?.label,
    }).then((res) => {
      const data = res?.data?.data;
      setIsLoader(false);

      // Generate Graph Series
      const tempSeries = [];
      data.forEach((ele) => {
        tempSeries.push({
          name: Object.values(ele.combination).join(", "),
          data: ele.response.map((res, i) => [`W${ele.week_intervals[i]}`, res]),
        });
      });

      setSeries(tempSeries);
    });
  };

  useEffect(() => {
    navigate(pvSideWeeklyAcrossRoute(projectId, quantityAvailable));
    loadData();
  }, [quantityAvailable]);

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
                  {`${runs?.length} Items Selected`}
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
                      value={runs || pvRuns?.[0]?.value || []}
                      options={pvRuns}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setRuns(selectedValues.length === 0 ? [pvRuns?.[0]?.value] : selectedValues); // If no value selected, set first run
                      }}
                      width="100%"
                      subHeader="Select Runs"
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
                <CustomButton label="Apply" onClick={loadData} />
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

export default PVSideWeeklyAcross;
