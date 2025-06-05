import { ArrowBackIos } from "@mui/icons-material";
import { Box, Button, FormControlLabel, Stack, Switch } from "@mui/material";
import { getCrops, getDeltaGraphData } from "../../../../api/graphs";
import { myDebounce } from "../../../../utils/debounce";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import CustomButton from "../../../../components/CustomButton";
import CustomTextField from "../../../../components/CustomTextField";
import CustomSelect from "../../../../components/graphs/CustomSelect";
import LineGraph from "./LineGraph";
import CustomSelectMultiple from "../../../../components/graphs/CustomSelectMultiple";
import { getAllRunsData } from "../../../../api/runManager";

const QUANTITY_AVAILABLE = {
  CA_YIELD: "CA Yield",
  PV_YIELD: "PV Yield",
};

const CA_LINE_COLORS = ["#3d70c0", "#f47e3c"];
const PV_LINE_COLORS = ["#a5a5a5"];

const KeyDelta = ({ title = "Key Delta Graph" }) => {
  const theme = useTheme();
  const navigate = useNavigate();

  const { projectId } = useParams();

  // States
  const [defaultMin, setDefaultMin] = useState();
  const [defaultMax, setDefaultMax] = useState();
  const [minValueY, setMinValueY] = useState(null);
  const [maxValueY, setMaxValueY] = useState(null);
  const [errors, setErrors] = useState({});

  // Debounced
  const [minValueYDebounced, setMinValueYDebounced] = useState(null);
  const [maxValueYDebounced, setMaxValueYDebounced] = useState(null);

  const [runs, setRuns] = useState([]);

  const [quantityAvailable, setQuantityAvailable] = useState(
    QUANTITY_AVAILABLE.CA_YIELD
  );
  const [graphTitle, setGraphTitle] = useState("");
  const [isDelta, setIsDelta] = useState(false);
  const [cropOptions, setCropOptions] = useState([]);
  const [cropSelected, setCropSelected] = useState([]); // Should be multi select.
  const [series, setSeries] = useState([]);
  const [graphParams, setGraphParams] = useState({});

  useEffect(() => {
    fetchData();
  }, [cropSelected, runs]);

  useEffect(() => fetchRuns(), []);
  const fetchRuns = () => {
    getAllRunsData(projectId, "holding").then((res) => {
      if (res?.data?.data?.runs?.length) {
        setRuns(res?.data?.data?.runs.map((run) => run.id));
      }
    });
  };

  const fetchData = () => {
    if (!runs?.length) return;

    setErrors([]);
    setMinValueY("");
    setMaxValueY("");
    getDeltaGraphData(projectId, {
      runId: runs, // TODO: Hardcoded, make dynamic.
      cropId: cropSelected,
      typeGraph: quantityAvailable,
      isAbsolute: !isDelta,
    }).then((res) => {
      const resData = res?.data?.data;

      if (resData?.graphType === QUANTITY_AVAILABLE.CA_YIELD) {
        setDefaultMin(resData.minCAYield);
        setDefaultMax(resData.maxCAYield);
        const temp_series = [];
        resData?.cropData?.map((crop, outerIndex) => {
          const ca_series = {
            name: isDelta ? `${crop.cropName} w.r.t Control` : crop.cropName,
            data: [],
            color: CA_LINE_COLORS[outerIndex % CA_LINE_COLORS.length],
          };

          crop?.data?.map((datum, index) => {
            ca_series.data.push([datum.runName, datum.carbonAssimilation]);
          });

          temp_series.push(ca_series);
        });

        setSeries(temp_series);
      } else if (resData?.graphType === QUANTITY_AVAILABLE.PV_YIELD) {
        setDefaultMin(resData.minPvYield);
        setDefaultMax(resData.maxPvYield);
        const pv_series = {
          name: isDelta ? "Delta w.r.t Control" : "PV Annual Yield per KWH",
          data: [],
          color: PV_LINE_COLORS[0],
        };

        resData?.data?.map((datum, index) => {
          pv_series.data.push([datum.runName, datum.pvYield]);
        });

        setSeries([pv_series]);
      }
    });
  };

  useEffect(() => fetchCrops(), []);
  const fetchCrops = () => {
    getCrops().then((res) => {
      const data = res?.data?.data;

      if (data?.length) {
        setCropOptions(
          data.map((crop) => ({
            label: crop.name,
            value: crop.id,
          }))
        );
        setCropSelected(data.map((crop) => crop.id));
      }
    });
  };

  useEffect(() => {
    if (quantityAvailable === QUANTITY_AVAILABLE.CA_YIELD) {
      if (isDelta) {
        setGraphTitle("Carbon Assimilation Sim - Wise Delta w.r.t Control");
        setGraphParams({
          ...graphParams,
          backgroundColor: "#4a4a4a",
          xTitle: "Simulation Name",
          yTitle: "Carbon Assim (mols/m2/yr)",
        });
      } else {
        setGraphTitle("Carbon Assimilation Sim - Wise");
        setGraphParams({
          ...graphParams,
          backgroundColor: "white",
          xTitle: "Simulation Name",
          yTitle: "Carbon Assim (mols/m2/yr)",
        });
      }
    }

    if (quantityAvailable === QUANTITY_AVAILABLE.PV_YIELD) {
      if (isDelta) {
        setGraphTitle("PV Yield Sim - Wise Delta w.r.t Control");
        setGraphParams({
          ...graphParams,
          backgroundColor: "#2c2c2c",
          xTitle: "",
          yTitle: "",
        });
      } else {
        setGraphTitle("PV Yield Sim - Wise");
        setGraphParams({
          ...graphParams,
          backgroundColor: "white",
          xTitle: "",
          yTitle: "",
        });
      }
    }

    fetchData();
  }, [quantityAvailable, isDelta]);

  useEffect(() => {
    if (minValueY !== "" && Number(minValueY) < defaultMin) {
      setErrors({
        ...errors,
        minValueY: {
          message: `Min value can't be less than ${defaultMin}`,
        },
      });
    } else {
      setErrors({
        ...errors,
        minValueY: null,
      });
      myDebounce(setMinValueYDebounced, minValueY, 1000);
    }
  }, [minValueY]);

  useEffect(() => {
    if (maxValueY !== "" && Number(maxValueY) > defaultMax) {
      setErrors({
        ...errors,
        maxValueY: {
          message: `Max value can't be more than ${defaultMax}`,
        },
      });
    } else {
      setErrors({
        ...errors,
        maxValueY: null,
      });
      myDebounce(setMaxValueYDebounced, maxValueY, 1000);
    }
  }, [maxValueY]);

  return (
    <Box
      sx={{
        width: "100%",
        display: "flex",
        flexDirection: "column",
        boxSizing: "border-box",
        paddingBottom: "calc(49px + 1em)",
        color: theme.palette.text.main,
      }}
    >
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
          onClick={() => navigate(-1)}
        >
          Back
        </Button>
        <h3 style={{ fontSize: "18px", marginLeft: "1.25em" }}>{title}</h3>
      </Box>

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
        <Box
          sx={{
            "& .emptyLabel": {
              height: 0,
            },
          }}
        >
          <h4
            style={{
              margin: 0,
              marginBottom: "1.5em",
              fontWeight: "500",
              color: theme.palette.text.main,
            }}
          >
            Graph Controls
          </h4>
          <Stack spacing={2}>
            <Stack direction="row" spacing={2}>
              <CustomTextField
                name="minValueY"
                placeHolder="min value"
                suffix="y axis"
                value={minValueY}
                onChange={(e) => setMinValueY(e.target.value)}
                errors={errors}
              />
              <CustomTextField
                name="maxValueY"
                placeHolder="max value"
                suffix="y axis"
                value={maxValueY}
                onChange={(e) => setMaxValueY(e.target.value)}
                errors={errors}
              />
            </Stack>
          </Stack>
        </Box>
        <Box sx={{ marginLeft: "5em" }}>
          <h4
            style={{
              marginTop: 0,
              marginBottom: "1.5em",
              fontWeight: "500",
              color: theme.palette.text.main,
            }}
          >
            Quantity Available
          </h4>
          <CustomSelect
            id="quantity-available"
            value={quantityAvailable}
            width="25em"
            onChange={(e) => setQuantityAvailable(e.target.value)}
            options={[
              {
                value: QUANTITY_AVAILABLE.CA_YIELD,
                label: QUANTITY_AVAILABLE.CA_YIELD,
              },
              {
                value: QUANTITY_AVAILABLE.PV_YIELD,
                label: QUANTITY_AVAILABLE.PV_YIELD,
              },
            ]}
          />
        </Box>
      </Box>

      <Box
        sx={{
          widht: "100%",
          flexGrow: 1,
          display: "flex",
          flexDirection: "column",
          boxSizing: "border-box",
          padding: "1.5em",
          marginTop: "1.5em",
          borderRadius: "1em",
          background: "white",
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
            {graphTitle}
          </h4>
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <span
              style={{
                display: "inline-block",
                margin: "0 1em 0 2em",
                color: isDelta
                  ? theme.palette.text.light
                  : theme.palette.text.main,
              }}
            >
              Absolute
            </span>
            <FormControlLabel
              control={
                <Switch
                  sx={{
                    width: 42,
                    height: 26,
                    padding: 0,
                    marginRight: ".5em",
                    "& .MuiSwitch-switchBase": {
                      padding: 0,
                      margin: "5px",
                      transitionDuration: "300ms",
                      "&.Mui-checked": {
                        transform: "translateX(16px)",
                        color: "#fff",
                        "& + .MuiSwitch-track": {
                          backgroundColor: theme.palette.primary.secondary,
                          opacity: 1,
                          border: 0,
                          ...theme.applyStyles("dark", {
                            backgroundColor: "#2ECA45",
                          }),
                        },
                        "&.Mui-disabled + .MuiSwitch-track": {
                          opacity: 0.5,
                        },
                      },
                      "&.Mui-focusVisible .MuiSwitch-thumb": {
                        color: "#33cf4d",
                        border: "6px solid #fff",
                      },
                      "&.Mui-disabled .MuiSwitch-thumb": {
                        color: theme.palette.grey[100],
                        ...theme.applyStyles("dark", {
                          color: theme.palette.grey[600],
                        }),
                      },
                      "&.Mui-disabled + .MuiSwitch-track": {
                        opacity: 0.7,
                        ...theme.applyStyles("dark", {
                          opacity: 0.3,
                        }),
                      },
                    },
                    "& .MuiSwitch-thumb": {
                      boxSizing: "border-box",
                      width: 16,
                      height: 16,
                    },
                    "& .MuiSwitch-track": {
                      borderRadius: 26 / 2,
                      backgroundColor: "#E9E9EA",
                      opacity: 1,
                      transition: theme.transitions.create(
                        ["background-color"],
                        {
                          duration: 500,
                        }
                      ),
                      ...theme.applyStyles("dark", {
                        backgroundColor: "#39393D",
                      }),
                    },
                  }}
                  value={isDelta}
                  onClick={(e) => setIsDelta(e.target.checked)}
                />
              }
              label="Delta"
              sx={{
                color: isDelta
                  ? theme.palette.text.main
                  : theme.palette.text.light,
              }}
            />
          </Box>

          <Box sx={{ flexGrow: 1 }}></Box>

          <Button
            variant="text"
            sx={{
              textTransform: "capitalize",
              fontWeight: "bold",
              color: theme.palette.primary.secondary,
            }}
          >
            Download Report
          </Button>
          <Box sx={{ margin: "0 1em" }}>
            <CustomSelectMultiple
              id="crops"
              value={cropSelected}
              width="15em"
              onChange={(e) => setCropSelected(e.target.value)}
              options={cropOptions}
            />
          </Box>
          <CustomButton label="Download CSV" variant="contained" />
        </Box>

        <Box sx={{ width: "100%", flexGrow: 1 }}>
          <LineGraph
            minValueY={
              minValueYDebounced || minValueYDebounced === 0
                ? Number(minValueYDebounced)
                : defaultMin
            }
            maxValueY={
              maxValueYDebounced || maxValueYDebounced === 0
                ? Number(maxValueYDebounced)
                : defaultMax
            }
            xTitle={graphParams.xTitle}
            yTitle={graphParams.yTitle}
            isDark={isDelta}
            backgroundColor={graphParams.backgroundColor}
            series={series}
          />
        </Box>
      </Box>
    </Box>
  );
};

export default KeyDelta;
