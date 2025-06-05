import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  styled,
  tableCellClasses,
} from "@mui/material";
import React from "react";
import "react-quill/dist/quill.snow.css";
import { BASE_URL_FOR_PUBLIC_ASSET, BASE_URL_FOR_USER_AVATAR } from "../../api/config";
import { downloadURL } from "../../utils/constant";

const CustomTableForView = ({ data }) => {
  const StyledTableCell = styled(TableCell)`
    &.${tableCellClasses.head} {
      background-color: #f5f7fc;
      color: #474f50;
      font-weight: 600;
      font-family: "Montserrat";
    }
    &.${tableCellClasses.body} {
      font-size: 14px;
      color: #252727;
      font-family: "Montserrat";
    }
  `;

  const StyledTableRow = styled(TableRow)`
    &:nth-of-type(even) {
      /* background-color: #dfe4e53d; // disable #DFE4E53D */
    }
    &:last-child td,
    &:last-child th {
      border: 0;
    }
  `;

  const getActualValue = (label, data) => {

    if (imageLinkMapping[label] == "image") return renderImage(data);
    if (imageLinkMapping[label] == "html") return renderHtml(data);
    if (imageLinkMapping[label] == "link") return renderLink(data);

    if (data === true) return "true";
    else if (data === false) return "false";
    else return data;
  };

  const renderImage = (imgUrl) => {
    if (!imgUrl) return null;
    const img = `${BASE_URL_FOR_USER_AVATAR}${BASE_URL_FOR_PUBLIC_ASSET}${imgUrl}`.replace(/\s/g, "%20");
    return <img src={img} alt="" className="src" />;
  };
  const renderHtml = (data) => {
    if (!data) return null;
    return <div
      style={{ overflow: "auto", maxHeight: "400px" }} // Adjust height as needed
      dangerouslySetInnerHTML={{ __html: data }}
    />;
  };
  const renderLink = (data) => {
    if (!data) return null;
    return <a href={downloadURL(data)}
      download rel="noopener noreferrer">
      {data?.split('/').pop()}
    </a>
  }

  const imageLinkMapping = {
    "Background Tile": "image",
    "Link To Texture": "image",
    "Description": "html",
    "Front Optical Property File": "link",
    "Back Optical Property File": "link",
    "Optical Property File": "link",
    "Link To Texture": "link",
  };

  return (
    <div>
      <TableContainer
        component={Paper}
        sx={{
          boxShadow: "none",
          borderRadius: "8px",
        }}
      >
        <Table
          sx={{
            minWidth: 600,
            "&:last-child td, &:last-child th": { border: 0 },
            marginTop: "20px",
            color: "red",
          }}
          aria-label="customized table"
        >
          <TableBody>
            {data &&
              data?.map((row) =>
                Object.keys(row).map((field) => {
                  return (
                    <StyledTableRow key={row["id"]}>
                      <StyledTableCell
                        component="th"
                        scope="row"
                        style={{ fontWeight: 500 }}
                      >
                        {field}
                      </StyledTableCell>
                      <StyledTableCell
                        component="th"
                        scope="row"
                        style={{ fontWeight: 600 }}
                      >
                        {getActualValue(field, row[field])}

                      </StyledTableCell>
                    </StyledTableRow>
                  );
                })
              )}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default CustomTableForView;
