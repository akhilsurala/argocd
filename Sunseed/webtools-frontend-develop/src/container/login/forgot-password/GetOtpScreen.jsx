import React, { useEffect, useState } from "react";
import { Box, Button, Stack } from "@mui/material";
import { useTheme } from "styled-components";
import { useNavigate } from "react-router-dom";
import { Container } from "../CustomContainer";
import { useCountdown } from "../../../hooks/useCountDown";
import { checkVerifyEmail, sendVerifyEmail } from "../../../api/userEndPoints";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { maskEmail } from "../../../utils/maskEmail";
import CustomLinearProgress from "../../../components/CustomLinearProgress";

const getRemainingTime = (val) => {
  const MS = val * 1000;
  const NOW_IN_MS = new Date().getTime();
  const remSec = NOW_IN_MS + MS;
  return remSec;
};

const GetOtpScreen = ({ handleNext }) => {
  const theme = useTheme();
  const emailId = localStorage.getItem("emailId");
  const userId = localStorage.getItem("userId");

  const navigate = useNavigate();
  const [remainingSecond, setRemainingSecond] = useState(getRemainingTime(30));
  const [days, hours, minutes, seconds] = useCountdown(remainingSecond);
  const [watchState, setWatchState] = useState();
  const [loader, setLoader] = useState(false);

  const handleVerificationapiCall = () => {
    setLoader(true);
    setRemainingSecond(getRemainingTime(30));
    sendVerifyEmail({
      email: emailId,
      otpFor: "forget password",
      // userId: userId,
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

  const handleSubmit = (data) => {
    setLoader(true);
    checkVerifyEmail({
      // userId: userId,
      email: emailId,
      otp: data.otp,
      otpFor: "forget password",
    })
      .then((response) => {
        handleNext();
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
            <Box className="formContent" >
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

export default GetOtpScreen;
