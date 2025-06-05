import React, { useEffect, useState } from "react";
import styled, { useTheme } from "styled-components";
// import completedRunIcon from "../../assets/completedRunIcon.svg";
import { Box, Button, Checkbox } from "@mui/material";
import PriorityHighIcon from "@mui/icons-material/PriorityHigh";
import CustomButton from "../CustomCard/CustomButton";
import {
  CustomSvgIconForCompleted,
  CustomSvgIconForInQueue,
  CustomSvgIconForRunIcon,
} from "../../container/dashboard/CustomSvgIcon";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import RadioButtonUncheckedRoundedIcon from "@mui/icons-material/RadioButtonUncheckedRounded";
import CustomLinearProgressWithLabel from "./LinearProgressWithLabel";
import ConfirmationDialog from "../ConfirmationDialog";

const CustomRunningCard = ({
  progress,
  status,
  runName,
  simulatedId,
  runId,
  handleStatusChange,
  handleSelection,
}) => {
  const theme = useTheme();

  const [open, setOpen] = useState(false);
  const handleStop = () => {
    handleStatusChange(runId, "Pause");
  };
  const handleDelete = () => {
    handleStatusChange(runId, "Cancel");
    setOpen(false)
  };
  const handleResume = () => {
    handleStatusChange(runId, "Resume");
  };
  const handleRestart = () => {
    handleStatusChange(runId, "Resume");
  };

  const handleDownloadReport = async (id) => {
    // downloadSimulationReport(id).then(res => {
    //     const props = {
    //         data: res?.data?.data,
    //         filename: `${runName}_report`,
    //         delimiter: ","
    //     };
    //     csvDownload(props);
    //     return res;
    // }).catch(err => console.log("failed to download report", err));
  };

  // useEffect(() => {
  //   const projectId = getLocalStorageData("projectId");

  //   getRunsDetails(projectId,"running")
  //     .then((response) => {
  //       console.log(response);
  //       // setPvParametersData(
  //       //   response?.data?.data?.runs[0]?.pvParametersResponseDto
  //       // );
  //       // setCropParametersData(
  //       //   response?.data?.data?.runs[0]?.cropParametersResponseDto
  //       // );
  //     })
  //     .catch((error) => {
  //       console.log(error);
  //     });
  // }, []);

  const FailedSimulation = () => (
    <>
      <div className="runningStatus">
        <div className="background">
          <CustomSvgIconForRunIcon />
        </div>

        <Box className="failed">Failed!</Box>
        <PriorityHighIcon
          sx={{
            color: "#FFFFFF",
            backgroundColor: "#AA4444",
            borderRadius: "50px",
            fontSize: "16px",
            padding: "2px",
          }}
        />
      </div>
      <div className="cardFooterSection">
        <div className="timeLapsedSection">
          {/* <div className="timeLapsed">Time Elapsed: 00:00:30</div> */}
        </div>
        <div className="btnContainer">
          <Button
            variant="text"
            sx={{
              color: "#DB8C47",
              fontFamily: theme.palette.fontFamily.main,
              fontWeight: 700,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "12px",
            }}
            onClick={() => setOpen(true)}
            disableElevation={true}
          >
            Delete
          </Button>
          {/* <Button
                        variant="outlined"
                        sx={{
                            color: "#25272759",
                            fontFamily: theme.palette.fontFamily.main,
                            fontWeight: 600,
                            textTransform: "capitalize",
                            outline: "none !important",
                            borderColor: theme.palette.border.light,
                            fontSize: "12px",
                            "&:hover": {
                                borderColor: theme.palette.border.light,
                            },
                        }}
                        onClick={() => handleDelete()}
                        disableElevation={true}
                    >
                        Check Details
                    </Button> */}
          <Button
            variant="contained"
            sx={{
              color: "#fff",
              backgroundColor: theme.palette.secondary.main,
              fontFamily: "Montserrat",
              fontWeight: 600,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "11px",
              ":hover": {
                backgroundColor: theme.palette.secondary.main,
              },
            }}
            label="Restart"
            onClick={() => handleRestart()}
          >
            Restart
          </Button>
        </div>
      </div>
    </>
  );

  const RunningSimulation = () => (
    <>
      <div className="runningStatus">
        <div className="background">
          <CustomSvgIconForRunIcon />
        </div>
        <CustomLinearProgressWithLabel progress={progress} />
      </div>
      <div className="cardFooterSection">
        <div className="timeLapsedSection">
          {/* <div className="timeLapsed">Time Elapsed: 00:00:30</div>
          <div className="timeLeft">Time Left: 00:01:30</div> */}
        </div>
        <div className="btnContainer">
          <Button
            variant="text"
            sx={{
              color: theme.palette.secondary.main,
              fontFamily: theme.palette.fontFamily.main,
              fontWeight: 700,
              fontSize: "11px",
              textTransform: "capitalize",
              outline: "none !important",
            }}
            onClick={() => setOpen(true)}
            disableElevation={true}
          >
            Delete
          </Button>
          <Button
            variant="contained"
            sx={{
              color: "#fff",
              backgroundColor: theme.palette.secondary.main,
              fontFamily: "Montserrat",
              fontWeight: 600,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "11px",
              ":hover": {
                backgroundColor: theme.palette.secondary.main,
              },
            }}
            onClick={() => handleStop()}
          >
            Pause
          </Button>
        </div>
      </div>
    </>
  );

  const PausedSimulation = () => (
    <>
      <div className="runningStatus">
        <div className="background">
          <CustomSvgIconForRunIcon />
        </div>
        <CustomLinearProgressWithLabel progress={progress} />
      </div>
      <div className="cardFooterSection">
        <div className="timeLapsedSection">
          {/* <div className="timeLapsed">Time Elapsed: 00:00:30</div>
          <div className="timeLeft">Time Left: 00:01:30</div> */}
        </div>
        <div className="btnContainer">
          <Button
            variant="text"
            sx={{
              color: theme.palette.secondary.main,
              fontFamily: theme.palette.fontFamily.main,
              fontWeight: 700,
              fontSize: "11px",
              textTransform: "capitalize",
              outline: "none !important",
            }}
            onClick={() => setOpen(true)}
            disableElevation={true}
          >
            Delete
          </Button>
          <Button
            variant="contained"
            sx={{
              color: "#fff",
              backgroundColor: theme.palette.secondary.main,
              fontFamily: "Montserrat",
              fontWeight: 600,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "11px",
              ":hover": {
                backgroundColor: theme.palette.secondary.main,
              },
            }}
            onClick={() => handleResume()}
          >
            Resume
          </Button>
        </div>
      </div>
    </>
  );

  const CompletedSimulation = ({ simulatedId }) => (
    <>
      <div className="runningStatus">
        <div className="background">
          <CustomSvgIconForRunIcon />
        </div>
        <Box className="completed">Completed!</Box>
        <CustomSvgIconForCompleted />
      </div>
      <div className="cardFooterSection">
        <div className="timeLapsedSection">
          {/* <div className="timeLapsed">Time Elapsed: 00:00:30</div> */}
        </div>
        <div className="btnContainer">
          <Button
            variant="text"
            sx={{
              color: theme.palette.secondary.main,
              fontFamily: theme.palette.fontFamily.main,
              fontWeight: 700,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "12px",
            }}
            onClick={() => setOpen(true)}
            disableElevation={true}
          >
            Delete
          </Button>
          {/* <Button
                        variant="outlined"
                        sx={{
                            color: "#25272759",
                            fontFamily: theme.palette.fontFamily.main,
                            fontWeight: 600,
                            textTransform: "capitalize",
                            outline: "none !important",
                            borderColor: theme.palette.border.light,
                            fontSize: "12px",
                            "&:hover": {
                                borderColor: theme.palette.border.light,
                            },
                        }}
                        onClick={() => handleDelete()}
                        disableElevation={true}
                    >
                        Check Details
                    </Button>
                    <CustomButton label="Download Report" onClick={() => handleDownloadReport(simulatedId)} /> */}
        </div>
      </div>
    </>
  );

  const InQueueSimulation = ({ simulatedId }) => (
    <>
      <div className="runningStatus">
        <div className="background">
          <CustomSvgIconForRunIcon />
        </div>
        <Box className="queued">1 In Queue</Box>
        <CustomSvgIconForInQueue />
      </div>
      <div className="cardFooterSection">
        <div className="timeLapsedSection">
          {/* <div className="timeLapsed">Time Elapsed: 00:00:30</div> */}
        </div>
        <div className="btnContainer">
          <Button
            variant="text"
            sx={{
              color: theme.palette.secondary.main,
              fontFamily: theme.palette.fontFamily.main,
              fontWeight: 700,
              textTransform: "capitalize",
              outline: "none !important",
              fontSize: "12px",
            }}
            onClick={() => setOpen(true)}
            disableElevation={true}
          >
            Delete
          </Button>
          {/* <Button
                        variant="outlined"
                        sx={{
                            color: "#25272759",
                            fontFamily: theme.palette.fontFamily.main,
                            fontWeight: 600,
                            textTransform: "capitalize",
                            outline: "none !important",
                            borderColor: theme.palette.border.light,
                            fontSize: "12px",
                            "&:hover": {
                                borderColor: theme.palette.border.light,
                            },
                        }}
                        onClick={() => handleDelete()}
                        disableElevation={true}
                    >
                        Check Details
                    </Button>
                    <CustomButton label="Download Report" onClick={() => handleDownloadReport(simulatedId)} /> */}
        </div>
      </div>
    </>
  );

  return (
    <Container>
      <div className="cardHeaderSection">
        <div className="runTitle">{runName}</div>

        <Checkbox
          checkedIcon={<CheckCircleRoundedIcon sx={{ color: "#DB8C47" }} />}
          icon={<RadioButtonUncheckedRoundedIcon sx={{ color: "#DB8C47" }} />}
          onChange={(e) => {
            handleSelection(runId, e.target.checked);
          }}
        />
      </div>
      {status === "failed" ? (
        <FailedSimulation />
      ) : status === "running" ? (
        <RunningSimulation />
      ) : status === "completed" ? (
        <CompletedSimulation simulatedId={simulatedId} />
      ) : status === "queued" ? (
        <InQueueSimulation simulatedId={simulatedId} />
      ) : status === "pause" ? (
        <PausedSimulation />
      ) : (
        ""
      )}
      <ConfirmationDialog open={open} onClose={setOpen} onConfirm={handleDelete} title="Confirmation" content="Once deleted, you will not be able to recover this Run" />

    </Container>
  );
};

