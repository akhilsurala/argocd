import React, { useState } from "react";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Box from "@mui/material/Box";
import PersonIcon from "@mui/icons-material/Person";
import PowerSettingsNewIcon from "@mui/icons-material/PowerSettingsNew";
import { styled } from "styled-components";
import logo from "../../assets/logo.svg";
import Award from "../../assets/award.svg";
import Book from "../../assets/book.svg";
import Home from "../../assets/home.svg";
import { Typography } from "@mui/material";
import { AppRoutesPath } from "../../utils/constant";
import { useNavigate } from "react-router-dom";

const SideMenu = () => {
  const [activeTab, setActiveTab] = useState("Home");
  const navigate = useNavigate();

  const handleItemClick = (tabName) => {
    setActiveTab(tabName);
  };

  const handleLogOut = () => {
    localStorage.removeItem("apiToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("emailId");
    localStorage.removeItem("firstName");
    localStorage.removeItem("lastName");
    navigate(AppRoutesPath.LOGIN);
  };

  const redirectToHomePage = () => {
    navigate(AppRoutesPath.APV_SIM);
    setActiveTab("Home");
  };

  return (
    <Container>
      <Drawer className="sideNavBar" variant="permanent" anchor="left">
        <Box
          sx={{ p: 2, textAlign: "center", cursor: "pointer" }}
          onClick={() => redirectToHomePage()}
        >
          <img sx={{ marginBottom: "50px" }} src={logo} alt="logo" />
        </Box>
        <List>
          <ListItemStyled
            onClick={() => handleItemClick("Home")}
            sx={{
              backgroundColor: activeTab === "Home" ? "#66AFA4" : "transparent",
              borderRadius: activeTab === "Home" ? "20px" : "",
            }}
          >
            <ListItemIcon sx={{ color: "#FFFFFF", mr: -4 }}>
              <img src={Home} alt="" />
            </ListItemIcon>
            <Typography sx={{ fontWeight: activeTab === "Home" ? 700 : 500 }}>
              Home
            </Typography>
          </ListItemStyled>
          <ListItemStyled
            onClick={() => handleItemClick("License Management")}
            sx={{
              backgroundColor:
                activeTab === "License Management" ? "#66AFA4" : "transparent",
              borderRadius: activeTab === "License Management" ? "20px" : "",
            }}
          >
            <ListItemIcon sx={{ color: "#FFFFFF", mr: -4 }}>
              <img src={Award} alt="" />
            </ListItemIcon>
            <Typography
              sx={{
                fontWeight: activeTab === "License Management" ? 700 : 500,
              }}
            >
              License Management
            </Typography>
          </ListItemStyled>
          <ListItemStyled
            onClick={() => handleItemClick("Learning Resources")}
            sx={{
              backgroundColor:
                activeTab === "Learning Resources" ? "#66AFA4" : "transparent",
              borderRadius: activeTab === "Learning Resources" ? "20px" : "",
            }}
          >
            <ListItemIcon sx={{ color: "#FFFFFF", mr: -4 }}>
              <img src={Book} alt="" />
            </ListItemIcon>
            <Typography
              sx={{
                fontWeight: activeTab === "Learning Resources" ? 700 : 500,
              }}
            >
              Learning Resources
            </Typography>
          </ListItemStyled>
        </List>
        <Box sx={{ flexGrow: 1 }} />
        <List>
          <ListItemStyled>
            <ListItemText
              primary="Account"
              sx={{ color: "rgba(255, 255, 255, 0.5)" }}
            />
          </ListItemStyled>
          <ListItemStyled
            onClick={() => handleItemClick("My Profile")}
            sx={{
              backgroundColor:
                activeTab === "My Profile" ? "#66AFA4" : "transparent",
              borderRadius: activeTab === "My Profile" ? "20px" : "",
            }}
          >
            <ListItemIcon sx={{ color: "#FFFFFF", mr: -3 }}>
              <PersonIcon />
            </ListItemIcon>
            <ListItemText
              primary="My Profile"
              sx={{
                fontWeight: activeTab === "Learning Resources" ? 700 : 500,
              }}
            />
          </ListItemStyled>
          <ListItemStyled>
            <ListItemIcon sx={{ color: "#FFFFFF", mr: -3 }}>
              <PowerSettingsNewIcon />
            </ListItemIcon>
            <ListItemText onClick={() => handleLogOut()} primary="Log Out" />
          </ListItemStyled>
        </List>
      </Drawer>
    </Container>
  );
};

export default SideMenu;

const Container = styled.div`
  color: #ffffff;

  .sideNavBar {
    width: 242px;
    flex-shrink: 0;
  }

  .MuiDrawer-paper {
    width: 242px;
    background: #53988e;
    color: #ffffff;
    padding: 10px;
    box-sizing: border-box; /* Use dash instead of camelCase */
    font-size: 14px;
    font-weight: 500;
  }
`;

const ListItemStyled = styled(ListItem)`
  cursor: pointer;
  && {
    margin: 22px 0px 0px;
  }
`;
