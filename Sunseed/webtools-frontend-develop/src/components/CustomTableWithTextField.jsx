import * as React from "react";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import { TextField, styled } from "@mui/material";
import { useTheme } from "styled-components";

const columns = [
  { id: "hour", label: "Hours", minWidth: 170 },
  { id: "rate", label: "Rate/hour", minWidth: 100 },
];

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#ffffff",
    position: "sticky",
    top: 0,
    zIndex: 1000, // Ensure this is high enough
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  "&:nth-of-type(odd)": {
    backgroundColor: "#DFE4E53D",
  },
  // hide last border
  "&:last-child td, &:last-child th": {
    border: 0,
  },
}));

export default function CustomTableWithTextField({ disabled, data, setData }) {
  const theme = useTheme();

  function createData(hour, rate, id) {
    return { hour, rate, id };
  }

  const rows = Array.from({ length: 24 }, (_, i) => {
    const hour = `${String(i).padStart(2, "0")}:00 - ${String(i + 1).padStart(
      2,
      "0"
    )}:00`;
    return createData(hour, data[i], i);
  });

  const handleChange = (e, row) => {
    let inputValue = e.target.value;

    // If the input is empty, allow it temporarily
    if (inputValue === '') {
      const newData = [...data];
      newData[row.id] = inputValue; // Allow empty input temporarily
      setData(newData);
      return;
    }

    // Validate input as a number and ensure it stays within range and has only 2 decimals max
    const decimalPattern = /^\d+(\.\d{0,2})?$/; // Regular expression for max 2 decimal places
    if (!isNaN(inputValue) && decimalPattern.test(inputValue)) {
      const numericValue = parseFloat(inputValue);
      const maxValue = 1000000000;
      const minValue = 0;

      // Restrict value to min/max range
      if (numericValue >= minValue && numericValue <= maxValue) {
        const newData = [...data];
        newData[row.id] = inputValue; // Save valid input
        setData(newData);
      }
    }
  };
  const handleBlur = (row) => {
    // Set to '0' if the field is empty on blur
    const newData = [...data];
    if (newData[row.id] === '') {
      newData[row.id] = "0";
      setData(newData);
    }
  };

  return (
    <Paper
      sx={{
        width: "100%",
        overflow: "hidden",
        backgroundColor: disabled && "#E0E0E04D",
      }}
    >
      <TableContainer
        sx={{
          maxHeight: 440,
          "::-webkit-scrollbar": {
            width: "8px",
          },
          "::-webkit-scrollbar-thumb ": {
            background: theme.palette.primary.main,
            borderRadius: "8px",
          },
          "::-webkit-scrollbar-track": {
            background: "#E5E6E0",
          },
        }}
      >
        <Table>
          <TableHead>
            <StyledTableRow>
              {columns.map((column) => (
                <StyledTableCell
                  key={column.id}
                  align={column.align}
                  style={{
                    minWidth: column.minWidth,
                    fontSize: "14px",
                    fontFamily: theme.palette.fontFamily.main,
                    fontWeight: 600,
                    lineHeight: "26px",
                    color: theme.palette.text.main,
                    background: "inherit",
                    backgroundColor: "#DFE4E5",
                    zIndex: 100000,
                  }}
                >
                  {column.label}
                </StyledTableCell>
              ))}
            </StyledTableRow>
          </TableHead>  
          <TableBody>
            {rows.map((row) => (
              <StyledTableRow role="checkbox" tabIndex={-1} key={row.id}>
                {columns.map((column) => {
                  const value = row[column.id];
                  return (
                    <StyledTableCell
                      key={column.id}
                      align={column.align}
                      style={{
                        color: disabled && "#474F5080",
                        fontFamily: theme.palette.fontFamily.main,
                        fontWeight: 500,
                        fontSize: "14px",
                        lineHeight: '26px'
                      }}
                    >
                      {column.id === "hour" ? (
                        value
                      ) : (
                        <TextField
                          placeholder="Enter rate"
                          type="decimal"
                          value={value}
                          onChange={(e) => handleChange(e, row)}
                          onBlur={() => handleBlur(row)}
                          autoComplete="off"
                          size="small"
                          sx={{
                            "& fieldset": { border: "none" },
                          }}
                          InputProps={{
                            outline: "none",
                            border: "none",
                          }}
                          inputProps={{
                            min: 0,  // Replace with your minimum value
                            max: 1000000000,  // Replace with your maximum value
                          }}
                          disabled={disabled}
                        />
                      )}
                    </StyledTableCell>
                  );
                })}
              </StyledTableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
}
