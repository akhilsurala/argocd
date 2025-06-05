import { ArrowBackIos, ExpandMore } from "@mui/icons-material";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  CircularProgress,
  Grid,
  TextField,
} from "@mui/material";

import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import LineGraph from "./LineGraph";

import CustomSelectMultipleAlternate from "../../../../components/graphs/CustomSelectMultipleAlternate";
import CustomSelectAlternate from "../../../../components/graphs/CustomSelectAlternate";
import CustomButton from "../../../../components/CustomButton";

import {
  agriSideHourlyRoute,
  agriSideWeeklyRoute,
  hourlyHeatRoute,
  postProcessorRoute,
} from "../../../../utils/constant";
import { getGraphData, getGraphFilters } from "../../../../api/graphs";
import HeatMap from "./HeatMap";
import { usePostProcessing } from "../PostProcessingContext";
import CustomLinearProgress from "../../../../components/CustomLinearProgress";


const SampleuantityAvailableOptions = [
  {
    label: "Hourly Leaf Temperature Across The Year",
    payloadLabel: "Hourly Temperature Across The Year",
    value: "hourly-temperature-across-the-year",
    unit: "\u00b0C",
    showFilter: true,
    tooltipLabel: "Temperature"
  },
  {
    label: "Hourly Carbon Assimilation Across The Year",
    payloadLabel: "Hourly Carbon Assimilation Across The Year",
    value: "hourly-carbon-assimilation-across-the-year",
    unit: "MICRO MOLE / M² LEAF AREA",
    showFilter: true,
    tooltipLabel: "Carbon Assimilation"
  },
  {
    label: "Bifacial Gain",
    payloadLabel: "Bifacial Gain",
    value: "bifacial-gain",
    unit: "",
    showFilter: false,
    tooltipLabel: "Bifacial Gain"
  },
  {
    label: "Daily Air Temp",
    payloadLabel: "Daily Air Temp",
    value: "daily-air-temp",
    unit: "\u00b0C",
    showFilter: false,
    tooltipLabel: "Temperature"
  },
  {
    label: "Humidity",
    payloadLabel: "Humidity",
    value: "daily-relative-humidity",
    unit: "",
    showFilter: false,
    tooltipLabel: "Humidity"
  },
  {
    label: "Direct Normal Radiation",
    payloadLabel: "Direct Normal Radiation",
    value: "daily-direct-rad",
    unit: "",
    showFilter: false,
    tooltipLabel: "Radiation"
  },
  {
    label: "Diffuse Horizontal Radiation",
    payloadLabel: "Diffuse Horizontal Radiation",
    value: "daily-diffuse-rad",
    unit: "",
    showFilter: false,
    tooltipLabel: "Radiation"
  },
];

