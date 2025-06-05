import { Box, Grid } from "@mui/material";
import React, { useEffect, useState } from "react";
import { styled } from "styled-components";
import logo from "../../assets/logo.svg";
import loginBg from "../../assets/loginBg.png";
import { AppRoutesPath } from "../../utils/constant";
import { Outlet, useNavigate } from "react-router-dom";

const WrapperForLoginFlow = () => {
  const navigate = useNavigate();
  const [isAuthChecked, setIsAuthChecked] = useState(false);
  const apiToken = localStorage.getItem("apiToken");

  useEffect(() => {
    if (apiToken) {
      navigate(AppRoutesPath.DEFAULT, { replace: true });
    } else {
      setIsAuthChecked(true); // Only allow rendering if the user is NOT logged in
    }
  }, [apiToken, navigate]);

  if (!isAuthChecked) {
    return null; // Prevents rendering login screen until the auth check is done
  }

  return (
    <Container>
      <Grid container spacing={2}>
        <Grid item md={6} className="leftSection">
          <div style={{ margin: "20px" }}>
            <div className="bgImage" />
            <img
              src={logo}
              alt="logo"
              className="logo"
              onClick={() => navigate(AppRoutesPath.DEFAULT)}
            />
          </div>
          <Box className="agriInfoTextWrapper">
            <Box className="agriInfoText">
              AGRI-PHOTOVOLTAICS DECISION <br /> SUPPORT PLATFORM
            </Box>
            <Box className="subInfo">
              The most cutting edge scientifically backed suite to design,
              manage <br /> and run your APV farms.
            </Box>
          </Box>
        </Grid>
        <Grid item md={6} className="rightSection">
          <Outlet />
        </Grid>
      </Grid>
    </Container>
  );
};

export default WrapperForLoginFlow;

const Container = styled.div`
  height: 100%;
  width: 100%;
  max-height: 100%;
  overflow: hidden;
  .leftSection {
    overflow: hidden;
    height: 100%;
  }
  .bgImage {
    height: 96vh;
    max-width: 752px;
    object-fit: hidden;
    border-radius: 20px;
    background: linear-gradient(
        5deg,
        rgba(29, 31, 31, 0.7) 4.48%,
        rgba(141, 151, 149, 0) 103.14%
      ),
      url(${loginBg});
    position: relative;
  }
  .rightSection {
    background-color: ${({ theme }) => theme.palette.background.main};
    position: relative;
  }
  .logo {
    position: absolute;
    width: 180px;
    left: 2%;
    top: 0;
    cursor: pointer;
  }
  .agriInfoTextWrapper {
    position: absolute;
    bottom: 10%;
    left: 40px;
    max-width: 50%;
    display: grid;
    gap: 12px;
  }
  .agriInfoText {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 32px;
    font-weight: 700;
    line-height: 44px;
    text-align: left;
    color: #ffffff;
  }
  .subInfo {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    text-align: left;
    color: #ffffff;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
  }
`;
