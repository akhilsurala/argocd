import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Stack from "@mui/material/Stack";
import NotificationsIcon from "@mui/icons-material/Notifications";
import { styled, useTheme } from "styled-components";
import { Badge, Popover, Card, CardContent, Typography, Avatar, Button, Divider, IconButton, ListItemIcon, Menu, MenuItem } from "@mui/material";
import { Logout, Settings } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";
import { clearLocalStore, getLocalStorageData } from "../../utils/localStorage";
import HomeIcon from "@mui/icons-material/Home";
import { formatDistanceToNow } from 'date-fns';

import logo from "../../assets/logo.svg";
import { getNotificationList, markAllNotifications } from "../../api/userProfile";
import { BASE_URL_FOR_PUBLIC_ASSET, BASE_URL_FOR_USER_AVATAR } from "../../api/config";
import { CustomSvgLOGO } from "../dashboard/CustomSvgIcon";
import { ErrorIcon } from "../../admin/icons/Error";
import { SuccessIcon } from "../../admin/icons/Success";
import { updateNotificationCount } from "../../redux/action/homeAction";
import { deleteAllDatabases } from "../../utils/indexDb/indexDbSetup";
import { disconnectWebSocket } from "../../utils/websocket";

const StyledBadge = styled(Badge)(({ theme }) => ({
  '& .MuiBadge-badge': {
    right: 1,
    top: 5,
    border: `2px solid ${theme.palette.background.main}`,
    backgroundColor: theme.palette.secondary.main,
    padding: '0 4px',
  },
}));

const StyledBadgeForHeading = styled(Badge)(({ theme }) => ({
  '& .MuiBadge-badge': {
    right: -10,
    top: -1,
    font: 6,
    border: `2px solid ${theme.palette.background.main}`,
    backgroundColor: '#AD2B215C',
    padding: '0 4px',
  },
}));

const NotificationItem = ({ type, message, time, theme }) => (
  <Card
    sx={{
      px: 0,
      ':hover': {
        backgroundColor: '#AD2B2B14'
      },
    }}
  >
    <CardContent sx={{ display: 'flex' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flex: 1 }}>
        {type === 'error' ? (
          <ErrorIcon sx={{ width: '24px', height: '24px', flexShrink: 0 }} />
        ) : (
          <SuccessIcon sx={{ width: '24px', height: '24px', flexShrink: 0 }} />
        )}
        <Typography
          variant="body2"
          sx={{
            fontFamily: theme.palette.fontFamily.main,
            fontWeight: 400,
            fontSize: '14px',
            whiteSpace: 'normal', // Allows wrapping
            overflowWrap: 'break-word', // Wraps long words if necessary
            wordBreak: 'break-word', // Ensures wrapping for very long strings
            flex: 1, // Makes text take up remaining space
          }}
        >
          {message}
        </Typography>
      </div>
    </CardContent>
    <Typography variant="caption" sx={{ px: 2, display: 'block', textAlign: 'right', color: 'gray' }}>{time}</Typography>
  </Card>
);

