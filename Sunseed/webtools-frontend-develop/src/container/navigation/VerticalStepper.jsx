import React, { useEffect } from "react";
import { useState } from "react";
import styled, { useTheme } from "styled-components";
import TripOriginIcon from "@mui/icons-material/TripOrigin";
import filledCircle from "../../assets/filledCircle.svg";
import vector from "../../assets/Vector.svg";
import vectorHighlighted from "../../assets/VectorHighlighted.svg";
import roundCircle from "../../assets/roundCircle.svg";
import preProcessorActive from "../../assets/preProcessorActive.svg";
import analysisManagerActive from "../../assets/analysisManagerActive.svg";
import postProcessorActive from "../../assets/postProcessorActive.svg";
import preProcessor from "../../assets/preProcessor.svg";
import analysisManager from "../../assets/analysisManager.svg";
import postProcessor from "../../assets/postProcessor.svg";
import highlightedCircle from "../../assets/highlightedCircle.svg";
import {
  AppRoutesPath,
  postProcessorRoute,
  preProcessorRoute,
  runManagerRoute,
} from "../../utils/constant";
import { useNavigate, useParams } from "react-router-dom";

const VerticalStepper = ({ onClick, activeTab, open }) => {
  const theme = useTheme();

  const { projectId } = useParams();

  const getCurrentSelectedStep = () => {
    const urlSplit = window.location.pathname.split("/");
    if (urlSplit.includes("pre-processor")) {
      return 0;
    } else if (urlSplit.includes("run-manager")) {
      return 1;
    } else if (urlSplit.includes("post-processor")) {
      return 2;
    }
  };
  const [activeStep, setActiveStep] = useState(getCurrentSelectedStep());
  const navigate = useNavigate();
  const steps = [
    {
      label: "Pre Processor",
      disabled: false,
    },
    {
      label: "Run manager",
      disabled: false,
    },
    {
      label: "Post Processor",
      disabled: false,
    },
  ];

  React.useEffect(() => {
    setActiveStep(getCurrentSelectedStep());
  }, [window.location.pathname]);

  const handleClick = (step, index) => {
    setActiveStep(index);
  };

  // console.log("apv sim", activeTab)

  useEffect(() => {
    if (activeTab === AppRoutesPath.PROJECT) {
      if (activeStep === 0) {
        navigate(preProcessorRoute(projectId));
      } else if (activeStep === 1) {
        navigate(runManagerRoute(projectId));
      } else if (activeStep === 2) {
        navigate(postProcessorRoute(projectId));
        // navigate(`${AppRoutesPath.APV_SIM + AppRoutesPath.POST_PROCESSOR}/${row.original.projectId}`);
      }
    }
  }, [activeStep]);

  const insertSvgIcons = (index) => {
    switch (index) {
      case 0:
        return (
          <img
            style={{ width: "16px", height: "16px", background: "#DB8C47" }}
            src={activeStep === index ? preProcessorActive : preProcessor}
            alt=""
          />
        );
      case 1:
        return (
          <img
            style={{ width: "16px", height: "16px", background: "#DB8C47" }}
            src={activeStep === index ? analysisManagerActive : analysisManager}
            alt=""
          />
        );
      case 2:
        return (
          <img
            style={{ width: "16px", height: "16px", background: "#DB8C47" }}
            src={activeStep === index ? postProcessorActive : postProcessor}
            alt=""
          />
        );
      default:
        return <div />;
    }
  };

  const activeStepsHeight = ["35%", "65%","100%"];

  return (
    <Container>
      {activeTab === AppRoutesPath.PROJECT && (
        <div className="stepperWrapper">
          {steps.map((step, index) => (
            <div
              key={index}
              className="stepsWrapper"
              onClick={() => handleClick(step, index)}
            >
              {open ? (
                <img
                  src={
                    activeStep === index
                      ? filledCircle
                      : index < activeStep
                      ? highlightedCircle
                      : roundCircle
                  }
                  alt=""
                  style={{ zIndex: 1 }}
                />
              ) : (
                <div
                  style={{
                    position: "absolute",
                    zIndex: "2",
                    height: "20px",
                    width: "20px",

                    padding: activeStep === index ? "12px" : "",
                    left: activeStep === index ? "5px" : "17px",
                    borderRadius: activeStep === index ? "30px" : "",
                    background: activeStep === index ? "#EC9954" : "",
                  }}
                >
                  {insertSvgIcons(index)}
                  <div
                    className="steps"
                    style={{
                      color: activeStep === index ? "#FFFFFF" : "#FFFFFF80",
                    }}
                  ></div>
                </div>
              )}

              {open && (
                <div className="labelWrapper">
                  {insertSvgIcons(index)}
                  <div
                    className="steps"
                    style={{
                      color: activeStep === index ? "#FFFFFF" : "#FFFFFF80",
                    }}
                  >
                    {step.label}
                  </div>
                </div>
              )}
            </div>
          ))}
          <div className="verticalLine" />
          <div
            className="verticalLineActive"
            style={{ height: activeStepsHeight[activeStep] }}
          />
        </div>
      )}
    </Container>
  );
};

export default VerticalStepper;

const Container = styled.div`
  position: relative;
  color: #ffffff;
  overflow: hidden;
  .stepsWrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
  }
  .steps {
    font-weight: 600;
  }
  .verticalLine {
    position: absolute;
    top: 0;
    left: 25px;
    width: 2px;
    height: 99%;
    background-color: #ffffff80;
    z-index: 0;
    margin-top: -28px;
  }
  .verticalLineActive {
    position: absolute;
    top: 0;
    left: 25px;
    width: 2px;
    height: 70%; // 40% 70% 100%
    background-color: #ffffff;
    margin-top: -28px;
    z-index: 0;
  }
  .apvSimWrapper {
    display: flex;
    align-items: center;
    padding: 10px 10px 10px 20px;
    /* color: #DB8C47;
    background-color: #ffffff; */
    border-radius: 12px;
    font-family: Montserrat;
    font-size: 14px;
    line-height: 20px;
    text-align: left;
    gap: 10px;
  }
  .stepperWrapper {
    height: 200px;
    margin-left: 20px;
    z-index: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
  }
  .labelWrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-left: 20px;
  }
`;
