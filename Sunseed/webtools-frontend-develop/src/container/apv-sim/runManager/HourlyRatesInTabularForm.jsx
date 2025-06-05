import * as React from "react";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import styled, { useTheme } from "styled-components";

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

export default function HourlyRatesInTabularForm({ data, labels, suffix='' }) {
  const theme = useTheme();

  const columns = [
    { id: "hour", label: labels[0], minWidth: 170 },
    { id: "rate", label: labels[1], minWidth: 100 },
  ];

  return (
    <Paper
      sx={{
        width: "100%",
        overflow: "hidden",
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
            {data?.map((row) => (
              <StyledTableRow role="checkbox" tabIndex={-1} key={row.id}>
                {columns.map((column) => {
                  const value = row[column.id];
                  return (
                    <StyledTableCell
                      key={column.id}
                      align={column.align}
                      style={{
                        fontFamily: theme.palette.fontFamily.main,
                        fontWeight: 500,
                        fontSize: "14px",
                        lineHeight: "26px",
                      }}
                    > 
                      {column.id === "rate" ? `${value || 0} ${suffix}` : value || 0}
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
