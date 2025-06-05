import {
  FormControl,
  FormControlLabel,
  FormLabel,
  Radio,
  RadioGroup,
} from "@mui/material";
import React from "react";
import { styled, useTheme } from "styled-components";

const CustomRadioButtons = ({
  label,
  placeHolder,
  onChange,
  value,
  handleChange,
  dataSet,
  testId,
}) => {
  const theme = useTheme();
  const handleRadioButtonChange = (e) => {
    onChange(e);
    handleChange(e);
  };
  return (
    <Container>
      <FormControl>
        <div className={`${label} ? label : emptyLabel`}>{label}</div>
        <RadioGroup
          row
          aria-labelledby="demo-row-radio-buttons-group-label"
          name="row-radio-buttons-group"
          value={value}
          onChange={handleRadioButtonChange}
          data-testid={testId}
        >
          {dataSet?.map((data) => {
            return (
              <FormControlLabel
                key={data}
                value={data}
                control={
                  <Radio
                    sx={{
                      color: theme.palette.secondary.main,
                      marginRight: '-4px',
                      "&.Mui-checked": {
                        color: theme.palette.secondary.main,
                      },
                    }}
                  />
                }
                label={data}
                sx={{
                  "& .MuiTypography-root": {
                    color: theme.palette.text.main,
                    fontFamily: theme.palette.fontFamily.main,
                    size: "14px",
                    fontWeight: 500,
                    lineHeight: "26px",
                    marginRight: '20px'
                  },
                }}
              />
            );
          })}
        </RadioGroup>
      </FormControl>
    </Container>
  );
};

export default CustomRadioButtons;

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
    /* height: 26px; */
  }
`;
