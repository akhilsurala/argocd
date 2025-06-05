import { Box, IconButton } from "@mui/material";
import { styled, useTheme } from "styled-components";
import { useEffect, useState } from "react";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { useNavigate } from "react-router-dom";

import CustomFormContainer from "../../../components/CustomFormContainer";
import { forgotPasswordApi } from "../../../api/userEndPoints";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { myDebounce } from "../../../utils/debounce";

const NewPasswordScreen = ({ handleNext }) => {
  const defaultValues = {
    password: "",
    confirmPassword: "",
  };

  //rerendering form only on confirmpassword value changes
  const watchList = ["password", "confirmPassword"]; // list for which we want to check real time validation
  const theme = useTheme();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [watchState, setWatchState] = useState();
  const [loader, setLoader] = useState();
  const [password,setPassword] = useState("");
  const [confirmPassword,setConfirmPassword] = useState("");

  const handleClickShowPassword = () => setShowPassword((show) => !show);
  const handleClickConfirmShowPassword = () =>
    setShowConfirmPassword((show) => !show);

  const inputProps = {
    endAdornment: (
      <IconButton
        aria-label="toggle password visibility"
        onClick={() => handleClickShowPassword()}
        edge="end"
        disabled={showPassword.length === 0}
      >
        {showPassword ? (
          <VisibilityIcon style={{ color: theme.palette.border.main }} />
        ) : (
          <VisibilityOffIcon style={{ color: theme.palette.border.main }} />
        )}
      </IconButton>
    ),
    sx: {
      borderRadius: "6px",
      "& fieldset": {
        borderColor: theme.palette.border.main,
      },
    },
  };
  const confirmInputProps = {
    endAdornment: (
      <IconButton
        aria-label="toggle password visibility"
        onClick={() => handleClickConfirmShowPassword()}
        edge="end"
        disabled={showConfirmPassword.length === 0}
      >
        {showConfirmPassword ? (
          <VisibilityIcon style={{ color: theme.palette.border.main }} />
        ) : (
          <VisibilityOffIcon style={{ color: theme.palette.border.main }} />
        )}
      </IconButton>
    ),
    sx: {
      borderRadius: "6px",
      "& fieldset": {
        borderColor: theme.palette.border.main,
      },
    },
  };

  const validatePassword = (value) => {
    // here we are getting confirmPassword value, and check it with password value for validation
    return password === value || "Passwords do not match";
  };

  // for dynamic validations
  const updateMinMax = (field, formValues) => {
    setConfirmPassword(formValues.confirmPassword);
    setPassword(formValues.password);
  };

  const handleForgotPasswordApi = (data)=>{
    setLoader(true);
    const userId = localStorage.getItem("userId");
    const emailId = localStorage.getItem('emailId');
    const payload = {
      newPassword: data.password,
      userProfileId: userId,
      email : emailId
    };
    forgotPasswordApi(payload)
      .then((response) => {
        handleNext();
        setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        setLoader(false);
      });

  }

  const onSubmit = (data) => {
    myDebounce(handleForgotPasswordApi, data, 500);
  };

  const formData = [
    [
      {
        key: "password",
        label: "Password",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter password",
        name: "password",
        pattern: {
          value: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/,
          message:
            "Password must contain one digit, one lowercase letter, one uppercase letter, and one special character, and it must be 8-16 characters long.",
        },
        inputProps: inputProps,
        type: showPassword ? "text" : "password",
        componentType: "textField",
        validate: {},
        requiredMessage: "Enter password",
      },
    ],
    [
      {
        key: "confirmPassword",
        label: "Confirm Password",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter confirm password",
        name: "confirmPassword",
        pattern: {},
        inputProps: confirmInputProps,
        type: showConfirmPassword ? "text" : "password",
        componentType: "textField",
        validate: validatePassword,
        requiredMessage:"Enter password again"
      },
    ],
  ];

  return (
    <div
      className="loginTextContainer"
      style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
    >
      <div className="loginText">New Password</div>
      {loader ? <CustomLinearProgress /> : <div className="customBorder" />}
      <div className="loginSubText">Enter your new password.</div>
      <div className="formContent">
        <CustomFormContainer
          formData={formData}
          defaultValues={defaultValues}
          updateMinMax={updateMinMax}
          setWatchState={setWatchState}
          watchList={watchList}
          onFormSubmit={onSubmit}
          buttonLabel="Submit"
        />
      </div>
    </div>
  );
};

export default NewPasswordScreen;