export default CustomRunningCard;

const Container = styled.div`
  border: 1px solid;
  width: 100%;
  border-radius: 8px;
  border-color: #e3e3e3;
  padding: 16px;
  .runTitle {
    border-color: #e3e3e3;
    color: ${({ theme }) => theme.palette.text.main};
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 14px;
    font-weight: 600;
    line-height: 26px;
    text-align: left;
    padding: 4px 0px;
  }
  .cardHeaderSection {
    border-bottom: 1px solid #e3e3e3;
    display: flex;
    width: 100%;
    justify-content: space-between;
  }
  .runningStatus {
    background-color: #db8c470f;
    padding: 11px 24px;
    margin: 18px 0px;
    display: flex;
    align-items: center;
    gap: 12px;
    border-radius: 8px;
  }

  .background {
    height: 41px;
    width: 41px;
    background-color: #53988e18;
    display: flex;
    align-items: center;
    justify-content: center;

    border-radius: 50%;
  }
  .cardFooterSection {
    display: flex;
    align-items: center;
    /* padding:20px; */
  }
  .timeLapsedSection {
    display: flex;
    align-items: center;
    flex: 1;
    gap: 12px;
  }
  .btnContainer {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .timeLapsed,
  .timeLeft {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 12px;
    font-weight: 600;
    line-height: 22px;
    text-align: left;
    color: #25272759;
  }
  .failed {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 13px;
    font-weight: 600;
    line-height: 22px;
    text-align: left;
    color: #aa4444;
    flex: 1;
  }
  .completed {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 13px;
    font-weight: 600;
    line-height: 22px;
    text-align: left;
    color: #6baa44;
    flex: 1;
  }
  .queued {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 13px;
    font-weight: 600;
    line-height: 22px;
    text-align: left;
    color: #d3c11d;
    flex: 1;
  }
`;
