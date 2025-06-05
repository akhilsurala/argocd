import React from "react";
import { useTheme } from "styled-components";

const CustomDashedLine = (testId) => {
  const theme = useTheme();
  return (
    <div
    data-testid={testId}
      style={{
        width: "100%",
        height: "1px",
        background: 
         `radial-gradient(4px 8px, ${theme.palette.border.light} 80%,transparent 100%) 0 0/20px 100%`
      }}
    ></div>
  );
};

export default CustomDashedLine;
