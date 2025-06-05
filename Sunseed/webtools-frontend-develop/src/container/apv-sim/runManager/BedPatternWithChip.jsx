import { Box, Chip } from "@mui/material";
import React from "react";
import styled, { useTheme } from "styled-components";
import CloseIcon from "@mui/icons-material/Close";

const BedPatternWithChip = ({ data, removeCross = false }) => {
  const theme = useTheme();
  const CustomChip = ({ label }) => {
    return (
      <Chip
        label={label}

        variant="outlined"
        onClick={() => { }}
        {...(!removeCross && {
          onDelete: () => { },
          deleteIcon: <CloseIcon sx={{ fill: theme.palette.secondary.main }} />,
        })}
        sx={{
          backgroundColor: "#53988E1F",
          borderRadius: "8px",
          border: "1px solid",
          borderColor: theme.palette.secondary.main,
        }}
      />
    );
  };
  return (
    <Container>
      <Box className="title">Inter Bed Pattern</Box>
      <Box className="chipsetWrapper">
        {data?.map((data, index) => {
          return <CustomChip label={data} key={index} />;
        })}
      </Box>
    </Container>
  );
};

export default BedPatternWithChip;

const Container = styled.div`
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    text-align: left;
  }
  .chipsetWrapper {
    padding: 10px;
    border: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.light};
    border-radius: 8px;
    margin: 20px 0px;
    gap: 8px;
    display: flex;
    flex-wrap: wrap;
  }
`;
