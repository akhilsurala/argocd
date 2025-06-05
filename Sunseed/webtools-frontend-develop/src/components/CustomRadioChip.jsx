import React from "react";

import { Chip, Radio, FormControlLabel } from "@mui/material";
import { useTheme } from "styled-components";

const CustomRadioChip = ({
  label,
  value,
  onChange,
  disabled = false,
  optional = false,
}) => {
  const theme = useTheme();

  return (
    <Chip
      variant="outlined"
      label={
        optional ? (
          <span
            style={{
              fontSize: "1em",
              fontFamily: "Montserrat",
              fontWeight: 500,
              color: theme.palette.text.main,
            }}
          >
            {label}
          </span>
        ) : (
          <FormControlLabel
            control={
              <Radio
                checked={value}
                onChange={onChange}
                sx={{
                  color: theme.palette.primary.secondary,
                  "&.Mui-checked": {
                    color: disabled
                      ? "rgba(0, 0, 0, 0.38)"
                      : theme.palette.primary.secondary,
                  },
                }}
                disabled={disabled || optional}
              />
            }
            label={
              <span
                style={{
                  fontSize: ".85em",
                  fontFamily: "Montserrat",
                  fontWeight: 500,
                  color: theme.palette.text.main,
                }}
              >
                {label}
              </span>
            }
          />
        )
      }
      onClick={() => true}
      sx={{
        display: "flex",
        alignItems: "center",
        cursor: disabled ? "not-allowed" : "pointer",
        paddingLeft: optional ? "1em" : "0em !important",
        paddingRight: optional ? "1em" : "0",
        borderColor: disabled
          ? "rgba(0, 0, 0, 0.23)"
          : theme.palette.primary.secondary,
        "&.MuiChip-outlined": {
          borderColor: theme.palette.primary.secondary,
        },
        backgroundColor: "#e3edec",
        opacity: disabled ? 0.5 : 1,
      }}
    />
  );
};

export default CustomRadioChip;
