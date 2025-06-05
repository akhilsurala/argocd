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

import CustomSelectMultipleAlternate from "../../../../components/graphs/CustomSelectMultipleAlternate";
import CustomSelectAlternate from "../../../../components/graphs/CustomSelectAlternate";
import CustomButton from "../../../../components/CustomButton";

import {
  cumulativeAgriPVAcrossRoute,
  postProcessorRoute,
} from "../../../../utils/constant";
import { getGraphData, getGraphFilters } from "../../../../api/graphs";
import { useSelector } from "react-redux";
import BarChart from "./BarChart";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from "../../../../components/CustomLinearProgress";

const CumulativeAgriPVAcross = ({
  title = "Cumulative Bar Charts Agri and PV / Across Run",
}) => {
  const navigate = useNavigate();
  const theme = useTheme();

  // Query Params
  const { projectId, runId } = useParams();

  // Post Processing Context
  const {
    postProcessingRuns,
    apvAgriRuns,
    pvRuns,
  } = usePostProcessing();

  // Search Params
  const searchParams = new URLSearchParams(window.location.search);
  const graphType = searchParams.get("graphType");

  const [runOptions, setRunOptions] = useState([]);
  const [runs, setRuns] = useState([]);

  // State
  const [isLoader, setIsLoader] = useState(false);
  const [cycles, setCycles] = useState();

  const [cropCycleOptions, setCropCycleOptions] = useState([]);
  const [cropOptions, setCropOptions] = useState([]);
  const [weekFromOptions, setWeekFromOptions] = useState([]);
  const [weekToOptions, setWeekToOptions] = useState([]);

  const [cropCycle, setCropCycle] = useState("");
  const [crop, setCrop] = useState([]);
  const [weekFrom, setWeekFrom] = useState("");
  const [weekTo, setWeekTo] = useState("");

  const SampleuantityAvailableOptions = [
    {
      label: "PV Revenue Per Mega Watt",
      payloadLabel: "PV Revenue Per Mega Watt",
      value: "pv-revenue-per-mega-watt",
      unit: "Revenue Per Mega Watt",
      toggle: "PV",
      controlCropName: true,
    },
    {
      label: "PV Revenue Per Acre",
      payloadLabel: "PV Revenue Per Acre",
      value: "pv-revenue-per-acre",
      unit: "Revenue Per Mega Watt / M²",
      toggle: "PV",
      controlCropName: true,
    },
    {
      label: "Agri Revenue Per Acre",
      payloadLabel: "Agri Revenue",
      value: "agri-revenue",
      unit: "Revenue Per Acre",
      toggle: "AGRI",
      controlCropName: true,
    },
    {
      label: "Total Revenue Per Acre",
      payloadLabel: "Total Revenue",
      value: "total-revenue",
      unit: "Revenue / Acre",
      toggle: "APV",
      controlCropName: true,
    },
    {
      label: "Profit Per Acre",
      payloadLabel: "Profit",
      value: "profit",
      unit: "Profit / Acre",
      toggle: "APV",
      controlCropName: true,
    },
    {
      label: "Cumulative Carbon Assim / Plant",
      payloadLabel: "Cumulative Carbon Assim / Plant",
      value: "cumulative-carbon-assim-per-plant",
      unit: "MICRO MOLE / M² LEAF AREA",
      toggle: "AGRI",
      controlCropName: false,
    },
    {
      label: "Cumulative Carbon Assim / Ground",
      payloadLabel: "Cumulative Carbon Assim / Ground",
      value: "cumulative-carbon-assim-per-ground",
      unit: "MICRO MOLE / M² LEAF AREA / M² GROUND AREA",
      toggle: "AGRI",
      controlCropName: false,
    },
    {
      label: "Total Transpiration / Plant",
      payloadLabel: "Total Transpiration",
      value: "total-transpiration",
      unit: "LITRES/TREE",
      toggle: "AGRI",
      controlCropName: false,
    },
  ];

  const getLabelForValue = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    return option ? `${option.label} (${option.unit})` : null;
  };

  const shouldUseControlCropNames = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    if (option) {
      return option.controlCropName
    }
    return false;
  };

  let quantityAvailableOptions = [];

  if (apvAgriRuns.length > 0 && pvRuns.length > 0) {
    // Include all options for APV
    quantityAvailableOptions = [...SampleuantityAvailableOptions];
  } else if (apvAgriRuns.length > 0) {
    quantityAvailableOptions = SampleuantityAvailableOptions.filter(
      (option) =>
        ![
          "pv-revenue-per-mega-watt",
          "pv-revenue-per-acre",
        ].includes(option.value)
    );
  } else if (pvRuns.length > 0) {
    // Exclude "Hourly Temperature Across The Year" and "Hourly Carbon Assimilation Across The Year"
    quantityAvailableOptions = SampleuantityAvailableOptions.filter(
      (option) =>
        ![
          "agri-revenue",
          "cumulative-carbon-assim-per-plant",
          "cumulative-carbon-assim-per-ground",
          "total-transpiration",
        ].includes(option.value)
    );
  }

  const [quantityAvailable, setQuantityAvailable] = useState(graphType);
  const [series, setSeries] = useState({});

  useEffect(() => {
    navigate(cumulativeAgriPVAcrossRoute(projectId, quantityAvailable));
    const toggle = SampleuantityAvailableOptions.find(
      (option) => option.value === quantityAvailable
    )?.toggle;
    const runList = toggle === "APV" ? [...apvAgriRuns, ...pvRuns] : toggle === "AGRI" ? apvAgriRuns : pvRuns;

    const uniqueRunList = runList.filter(
      (item, index, self) =>
        index === self.findIndex((t) => t.value === item.value)
    );

    setRuns(uniqueRunList.length > 0 ? [uniqueRunList?.[0]?.value] : []);
    setRunOptions(uniqueRunList);
  }, [quantityAvailable, pvRuns, apvAgriRuns]);

  useEffect(() => {
    async function loadFilters() {
      if (runs?.length) {
        const filters = await getGraphFilters(projectId, {
          runIds: apvAgriRuns.map(item => item.value),
          dataType: "with in run",
          frequency: "weekly",
        });

        const cropList = shouldUseControlCropNames(quantityAvailable) ?
          filters?.data.data.runs.flatMap(run => run.controlCropName) :
          filters?.data.data.runs.flatMap(run => run.cropName);
        const weeks = filters?.data.data.runs[0]?.pvWeeks;
        const crops = [...new Set(cropList)]

        setCropOptions([
          // ...(crops.length > 1 ? [{ label: "All", value: "all" }] : []),
          ...crops.map((crop) => ({ label: crop, value: crop })),
        ]);
        setCrop([crops[0]]);

        setWeekFromOptions(
          weeks.map((week, index) => ({ label: week, value: index + 1, disabled: false }))
        );
        setWeekFrom(1);
        setWeekToOptions(
          weeks.map((week, index) => ({ label: week, value: index + 1, disabled: false }))
        );
        setWeekTo(weeks.length);
      }
    }

    loadFilters();
  }, [runOptions]);

  useEffect(() => {
    const toggle = SampleuantityAvailableOptions.find(
      (option) => option.value === quantityAvailable
    )?.toggle;

    if (toggle !== "AGRI" &&
      toggle !== "APV" &&
      quantityAvailable
    ) {
      loadData();
    }
  }, [runOptions]);

  useEffect(() => {
    const toggle = SampleuantityAvailableOptions.find(
      (option) => option.value === quantityAvailable
    )?.toggle;

    if (toggle !== "PV" &&
      crop?.length &&
      quantityAvailable
    ) {
      loadData();
    }
  }, [cropOptions]);

  // const applyFilters = () => {
  //   if(cropCycle && crop.length> 0 && bed.length > 0 && lightConfig.length > 0){
  //     loadData();
  //   }
  // }
  const loadData = () => {
    if (runs?.length) {
      setIsLoader(true);
      getGraphData(projectId, null, {
        runIds: runs,
        crops: crop,
        dataType: "across",
        frequency: "weekly",
        from: weekFrom,
        to: weekTo,
        quantityAvailable: quantityAvailableOptions.filter(
          (obj) => obj.value === quantityAvailable
        )[0]?.payloadLabel,
      }).then((res) => {
        const data = res?.data?.data;
        setIsLoader(false);

        setSeries(data);
      });
    }
  };

  const handleWeekFromChange = (week) => {
    if (weekTo !== null && week > weekTo) return;
    setWeekFrom(week);

    setWeekFromOptions((prev) =>
      prev.map((w) => ({
        ...w,
        disabled: weekTo !== null && w.value > weekTo,
      }))
    );

    // Update toOptions: Enable all, then disable weeks smaller than selected weekFrom
    setWeekToOptions((prev) =>
      prev.map((w) => ({
        ...w,
        disabled: w.value < week,
      }))
    );
  }

  const handleWeekToChange = (week) => {
    if (weekFrom !== null && week < weekFrom) return;
    setWeekTo(week);

    // Update options: Disable weeks smaller than weekFrom in toOptions
    setWeekToOptions((prev) =>
      prev.map((w) => ({
        ...w,
        disabled: weekFrom !== null && w.value < weekFrom,
      }))
    );

    // Update fromOptions: Enable all, then disable weeks greater than selected weekTo
    setWeekFromOptions((prev) =>
      prev.map((w) => ({
        ...w,
        disabled: w.value > week,
      }))
    );
  }

  const generateSeries = () => {
    const tempSeries = [];

    series.map((ele) => {
      tempSeries.push({
        name: ele?.control?.toUpperCase()  === "AGRI" ? `${ele.runName || ""} (Agri-Control)` : 
          ele?.control?.toUpperCase()  === "PV" ? `${ele.runName || ""} (PV-Control)` : ele.runName || "",
        data: [ele.value],
        color: ele?.control?.toUpperCase() === "AGRI" ? 'red' :
          ele?.control?.toUpperCase() === "PV" ? 'blue' : ele.color,
        borderWidth: 1,
        borderColor: "#ccc",
      });
    });
    return tempSeries;
  };

  const getCategories = () => {
    const temp = [];
    series.forEach((ele) => temp.push( ele.runName ));
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
                  {`${runs?.length} Runs Selected`}
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
                  <Grid item xs={12} sm={6} md={4} lg={3} key={"id1"}>
                    <CustomSelectMultipleAlternate
                      id="runs-dropdown"
                      name="Runs"
                      value={runs || runOptions?.[0]?.value || []}
                      options={runOptions}
                      onChange={(e) => {
                        const selectedValues = e.target.value;
                        setRuns(selectedValues.length === 0 ? [runOptions?.[0]?.value] : selectedValues); // If no value selected, set first run
                      }}
                      width="100%"
                      subHeader="Select Runs"
                    />
                  </Grid>
                  {
                    apvAgriRuns.length > 0 &&
                      (
                        graphType === "agri-revenue" ||
                        graphType === "cumulative-carbon-assim-per-plant" ||
                        graphType === "cumulative-carbon-assim-per-ground" ||
                        graphType === "total-transpiration"
                      ) ? (
                      <Grid item xs={12} sm={6} md={4} lg={3} key={"id2"}>
                        <CustomSelectMultipleAlternate
                          id="crop-dropdown"
                          name="Crops"
                          value={crop}
                          options={cropOptions}
                          onChange={(e) => {
                            const selectedValues = e.target.value;
                            setCrop(selectedValues.length === 0 ? [cropOptions?.[0]?.value] : selectedValues); // If no value selected, set 'All'
                          }}
                          width="100%"
                          subHeader="Select Crops"
                        />
                      </Grid>
                    ) : (
                      <></>
                    )}

                </Grid>
                <Grid container spacing={2} style={{
                  marginTop: "1em"
                }}>
                  {
                    apvAgriRuns.length > 0 &&
                      (
                        graphType === "cumulative-carbon-assim-per-plant" ||
                        graphType === "cumulative-carbon-assim-per-ground" ||
                        graphType === "total-transpiration"
                      ) ? (
                      <>
                        <Grid item xs={12} sm={6} md={4} lg={3} key={"id3"}>
                          <label style={{
                            fontWeight: 600,
                            font: 'Montserrat',
                            size: '12px'
                          }}>From</label>
                          <CustomSelectAlternate
                            id="weeks-from"
                            value={weekFrom}
                            options={weekFromOptions}
                            onChange={(e) => handleWeekFromChange(e.target.value)}
                            width="100%"
                            placeholder="Select Weeks From"
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={4} lg={3} key={"id4"}>
                          <label style={{
                            fontWeight: 600,
                            font: 'Montserrat',
                            size: '12px'
                          }}>To</label>
                          <CustomSelectAlternate
                            id="weeks-to"
                            value={weekTo}
                            options={weekToOptions}
                            onChange={(e) => handleWeekToChange(e.target.value)}
                            width="100%"
                            placeholder="Select Weeks To"
                          />
                        </Grid>
                      </>
                    ) : (
                      <></>
                    )}
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
          <Box sx={{ width: "100%", flexGrow: 1 }}>
            {series?.length && <BarChart
              title={getLabelForValue(quantityAvailable)}
              xTitle="Runs"
              series={generateSeries()}
              categories={getCategories()}
              showBarLabels={false}
            />}
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default CumulativeAgriPVAcross;
