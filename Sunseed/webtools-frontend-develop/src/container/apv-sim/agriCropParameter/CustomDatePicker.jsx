import React from "react";

import dayjs from "dayjs";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import styled, { useTheme } from "styled-components";

const CustomDatePicker = ({
    inputProps,
    label,
    value,
    onChange,
    testId,
    disabled,
    errors,
    name,
}) => {
    const theme = useTheme();
    const currentDate = new Date();
    const pastDate = new Date();
    pastDate.setFullYear(currentDate.getFullYear() - 4);

    const futureDate = new Date();
    futureDate.setFullYear(currentDate.getFullYear() + 1);

    const handleDateChange = (newValue) => {
        console.log("newvalue", newValue)
        if (!dayjs(newValue).isValid()) {
            onChange(null);
            return;
        }
        if (!dayjs(newValue).isBetween(dayjs(pastDate), dayjs(futureDate))) {
            onChange(null);
            return;
        }
        onChange(newValue);
    };


    return (
        <Container>
            <div className={`${label} ? label : emptyLabel`}>{label}</div>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DemoContainer components={["DatePicker"]}>
                    <DatePicker
                        value={value ? value : null}
                        onChange={handleDateChange}
                        onBlur={handleDateChange}
                        minDate={dayjs(pastDate)}
                        maxDate={dayjs(futureDate)}
                        inputProps={inputProps}
                        disabled={disabled}
                        data-testid={testId}
                        slotProps={{
                            popper: { placement: "bottom-end" },
                            // textField: {
                            //     helperText: errors[name]?.message,
                            //     error: !!errors[name],
                            // },
                        }} // This is used to set the position of the date picker
                        style={{
                            width: '100%'
                        }}
                        sx={{
                            // width: "-webkit-fill-available",
                            width: "fill-available",

                            "& :focus": {
                                outline: "0",
                            },
                            "& .MuiInputBase-root": {
                                height: "42px",
                                overflow: "hidden",
                                color: theme.palette.text.secondary,
                                backgroundColor: disabled
                                    ? theme.palette.background.faded
                                    : theme.palette.background.secondary,
                            },
                            "& fieldset": {
                                borderColor: theme.palette.border.main,
                            },
                            "& .MuiButtonBase-root": {
                                color: theme.palette.border.main,
                            },
                        }}
                    />
                </DemoContainer>
            </LocalizationProvider>
        </Container>
    );
};

export default CustomDatePicker;

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
