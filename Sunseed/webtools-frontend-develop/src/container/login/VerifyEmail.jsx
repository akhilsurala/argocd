import React, { useEffect, useState } from "react";
import { Container } from "./CustomContainer";
import { Box, Button, Stack } from "@mui/material";
import { MuiOtpInput } from "mui-one-time-password-input";
import { useCountdown } from "../../hooks/useCountDown";
import { checkVerifyEmail, sendVerifyEmail } from "../../api/userEndPoints";
import { useTheme } from "styled-components";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";
import CustomFormContainer from "../../components/CustomFormContainer";
import { maskEmail } from "../../utils/maskEmail";
import CustomLinearProgress from "../../components/CustomLinearProgress";
import { saveLoginInfo } from "../../utils/localStorage";

const getRemainingTime = (val) => {
  const MS = val * 1000;
  const NOW_IN_MS = new Date().getTime();
  const remSec = NOW_IN_MS + MS;
  return remSec;
};

const VerifyEmail = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const [remainingSecond, setRemainingSecond] = useState(getRemainingTime(30));
  const [days, hours, minutes, seconds] = useCountdown(remainingSecond);
  const [watchState, setWatchState] = useState();
  const [loader, setLoader] = useState(false);

  const handleVerificationapiCall = () => {
    setLoader(true);
    setRemainingSecond(getRemainingTime(30));
    const emailId = localStorage.getItem("emailId");
    const userId = localStorage.getItem("userId");
    sendVerifyEmail({
      email: emailId,
      otpFor: "email verification",

    })
      .then((response) => {
        console.log(response);
        setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        setLoader(false);
      });
  };

  useEffect(() => {
    handleVerificationapiCall();
  }, []);

  const handleSubmit = (data) => {
    setLoader(true);
    const emailId = localStorage.getItem("emailId");
    checkVerifyEmail({
      email: emailId,
      otp: data.otp,
      otpFor: "email verification",
    })
      .then((response) => {
        saveLoginInfo(response.data.data);
        navigate(AppRoutesPath.DEFAULT);
        setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        setLoader(false);
      });
  };

  const formData = [
    [
      {
        key: "otp",
        label: "OTP",
        isRequired: true,
        placeHolder: "",
        name: "otp",
        validate: {},
        pattern: {},
        inputProps: {
          sx: {},
        },
        type: "text",
        componentType: "otpField",
        minLength: 6,
        maxLength: 6,
      },
    ],
  ];
  const defaultValues = {
    otp: "",
  };

  return (
    <Container>
      <div
        className="loginTextContainer"
        style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
      >
        <div className="loginText">Verify Email</div>
        {loader ? <CustomLinearProgress /> : <div className="customBorder" />}
        <div className="loginSubText">
          Please enter the 6 digit verification code sent to
          <br /> <strong>{maskEmail(localStorage.getItem("emailId"))}</strong>
        </div>
        <div style={{ marginTop: "50px" }}>
          <Stack spacing={4}>
            <Box className="formContent">
              <CustomFormContainer
                formData={formData}
                defaultValues={defaultValues}
                setWatchState={setWatchState}
                watchList={[]}
                onFormSubmit={handleSubmit}
                buttonLabel="Submit"
              />
            </Box>
            <div className="createAccountSection">
              <div className="noMember"> Didn’t receive the code? </div>
              {Boolean(seconds) ? (
                <Button disabled={Boolean(seconds)} className="createAccount">
                  Resend code in {seconds}s
                </Button>
              ) : (
                <Button
                  disabled={Boolean(seconds)}
                  className="createAccount"
                  onClick={() => handleVerificationapiCall()}
                >
                  Resend code
                </Button>
              )}
            </div>
          </Stack>
        </div>
      </div>
    </Container>
  );
};

export default VerifyEmail;
