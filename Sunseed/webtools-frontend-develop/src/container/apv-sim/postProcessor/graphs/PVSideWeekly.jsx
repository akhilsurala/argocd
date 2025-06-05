import { ArrowBackIos } from "@mui/icons-material";
import {
  Box,
  Button,
} from "@mui/material";

import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import LineGraph from "./LineGraph";

import {
  postProcessorRoute,
  pvSideWeeklyRoute,
} from "../../../../utils/constant";
import { getGraphData } from "../../../../api/graphs";
import { usePostProcessing } from "../PostProcessingContext";
import BarChart from "./BarChart";

const PVSideWeekly = ({
  title = "Weekly Plots for PV Spanning The Year",
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

  // State
  const [loading, setLoading] = useState(true);

  const [series, setSeries] = useState({});

  const loadData = () => {
    getGraphData(projectId, null, {
      runIds: [runId],
      dataType: "with in run",
      frequency: "weekly",
      quantityAvailable: "Daily DLI",
    }).then((res) => {
      const data = res?.data?.data;

      // Generate Graph Series
      const tempSeries = [];
      data.forEach((ele) => {
        tempSeries.push({
          name: ele.combination?.runName,
          data: ele.response.map((res, i) => [`W${ele.week_intervals[i]}`, res]),
        });
      });

      setSeries(tempSeries);
      setLoading(false);
    });
  };

  useEffect(() => {
    navigate(pvSideWeeklyRoute(projectId, runId, "Daily DLI"));
    loadData();
  }, []);

  const getCategories = () => {
    const temp = [];
    series[0].data.map(subArray => temp.push( subArray[0] ));
    return temp;
  };

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
          <Box sx={{ width: "100%", flexGrow: 1 }}>
            {series?.length && <BarChart
              title="Daily DLI (JOULE / MOLES)"
              xTitle="Weeks of the year"
              series={series}
              categories={getCategories()}
              showBarLabels={true}
            />}
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default PVSideWeekly;
