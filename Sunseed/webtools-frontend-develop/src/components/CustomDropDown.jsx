import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import React from "react";
import { styled, useTheme } from "styled-components";
import CustomToolTip from "./CustomToolTip";

const CustomDropDown = ({
  label,
  handleChange,
  placeHolder,
  errors,
  name,
  inputProps,
  type,
  value,
  onChange,
  dataSet,
  testId,
  disabled,
  showToolTip = false,
  toolTipMessage,
  noneNotRequire = false
}) => {
  const theme = useTheme();

  const handleDropDownChange = (e) => {
    handleChange?.(e.target.value);
    onChange?.(e);
  };

  return (
    <Container>
      <div className={`${label} ? label : emptyLabel`} style={{ display: 'flex', alignItems: "center" }} >
        <span>{label} </span>
        {showToolTip && <CustomToolTip toolTipMessage={toolTipMessage} />}
      </div>

      <FormControl fullWidth size="small">
        <InputLabel
          id="demo-simple-select-label"
          sx={{
            color: disabled && "rgba(0, 0, 0, 0.38)",
            opacity: disabled ? 0.5 : 0.7,
          }}
        >
          {placeHolder}
        </InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={value}
          placeholder="Age"
          onChange={(e) => handleDropDownChange(e)}
          onBlur={(e) => handleDropDownChange(e)}
          error={!!errors[name]}
          label={placeHolder}
          data-testid={testId}
          disabled={disabled}
          sx={{
            "& fieldset": {
              borderColor: theme.palette.border.main,
              backgroundColor: disabled && theme.palette.background.faded,
            },
            "& .MuiSvgIcon-root": {
              color: theme.palette.border.main,
            },
          }}
          MenuProps={{
            PaperProps: {
              sx: {
                maxHeight: 150,

                "&::-webkit-scrollbar": {
                  width: 6,
                  // backgroundColor: theme.palette.background.secondary,
                },
                "&::-webkit-scrollbar-track": {
                  boxShadow: `#D5D5D5`,
                },
                "&::-webkit-scrollbar-thumb": {
                  backgroundColor: theme.palette.primary.main,

                  borderRadius: "8px",
                },
              },
            },
          }}
        >
          {
            !noneNotRequire &&
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
          }
          {dataSet.map((name) => (
            <MenuItem
              key={name.id}
              value={name.id}
              sx={{
                color: theme.palette.text.main,
                fontSize: "14px",
                lineHeight: "26px",
                fontFamily: theme.palette.fontFamily.main,
                fontWeight: 500,
              }}
            >
              {name.name}
            </MenuItem>
          ))}
        </Select>
        <FormHelperText sx={{ color: "#d32f2f" }}>
          {errors[name]?.message}
        </FormHelperText>
      </FormControl>
    </Container>
  );
};

export default CustomDropDown;

const Container = styled.div`
  .label {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
    color: #474f50;
    margin-bottom: 6px;
  }
  .emptyLabel {
    height: 26px;
  }
`;
