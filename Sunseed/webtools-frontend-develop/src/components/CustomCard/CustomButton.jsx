import React from "react";
import { Button } from "@mui/material";
import { styled } from "styled-components";

const CustomButton = ({
  label,
  onClick,
  variant,
  testId,
  disabled,
  sx, // This will allow you to pass custom styles
}) => {
  return (
    <StyledButton
      type="submit"
      variant={variant}
      onClick={onClick}
      data-testid={testId}
      disabled={disabled}
      sx={sx}
    >
      {label}
    </StyledButton>
  );
};

export default CustomButton;

const StyledButton = styled(Button)`
  && {
    border-radius: 6px;
    height: 44px;
    /* background-color: ${({ theme }) => theme.palette.secondary.main}; */
    
  }
`;