// console.log("isNotificationOpen", isNotificationOpen)
const NotificationPopover = ({ isNotificationOpen, notificationAnchorEl, handleNotificationClose, notificationCount, markAllNotificationsAsRead, notificationList }) => {
  const theme = useTheme()

  return (
    <Popover
      open={isNotificationOpen}
      anchorEl={notificationAnchorEl}
      onClose={handleNotificationClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: "right"
      }}
      transformOrigin={{
        vertical: 'top',
        horizontal: "right"
      }}
      PaperProps={{
        sx: {
          borderRadius: '16px',
          width: '421px', // Adjust width
          p: 0,
          maxHeight: '300px',
          position: 'absolute', // Force absolute positioning
          right: 0, // Align to the right
          top: '65px', // Adjust to match toolbar height if needed
          zIndex: 1300, // Ensure it appears above other elements
        },
      }}
    >
      <Stack
        direction="row"
        justifyContent="space-between"
        alignItems="center"
        sx={{ px: 0 }}
      >
        <Typography variant="h6" sx={{ px: 2, py: 2, fontFamily: theme.palette.fontFamily.main, fontWeight: 500, fontSize: 16 }}>
          Notifications <StyledBadgeForHeading badgeContent={notificationCount} color="secondary" />
        </Typography>
        <Typography
          variant="h6"
          sx={{ px: 2, py: 0.5, color: theme.palette.primary.main, fontFamily: theme.palette.fontFamily.main, fontWeight: 500, fontSize: 15, textAlign: 'right', cursor: 'pointer' }}
          onClick={markAllNotificationsAsRead}
        >
          {(notificationCount > 0) ? "mark all as read" : ''}
        </Typography>
      </Stack>
      {notificationList.length > 0 && notificationList.map((notification, index) => (
        <NotificationItem
          key={index}
          type={notification?.isSuccess ? "success" : "error"}
          message={notification.message}
          time={formatDistanceToNow(new Date(notification.createdAt), { addSuffix: true })}
          theme={theme}
        />
      ))}
      {/* <NotificationItem
          type="error"
          message="Lorem ipsum dolor sit amet consectetur sum dolor sit amet consectetu sum dolor sit amet consectetu."
          time="4 hrs ago"
          project="Project 3"
        />
        <NotificationItem
          type="success"
          message="Lorem ipsum dolor sit amet consectetur."
          time="6 hrs ago"
          project="Project 3"
        /> */}
    </Popover>
  );
};
const Header = ({ drawerWidth, drawerOpen }) => {
  const theme = useTheme();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const currentProjectName = localStorage.getItem('currentProjectName')
  const roles = localStorage.getItem("roles") || [];

  const [projectName, setProjectName] = useState('');
  const [notificationList, setNotificationList] = useState([]);
  const [anchorEl, setAnchorEl] = useState(null);
  const [notificationAnchorEl, setNotificationAnchorEl] = useState(null);

  const notificationCount = useSelector((state) => state.homeDashboard.notificationcount);
  const updateUserProfileInfo = useSelector((state) => state.homeDashboard.userProfileUpdated);

  const handleNotificationClick = (event) => {
    callNotificationListApi();

    setNotificationAnchorEl(event.currentTarget);
  };
  // console.log("notificationAnchorEl")

  const handleNotificationClose = () => {
    setNotificationAnchorEl(null);
  };

  const isNotificationOpen = Boolean(notificationAnchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleLogOut = () => {
    clearLocalStore();
    deleteAllDatabases();
    dispatch(updateNotificationCount('0'));
    navigate(AppRoutesPath.LOGIN);
    disconnectWebSocket();
  };

  const handleClose = (type) => {
    setAnchorEl(null);
    if (type === "logout") handleLogOut();
  };

  const handleProfilePage = () => {
    roles.includes("admin") ? navigate(AppRoutesPath.ADMIN_PROFILE) : navigate(AppRoutesPath.USER_PROFILE);
  }

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;

  const firstName = getLocalStorageData("firstName");
  const lastName = getLocalStorageData("lastName");
  const profilePicturePath = getLocalStorageData("profilePicturePath");

  useEffect(() => {
    const urlSplit = window.location.pathname.split("/");

    if (urlSplit.includes('project')) {
      setProjectName(" / " + currentProjectName);
    }
    else {
      setProjectName("")
    }
  }, [currentProjectName, window.location.pathname]);

  const callNotificationListApi = () => {
    getNotificationList(localStorage.getItem("userId"))
      .then((response) => {
        setNotificationList(response.data.data);
      })
      .catch((error) => {
        console.log(error);
      });
  }

  useEffect(() => {
    // if(notificationCount > 0) {
    callNotificationListApi();
    // }
  }, [updateUserProfileInfo]);

  const markAllNotificationsAsRead = () => {
    if (notificationCount > 0) {
      markAllNotifications({ notificationIds: [] })
        .then((response) => {
          dispatch(updateNotificationCount(0));
          setNotificationAnchorEl(null);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }



  const MenuPopper = () => {
    return (
      <Menu
        anchorEl={anchorEl}
        id="account-menu"
        open={open}
        onClose={() => handleClose()}
        onClick={() => handleClose()}
        PaperProps={{
          elevation: 0,
          sx: {
            overflow: "visible",
            filter: "drop-shadow(0px 2px 8px rgba(0,0,0,0.32))",
            mt: 1.5,
            minWidth: 180,
            "& .MuiAvatar-root": {
              width: 32,
              height: 32,
              ml: -0.5,
              mr: 1,
            },
            "&::before": {
              content: '""',
              display: "block",
              position: "absolute",
              top: 0,
              right: 14,
              width: 10,
              height: 10,
              bgcolor: "background.paper",
              transform: "translateY(-50%) rotate(45deg)",
              zIndex: 0,
            },
          },
        }}
        transformOrigin={{ horizontal: "right", vertical: "top" }}
        anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
      >
        <MenuItem onClick={() => handleProfilePage("profile")}>
          <Avatar
            style={{ width: "40px", height: "40px" }}
            src={profilePicturePath && `${BASE_URL_FOR_USER_AVATAR}${BASE_URL_FOR_PUBLIC_ASSET}${profilePicturePath}`}
          >
            {(profilePicturePath ? `${firstName && firstName[0]}${lastName && lastName[0]}` : undefined)}
          </Avatar>
          <Profile>
            <div className="userName">{firstName} </div>
            <div className="viewProfile">View Profile</div>
          </Profile>
        </MenuItem>
        <Divider />
        {/* <MenuItem onClick={() => handleClose("settings")}>
          <ListItemIcon>
            <Settings fontSize="small" />
          </ListItemIcon>
          Settings
        </MenuItem> */}
        <MenuItem onClick={() => handleClose("logout")}>
          <ListItemIcon>
            <Logout fontSize="small" />
          </ListItemIcon>
          Logout
        </MenuItem>
      </Menu>
    );
  };

  const urlSplit = window.location.pathname;
  const showAPVTitle = urlSplit.includes('apv-sim');



  return (
    <StyledAppBar style={{ width: `calc(100% - ${drawerWidth}px)` }}>
      <Toolbar>
        <div style={{ flexGrow: 1, display: 'flex' }}>

          {!drawerOpen && <div > <CustomSvgLOGO style={{ height: "30px", width: "168px", color: theme.palette.primary.main }} /> </div>}
          {/* <img sx={{ marginBottom: "50px" }} src={logo} alt="logo" /> */}
          {urlSplit !== '/' &&
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <div style={{ border: '0.5px solid #BEC1C6', height: '25px', opacity: '0.5', marginLeft: '20px', }}></div>
              <IconButton aria-label="Home" onClick={() => navigate(AppRoutesPath.DEFAULT)} style={{ backgroundColor: 'transparent' }} sx={{ outline: 'none !important' }}>
                <HomeIcon
                  style={{
                    color: theme.palette.primary.main, height: "19.5px"
                  }}
                />
                <div style={{

                  fontFamily: 'Montserrat',
                  fontSize: '16px',
                  color: theme.palette.primary.main
                }}>Home</div>
              </IconButton>
            </div>
          }
        </div>

        <Stack direction="row" spacing={2} alignItems="center">
          {showAPVTitle && <><Button

            onClick={() => navigate(AppRoutesPath.APV_SIM)} style={{ backgroundColor: 'transparent', color: theme.palette.primary.main, height: "19.5px" }} sx={{
              //styleName: Body1;
              fontFamily: 'Montserrat',
              fontSize: '16px',
              fontWeight: '500',
              margin: '0px',
              color: '#525B5C99',
              outline: 'none !important',


            }}>APV SIM</Button>
            <div style={{
              fontFamily: 'Montserrat',
              fontSize: '16px',
              fontWeight: '500',
              color: '#525B5C99',
              marginLeft: "0px"
            }}>{projectName}</div>
          </>
          }
          <div style={{ cursor: 'pointer' }} onClick={(event) => handleNotificationClick(event)}>
            <StyledBadge badgeContent={notificationCount} max={99} color="secondary" >
              <NotificationsIcon
                sx={{ color: theme.palette.primary.main }}
                data-testid="notifications-icon"
              />
            </StyledBadge>
          </div>

          <Username>
            {firstName} {lastName}
          </Username>
          <Avatar
            alt="Profile icon"
            src={profilePicturePath && `${BASE_URL_FOR_USER_AVATAR}${BASE_URL_FOR_PUBLIC_ASSET}${profilePicturePath}`}
            onClick={(event) => handleClick(event)}
            sx={{ width: "30px", height: "30px", cursor: "pointer" }}
          >
            {(profilePicturePath ? `${firstName && firstName[0]}${lastName && lastName[0]}` : undefined)}
          </Avatar>
          <MenuPopper />
          <NotificationPopover isNotificationOpen={isNotificationOpen}
            notificationAnchorEl={notificationAnchorEl}
            handleNotificationClose={handleNotificationClose}
            notificationCount={notificationCount}
            markAllNotificationsAsRead={markAllNotificationsAsRead}
            notificationList={notificationList}
          />
        </Stack>
      </Toolbar>
    </StyledAppBar >
  );
};

export default Header;

const StyledAppBar = styled(AppBar)`
  && {
    position: fixed;
    height: 65px;
    background: #ffffff;
    box-shadow: none;
    border-bottom: ${({ theme }) => `1px solid ${theme.palette.border.light}`};
  }
`;

const Profile = styled.div`
  .viewProfile {
    font-size: 12px;
  }
`;

const Username = styled.div` 
  color: ${({ theme }) => `${theme.palette.text.main}`};
  font-weight: 500;
`
