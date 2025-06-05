import { Box } from "@mui/material";
import React, { useState } from "react";
import styled from "styled-components";
import success from "../../assets/success.svg";
import CustomButton from "../../components/CustomButton";

const CustomSuccessPage = ({title,text,buttonLabel}) => {
  const handleClick = () => {};

  return (
    <Container>
      <Box className="title">{title}</Box>
      <Box className="contentWrapper">
        <img src={success} alt="sucessfully created" className="success" />
        <Box className="text" mt={6}>
          {text}
        </Box>
        <CustomButton label={buttonLabel} onClick={() => handleClick()} />
      </Box>
    </Container>
  );
};

export default CustomSuccessPage;

const Container = styled.div`
  background-color: #ffffff;
  max-width: max(40%, 500px);
  padding: 26px 20px;
  border-radius: 16px;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;

  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 18px;
    font-weight: 700;
    line-height: 21.94px;
    text-align: left;
  }
  .contentWrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex: 1;
    gap: 30px;
  }
  .text {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    text-align: center;
  }
`;