const HourlyHeat = ({
  title = "Hourly Leaf Temperature Across The Year / Within Runs",
}) => {
  const navigate = useNavigate();
  const theme = useTheme();

  // Post Processing Context
  const {
    postProcessingRuns,
    defaultRun,
    setDefaultRun,
    runsOptions,
    apvAgriRuns,
    pvRuns,
  } = usePostProcessing();

  // Query Params
  const { projectId, runId } = useParams();

  // Search Params
  const searchParams = new URLSearchParams(window.location.search);
  const graphType = searchParams.get("graphType");

  // State
  const [runName, setRunName] = useState("");
  const [isLoader, setIsLoader] = useState(false);
  const [cycles, setCycles] = useState();

  const [minMaxValue, setMinMaxValue] = useState(null);
  const [minMaxDefaultValue, setMinMaxDefaultValue] = useState(null);

  const [cropCycleOptions, setCropCycleOptions] = useState([]);
  const [bedOptions, setBedOptions] = useState([]);
  const [cropOptions, setCropOptions] = useState([]);
  const [lightConfigOptions, setLightConfigOptions] = useState([]);

  const [cropCycle, setCropCycle] = useState("");
  const [bed, setBed] = useState("");
  const [crop, setCrop] = useState("");
  const [lightConfig, setLightConfig] = useState("");

  const [firstFetch, setFirstFetch] = useState(true);

  const [maxValueRef, setMaxValueRef] = useState(""); // useRef to store value without re-rendering
  const [minValueRef, setMinValueRef] = useState(""); // useRef to store value without re-rendering
  const selctedRunObj = runsOptions.find(item => item.id === defaultRun) || null;


  // const getLabelForValue = (value) => {
  //   const option = SampleuantityAvailableOptions.find((item) => item.value === value);
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

  const getTooltipLabel = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    if (option) {
      return option.tooltipLabel
    }
    return null;
  }

  let quantityAvailableOptions = [];

  if (selctedRunObj?.toggle?.toUpperCase() === "APV") {
    // Include all options for APV
    quantityAvailableOptions = [...SampleuantityAvailableOptions];
  } else if (selctedRunObj?.toggle?.toUpperCase() === "ONLY AGRI") {
    // Exclude "Bifacial Gain"
    quantityAvailableOptions = SampleuantityAvailableOptions.filter(
      (option) => option.value !== "bifacial-gain"
    );
  } else if (selctedRunObj?.toggle?.toUpperCase() === "ONLY PV") {
    // Exclude "Hourly Temperature Across The Year" and "Hourly Carbon Assimilation Across The Year"
    quantityAvailableOptions = SampleuantityAvailableOptions.filter(
      (option) =>
        ![
          "hourly-temperature-across-the-year",
          "hourly-carbon-assimilation-across-the-year",
        ].includes(option.value)
    );
  }

  const [quantityAvailable, setQuantityAvailable] = useState(graphType);

  const [series, setSeries] = useState({});

  const showFilterForQuantity = (value) => {
    const option = quantityAvailableOptions.find((item) => item.value === value);
    return option ? option.showFilter : false;
  };

  useEffect(() => {
    async function loadFilters() {
      const runIds = localStorage.getItem("post-processing-runs");
      if (JSON.parse(runIds)?.length) {
        const filters = await getGraphFilters(projectId, {
          runIds: [runId],
          dataType: "with in run",
          frequency: "weekly",
        });

        const cycles = filters?.data?.data?.runs?.[0]?.cycles;
        const beds = filters?.data?.data?.runs?.[0]?.beds;
        const cropName = filters?.data?.data?.runs?.[0]?.cropName;

        // setCycles(cycles);
        // if (cycles?.length) {
        //   setCropCycleOptions(
        //     cycles.map((cycle) => ({ label: cycle.name, value: cycle.id }))
        //   );
        //   setCropCycle(cycles[0].id);
        // }

        // updateFiltersForCycle(cycles[0]?.id, cycles);

        setBedOptions([
          ...beds.map((bed) => ({ label: `Bed ${bed}`, value: bed })),
        ]);
        setBed(beds[0]);

        setCropOptions([
          ...cropName.map((crop) => ({ label: crop, value: crop })),
        ]);
        setCrop(cropName[0]);

        setLightConfigOptions([
          { label: "sunlit", value: "sunlit" },
          { label: "sunshaded", value: "sunshaded" },
        ]);
        setLightConfig("sunlit");
      }
    }

    loadFilters();
  }, [postProcessingRuns]);

  const generateYearlyHeatMapData = () => {
    const data = [];
    const startDate = new Date("2023-01-01T00:00:00Z"); // Starting at midnight, Jan 1, 2023

    // Loop through each day of the year
    for (let day = 0; day < 365; day++) {
      const currentDay = new Date(startDate);
      currentDay.setUTCDate(startDate.getUTCDate() + day);

      // Loop through each hour of the day
      for (let hour = 0; hour < 24; hour++) {
        // Generate a random temperature value between -10 and 40
        const temperature = Math.round(Math.random() * 50) - 10; // Random value between -10°C and 40°C

        // Push data in the format [timestamp, hour, temperature]
        data.push([currentDay.getTime(), hour, temperature]);
      }
    }

    return data;
  };

  useEffect(() => {
    navigate(hourlyHeatRoute(projectId, runId, quantityAvailable));
    applyFilters();
  }, [quantityAvailable, selctedRunObj]);

  const applyFilters = () => {

    if ((
      (selctedRunObj?.toggle?.toUpperCase() === "ONLY AGRI" || selctedRunObj?.toggle?.toUpperCase() === "APV") &&
      bed?.length &&
      lightConfig?.length
    ) || (
        (selctedRunObj?.toggle?.toUpperCase() === "ONLY PV")
      )
    ) {
      loadData();
    }
  }

  const loadData = () => {
    setIsLoader(true);
    getGraphData(projectId, runId, {
      runIds: [runId],
      crops: [crop],
      beds: [bed],
      configurations: [lightConfig],
      dataType: "with in run",
      frequency: "hourly",
      quantityAvailable: quantityAvailableOptions.filter(
        (obj) => obj.value === quantityAvailable
      )[0]?.payloadLabel,
    }).then((res) => {
      const data = res?.data?.data;
      setIsLoader(false);
      // Generate Graph Series
      const tempSeries = [];
      // console.log("000000000000000000000", data);
      // console.log("555555555555555555555", generateYearlyHeatMapData());
      // data.forEach((ele) => {
      //   tempSeries.push({
      //     name: Object.values(ele?.combination).join(", "),
      //     data: ele.response.map((res, i) => [i, res]),
      //   });
      // });

      const transformedData = data.map(([dateStr, y, value, week_interval]) => [
        new Date(new Date(dateStr).toISOString()).getTime(), // Convert date string to timestamp
        parseInt(y),         // Convert '1', '2', ... to zero-based index
        parseFloat(value),         // Convert '87' to integer,
        week_interval,
      ]);
      // console.log("6666666666666655555555555555555555", data, transformedData)

      setSeries(transformedData);
      // setSeries(generateYearlyHeatMapData());
    });
  };

  // useEffect(() => {
  //   if ((
  //       (selctedRunObj?.toggle?.toUpperCase() === "ONLY AGRI" || selctedRunObj?.toggle?.toUpperCase() === "APV") &&
  //       bed?.length &&
  //       lightConfig?.length
  //     )
  //   ) {
  //     applyFilters();
  //   }
  // }, [bed, crop, lightConfig]);

  useEffect(() => {
    if (
      bed?.length &&
      crop?.length &&
      lightConfig?.length &&
      quantityAvailable &&
      firstFetch
    ) {
      applyFilters();
      setFirstFetch(false);
    }
  }, [bed, crop, lightConfig, quantityAvailable]);

  useEffect(() => {
    const currentRuns = JSON.parse(localStorage.getItem("current-runs"));
    setRunName((currentRuns && currentRuns.find(item => item.id == runId)?.name) || "");
  }, []);

  const handleChangeForMin = (e) => {
    setMinValueRef(e.target.value)
  };
  const handleChangeForMax = (e) => {
    setMaxValueRef(e.target.value); 
  };
  const [error, setError] = useState("");
  const handleApplyFilters = () => {
    if (Number(minValueRef) < minMaxDefaultValue[0] || Number(minValueRef) > minMaxDefaultValue[1] || Number(maxValueRef) > minMaxDefaultValue[1] || Number(maxValueRef) < minMaxDefaultValue[0] || Number(maxValueRef) < Number(minValueRef)) {
      setError(`Minimum and Maximum should be in the range of [${minMaxDefaultValue[0]}, ${minMaxDefaultValue[1]}]`)
      return;
    } else {
      setError("");
      setMinMaxValue([Number(minValueRef), Number(maxValueRef)]);

      applyFilters();
    }

  };


  useEffect(() => {
    if (minMaxDefaultValue) {

      setMinMaxValue([minMaxDefaultValue[0], minMaxDefaultValue[1]]);
      setMinValueRef(minMaxDefaultValue[0]);
      setMaxValueRef(minMaxDefaultValue[1]);
      setError("");

    }
  }, [minMaxDefaultValue])

  const renderHeatMapView = useMemo(() => (
    <Box sx={{ width: "100%", flexGrow: 1 }}>
      {series?.length && (
        <HeatMap
          series={series}
          tooltipLabel={getTooltipLabel(quantityAvailable)}
          setMinMaxDefaultValue={setMinMaxDefaultValue}
          minMaxValue={minMaxValue}
        />
      )}
    </Box>
  ), [series, quantityAvailable, setMinMaxDefaultValue, minMaxValue]);

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
                  {`${showFilterForQuantity(quantityAvailable) ? 3 : 1} Items Selected`}
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
                  {(showFilterForQuantity(quantityAvailable)) && (
                    <>
                      <Grid item xs={12} sm={6} md={4} lg={3} key={"id-1"}>
                        <CustomSelectAlternate
                          id="bed-dropdown"
                          name="Beds"
                          value={bed}
                          options={bedOptions}
                          onChange={(e) => setBed(e.target.value)}
                          width="100%"
                          placeholder="Select Bed Indexes"
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3} key={"id-2"}>
                        <CustomSelectAlternate
                          id="crop-dropdown"
                          name="Crops"
                          value={crop}
                          options={cropOptions}
                          onChange={(e) => setCrop(e.target.value)}
                          width="100%"
                          placeholder="Select Crops"
                        />
                      </Grid>
                      <Grid item xs={12} sm={6} md={4} lg={3} key={"id-3"}>
                        <CustomSelectAlternate
                          id="light-config-dropdown"
                          name="Configurations"
                          value={lightConfig}
                          options={lightConfigOptions}
                          onChange={(e) => setLightConfig(e.target.value)}
                          width="100%"
                          placeholder="Select Configurations"
                        />
                      </Grid>
                    </>)}

                </Grid>
                <Grid container spacing={2} style={{
                  marginTop: "0.5em"
                }}>
                  <>
                    <Grid item xs={12} sm={6} md={4} lg={3} key={"id3"}>
                      <label style={{
                        fontWeight: 400,
                        font: 'Montserrat',
                        size: '12px'
                      }}>Minimum</label>
                      <TextField
                        size="small"
                        value={minValueRef}
                        sx={{ width: "100%" }}
                        onChange={handleChangeForMin}
                        InputProps={{
                          style: {

                            height: "40px",
                            padding: "4px 8px",
                            width: "100%",
                            borderRadius: "8px",
                            fontFamily: theme.palette.fontFamily.main,
                            borderColor: "#C7C9CA",
                            fontSize: "14px",
                          },

                        }}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3} key={"id4"}>
                      <label style={{
                        fontWeight: 400,
                        font: 'Montserrat',
                        size: '12px'
                      }}>Maximum</label>
                      <TextField
                        size="small"
                        value={maxValueRef}
                        sx={{ width: "100%" }}
                        onChange={handleChangeForMax}
                        InputProps={{
                          style: {
                            width: "100%",
                            height: "40px",
                            padding: "4px 8px",
                            width: "100%",
                            borderRadius: "8px",
                            fontFamily: theme.palette.fontFamily.main,
                            borderColor: "#C7C9CA",
                            fontSize: "14px",
                          },

                        }}
                      />
                    </Grid>

                  </>
                </Grid>
              </Box>
              <label style={{ color: 'red' }}>{error}</label>
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  justifyContent: "flex-end",
                  boxSizing: "border-box",
                  paddingTop: "1em",
                }}
              >

                <CustomButton label="Apply" onClick={handleApplyFilters} />
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
          <Box sx={{ width: "100%", display: "flex", alignItems: "center" }}>
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

          </Box>

          {renderHeatMapView}
        </Box>
      </Box>
    </Box>
  );
};

export default HourlyHeat;
