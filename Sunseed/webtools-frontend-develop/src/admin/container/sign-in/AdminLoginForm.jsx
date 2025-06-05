
import { useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { useForm } from "react-hook-form";
import { FormattedMessage } from "react-intl";
import { useNavigate } from "react-router-dom";

import { styled, useTheme } from "styled-components";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";import {
  Button,
  Checkbox,
  FormControlLabel,
  IconButton,
  Stack,
  TextField,
} from "@mui/material";

import messages from "../../../container/login/messages";
import { AppRoutesPath } from "../../../utils/constant";
import { signInApi } from "../../../api/userEndPoints";
import VerifyEmail from "../../../container/login/VerifyEmail";
import {
  getRememberMeCredentials,
  removeRememberMeCredentials,
  saveLoginInfo,
  saveRememberMeCredentials,
} from "../../../utils/localStorage";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../../container/login/CustomContainer";
import { myDebounce } from "../../../utils/debounce";
import { connectWebSocket } from "../../../utils/websocket";

const AdminLoginForm = () => {
  const theme = useTheme();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [activeStep, setActiveStep] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const [isRememberMe, setIsRememberMe] = useState(false);
  const [loader, setLoader] = useState(false);

  const cacheData = getRememberMeCredentials();

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSubmit();
    }
  };

  useEffect(() => {
    cacheData?.rememberMe && setIsRememberMe(cacheData?.rememberMe);
  }, []);

  const {
    handleSubmit,
    register,
    formState: { errors },
    control,
  } = useForm({
    defaultValues: {
      email: (cacheData?.rememberMe && cacheData?.email) || "",
      password: (cacheData?.rememberMe && cacheData?.password) || "",
    },
  });

  function callSignInApi(data) {
    setLoader(true);
    signInApi({
      emailId: data.email,
      password: data.password,
      signInAs :"admin"
    })
      .then((response) => {
        if (response.data.data.user.isVerified === true) {
          saveLoginInfo(response.data.data);
          connectWebSocket(response.data.data.user.emailId, response.data.data.accessToken, dispatch);

          const intendedRoute = localStorage.getItem("intendedRoute");
          if (intendedRoute) {
            localStorage.removeItem("intendedRoute");
            navigate(intendedRoute);
          } else {
            navigate(AppRoutesPath.ADMIN_HOME);
          }
        } else {
          localStorage.setItem("userId", response.data.data.user.userProfileId);
          localStorage.setItem("emailId", response.data.data.user.emailId);
          setActiveStep((prevActiveStep) => prevActiveStep + 1);
        }
        if (isRememberMe) {
          const cacheLogin = {
            rememberMe: isRememberMe,
            email: data.email,
            password: data.password,
          };
          saveRememberMeCredentials(cacheLogin);
        }
        setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        setLoader(false);
      });
  }

  const onSubmit = (data) => {
    console.log(data);
    const getData = setTimeout(() => {
      myDebounce(callSignInApi, data, 500);
    }, 100);
    return () => clearTimeout(getData);
  };

  const handleRememberMe = (e) => {
    setIsRememberMe(e.target.checked);
    !e.target.checked && removeRememberMeCredentials();
  };

  const redirectToSignInAsUser = () => {
    navigate(AppRoutesPath.LOGIN);
  };
  const redirectToForgotPassword = () => {
    navigate(AppRoutesPath.FORGOT_PASSWORD);
  };

  return (
    <Container>
      <div
        onClick={()=> redirectToSignInAsUser()}
        style={{
          position: "absolute",
          top: "36px",
          right: "30px",
          color: theme.palette.primary.main,
          fontFamily: theme.palette.fontFamily.main,
          fontWeight:700,
          cursor:'pointer'
        }}
      >
        Login as User
      </div>
      <div className="loginContainer" style={{left:'50%',maxWidth:"600px"}} >
        {activeStep === 0 && (
          <>
            <div
              className="loginTextContainer"
              style={{ marginTop: window.innerHeight > 900 ? "100px" : "10px" }}
            >
              <div className="loginText">
                <FormattedMessage {...messages.adminSignIn} />
              </div>
              {loader ? (
                <CustomLinearProgress />
              ) : (
                <div className="customBorder" />
              )}
              <div className="loginSubText">
                <FormattedMessage {...messages.welcomeBack} />
              </div>
              <form
                onSubmit={handleSubmit(onSubmit)}
                style={{ marginTop: "50px" }}
              >
                <Stack spacing={4}>
                  <div style={{ width: "100%" }}>
                    <div className="email">Email</div>
                    <TextField
                      type="email"
                      {...register("email", {
                        required: "Email is required",
                        pattern: {
                          value:
                            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                          message: "Invalid email format",
                        },
                      })}
                      error={!!errors.email}
                      helperText={errors.email?.message}
                      sx={{
                        width: "inherit",
                        "& :focus": {
                          outline: "0",
                        },
                      }}
                      InputProps={{
                        sx: {
                          borderRadius: "6px",
                          "& fieldset": {
                            borderColor: theme.palette.border.main,
                          },
                        },
                      }}
                      size="small"
                      placeholder="Enter email"
                      autoComplete="off"
                    />
                  </div>
                  <div style={{ width: "100%" }}>
                    <div className="password">Password</div>
                    <TextField
                      {...register("password", {
                        required: "Password is required",
                      })}
                      error={!!errors.password}
                      helperText={errors.password?.message}
                      sx={{
                        width: "inherit",
                        "& :focus": {
                          outline: "0",
                        },
                      }}
                      size="small"
                      placeholder="Enter password"
                      type={showPassword ? "text" : "password"}
                      onKeyDown={handleKeyDown}
                      autoComplete="off"
                      InputProps={{
                        endAdornment: (
                          <IconButton
                            aria-label="toggle password visibility"
                            onClick={() => setShowPassword(!showPassword)}
                            edge="end"
                            disabled={showPassword.length === 0}
                          >
                            {showPassword ? (
                              <VisibilityIcon
                                style={{ color: theme.palette.border.main }}
                              />
                            ) : (
                              <VisibilityOffIcon
                                style={{ color: theme.palette.border.main }}
                              />
                            )}
                          </IconButton>
                        ),
                        sx: {
                          borderRadius: "6px",
                          "& fieldset": {
                            borderColor: theme.palette.border.main,
                          },
                        },
                      }}
                    />
                    <div className="rememberMeSection">
                      <FormControlLabel
                        sx={{
                          "& .MuiFormControlLabel-label": {
                            color: theme.palette.text.main,
                            fontSize: "14px",
                            lineHeight: "26px",
                            fontFamily: theme.palette.fontFamily.main,
                            fontWeight: 500,
                          },
                        }}
                        control={
                          <Checkbox
                            onClick={(e) => handleRememberMe(e)}
                            checked={isRememberMe}
                            sx={{
                              color: theme.palette.primary.main,
                              "&.Mui-checked": {
                                color: theme.palette.primary.main,
                              },
                            }}
                          />
                        }
                        label="Remember Me"
                      />
                      <div
                        className="forgotPassword"
                        onClick={() => redirectToForgotPassword()}
                      >
                        Forgot Password
                      </div>
                    </div>
                  </div>
                  <Button
                    type="submit"
                    variant="contained"
                    className="btn"
                    sx={{
                      "&:hover": {
                        backgroundColor: theme.palette.secondary.main,
                      },
                    }}
                  >
                    Sign In
                  </Button>
                  <div className="createAccountSection">
                    <div className="noMember"> Couldn't Sign in?</div>{" "}
                    <span
                      className="createAccount"
                    >
                      Get help
                    </span>
                  </div>
                </Stack>
              </form>
            </div>
          </>
        )}
        {activeStep === 1 && <VerifyEmail />}
      </div>
    </Container>
  );
};

export default AdminLoginForm;
