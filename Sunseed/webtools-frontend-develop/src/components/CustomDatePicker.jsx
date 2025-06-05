import React from "react";

import dayjs from "dayjs";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import styled, { useTheme } from "styled-components";

const Error = ({ children }) => <p style={{ color: "red", margin: '0px' }}>{children}</p>;
const CustomDatePicker = ({
  inputProps,
  label,
  value,
  onChange,
  testId,
  disabled,
  errors,
  name,
  pastYearRange = 4,
  futureYearRange = 1,
  firstSelectedDate = null,
  previouslySelectedDate,   // New prop for previously selected date
  daysCount,
  page = null,              // New prop for days count
}) => {
  const theme = useTheme();
  const currentDate = new Date();
  const pastDate = dayjs().subtract(pastYearRange, 'year').startOf('year').toDate();


  const futureDate = new Date();
  futureDate.setFullYear(currentDate.getFullYear() + futureYearRange);

  const disableStartDate = previouslySelectedDate ? dayjs(previouslySelectedDate) : null;
  const disableEndDate = disableStartDate && daysCount ? disableStartDate.add(daysCount, 'day') : null;

  const handleDateChange = (newValue) => {
    if (!dayjs(newValue).isValid()) {
      onChange(null);
      return;
    }
    if (!dayjs(newValue).isBetween(dayjs(pastDate).subtract(1, 'day'), dayjs(futureDate), null, '[]')) {
      onChange(null);
      return;
    }

    onChange(newValue);
  };

  const shouldDisableDate = (date) => {
    const currentDayjs = dayjs();
    const disableAfterDate = firstSelectedDate ? dayjs(firstSelectedDate).add(365, 'day') : null;

    // 1. Disable dates before or equal to today (minDate handles this)
    // if (date.isBefore(currentDayjs, 'day')) {
    //   return true;
    // }

    // 2. Disable dates between current date and previouslySelectedDate
    if (disableStartDate && date.isBefore(disableStartDate, 'day')) {
      return true;
    }

    // 3. Disable dates from previouslySelectedDate to previouslySelectedDate + daysCount
    if (disableStartDate && disableEndDate && date.isBetween(disableStartDate, disableEndDate, null, '[)')) {
      return true;
    }

    // 4. Disable dates after firstSelectedDate + 365 days
    if (disableAfterDate && date.isAfter(disableAfterDate, 'day')) {
      return true;
    }


    return false;
  };


  // console.log("value", value.format("DD/MM/YYYY"))
  return (
    <Container>
      <div className={`${label} ? label : emptyLabel`}>{label}</div>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DemoContainer components={["DatePicker"]}>
          <DatePicker

            format={value ? value?.format("YYYY/MM/DD") : null}
            value={value ? value : null}
            onChange={handleDateChange}
            onBlur={handleDateChange}
            minDate={dayjs(pastDate)}
            shouldDisableDate={shouldDisableDate}  // Disable logic here

            maxDate={dayjs(futureDate)}

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
              },
            }} // This is used to set the position of the date picker
            sx={{
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

        {errors['startDate'] && <Error>{errors['startDate'].message}</Error>}
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
