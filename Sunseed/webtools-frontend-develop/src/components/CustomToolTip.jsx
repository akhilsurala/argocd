import React from "react";
import { useTheme } from "styled-components";
import { IconButton, InputAdornment, TextField, Tooltip, Typography } from "@mui/material";
import { CustomSvgIconForToolTip } from "../container/dashboard/CustomSvgIcon";

const CustomToolTip = ({ toolTipMessage }) => {
  const theme = useTheme();
  return (
    <Tooltip 
      title={<div dangerouslySetInnerHTML={{ __html: toolTipMessage }} />}
      placement="right-end"
      style={{ marginLeft: "10px",cursor:'pointer',zIndex:100 }}
      componentsProps={{
        tooltip: {
          sx: {
            color: "#53988E",
            backgroundColor: "#F2F7F6",
            borderRadius: '8px',
            border: '1px solid #53988E',
          },
        },
      }}>
      <IconButton sx={{
        padding: '0px', marginLeft: '5px',
        ':focus': {
          outline: 0,
        }
      }}>
        <CustomSvgIconForToolTip style={{ color: '#53988E' }} />
      </IconButton>
    </Tooltip>
  );
};

export default CustomToolTip;
