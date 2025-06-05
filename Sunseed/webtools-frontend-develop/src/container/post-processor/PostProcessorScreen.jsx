import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useSelector } from 'react-redux';
import styled, { useTheme } from "styled-components";
import HorizontalStepper from "../../components/HorizontalStepper";
import FormSection from "./FormSection";
import GraphSection from "./GraphSection";
import { ArrowBackIos } from "@mui/icons-material";
import { Box, Button } from "@mui/material";
import Timeline from "../timeline";
const gradientOptions = [
  {
    label: "Turbo", stops: [
      { percentage: 0, color: "#2B1934" },
      { percentage: 6, color: "#2E2C7F" },
      { percentage: 21, color: "#4297FF" },
      { percentage: 41, color: "#58FB72" },
      { percentage: 70, color: "#FDBC27" },
      { percentage: 100, color: "#760902" },]
  },
  {
    label: "magma", stops: [{ percentage: 0, color: "#020106" },
    { percentage: 14, color: "#2F1B6A" },
    { percentage: 49, color: "#C83C74" },
    { percentage: 71, color: "#FB7561" },
    { percentage: 100, color: "#FDFAC3" },]
  },
  {
    label: "plasma", stops: [{ percentage: 0, color: "#120869" },
    { percentage: 21, color: "#2F1B6A" },
    { percentage: 49, color: "#C83C74" },
    { percentage: 71, color: "#FB7561" },
    { percentage: 100, color: "#EDFA23" },]
  },

];
// const fileRelatedInfos = {
//   projectId: 1,
//   projectName: 'lpha',
//   runId: 1002,
//   runName: 'A',
//   week: '2023-09-01',
// }

const PostProcessorScreen = () => {
  const [activeStep, setActiveStep] = useState(0);

  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const receivedData = location.state;

  // console.log("obj2", receivedData)
  const { projectId, runId, runName, toggle, quantityAvalilable } = receivedData
  const [fileRelatedInfo, setFileRelatedInfo] = useState({
    projectId: projectId ? projectId : 1,
    projectName: 'testing project name',
    runId: runId ? runId : 1002,
    runName: runName ? runName : 'this is not run name',
    week: '2023-09-01',
  });



  // console.log("fileRelatedInfo", fileRelatedInfo)

  const [minVal, setMinVal] = useState("");
  const [maxVal, setMaxVal] = useState("");
  const [clickedButton, setClickedButton] = useState(null);
  const [hasCrops, setHasCrops] = useState(false);
  const controlPanel = useSelector(state => state.postProcessor.linkDetailsAsPerHours.controlPanel);

  const [selectedQuantityAvailable, setSelectedQuantityAvailable] = useState(quantityAvalilable);

  const [selectedGradient, setSelectedGradient] = useState(gradientOptions[0]);

  useEffect(() => {
    if(controlPanel?.length){
      setHasCrops(
        controlPanel.some(cycle => cycle.weeks.hasOwnProperty(clickedButton))
      );
    }
  }, [clickedButton, controlPanel])

  useEffect(() => {
    if(!hasCrops && (selectedQuantityAvailable === 1 || selectedQuantityAvailable === 2)){
      setSelectedQuantityAvailable(3);
    }
  }, [hasCrops])

  useEffect(() => {
    setSelectedQuantityAvailable(quantityAvalilable)
  }, [quantityAvalilable])
  
  const [quantityAvailable, setQuantityAvailable] = useState([
    {
      id: 3,
      name: "3D-Scene",
    },
    {
      id: 1,
      name: "Carbon assimilation",
    },
    {
      id: 2,
      name: "Temperature Profile",
    }
  ]);

  useEffect(() => {
    const common = [{ id: 3, name: "3D-Scene" }];
    const cropMetrics = hasCrops
      ? [
          { id: 1, name: "Carbon assimilation" },
          { id: 2, name: "Temperature Profile" },
        ]
      : [];

    if (toggle.toUpperCase() === 'ONLY AGRI') {
      setQuantityAvailable([...common, ...cropMetrics]);
    } else if (toggle.toUpperCase() === 'APV') {
      const apvExtras = hasCrops ? cropMetrics : [];
      setQuantityAvailable([...common, ...apvExtras]);
    } else if (toggle.toUpperCase() === 'ONLY PV') {
      setQuantityAvailable([
        ...common,
        { id: 4, name: "DLI" },
      ]);
    }
  }, [toggle, hasCrops]);



  return (
    <ParentContainer>
      <Box
        sx={{
          width: "100%",
          paddingLeft: '20px',
          display: "flex",
          background: "white",
        }}
      >
        <Button
          variant="text"
          size="large"
          style={{ outline: 'none' }}
          sx={{ textTransform: "capitalize", color: theme.palette.text.main }}
          startIcon={<ArrowBackIos />}
          onClick={() => navigate(-1)}
        >
          Back
        </Button>
        <h3 style={{ fontSize: "18px", marginLeft: "1.25em" }}>{"Deep 3d Visualization"}</h3>
        <Box sx={{ flexGrow: 1 }}></Box>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            boxSizing: "border-box",
            paddingRight: "1em",
          }}
        >
          {/* <span style={{ fontSize: "1em" }}>Run Id: {runId}</span> */}
        </Box>
      </Box>
      <Container>
        <FormSection quantityAvailable={quantityAvailable} selectedQuantityAvailable={selectedQuantityAvailable} setSelectedQuantityAvailable={setSelectedQuantityAvailable} selectedGradient={selectedGradient} setSelectedGradient={setSelectedGradient} gradientOptions={gradientOptions} minVal={minVal} maxVal={maxVal} setMinVal={setMinVal} setMaxVal={setMaxVal} />
        <GraphSection selectedQuantityAvailable={selectedQuantityAvailable} setSelectedQuantityAvailable={setSelectedQuantityAvailable} fileRelatedInfo={fileRelatedInfo} gradientStops={selectedGradient} minVal={minVal} maxVal={maxVal} receivedData={receivedData} />
        <Timeline clickedButton={clickedButton} setClickedButton={setClickedButton}/>

      </Container>
    </ParentContainer>
  );
};

export default PostProcessorScreen;

const Container = styled.div`
  padding: 20px;
  display: grid;
  gap: 20px;
  padding-bottom: 40px;
`;

const ParentContainer = styled.div`
  margin-top: -25px;
    margin-left: -20px;
    margin-right: -20px;
`;