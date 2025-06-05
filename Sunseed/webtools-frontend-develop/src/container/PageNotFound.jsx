import React from "react";
import FourOFourNotFound from "../assets/FourOFourNotFound.png";
import logo from "../assets/sunseedLogo.svg";
import { Box } from "@mui/material";
import styled from "styled-components";
import CustomButton from "../components/CustomButton";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../utils/constant";

export default function PageNotFound() {
  const navigate = useNavigate();
  const redirectToHomePage = () => {
    navigate(AppRoutesPath.DEFAULT);
  };
  return (
    <Container>
      <img
        src={logo}
        style={{
          height: "40px",
          width: '200px'
        }}

        alt="logo"
        className="gizLogo"
        onClick={() => redirectToHomePage()}
      />
      <Box className="wrapper">
        <img src={FourOFourNotFound} alt="404 Not Found" />
        <Box>We tried but we couldn’t find the page you are looking for.</Box>
        <CustomButton label="Home" onClick={() => redirectToHomePage()} />
      </Box>
    </Container>
  );
}

const Container = styled.div`
  .gizLogo {
    margin: 20px;
    cursor: pointer;
  }
  .wrapper {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 40px;
  }
`;
