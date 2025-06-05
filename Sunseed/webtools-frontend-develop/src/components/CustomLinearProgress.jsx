import { LinearProgress } from "@mui/material";
import React from "react";
import { useTheme } from "styled-components";

const CustomLinearProgress = () => {
    const theme = useTheme();
  return (
    <div>
      <LinearProgress
        sx={{
          backgroundColor: theme.palette.background.main,
          margin: "4px",
          height: "3px",
          "& .MuiLinearProgress-barColorPrimary": {
            backgroundColor: theme.palette.secondary.main,
          },
        }}
      />
    </div>
  );
};

export default CustomLinearProgress;
