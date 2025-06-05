import React from "react";

import dayjs from "dayjs";
import { Controller } from "react-hook-form";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import styled, { useTheme } from "styled-components";

const Error = ({ children }) => <p style={{ color: "red", margin: '0px' }}>{children}</p>;
const CustomDatePicker = ({
  inputProps,
  label,
  control,
  rules,
  value,
  setError,
  clearErrors,
  onChange,
  testId,
  disabled,
  errors,
  name,
  pastYearRange = 0,
  futureYearRange = 0,
  firstSelectedDate = null,
  previouslySelectedDate,   // New prop for previously selected date
  daysCount,
  page = null,              // New prop for days count
}) => {
  const theme = useTheme();

  const currentDate = new Date();
  // Set pastDate to January 1st of pastYearRange years ago
  const pastDate = dayjs().subtract(pastYearRange, 'year').startOf('year').toDate();

  // Set futureDate to December 31st of futureYearRange years ahead
  const futureDate = dayjs().add(futureYearRange, 'year').endOf('year').toDate();

  // const disableStartDate = previouslySelectedDate ? dayjs(previouslySelectedDate) : null;
  // const disableEndDate = disableStartDate && daysCount ? disableStartDate.add(daysCount, 'day') : null;

  const handleDateChange = (newValue) => {
    if (!dayjs(newValue).isValid()) {
        setError(name, { type: "manual", message: "Invalid Date" });
      onChange(null);
      return;
    }
    if (!dayjs(newValue).isBetween(dayjs(pastDate).subtract(1, 'day'), dayjs(futureDate), null, '[]')) {
      setError(name, { type: "manual", message: "Date out of allowed range" });
      onChange(null);
      return;
    }
    clearErrors(name);
    onChange(newValue);
  };

  return (
    <section>
      <Controller
        name={name}
        control={control}
        rules={rules}
        disabled={disabled}
        defaultValue=""

        render={({ field }) => (
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DemoContainer components={["DatePicker"]}>
              <DatePicker
                value={value ? value : null}
                onChange={handleDateChange}
                onAccept={handleDateChange}
                onClose={() => {
                  if (!dayjs(value).isValid()) {
                    setError(name, { type: "manual", message: "Invalid Date" });
                  }
                }}
                minDate={dayjs(pastDate)}
                maxDate={dayjs(futureDate)}
                // shouldDisableDate={shouldDisableDate}  // Disable logic here

                inputProps={inputProps}
                disabled={disabled}
                data-testid={testId}
                // Set the default open month to January

                openTo={page ? "month" : null}
                views={page ? ['year', 'month', 'day'] : null} // Specify views to allow year and month selection

                defaultValue={page ? dayjs().startOf('year') : null}
                slotProps={{
                  popper: { placement: "bottom-end" },
                  textField: {
                    // helperText: errors[name]?.message,
                    error: !!errors[name],
                    placeholder:"Select Date",
                    inputProps:{
                      ...inputProps,
                      disabled:true,
                      readOnly: true,
                    }
                  },
                }} // This is used to set the position of the date picker
                sx={{
                  // width: "-webkit-fill-available",
                  width: "-moz-available",
                  width: "-webkit-fill-available",
                  width: "fill-available",

                  "& :focus": {
                    outline: "0",
                  },
                  "& .MuiInputBase-root": {
                    height: "42px",
                    overflow: "hidden",
                    color: theme.palette.text.main,
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
        )}
      />
      {errors[name] && <Error>{errors[name].message}</Error>}
    </section>
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
