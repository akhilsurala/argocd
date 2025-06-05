import React from "react";
import { useNavigate } from "react-router-dom";
import { sendVerifyEmail } from "../../../api/userEndPoints";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { useEffect, useState } from "react";
import { Button, LinearProgress, Stack } from "@mui/material";
import { AppRoutesPath } from "../../../utils/constant";
import { useTheme } from "styled-components";
import CustomLinearProgress from "../../../components/CustomLinearProgress";

const GetEmailIdScreen = ({ handleNext }) => {
  const theme = useTheme();
  const navigate = useNavigate();
  const [watchState, setWatchState] = useState();
  const [loader, setLoader] = useState(false);
  const formData = [
    [
      {
        key: "email",
        label: "Email",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter Email",
        // register: register,
        // errors: errors,
        name: "email",
        validate: {},
        pattern: {
          value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
          message: "Invalid email format",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: "#E1D6C8",
            },
          },
        },
        type: "text",
        componentType: "textField",
        maxLength: {
          value: 50,
          message: "Maximum 50 characters are allowed"
        }
      },
    ],
  ];
  const defaultValues = {
    email: "",
  };

  const onSubmit = (data) => {
    setLoader((prev) => !prev);
    sendVerifyEmail({
      email: data.email,
      otpFor: "forget password",
    })
      .then((response) => {
        setLoader((prev) => !prev);
        localStorage.setItem("emailId", response.data.data.emailId);
        localStorage.setItem("userId", response.data.data.userProfileId);
        handleNext();
      })
      .catch((error) => {
        setLoader((prev) => !prev);
        console.log(error);
      });
  };

  const redirectToSignUpPage = () => {
    navigate(AppRoutesPath.SIGN_UP);
  };

  console.log(loader);

  return (
    <div
      className="loginTextContainer"
      style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
    >
      <div className="loginText">Forgot Password</div>
      {loader ? <CustomLinearProgress /> : <div className="customBorder" />}
      <div className="loginSubText">Enter you email to get an OTP.</div>
      {/* <form style={{ marginTop: "50px" }}> */}
      <Stack spacing={4}>
        <div className="formContent">
          <CustomFormContainer
            formData={formData}
            defaultValues={defaultValues}
            setWatchState={setWatchState}
            // watchList={watchList}
            onFormSubmit={onSubmit}
            buttonLabel="Send OTP"
          />
        </div>
        <div className="createAccountSection">
          <div className="noMember"> Not a member? </div>
          <Button
            className="createAccount"
            onClick={() => redirectToSignUpPage()}
          >
            Create account
          </Button>
        </div>
      </Stack>
      {/* </form> */}
    </div>
  );
};

export default GetEmailIdScreen;
