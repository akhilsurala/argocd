import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import styled from "styled-components";

import HorizontalStepper from "../../components/HorizontalStepper";
import PvGeometry from "./PvGeometry";
import AgriGeneralParameter from "./agriGeneralPage/AgriGeneralParameter";
import EconomicParameters from "./EconomicParameters";
import AgriCropParameter from "./agriCropParameter/AgriCropParameter";
import { Divider, Grid, Stack } from "@mui/material";
import PVPanelVisualizer from "../2d-graphics/PVPanelVisualizer";
import CustomTabs from "./agriGeneralPage/component/CustomTabs";
import Figure from "./agriGeneralPage/component/Figure";

import CropImage3 from "../../assets/crop-figures/crop-figure-3.svg";
import CropImage4 from "../../assets/crop-figures/crop-figure-4.svg";
import CropImage5 from "../../assets/crop-figures/crop-figure-5.svg";
import CropImage6 from "../../assets/crop-figures/crop-figure-6.svg";
import CropImage7 from "../../assets/crop-figures/crop-figure-7.svg";
import CropImage8 from "../../assets/crop-figures/crop-figure-8.svg";
import CropImage9 from "../../assets/crop-figures/crop-figure-9.svg";
import CropImage1 from "../../assets/crop-figures/crop-figure-1.svg";
import CropImage2 from "../../assets/crop-figures/crop-figure-2.svg";
import CropImage10 from "../../assets/crop-figures/crop-figure-10.svg";
import { getPvParametersDropDownData } from "../../api/pvParameters";

