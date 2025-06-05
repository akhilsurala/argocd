import React, { useEffect, useState } from "react";

import { ArrowBackIos } from "@mui/icons-material";
import { Box, Button } from "@mui/material";
import {
  getAllRunsData,
  getDesignExplorerData,
} from "../../../../api/runManager";
import { useParams, useNavigate } from "react-router-dom";
import { useTheme } from "styled-components";

import CustomSelect from "../../../../components/graphs/CustomSelect";
import ParallelGraph from "./ParallelGraph";
import styled from "styled-components";

const DesignExplorer = () => {
  const navigate = useNavigate();
  const theme = useTheme();

  const { projectId } = useParams();

  const [runs, setRuns] = useState([]);
  const [selectedRun, setSelectedRun] = useState(null);
  const [graphData, setGraphData] = useState([]);
  const [highlightedIndex, setHighlightedIndex] = useState(null);

  const generateDataForGraph = (formattedRuns) => {
    return formattedRuns.map((run) => [
      run.id || 0,
      run.azimuth || 0,
      run.lengthOfOneRow || 0,
      run.pitchOfRows || 0,
      run.tiltAngle || 0,
      run.maxAngleOfTracking || 0,
      run.height || 0,
      run.gapBetweenModules || 0,
    ]);
  };

  useEffect(() => fetchRuns(), []);
  const fetchRuns = () => {
    getDesignExplorerData(projectId, {
      runIdList: JSON.parse(localStorage.getItem("post-processing-runs")).map(
        (id) => Number(id)
      ),
    }).then((res) => {
      if (res?.data?.data?.runs?.length) {
        console.log("Runs: ", res?.data?.data?.runs);
        const formattedRuns = res?.data?.data?.runs.map((run) => ({
          id: run.id,
          name: run.runName,
          azimuth: run?.preProcessorToggle?.azimuth,
          lengthOfOneRow: run?.preProcessorToggle?.lengthOfOneRow,
          pitchOfRows: run?.preProcessorToggle?.pitchOfRows,
          pvModule: run?.pvParameters?.pvModule?.moduleType,
          modeOfPvOperation:
            run?.pvParameters?.modeOfOperationId?.modeOfOperation,
          moduleConfiguration:
            run?.pvParameters?.moduleConfigs &&
            run?.pvParameters?.moduleConfigs[0]?.moduleConfig,
          moduleMaskPattern: run?.pvParameters?.moduleMaskPattern,
          tiltAngle: run?.pvParameters?.tiltIfFt,
          maxAngleOfTracking: run?.pvParameters?.maxAngleOfTracking,
          height: run?.pvParameters?.height,
          gapBetweenModules: run?.pvParameters?.gapBetweenModules,
        }));

        setRuns(formattedRuns);
        setGraphData(generateDataForGraph(formattedRuns));
      }
    });
  };

  return (
    <Box>
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
          <h3 style={{ fontSize: "18px", marginLeft: "1.25em" }}>
            Design Explorer
          </h3>
        </Box>

        <Box
          sx={{
            height: "100%",
            minHeight: "100vh",
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
              Design Explorer Sim - Wise
            </h4>
          </Box>

          <Box>
            <ParallelGraph
              data={graphData}
              highlightedIndex={highlightedIndex}
            />
            <Box
              sx={{
                display: "flex",
                minHeight: "10em",
                maxHeight: "20em",
                marginTop: "2em",
              }}
            >
              {selectedRun ? (
                <Box
                  sx={{
                    flexBasis: "1%",
                    flexGrow: 1,
                    boxSizing: "border-box",
                    padding: "1em",
                    paddingTop: "4.5em",
                  }}
                >
                  <DetailsContainer>
                    <DetailsRow>
                      <Detail>Run ID</Detail>
                      <Detail>{selectedRun?.id}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Run Name</Detail>
                      <Detail>{selectedRun?.name}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Azimuth</Detail>
                      <Detail>{selectedRun?.azimuth} Degrees</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Length of One Row</Detail>
                      <Detail>{selectedRun?.lengthOfOneRow} Meters</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Pitch of Rows</Detail>
                      <Detail>{selectedRun?.pitchOfRows}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>PV Module</Detail>
                      <Detail>{selectedRun?.pvModule}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Mode of PV Operation</Detail>
                      <Detail>{selectedRun?.modeOfPvOperation}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Module Configuration</Detail>
                      <Detail>{selectedRun?.moduleConfiguration}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Module Mask Pattern</Detail>
                      <Detail>{selectedRun?.moduleMaskPattern}</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Tile Angle</Detail>
                      <Detail>{selectedRun?.tiltAngle} Degrees</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Max Angle of Tracking</Detail>
                      <Detail>
                        {selectedRun?.maxAngleOfTracking || "NA"} Degrees
                      </Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Height</Detail>
                      <Detail>{selectedRun?.height} Meters</Detail>
                    </DetailsRow>
                    <DetailsRow>
                      <Detail>Gap Between Modules</Detail>
                      <Detail>
                        {selectedRun?.gapBetweenModules} Millimeters
                      </Detail>
                    </DetailsRow>
                  </DetailsContainer>
                </Box>
              ) : (
                <Box
                  sx={{
                    flexBasis: "1%",
                    flexGrow: 1,
                    boxSizing: "border-box",
                    padding: "1em",
                    paddingTop: "4.5em",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                  }}
                >
                  <h4
                    style={{
                      textAlign: "center",
                      color: "#aaa",
                      fontSize: "2em",
                      fontWeight: "normal",
                      margin: 0,
                    }}
                  >
                    Select a run to see details.
                  </h4>
                </Box>
              )}
              <Box
                sx={{
                  flexBasis: "1%",
                  flexGrow: 1,
                  maxWidth: "50%",
                  boxSizing: "border-box",
                  padding: "1em",
                }}
              >
                <CustomSelect
                  id="design-explorer"
                  value={""}
                  width="100%"
                  onChange={(e) => console.log(e.target.value)}
                  options={[
                    {
                      value: "Value 1",
                      label: "Option 1",
                    },
                    {
                      value: "Value 2",
                      label: "Option 2",
                    },
                  ]}
                />
                <Box
                  sx={{
                    boxSizing: "border-box",
                    marginTop: "1em",
                    display: "flex",
                    flexWrap: "wrap",
                    justifyContent: "center",
                  }}
                >
                  {runs?.length &&
                    runs.map((run, index) =>
                      index === highlightedIndex ? (
                        <RunBoxActive
                          onClick={() => {
                            setSelectedRun(run);
                            setHighlightedIndex(index);
                          }}
                        >
                          {run.name}
                        </RunBoxActive>
                      ) : (
                        <RunBox
                          onClick={() => {
                            setSelectedRun(run);
                            setHighlightedIndex(index);
                          }}
                        >
                          {run.name}
                        </RunBox>
                      )
                    )}
                </Box>
              </Box>
            </Box>
          </Box>
        </Box>
      </Box>
    </Box>
  );
};

export default DesignExplorer;

const RunBox = styled.div`
  height: 4em;
  box-sizing: border-box;
  padding: 0.5em;
  margin: 0.25em;
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 2px solid green;
  border-radius: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
`;

const RunBoxActive = styled(RunBox)`
  color: white;
  background: green;
`;

const DetailsContainer = styled.div`
  width: 100%;
`;

const DetailsRow = styled.div`
  width: 100%;
  display: flex;
`;

const Detail = styled.div`
  flex-basis: 50%;
`;
