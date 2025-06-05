import { Button } from "@mui/material";
import React from "react";
import styled, { useTheme } from "styled-components";

const CustomButton = ({ onClick, label }) => {
  const theme = useTheme();
  return (
    <Container>
      <Button
       disableElevation={true}
        variant="contained"
        className="contained"
        data-testid="button"
        onClick={(e) => {
          e.preventDefault();
          onClick();
        }}
        sx={{
          "&:hover": {
            background: theme.palette.secondary.main,
          },
        }}
      >
        {label}
      </Button>
    </Container>
  );
};

export default CustomButton;

const Container = styled.div`
  .contained {
    padding: 10px 24px;
    border-radius: 8px;
    background-color: ${({ theme }) => theme.palette.secondary.main};
    text-transform: capitalize;
    font-weight: 700;
    color: #ffffff;
    outline: none;
    font-size:12px;
    :hover {
      background-color: ${({ theme }) => theme.palette.secondary.main};
    }
  }
  .prevBtn {
    height: 44px;
    padding: 12px, 24px, 12px, 24px;
    border-radius: 8px;
    gap: 10px;
    color: #25272759;
    background-color: ${({ theme }) => theme.palette.background.secondary};
    text-transform: capitalize;
    border: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.main};
    font-weight: 700;
  }
`;
