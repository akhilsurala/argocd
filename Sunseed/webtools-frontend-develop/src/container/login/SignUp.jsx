import { Box, IconButton } from "@mui/material";
import { styled, useTheme } from "styled-components";
import { useEffect, useState } from "react";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import { Container } from "./CustomContainer";
import CustomFormContainer from "../../components/CustomFormContainer";
import { signUpPage } from "../../utils/formData/signUpPage";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";
import { callSignUp } from "../../api/userEndPoints";
import CustomLinearProgress from "../../components/CustomLinearProgress";

const SignUp = ({ handleNext }) => {
  const defaultValues = {
    firstName: "",
    lastName: "",
    email: "",
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
  const [loader, setLoader] = useState(false);

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleClickShowPassword = () => setShowPassword((show) => !show);
  const handleClickConfirmShowPassword = () =>
    setShowConfirmPassword((show) => !show);

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSubmit();
    }
  };

  const onSubmit = (data) => {
    // console.log(data);
    setLoader(true);
    callSignUp({
      emailId: data.email,
      password: data.password,
      firstName: data.firstName,
      lastName: data.lastName,
    })
      .then((response) => {
        localStorage.setItem("userId", response?.data?.data?.user?.userId);
        localStorage.setItem("emailId", response?.data?.data?.user?.emailId);
        localStorage.setItem(
          "firstName",
          response?.data?.data?.user?.firstName
        );
        localStorage.setItem("lastName", response?.data?.data?.user?.lastName);
        handleNext();
        setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        setLoader(false);
      });
  };

  const handleChange = (e) => {
    console.log(e.target.value);
  };

  const handleRenderSignInPage = () => {
    navigate(AppRoutesPath.LOGIN);
  };

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

  const updateMinMax = (field, formValues) => {
    setConfirmPassword(formValues.confirmPassword);
    setPassword(formValues.password);
  };

  return (
    <Container>
      {/* <div className="loginContainer"> */}
      <div
        className="loginTextContainer"
        style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
      >
        <div className="loginText">Sign Up</div>
        {loader ? <CustomLinearProgress /> : <div className="customBorder" />}
        <div className="loginSubText">
          Create a new account to start your journey with us.
        </div>
        <div className="formContent">
          <CustomFormContainer
            formData={signUpPage(
              showPassword,
              showConfirmPassword,
              inputProps,
              confirmInputProps,
              validatePassword
            )}
            defaultValues={defaultValues}
            setWatchState={setWatchState}
            updateMinMax={updateMinMax}
            watchList={watchList}
            onFormSubmit={onSubmit}
            buttonLabel="Sign Up"
          />
        </div>
        <div className="createAccountSection" style={{ marginTop: "20px" }}>
          <div className="noMember"> Already a member?</div>
          <Box
            className="createAccount"
            onClick={() => handleRenderSignInPage()}
          >
            Sign In
          </Box>
        </div>
      </div>
      {/* </div> */}
    </Container>
  );
};

export default SignUp;
