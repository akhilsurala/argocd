import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { Button, IconButton } from "@mui/material";
import React from "react";
import { useTheme, styled } from "styled-components";

const CustomCard = ({ title, subtitle, handleClick, imgIcon }) => {
  const theme = useTheme();
  return (
    <Card>
      <Button className="card" onClick={handleClick} >
        <img src={imgIcon} alt="card" />
        <div className="card-body">
          <div className="card-title">{title}</div>

          <div className="card-text">{subtitle}</div>
        </div>


      </Button>
    </Card>
  );
};

export default CustomCard;

const Card = styled.div`
width: 100%;
  .card {
    place-content: start;
    width: 100%;
    min-height: 161px;
    background: ${({ theme }) => theme.palette.background.secondary};;
    border-radius: 18px;
    padding: 20px;
    text-transform: capitalize;
  }

  .card-body {
    display: flex;
    margin-left: 15px;
    flex-direction: column;
    border-radius: 3px;
    border-width: 1em;
    border-color: ${({ theme }) => theme.palette.text.main};
  }

  .card-title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 24px;
    font-weight: 600;
    color: ${({ theme }) => theme.palette.text.main};
    line-height: 30px;
    letter-spacing: 0em;
    text-align: left;
    margin: 6px 0px;
  }

  .card-text {
    
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 14px;
    font-weight: 500;
    color: ${({ theme }) => theme.palette.text.main};
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
  }

`;
