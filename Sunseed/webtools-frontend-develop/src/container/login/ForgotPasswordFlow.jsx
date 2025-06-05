import { useForm, Controller } from "react-hook-form";
import {
  Box,
  Button,
  Checkbox,
  FormControlLabel,
  IconButton,
  InputAdornment,
  LinearProgress,
  Stack,
  TextField,
} from "@mui/material";
import { styled, useTheme } from "styled-components";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { MuiOtpInput } from "mui-one-time-password-input";
import { useCountdown } from "../../hooks/useCountDown";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import {
  Stepper,
  Step,
  StepLabel,
  CircularProgress,
  Typography,
} from "@mui/material";
import { Container } from "./CustomContainer";
import { AppRoutesPath } from "../../utils/constant";
import { maskEmail } from "../../utils/maskEmail";
import CustomFormContainer from "../../components/CustomFormContainer";
import { sendVerifyEmail } from "../../api/userEndPoints";
import GetEmailIdScreen from "./forgot-password/GetEmailIdScreen";
import GetOtpScreen from "./forgot-password/GetOtpScreen";
import NewPasswordScreen from "./forgot-password/NewPasswordScreen";
const steps = ["Step 1", "Step 2", "Step 3", "Step 4"];

const getRemainingTime = (val) => {
  const MS = val * 1000;
  const NOW_IN_MS = new Date().getTime();
  const remSec = NOW_IN_MS + MS;
  return remSec;
};
const ForgotPasswordFlow = () => {
  const [remainingSecond, setRemainingSecond] = useState(getRemainingTime(1));
  const [days, hours, minutes, seconds] = useCountdown(remainingSecond);
  const [otp, setOtp] = useState("");

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
  const redirectToSignInPage = () => {
    navigate(AppRoutesPath.LOGIN);
  };

  const getResetSuccessfulStep4 = () => {
    console.log("otp 4");
    return (
      <div
        className="loginTextContainer"
        style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
      >
        <div className="loginText">Reset Successful</div>
        <div className="customBorder"></div>
        <div className="loginSubText">Hurray!, All done</div>
        <form style={{ marginTop: "50px" }}>
          <Stack spacing={4}>
            <div className="email">
              Your password has been successfully reset , Click below to
              continue
            </div>

            <Button
              type="submit"
              variant="contained"
              className="btn"
              onClick={redirectToSignInPage}
              sx={{
                "&:hover": {
                  backgroundColor: theme.palette.secondary.main,
                },
              }}
            >
              Sign In
            </Button>
          </Stack>
        </form>
      </div>
    );
  };

  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);

  const handleNext = () => {
    if (activeStep === 0) {
      setRemainingSecond(getRemainingTime(30));
    } else {
      setRemainingSecond(getRemainingTime(1));
    }
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }, 1000); // Simulating asynchronous task completion
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  return (
    <Container>
      <div className="loginContainer">
        <div>
          <Box position="relative" sx={{ color: theme.palette.primary.main }}>
            {activeStep === 0 && <GetEmailIdScreen handleNext={handleNext} />}
            {activeStep === 1 && <GetOtpScreen handleNext={handleNext} />}
            {activeStep === 2 && <NewPasswordScreen handleNext={handleNext} />}
            {activeStep === 3 && getResetSuccessfulStep4()}
          </Box>
        </div>
      </div>
    </Container>
  );
};

export default ForgotPasswordFlow;
