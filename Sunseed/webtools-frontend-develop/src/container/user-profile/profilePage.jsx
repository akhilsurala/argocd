import React from 'react';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import {
  Stack,
  Grid,
  Card,
  CardContent,
  Typography,
  Avatar,
  Badge,
  Button,
  TextField,
  Box,
  IconButton,
} from '@mui/material';
import { Controller, useFieldArray, useForm } from "react-hook-form";

import styled from 'styled-components';
import { useTheme } from "styled-components";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";

import userProfileIcon from "../../assets/userProfile.svg";
import CustomInputField from '../apv-sim/agriGeneralPage/component/CustomInputField';
import { myDebounce } from '../../utils/debounce';
import { getUserProfileDetails, updatePassword, updateUserProfile } from '../../api/userProfile';
import { sendVerifyEmail } from '../../api/userEndPoints';
import { updateUserInfo } from '../../utils/localStorage';
import { userProfileUpdated } from '../../redux/action/homeAction';
import { useCountdown } from '../../hooks/useCountDown';
import { BASE_URL_FOR_PUBLIC_ASSET, BASE_URL_FOR_USER_AVATAR } from '../../api/config';
import Header from '../home/Header';
import Footer from '../home/Footer';

const ProfilePage = () => {
  const dispatch = useDispatch();
  const theme = useTheme();

  const getRemainingTime = (val) => {
    const MS = val * 1000;
    const NOW_IN_MS = new Date().getTime();
    const remSec = NOW_IN_MS + MS;
    return remSec;
  };

  const [errorMessageForAvatar, setErrorMessageForAvatar] = useState('');
  const [userInfo, setUserInfo] = useState({});
  const [editable, setEditable] = useState(false);
  const [watchState, setWatchState] = useState([]);
  const [selectedImage, setSelectedImage] = useState();
  const [previewUrl, setPreviewUrl] = useState(null);
  const [open, setOpen] = React.useState(false);

  const [changePassword, setChangePassword] = useState(false);
  const [verificationCodeSent, setVerificationCodeSent] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [code, setCode] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [remainingSecond, setRemainingSecond] = useState(null);
  const [days, hours, minutes, seconds] = useCountdown(remainingSecond);

  const updateUserProfileInfo = useSelector((state) => state.homeDashboard.userProfileUpdated);

  const handleClickShowPassword = () => setShowPassword((show) => !show);
  const handleClickConfirmShowPassword = () =>
    setShowConfirmPassword((show) => !show);

  const defaultValues = {
    firstName: userInfo?.firstName,
    lastName: userInfo?.lastName,
    email: localStorage.getItem("emailId")
  };

  const {
    handleSubmit,
    control,
    watch,
    register,
    reset,
    trigger,
    setValue,
    formState: { errors, },
  } = useForm({
    mode: 'all',
    defaultValues: defaultValues,
  });

  const {
    register: register2,
    formState: { errors: errors2 },
    handleSubmit: handleSubmit2,
    control: control2,
    watch: watch2,
    reset: reset2,
    trigger: trigger2,
    setValue: setValue2,
    getValues: getValues,
  } = useForm({
    mode: 'all',
    defaultValues: {},
  });

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const validImageTypes = ['image/jpeg', 'image/png',];
      const maxSizeInMB = 2; // Maximum file size (2MB)

      // Check file type
      if (!validImageTypes.includes(file.type)) {
        setErrorMessageForAvatar("Only JPG and PNG images are allowed.");
        // alert("Only JPG and PNG images are allowed.");
        return;
      }

      // Check file size
      const fileSizeInMB = file.size / 1024 / 1024;
      if (fileSizeInMB > maxSizeInMB) {
        setErrorMessageForAvatar(`File size exceeds ${maxSizeInMB}MB.`);
        // alert(`File size exceeds ${maxSizeInMB}MB.`);
        return;
      }
      const imageUrl = URL.createObjectURL(file);
      setSelectedImage(file);
      setPreviewUrl(imageUrl);
      setErrorMessageForAvatar('');
    }
  };

  const watchFields = watch(["firstname", "lastname"]);

  const onSubmit = (data) => {
    myDebounce(handleUpdateUserProfileApi, data, 500);
  };

  const onPasswordSubmit = (data) => {
    updatePassword({
      userProfileId: localStorage.getItem('userId'),
      newPassword: data.password,
      otp: data.code,
    })
      .then((response) => {
        console.log(response);
        setChangePassword(false);
        setVerificationCodeSent(false);

        const variableName = "successMessage";
        window[variableName] = 'Password changed successfully';
        window['snackBargColor'] = "#28a745";
        const event = new CustomEvent("windowVariableAdded", {
          detail: { variableName },
        });
        window.dispatchEvent(event);
        // setLoader(false);
        reset2();
      })
      .catch((error) => {
        console.log(error);
        setChangePassword(false);
        setVerificationCodeSent(false);
        // setLoader(false);
      });
    // myDebounce(handleUpdateUserProfileApi, data, 500);
  };

  useEffect(() => {
    getUserProfileDetails()
      .then((response) => {
        setUserInfo(response.data?.data);
        dispatch(userProfileUpdated(!updateUserProfileInfo));
        updateUserInfo(response.data.data);
        setValue('firstName', response.data.data.firstName);
        setValue('lastName', response.data.data.lastName);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const handleUpdateUserProfileApi = (data) => {
    // setLoader(true);
    const payload = new FormData();
    payload.append("firstName", data.firstName);
    payload.append("lastName", data.lastName);
    payload.append("phoneNumber", "123");
    selectedImage && payload.append("profilePic", selectedImage);

    updateUserProfile(payload)
      .then((response) => {
        console.log("response", response);
        setUserInfo(response.data.data);
        dispatch(userProfileUpdated(!updateUserProfileInfo));

        const variableName = "successMessage";
        window[variableName] = 'Profile updated successfully';
        window['snackBargColor'] = "#28a745";
        const event = new CustomEvent("windowVariableAdded", {
          detail: { variableName },
        });
        window.dispatchEvent(event);

        // localStorage.setItem("userId", response?.data?.data?.user?.userProfileId);
        // localStorage.setItem("emailId", response?.data?.data?.user?.emailId);
        localStorage.setItem(
          "firstName",
          response?.data?.data?.firstName
        );
        localStorage.setItem("lastName", response?.data?.data?.lastName);
        localStorage.setItem("profilePicturePath", response?.data?.data?.profilePicturePath);
        // setLoader(false);
        setEditable(false);
      })
      .catch((error) => {
        console.log(error);
        setEditable(false);
        // setLoader(false);
      });
  }
  const getUserName = () => {
    const firstName = "firstName";
    const lastName = "lastName";
    return (
      <>
        <Gap />
        <div style={{ display: 'flex', gap: '20px', justifyContent: 'space-between' }}>
          <div style={{ flexGrow: '1' }}>
            <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >First Name</div>
            <Gap />
            <CustomInputField name={firstName} type={'text'} noFlotingValue={true} control={control} errors={errors} disabled={!editable} rules={{
              required: "Enter first name",
              pattern: {
                value: /^[A-Za-z]+$/i,
                message: "Invalid first name"
              },
              maxLength: {
                value: 20,
                message: "Maximum 20 characters are allowed"
              }
            }} />
          </div>
          <div style={{ flexGrow: '1' }}>
            <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >Last Name</div>
            <Gap />
            <CustomInputField name={lastName} type={'text'} control={control} errors={errors} disabled={!editable} rules={{
              pattern: {
                value: /^[A-Za-z]+$/i,
                message: "Invalid last name"
              },
              maxLength: {
                value: 20,
                message: "Maximum 20 characters are allowed"
              }
            }} />
          </div>
        </div>
        <Gap />
      </>
    )
  }

  const getRestOfUserDetails = () => {
    const email = "email";
    const roles = localStorage.getItem("roles") || [];

    return (
      <>
        <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >Email</div>
        <Gap />
        <CustomInputField name={email} type={'text'} noFlotingValue={true} control={control} errors={errors} disabled={true} rules={{}} />
        <Gap />
      </>
    )
  }

  const getUserEmailForVerification = () => {
    const email = "email";

    return (
      <>
        <Gap />
        <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
          <div style={{ flex: 1, display: 'flex', paddingRight: '0px', flexDirection: 'column' }}>
            <div
              style={{
                marginRight: '5px',
                fontWeight: 500,
                fontFamily: theme.palette.fontFamily.main,
              }}
            >
              Email
            </div>
            <Gap />
            <TextField
              value={defaultValues.email}
              disabled={true}
              fullWidth
              sx={{
                borderColor: 'E0E0E0',
                "& :focus": {
                  outline: 0,
                },
                backgroundColor: true && theme.palette.background.faded,
              }}
              variant="outlined"
              size="small"
              autoComplete="off"
            />
          </div>
          {
            changePassword && <div style={{ flexShrink: 0 }}>
              <Gap /><Gap />
              <Button
                type="verify"
                className="btn"
                data-testid="verifyButton"
                disabled={!changePassword || Boolean(seconds)}
                onClick={(e) => {
                  e.preventDefault();
                  handleSendVerificationCode();
                }}
                sx={{
                  "&:hover": {
                    backgroundColor: '#FFFFFF',
                  },
                  fontFamily: 'Montserrat',
                  width: '100%',
                  color: '#474F5080',
                  backgroundColor: '#FFFFFF',
                  alignSelf: "flex-end",
                  justifyContent: "flex-end",
                  borderRadius: '8px',
                  border: '1px solid #C7C9CA',
                  fontWeight: 700,
                  fontSize: '14px',
                  height: '44px',
                  whiteSpace: 'nowrap',
                }}
              >{Boolean(seconds) ? (`Resend code in ${seconds}s`) : 'Send Verification Code'}
              </Button>
            </div>
          }
        </div>
        <Gap />
      </>
    )
  }

  const getPasswordFields = () => {
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

    return verificationCodeSent && (
      <>
        <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >Verification Code</div>
        <Gap />
        <CustomInputField name={"code"} type={'number'} noFlotingValue={true} control={control2} errors={errors2} disabled={!verificationCodeSent} rules={{
          required: "Enter OTP to proceed",
          minLength: {
            value: 6,
            message: "Minimum 6 characters are allowed"
          },
          maxLength: {
            value: 6,
            message: "Maximum 6 characters are allowed"
          }
        }}
        />
        <Gap />

        <div style={{ display: 'flex', gap: '20px', justifyContent: 'space-between' }}>
          <div style={{
            flexGrow: '1',
            maxWidth: '45%',  // Constrain the width of the div
            whiteSpace: 'normal',  // Allow the content to wrap within the div
            overflow: 'hidden',    // Prevents overflow of content
            textOverflow: 'ellipsis'  // Add ellipsis for overflowing text
          }}>
            <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >New Password</div>
            <Gap />
            <CustomInputField name={"password"} type={(showPassword ? "text" : "password")} inputProps={inputProps} noFlotingValue={true} control={control2} errors={errors2} disabled={!verificationCodeSent} rules={{
              required: "Enter password",
              pattern: {
                value: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/,
                message:
                  "Password must contain one digit, one lowercase letter, one uppercase letter, and one special character, and it must be 8-16 characters long.",
              },
            }}
            />
          </div>
          <div style={{
            flexGrow: '1',
            maxWidth: '45%',  // Constrain the width of the div
            whiteSpace: 'normal',  // Allow the content to wrap within the div
            overflow: 'hidden',    // Prevents overflow of content
            textOverflow: 'ellipsis'  // Add ellipsis for overflowing text
          }}>
            <div style={{ marginRight: '5px', fontWeight: 500, fontFamily: theme.palette.fontFamily.main }} >Confirm Password</div>
            <Gap />
            <CustomInputField name={"confirmPassword"} type={(showConfirmPassword ? "text" : "password")} inputProps={confirmInputProps} noFlotingValue={true} control={control2} errors={errors2} disabled={!verificationCodeSent} rules={{
              validate: {
                validate: validatePassword,
              },
              required: "Enter password again"
            }} />
          </div>
        </div>
        <Gap />
      </>
    )
  }

  const handleSendVerificationCode = () => {
    handleVerificationapiCall();
    setVerificationCodeSent(true);
    setRemainingSecond(getRemainingTime(30));
  }
  const validatePassword = (value) => {
    return getValues('password') === value || "Passwords do not match";
  };

  const handleVerificationapiCall = () => {
    const emailId = localStorage.getItem("emailId");
    sendVerifyEmail({
      email: emailId,
      otpFor: "change password",
    })
      .then((response) => {
        console.log(response);
        // setLoader(false);
      })
      .catch((error) => {
        console.log(error);
        // setLoader(false);
      });
  };

  const handleUserProfileUpdate = () => {
    if (errorMessageForAvatar) {
      return
    }
    setErrorMessageForAvatar('');
    handleSubmit(onSubmit)()
  }

  const handlePCancelButtonForPasswordUpdate = () => {
    setChangePassword(false);
    setVerificationCodeSent(false);
  }

  const handlePCancelButtonForProfileUpdate = () => {
    setEditable(false);
  }

  const handleNewPasswordChange = () => {
    if (!verificationCodeSent) {
      return
    }
    handleSubmit2(onPasswordSubmit)()
  }

  function stringAvatar(name) {
    console.log("88888", name);
    return {
      children: `${name.split(' ')[0][0]}${name.split(' ')[1][0]}`,
    };
  }

  const renderButton = (button, setButton, handleOnClick, handlePCancelButton) => {
    return button ? (
      <div
        style={{
          display: "flex",
          justifyContent: "right",
          gap: "10px",
        }}
      >
        <Button
          type="reset"
          className="prevBtn"
          data-testid="previousButton"
          onClick={(e) => {
            e.preventDefault();
            handlePCancelButton();
          }}
          sx={{
            ...({
              color: '#474F5080',
              border: '1px solid',
              borderRadius: '8px',
              alignSelf: "flex-end",
              width: "100px",
            }),
          }}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          className="btn"
          data-testid="submitButton"
          onClick={(e) => {
            e.preventDefault();
            handleOnClick();
            // handleSubmit(onSubmit)()
          }}
          sx={{
            "&:hover": {
              backgroundColor: theme.palette.secondary.main,
            },
            color: "white",
            backgroundColor: theme.palette.secondary.main,
            alignSelf: "flex-end",
            width: "100px",
            borderRadius: '8px',
            padding: '8px 12px',
          }}
        >
          Update
        </Button>
      </div>
    ) : (
      <Button
        type="edit"
        className="btn"
        data-testid="editButton"
        onClick={(e) => {
          e.preventDefault();
          setButton(true);
        }}
        sx={{
          "&:hover": {
            backgroundColor: '#FFFFFF',
          },
          color: '#474F5080',
          backgroundColor: '#FFFFFF',
          alignSelf: "flex-end",
          width: "100px",
          borderRadius: '8px',
          border: '1px solid #C7C9CA',
          padding: '8px 12px',
        }}
      >
        Edit
      </Button>)
  };

  return (
    <>
      <Header drawerOpen={false} />
      <Grid container justifyContent="center" sx={{ margin: '70px 0px', padding: '20px', fontFamily: theme.palette.fontFamily.main, }}>
        <Card sx={{ width: '100%', margin: '20px', fontFamily: theme.palette.fontFamily.main, }}>
          <CardContent>
            <Grid container justifyContent="space-between" alignItems="center">
              <Typography variant="h5" fontWeight="bold"
                sx={{
                  fontSize: '18px',
                  marginLeft: 2,
                  fontFamily: theme.palette.fontFamily.main,
                }}>
                My Profile
              </Typography>
            </Grid>

            <Grid container spacing={0} >
              <Grid
                item
                xs={12}
                sx={{
                  margin: '20px',
                  border: '1px solid #e0e0e0',
                  borderRadius: '10px'
                }}
              >
                <Box
                  sx={{
                    display: 'block',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '30px 30px 10px 30px',
                    fontFamily: theme.palette.fontFamily.main,
                  }}
                >
                  <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box >
                      <Typography variant="h6" fontWeight="bold" fontSize='18px' fontFamily={theme.palette.fontFamily.main}>
                        Personal Information
                      </Typography>
                    </Box>
                    {renderButton(editable, setEditable, handleUserProfileUpdate, handlePCancelButtonForProfileUpdate)}
                  </Box>
                  <Box
                    sx={{
                      padding: '30px 0 0 0',
                      fontFamily: theme.palette.fontFamily.main,
                    }}
                    display="flex"
                    alignItems="center"
                  >
                    <Badge
                      overlap="circular"
                      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                      badgeContent={
                        <ProfilePic>
                          <img src={userProfileIcon} alt="profile pic" />
                        </ProfilePic>
                      }
                    >
                      <label htmlFor="upload-photo">
                        <Avatar alt="Remy Sharp" sx={{ width: 100, height: 100, cursor: editable ? 'pointer' : undefined }}
                          src={previewUrl || `${BASE_URL_FOR_USER_AVATAR}${BASE_URL_FOR_PUBLIC_ASSET}${userInfo?.profilePicturePath}`}
                        >
                          {(!previewUrl && userInfo?.firstName && userInfo?.firstName[0])} {(!previewUrl && userInfo?.lastName && userInfo?.lastName[0])}
                        </Avatar>
                        <input
                          id="upload-photo"
                          type="file"
                          accept="image/*"
                          style={{ display: 'none' }}

                          onChange={editable ? handleImageChange : undefined}
                          disabled={!editable}
                        />
                      </label>
                    </Badge>
                    <Box>
                      <Typography sx={{ margin: 2 }} fontSize='18px' variant="h6" fontWeight="bold" fontFamily={theme.palette.fontFamily.main}>
                        {`${userInfo?.firstName} ${userInfo?.lastName}`}
                      </Typography>
                    </Box>
                  </Box>
                  {errorMessageForAvatar && (
                    <Box sx={{ padding: '10px 0 0 10px' }}>
                      <Typography variant="caption" color={theme.palette.primary.main} sx={{ fontSize: '16px' }} fontFamily={theme.palette.fontFamily.main}>
                        {errorMessageForAvatar}
                      </Typography>
                    </Box>
                  )}
                </Box>
                <Box
                  sx={{
                    display: 'block',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '30px',
                    fontFamily: theme.palette.fontFamily.main,
                    maxWidth: '60%'
                  }}
                >
                  <form >
                    {getUserName()}
                    {getRestOfUserDetails()}
                  </form>

                </Box>
              </Grid>

              {/* Change Password Section */}
              <Grid
                item
                xs={12}
                sx={{
                  margin: '20px',
                  border: '1px solid #e0e0e0',
                  borderRadius: '10px'
                }}
              >
                <Box
                  sx={{
                    display: 'block',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '30px 30px 10px 30px',
                    fontFamily: theme.palette.fontFamily.main,
                  }}
                >
                  <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box >
                      <Typography variant="h6" fontWeight="bold" fontFamily={theme.palette.fontFamily.main} >
                        Change Password
                      </Typography>
                    </Box>
                    {renderButton(changePassword, setChangePassword, handleNewPasswordChange, handlePCancelButtonForPasswordUpdate)}
                  </Box>
                </Box>
                <Box
                  sx={{
                    display: 'block',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    padding: '0px 30px',
                    fontFamily: theme.palette.fontFamily.main,
                    maxWidth: '60%'
                  }}
                >
                  {getUserEmailForVerification()}
                  <form >
                    {getPasswordFields()}
                  </form>
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      </Grid>
      <Footer />
    </>
  );
};

export default ProfilePage;

const ProfilePic = styled.div`
  border-radius: 50%; /* Makes it circular */
  background-color: ${({ theme }) => theme.palette.secondary.main};
  width: 30px;
  height: 30px;
  display: flex; /* Enables flexbox */
  justify-content: center; /* Horizontally centers the image */
  align-items: center; /* Vertically centers the image */
  overflow: hidden; /* Ensures the image fits inside the circle */
  
  img {
    width: 50%; /* Ensures the image covers the entire div */
    height: 50%; /* Keeps the image proportional */
    object-fit: cover; /* Ensures the image covers the whole circle without distortion */
  }
`;

const Gap = styled.div`
  padding: 10px;
`
