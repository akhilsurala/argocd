import React, { useState } from "react";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import BorderColorIcon from "@mui/icons-material/BorderColor";
import OpenInNewIcon from "@mui/icons-material/OpenInNew";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import styled, { useTheme } from "styled-components";
import { Box, InputAdornment, TextField } from "@mui/material";
import CustomButton from "../../components/CustomButton";
import SearchIcon from "@mui/icons-material/Search";
import blockUser from "../../assets/icons/blockUser.svg"
import unBlockUser from "../../assets/icons/unBlockUser.svg"
import { changeDateFormat } from "../../utils/constant";
import dayjs from "dayjs";

const StyledTableCell = styled(TableCell)`
  &.${tableCellClasses.head} {
    background-color: #f5f7fc;
    color: #474f50;
    font-weight: 600;
    font-family: "Montserrat";
  }
  &.${tableCellClasses.body} {
    font-size: 14px;
  }
`;

const StyledTableRow = styled(TableRow)`
  &:nth-of-type(even) {
    background-color: #dfe4e53d; // disable #DFE4E53D
  }
  &:last-child td,
  &:last-child th {
    border: 0;
  }
`;

export default function CustomTable({
  title,
  createButtonLabel,
  showAddButton=true,
  showEyeIcon,
  showBlockcon,
  showEditIcon,
  showRedirectIcon,
  showDeleteIcon,
  data,
  headers,
  labels,
  handleNewItem,
  handleEditItem,
  handleHideItem,
  handleBlockItem,
  handleViewItem,
  handleDeleteItem,
  searchText,
  setSearchText
}) {

  const theme = useTheme();
  const [selectedRows, setSelectedRows] = useState([]);

  const handleCheckboxChange = (event, rowName) => {
    if (event.target.checked) {
      setSelectedRows((prevSelected) => [...prevSelected, rowName]);
    } else {
      setSelectedRows((prevSelected) =>
        prevSelected.filter((name) => name !== rowName)
      );
    }
  };

  // const handleViewButton = (event, rowName) => {
  //   console.log(event, rowName);
  // };

  const EyeIcon = ({ row }) => (
    (row['hide']) ? <VisibilityOffIcon className="icons"  onClick={(e) => handleHideItem(e, row)} /> :
      <VisibilityIcon className="icons"  onClick={(e) => handleHideItem(e, row)} />
  )

  const BlockIcon = ({ row }) => (
    (!row['isActive']) ? 
        <img src={blockUser} alt="icon" className="icons" onClick={(e) => handleBlockItem(e, row) } /> :
      <img src={unBlockUser} alt="icon" className="icons" onClick={(e) => handleBlockItem(e, row) } />
  )
  
  return (
    <Container>
      <Box className="headerWrapper">
        <Box className="title">{title}</Box>
        <Box className="searchContainer">
          <TextField
            id="outlined-basic"
            placeholder="Search"
            size="smaill"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            InputProps={{
              style: {
                height: "40px",
                padding: "4px 8px",
                width: "189px",
                borderRadius: "8px",
                fontFamily: theme.palette.fontFamily.main,
                borderColor: "#C7C9CA",
                fontSize: "14px",
              },
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon sx={{ color: "#C7C9CA" }} />
                </InputAdornment>
              ),
            }}
          />
          {showAddButton && <CustomButton label={createButtonLabel} onClick={ handleNewItem } />}
        </Box>
      </Box>
      <TableContainer
        component={Paper}
        sx={{
          boxShadow: "none",
          borderRadius: "8px",
        }}
      >
        <Table
          sx={{
            minWidth: 700,
            "&:last-child td, &:last-child th": { border: 0 },
          }}
          aria-label="customized table"
        >
          <TableHead>
            <TableRow>
              <StyledTableCell />
              {[...headers, 'action'].map((menu) => {
                return (
                  <StyledTableCell align="left" key={menu}>
                    {labels[menu]}
                  </StyledTableCell>
                );
              })}
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((row) => (
              <StyledTableRow key={row['id']}>
                  <StyledTableCell padding="checkbox">
                    {/* <Checkbox
                      checked={selectedRows.includes(row['id'])}
                      onChange={(event) => handleCheckboxChange(event, row['id'])}
                      style={{
                        color: theme.palette.primary.main,
                      }}
                    /> */}
                  </StyledTableCell>
                {headers.map((column) => (
                  <>
                    <StyledTableCell component="th" scope="row">
                      {column === "createdAt" || column ==="updatedAt" ? row[column] ? changeDateFormat(dayjs.utc(row[column]).tz(dayjs.tz.guess())) : null : row[column]}
                    </StyledTableCell>
                  </>
                ))}
                <StyledTableCell align="left" >
                  {showEyeIcon && ( <EyeIcon row={row} />) }
                  {showBlockcon && ( <BlockIcon row={row} />) }
                  {showEditIcon && <BorderColorIcon className="icons" onClick={(e) => handleEditItem(e, row)} /> }
                  {showRedirectIcon && <OpenInNewIcon className="icons" onClick={(e) => handleViewItem(e, row)} /> }
                  {showDeleteIcon && <DeleteForeverIcon className="icons" onClick={(e) => handleDeleteItem(e, row)} /> }
                </StyledTableCell>
              </StyledTableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
}

const Container = styled.div`
  border: 1px solid #ffffff;
  padding: 24px;
  background-color: #ffffff;
  border-radius: 16px;

  .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.secondary.main};
    font-size: 20px;
    margin-right: 14px;
  }

  .headerWrapper {
    display: flex;
    align-items: center;
    margin-bottom: 30px;
  }
  .title {
    flex: 1;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    color: ${({ theme }) => theme.palette.text.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
  }
  .searchContainer {
    display: flex;
    gap: 12px;
  }
`;