const ProjectWrapper = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { state } = location;

  const { projectId } = useParams();
  const [activeStep, setActiveStep] = useState(0);
  const [runId, setCurrentRunId] = useState(location.state?.runId);
  const [isCloned, setIsCloned] = useState(location.state?.isCloned);
  const [isVarient, setIsVarient] = useState(false);

  const [pvModuleList, setPvModuleList] = useState([]);
  const [modeOfPvOperationList, setModeOfPvOperationList] = useState([]);
  const [moduleConfigurationList, setModuleConfigurationList] = useState([]);
  const [soilTypeList, setSoilTypeList] = useState([]);

  const steps = [
    "PV Parameters",
    "Agri General Parameters",
    "Agri crop parameters",
    "Economic parameters",
  ];

  const visualizerRef = useRef(null);
  const [visualizerDimensions, setVisualizerDimensions] = useState({
    width: 0,
    height: 0,
  });

  useEffect(() => {
    callMasterDataApi();
    const handleResize = () => {
      if (visualizerRef.current) {
        setVisualizerDimensions({
          width: visualizerRef.current.offsetWidth,
          height: visualizerRef.current.offsetHeight,
        });
      }
    };

    window.addEventListener("resize", handleResize);
    handleResize(); // Initial call to set dimensions

    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const callMasterDataApi = (mode) => {
    getPvParametersDropDownData(mode)
      .then((res) => {
        setModeOfPvOperationList(res.data.data.modeOfOperations);
        setPvModuleList(res.data.data.pvModules);
        setModuleConfigurationList(res.data.data.moduleConfigurations);
        setSoilTypeList(res.data.data.soils);
      })
      .catch((err) => {
        console.error("Error fetching PV parameters master data:", err);
      });
  };

  // console.log("isCloned", isCloned);

  return (
    <Container>
      <div className="stepperContainer">
        <HorizontalStepper steps={steps} activeStep={activeStep} />
      </div>
      {isCloned && (
        <p
          style={{
            color: "#474F50",
            margin: "20px",
            backgroundColor: "#DB8C471F",
            padding: "12px",
            borderRadius: "6px",
          }}
        >
          <strong>Note: </strong>You should only make up to{" "}
          <strong>2 edits.</strong> Exceeding this, it is advisable to create a{" "}
          <strong> Master run </strong>instead..
        </p>
      )}

      <Grid
        container
        spacing={4}
        sx={{ background: "white", marginTop: "20px" }}
      >
        <Grid item md={6}>
          {activeStep === 0 && (
            <PvGeometry
              setActiveStep={setActiveStep}
              projectId={projectId}
              runId={runId}
              setCurrentRunId={setCurrentRunId}
              isCloned={isCloned}
              setIsCloned={setIsCloned}
              setIsVarient={setIsVarient}
              pvModuleList={pvModuleList}
              setPvModuleList={setPvModuleList}
              modeOfPvOperationList={modeOfPvOperationList}
              setModeOfPvOperationList={setModeOfPvOperationList}
              moduleConfigurationList={moduleConfigurationList}
              setModuleConfigurationList={setModuleConfigurationList}
              soilTypeList={soilTypeList}
              setSoilTypeList={setSoilTypeList}
            />
          )}
          {activeStep === 1 && (
            <AgriGeneralParameter
              setActiveStep={setActiveStep}
              projectId={projectId}
              runId={runId}
              isCloned={isCloned}
              setIsVarient={setIsVarient}
            />
          )}{" "}
          {activeStep === 2 && (
            <AgriCropParameter
              setActiveStep={setActiveStep}
              projectId={projectId}
              runId={runId}
              isCloned={isCloned}
              isVarient={isVarient}
              setIsVarient={setIsVarient}
            />
          )}
          {activeStep === 3 && (
            <EconomicParameters
              setActiveStep={setActiveStep}
              projectId={projectId}
              runId={runId}
              isCloned={isCloned}
              isVarient={isVarient}
              setIsVarient={setIsVarient}
            />
          )}
        </Grid>
        <GridContainer item md={6}>
          {activeStep === 0 && (
            <div
              ref={visualizerRef}
              style={{
                backgroundColor: "#53988E14",
                height: "80vh",
                width: "100%",
              }}
            >
              <div
                style={{
                  width: "100%",
                  boxSizing: "border-box",
                  background: "#e8ecf7",
                  padding: "1em",
                }}
              >
                <CustomTabs
                  tabs={[
                    {
                      label: "PV Design",
                      component: (
                        <>
                          <Figure image={CropImage3} width="90%" />
                          <Stack direction="row" spacing={2}>
                            <Figure
                              title="Tilt Angle - Fixed Tilt"
                              image={CropImage4}
                              width="75%"
                              height="200px"
                            />
                            <Figure
                              title="Max Angle of Tracking"
                              image={CropImage5}
                              width="75%"
                              height="200px"
                            />
                          </Stack>
                        </>
                      ),
                    },
                    {
                      label: "PV Configuration",
                      component: (
                        <CustomTabs
                          tabs={[
                            {
                              label: "Portrait",
                              internal: true,
                              component: (
                                <>
                                  <Stack direction="column">
                                    <Figure
                                      image={CropImage6}
                                      width="80%"
                                      align="flex-start"
                                    />
                                    <Divider />
                                    <Figure
                                      image={CropImage7}
                                      width="80%"
                                      align="flex-start"
                                    />
                                  </Stack>
                                </>
                              ),
                            },
                            {
                              label: "Landscape",
                              internal: true,
                              component: (
                                <>
                                  <Stack direction="column">
                                    <Figure
                                      image={CropImage8}
                                      width="80%"
                                      align="flex-start"
                                    />
                                    <Divider />
                                    <Figure
                                      image={CropImage9}
                                      width="80%"
                                      align="flex-start"
                                    />
                                  </Stack>
                                </>
                              ),
                            },
                          ]}
                          internal={true}
                          legends={[
                            {
                              label: "Longer Side",
                              color: "#53988E80",
                            },
                            {
                              label: "Shorter Side",
                              color: "#DB8C4780",
                            },
                          ]}
                        />
                      ),
                    },
                  ]}
                />
              </div>
              {/* <PVPanelVisualizer width={visualizerDimensions.width} height={visualizerDimensions.height} plotLength={10} plotWidth={20} gap={1000} /> */}
            </div>
          )}
          {activeStep === 1 && (
            <div
              style={{
                width: "100%",
                boxSizing: "border-box",
                padding: ".5em 1em",
                background: "#e8ecf7",
              }}
            >
              <Figure title="Cross Section Overall" image={CropImage1} />
              <Figure
                title="3D Cross Section Bed"
                image={CropImage2}
                width="50%"
              />
            </div>
          )}
          {activeStep === 2 && (
            <div
              style={{
                width: "100%",
                boxSizing: "border-box",
                padding: ".5em 1em",
                background: "#e8ecf7",
              }}
            >
              <Figure image={CropImage10} />
            </div>
          )}
        </GridContainer>
      </Grid>
      {/* add condition according to active step and render component accordingly */}
    </Container>
  );
};

export default ProjectWrapper;

const Container = styled.div`
  overflow: hidden;
  margin: -24px;
  .stepperContainer {
    background-color: ${({ theme }) => theme.palette.background.secondary};
    padding: 16px 0px;
  }
`;

const GridContainer = styled(Grid)`
  height: 85vh;
  overflow: auto;
  &::-webkit-scrollbar {
    height: 4px;
    width: 4px;
  }

  &::-webkit-scrollbar-track {
    box-shadow: #d5d5d5;
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.palette.primary.main};
    border-radius: 8px;
  }
`;
