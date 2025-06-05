import {
  IconButton,
  InputAdornment,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import React from "react";
import { styled, useTheme } from "styled-components";
import { CustomSvgIconForToolTip } from "../container/dashboard/CustomSvgIcon";
import CustomToolTip from "./CustomToolTip";

const CustomTextField = ({
  label,
  handleChange,
  placeHolder,
  errors,
  name,
  inputProps,
  type,
  value,
  onChange,
  testId,
  disabled,
  suffix,
  toolTipMessage,
  textFieldType,
  showToolTip,
  maxLength,
}) => {
  const theme = useTheme();

  const handleValChange = (e) => {
    const { value } = e.target;
    const regex = /^-?\d*(\.\d{0,2})?$/;

    if (textFieldType === "numeric") {
      if (value?.length > maxLength) return;
      if (value === "" || regex.test(value)) {
        handleChange?.(e);
        onChange?.(e);
      }
    } else {
      if (value?.length > maxLength) return;
      handleChange?.(e);
      onChange?.(e);
    }
  };

  const getInputProps = (label) => {
    return {
      endAdornment: (
        <InputAdornment position="end">
          <Typography
            sx={{
              fontFamily: "Montserrat",
              fontSize: "14px",
              fontStyle: "italic",
              fontWeight: "500",
              color: "#53988E80",
            }}
          >
            {label}
          </Typography>
        </InputAdornment>
      ),
    };
  };



  return (
    <Container>
      <div
        className={`${label} ? label : emptyLabel`}
        style={{ display: "flex", alignItems: "center" }}
      >
        <span>{label} </span>
        {showToolTip && <CustomToolTip toolTipMessage={toolTipMessage} />}
      </div>
      <TextField
        error={!!errors[name]}
        helperText={errors[name]?.message}
        style={{
          width: '100%'
        }}
        sx={{
          width: "-webkit-fill-available",
          "& :focus": {
            outline: "0",
          },
          backgroundColor: disabled && theme.palette.background.faded,
        }}
        disabled={disabled}
        variant="outlined"
        placeholder={placeHolder}
        autoComplete="off"
        value={value}
        onChange={handleValChange}
        onBlur={onChange}
        size="small"
        InputProps={
          suffix ? { inputProps, ...getInputProps(suffix) } : inputProps
        }
        type={type}
        data-testid={testId}
      />
    </Container>
  );
};

export default CustomTextField;

const Container = styled.div`
  width: 100%;
  .label {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
    color: #474f50;
  }
  .emptyLabel {
    height: 26px;
  }
`;
