import {
  Box,
  LinearProgress,
} from "@mui/material";
import { styled, useTheme } from "styled-components";
import { useEffect, useState } from "react";
import SignUp from "./SignUp";
import VerifyEmail from "./VerifyEmail";
import { Container } from "./CustomContainer";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";
const steps = ["Step 1", "Step 2", "Step 3", "Step 4"];



const SignUpWrapper = () => {

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleClickShowConfirmPassword = () =>
    setShowConfirmPassword(!showConfirmPassword);
  const handleMouseDownConfirmPassword = () =>
    setShowConfirmPassword(!showConfirmPassword);
  const handleClickShowPassword = () => setShowPassword(!showPassword);
  const handleMouseDownPassword = () => setShowPassword(!showPassword);

  const theme = useTheme();
  const navigate = useNavigate();

  const [activeStep, setActiveStep] = useState(0);

  const handleNext = () => {
    setTimeout(() => {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }, 500); // Simulating asynchronous task completion
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  // useEffect(()=>{
  //   const userId = localStorage.getItem("userId");
  //   if(!userId){
  //     navigate(AppRoutesPath.SIGN_UP);
  //     setActiveStep(0);
  //   }
  // },[])

  return (
    <Container>
      <div className="loginContainer">
          <Box position="relative" sx={{ color: theme.palette.primary.main }}>
            {activeStep === 0 && <SignUp handleNext={handleNext} />}
            {activeStep === 1 && <VerifyEmail  />}
          </Box>
      </div>
    </Container>
  );
};

export default SignUpWrapper;
