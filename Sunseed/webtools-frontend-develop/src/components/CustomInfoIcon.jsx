import React from "react";
import { useTheme } from "styled-components";
import infoIcon from "../assets/infoIcon.svg";
import { Tooltip } from "@mui/material";

const CustomInfoIcon = ({ toolTipMessage }) => {
  const theme = useTheme();
  return (
    <Tooltip
      title={<div dangerouslySetInnerHTML={{ __html: toolTipMessage }} />}
      placement="right-end"

      style={{ marginLeft: "10px",cursor:'pointer',zIndex:100 }}
      componentsProps={{
        tooltip: {
          sx: {
            maxWidth: "400px",
            borderRadius: "8px",
            padding: "16px",
            gap: "10px",
            backgroundColor: "#FBF4F4",
            border: "1px solid",
            borderColor: theme.palette.primary.main,
            fontFamily: theme.palette.fontFamily.main,
            color: theme.palette.text.main,
            fontSize: "12px",
            lineHeight: "24px",
            fontWeight: 500,
          },
        },
      }}
    >
      <img src={infoIcon} alt="info" />
    </Tooltip>
  );
};

export default CustomInfoIcon;
