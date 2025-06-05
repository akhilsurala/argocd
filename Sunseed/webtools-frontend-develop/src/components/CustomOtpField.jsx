import React from "react";
import { MuiOtpInput } from "mui-one-time-password-input";
import { useTheme } from "styled-components";

const CustomOtpField = ({ errors, name, value, onChange, testId }) => {
  const theme = useTheme();
  const validateChar = (value, index) => {
    return !isNaN(value) && value.trim() !== "";
  };
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <div>
        <MuiOtpInput
          autoFocus
          length={6}
          value={value}
          onChange={onChange}
          onBlur={onChange}
          data-testid={testId}
          validateChar={validateChar}
          sx={{ width: "350px" }}
        />
        {errors[name] && (
          <div
            style={{
              color: "red",
              fontFamily: theme.palette.fontFamily.main,
              fontSize: "14px",
              marginTop: "6px",
            }}
          >
            OTP is required
          </div>
        )}
      </div>
    </div>
  );
};

export default CustomOtpField;
