import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import CustomTableForRuns from "./CustomTableForRuns";
import CustomRunningCard from "../../../components/simulation-progressbar/CustomRunningCard";
import { Box, Button, Grid } from "@mui/material";
import styled from "styled-components";
import { CreateTableDataFormat } from "./CreateTableDataFormat";
import {
  getAllRunsData,
  getChildRunsData,
  runStatusApi,
} from "../../../api/runManager";
import { useDispatch, useSelector } from "react-redux";
import { setSelectedRunType } from "../../../redux/action/preProcessorAction";
import CustomButton from "../../../components/CustomButton";
import CustomSelect from "../../../components/graphs/CustomSelect";
import { AppRoutesPath, postProcessorRoute } from "../../../utils/constant";
import CustomModal from "../../../components/CustomModal";
import { updateProject } from "../../../api/userProfile";
import ExportPDF from "../../../export-pdf";

export default function RunManagerPage() {
  // const data = CreateTableDataFormat();
  const selectedRunFilter = useSelector(
    (state) => state.preProcessor.selectedRunType
  );

  const navigate = useNavigate();

  const [data, setData] = useState();
  const [loader, setLoader] = useState(false);
  const [allSimulationData, setAllSimulationData] = useState([]);
  const [SimulationData, setSimulationData] = useState();
  const [searchBoxValue, setSearchBoxValue] = useState("");
  const [filteredData, setFilteredData] = useState();
  const [shouldReFetchRuns, setShouldReFetchRuns] = useState(false);
  const [masterRunsOptions, setMasterRunsOptions] = useState([]);
  const [masterRunSelected, setMasterRunSelected] = useState(null);
  const [selectedRuns, setSelectedRuns] = useState({});
  const [selectionModalOpen, setSelectionModalOpen] = useState(false);
  const [downloadReport, setDownloadReport] = useState(false);

  const [currectRunsSelection, setCurrentRunsSelection] = useState("");

  const { projectId } = useParams();
  const dispatch = useDispatch();

  useEffect(() => {
    // Set a timer to update `debouncedSearchValue` after a delay
    const handler = setTimeout(() => {
      callRunningSimulationApi(searchBoxValue);

      callHoldingSimulationApi(searchBoxValue);
    }, 300); // 300ms debounce time

    // Cleanup function to clear the previous timer if searchBoxValue changes
    return () => {
      clearTimeout(handler);
    };
  }, [searchBoxValue]);

  const [firstFetch, setFirstFetch] = useState(true);

  // console.log("currectRunsSelection2", masterRunSelected)

  const callRunningSimulationApi = (searchBoxValue = "") => {
    // data in running bay
    getAllRunsData(projectId, "running", searchBoxValue)
      .then((response) => {
        const runs = response?.data?.data?.runs || [];
        const masterRuns = response?.data?.data?.masterRuns || [];

        if (masterRuns?.length) {
          setMasterRunsOptions(
            masterRuns.map((run) => ({ label: run.runName, value: run.id }))
          );
          if (firstFetch) {
            setMasterRunSelected(masterRuns[0]?.id);

            setFirstFetch(false)

          }
          if (currectRunsSelection) {
            // console.log("currectRunsSelection", currectRunsSelection,)
            runs.forEach((run) => {
              // console.log("currectRunsSelection1", run)

              if (run.id === currectRunsSelection) {
                setMasterRunSelected(run.cloneId ? run.cloneId : currectRunsSelection);
                // console.log("currectRunsSelections", run)


              }
            })
            setCurrentRunsSelection("")
          }
        }

        setAllSimulationData(runs);

        if (
          response?.data?.data?.runs.some(
            (run) => run.runStatus === "running" || run.runStatus === "queued"
          )
        ) {
          setShouldReFetchRuns(true);
        } else {
          setShouldReFetchRuns(false);
        }
      })
      .catch((error) => {
        // console.log(error);
      });
  };

  const extractMasterFamilyTree = (masterRunId, allSimulationRuns) => {

    const runIds = [
      ...allSimulationRuns
        .filter((run) => run.cloneId === masterRunId)
        .map((run) => run.id),
      masterRunId,
    ];

    // console.log("all simulatin", runIds);
    setSimulationData(
      allSimulationRuns.filter((run) => runIds.includes(run.id))
    );
  };

  useEffect(() => {
    if (masterRunSelected) {
      // console.log("allSimulationData", allSimulationData, masterRunSelected)
      extractMasterFamilyTree(masterRunSelected, allSimulationData);
    }
  }, [masterRunSelected, allSimulationData]);

  const callHoldingSimulationApi = (searchBoxValue) => {
    // data in holding bay
    setLoader(true);
    getAllRunsData(projectId, "holding", searchBoxValue)
      .then((response) => {
        const runs = response?.data?.data;
        setData(CreateTableDataFormat(runs));
        setLoader(false);
      })
      .catch((error) => {
        // console.log(error);
        setLoader(false);
      });
  };

  const handleFilterData = (value) => {
    // console.log("filtervvvvvv", value, SimulationData)
    const filterData = [];
    value = value.toLowerCase();
    if (value === "all") {
      setFilteredData(SimulationData);
      return;
    }
    SimulationData?.map((data) => {
      if (value === data.runStatus) filterData.push(data);
    });
    setFilteredData(filterData);
  };

  const callStatusApi = (runId, status) => {
    runStatusApi(runId, { status })
      .then((response) => {
        // console.log(response);
        callRunningSimulationApi();
        callHoldingSimulationApi();
      })
      .catch((err) => {
        // console.log(err);
      });
  };

  const buttonsLabels = ["All", "Completed", "Failed", "Queued"];
  const handleFilterSelect = (value) => {
    dispatch(setSelectedRunType({ selectedRunType: value }));
    handleFilterData(value);
  };

  useEffect(() => {
    callHoldingSimulationApi();
    callRunningSimulationApi();

    if (shouldReFetchRuns) {
      // calling simulation runs api every 5 seconds
      const intervalCall = setInterval(() => {
        callRunningSimulationApi();
      }, 5000);

      return () => {
        // clean up
        clearInterval(intervalCall);
      };
    }
  }, [shouldReFetchRuns]);

  useEffect(() => {
    // console.log("SimulationData", SimulationData)
    handleFilterData(selectedRunFilter);
  }, [selectedRunFilter, SimulationData]);

  const handleMoveToRunningBay = () => {
    callHoldingSimulationApi();
    callRunningSimulationApi();
  };

  const [subRowsLoading, setSubRowsLoading] = useState(true);
  const injectSubRows = (runId) => {
    const workingData = JSON.parse(JSON.stringify(data));

    // API call for child Ids.
    getChildRunsData(projectId, "holding", runId)
      .then((response) => {
        const childRuns = response?.data?.data;
        setData(
          workingData.map((datum) => {
            if (datum.runId === runId) {
              datum.subRows = [...CreateTableDataFormat(childRuns, false)];
            }

            return datum;
          })
        );
      })
      .catch((error) => {
        // console.log(error);
        setLoader(false);
      });
  };

  const detachSubRows = (runId) => {
    const workingData = JSON.parse(JSON.stringify(data));
    setData(
      workingData.map((datum) => {
        if (datum.runId === runId) {
          datum.subRows = [];
        }

        return datum;
      })
    );
  };

  const clearRuns = () => {
    localStorage.removeItem("current-runs");
    localStorage.removeItem("post-processing-runs");
    localStorage.removeItem("pv_runs");
    localStorage.removeItem("apv_agri_runs");
  };

  useEffect(() => {
    clearRuns();
  }, []);

  const handleUpdateProject = (runIds) => {
    const projectName = localStorage.getItem("currentProjectName");
    const dummyData = {
      projectId: projectId,
      projectName: projectName,
      runIds: runIds,
    };
    updateProject(dummyData, projectId)
      .then((response) => {
        // console.log(response);
        const data = response.data.data;
        localStorage.setItem(
          "post-processing-runs",
          JSON.stringify(data.runIds)
        );
        navigate(postProcessorRoute(projectId));
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const openInNewTab = (path, runs) => {
    // const url = `${window.location.origin}${path}`;
    // window.open(url, "_blank");

    window.localStorage.setItem("selected-runs", JSON.stringify(runs));
    window.localStorage.setItem("selected-project", JSON.stringify(projectId));

    setDownloadReport(true);
  };
  const handleMoveToPostProcessing = (downloadReport = false) => {
    clearRuns();
    const runs = Object.keys(selectedRuns).filter((key) => selectedRuns[key]);
    // console.log("Runs: ", runs);
    const currentRuns = {};
    let hasAtleastOneCompletedRun = false;
    allSimulationData.forEach((run) => {
      if (runs.indexOf(`${run.id}`) !== -1 && run.runStatus === "completed") {
        currentRuns[run.id] = {
          name: run.runName,
          toggle: run?.preProcessorToggle?.toggle,
        };
      }
    });

    if (Object.keys(currentRuns)?.length) {
      localStorage.setItem("current-runs", JSON.stringify(currentRuns));
      if (downloadReport) {


        openInNewTab(AppRoutesPath.DOWNLOAD_PDF, Object.keys(currentRuns));
      } else {
        handleUpdateProject(Object.keys(currentRuns));
      }
    } else setSelectionModalOpen(true);
  };

  const handleSelection = (id, checked) => {
    setSelectedRuns({
      ...selectedRuns,
      [`${id}`]: checked,
    });
  };

  // console.log("selectedRuns", currectRunsSelection);
  const moveToSimulateRuns = (data) => {
    // console.log('data', data)
    setCurrentRunsSelection(data[0]);

  };

  return (
    <Container>
      <div style={{ width: "100%", paddingBottom: "100px" }}>
        {data && (
          <CustomTableForRuns
            searchBoxValue={searchBoxValue}
            setSearchBoxValue={setSearchBoxValue}
            data={data}
            refetch={callHoldingSimulationApi}
            moveToSimulateRuns={moveToSimulateRuns}
            injectSubRows={injectSubRows}
            subRowsLoading={subRowsLoading}
            detachSubRows={detachSubRows}
            setEditedUsers={null}
            projectId={projectId}
            handleHoldingBayApiCall={handleMoveToRunningBay}
            isLoading={loader}
            setShouldReFetchRuns={setShouldReFetchRuns}
          />
        )}

        <Box
          sx={{
            marginTop: "20px",
            backgroundColor: "#FFFFFF",
            padding: "20px",
            borderRadius: "10px",
            minHeight: "200px",
          }}
        >
          <Box
            style={{
              width: "100%",
              display: "flex",
              justifyContent: "space-between",
              margin: "20px 0px",
            }}
          >
            <div className="title">Run Box</div>
            <div style={{ display: "flex", gap: "10px" }}>
              {buttonsLabels?.map((data, index) => {
                return (
                  <Button
                    key={index}
                    variant="text"
                    sx={{
                      color:
                        selectedRunFilter === data ? "#53988E" : "#474F5080",
                      border: "1px solid ",
                      borderColor:
                        selectedRunFilter === data ? "#53988E" : "#C7C9CA",
                      textTransform: "capitalize",
                      outline: "none !important",
                      fontSize: "12px",
                      "&:hover": {
                        borderColor: "#53988E",
                      },
                    }}
                    onClick={() => handleFilterSelect(data)}
                    disableElevation={true}
                  >
                    {data}
                  </Button>
                );
              })}
              <Button
                variant="text"
                size="medium"
                disabled={downloadReport}
                sx={{

                  color: "white",
                  backgroundColor: "#53988E",
                  // fontFamily: theme.palette.fontFamily.main,
                  border: "1px solid #C7C9CA",

                  textTransform: "capitalize",
                  outline: "none !important",
                  fontSize: "12px",
                  '&:hover': {
                    backgroundColor: "#53988E",

                    border: "1px solid #C7C9CA",
                  },
                }}
                onClick={() => handleMoveToPostProcessing(true)}
                disableElevation={true}
              >
                {downloadReport ? "Downloading.." : 'Download Report'}
              </Button>
              <CustomSelect
                id="master-run"
                value={masterRunSelected}
                width="15em"
                onChange={(e) => {
                  clearRuns();
                  // console.log("e.target.value: ", e.target.value);
                  const oldSelection = Object.keys(selectedRuns);
                  // console.log("Old Selection: ", oldSelection);
                  const currentSelection = {};
                  Object.keys(e.target.value).forEach((key) => {
                    if (!oldSelection[key])
                      currentSelection[key] = e.target.value[key];
                  });
                  // console.log("Current Selection: ", currentSelection);
                  setSelectedRuns(currentSelection);
                  setMasterRunSelected(e.target.value);
                }}
                options={masterRunsOptions}
              />
              <CustomButton
                label="Move To Post Processing"
                onClick={handleMoveToPostProcessing}
              />
            </div>
          </Box>
          {/* {console.log("filteredData: ", filteredData)} */}
          <Grid container spacing={6} width={"100%"}>
            {filteredData?.map((data, index) => (
              <Grid item md={6} key={index}>
                <Box>
                  {/* {data.id == '548' && console.log("data: ", data)} */}
                  <CustomRunningCard
                    progress={data.progress}
                    status={data.runStatus}
                    runName={data.runName}
                    simulatedId={data.simulatedId}
                    runId={data.id}
                    key={data.id}
                    handleStatusChange={callStatusApi}
                    handleSelection={handleSelection}
                  />
                </Box>
              </Grid>
            ))}
          </Grid>
        </Box>
      </div>
      <CustomModal
        openModal={selectionModalOpen}
        title="Invalid Selection"
        handleClose={() => setSelectionModalOpen(false)}
        children={
          Object.keys(selectedRuns).filter((id) => selectedRuns[id])?.length ? (
            <p>
              Only <b>Completed</b> runs can be moved to post processing.
            </p>
          ) : (
            <p>
              Please select atleast 1 <b>Completed</b> run.
            </p>
          )
        }
      />
      {downloadReport && <ExportPDF setDownloadReport={setDownloadReport} />}
    </Container>
  );
}

const Container = styled.div`
  .title {
    //styleName: H3;
    font-family: Montserrat;
    
    font-size: 18px;
    font-weight: 700;
    line-height: 21.94px;
    text-align: left;
  }
`;
