import { Box } from "@mui/material";
import React from "react";
import { Outlet } from "react-router-dom";
import styled from "styled-components";

const MyApps = () => {
  return (
    <Main>
      <Box style={{ paddingLeft: "6rem", width: "inherit" }}>
        <h3 className="subHeading">Welcome to</h3>
        <h1 className="heading" style={{ fontSize: "3rem" }}>
          AGRI-PHOTOVOLTAICS
          <br /> DECISION SUPPORT PLATFORM
        </h1>
        <Outlet />
      </Box>
    </Main>
  );
};

export default MyApps;

const Main = styled.div`
    margin-top: 150px;
  .subHeading {
    font-family: Montserrat;
    font-size: 24px;
    font-weight: 600;
    line-height: 29.26px;
    letter-spacing: 0.04em;
    text-align: left;
    text-decoration-skip-ink: none;
    color: #474f50;
    text-underline-position: from-font;
  }
  .heading {
    font-family: Montserrat;
    font-size: 49px;
    font-weight: 700;
    line-height: 58.31px;
    text-align: left;
    text-underline-position: from-font;
    text-decoration-skip-ink: none;
    color: #474f50;
  }
`;
