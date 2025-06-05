import { Box, Tooltip } from "@mui/material";
import { useTheme } from "styled-components";
import { statusColor } from "./utils";

const ColumnType = {
  text: "TEXT",
  link: "LINK",
  status: "STATUS",
  toolTip: "TOOLTIP",
};

const GenerateCell = ({ type, text, value, title, handleClick }) => {
  const theme = useTheme();

  // console.log('Tooltip');
  // console.log('Type: ', type);
  // console.log('Text: ', text);

  if (type === ColumnType.link) {
    return (
      <Box
        sx={{
          cursor: "pointer",
          color: theme.palette.secondary.main,
        }}
        onClick={handleClick}
      >
        <span>{text}</span>
      </Box>
    );
  }

  if (type === ColumnType.status) {
    return (
      <div
        style={{
          border: "1px solid",
          padding: "5px",
          borderRadius: "50px",
          color: statusColor(value).color,
          background: statusColor(value).backgroundColor,
          fontFamily: "Montserrat",
          borderColor: statusColor(value).color,
          fontSize: "12px",
          fontWeight: "600",
          lineheight: "24px",
        }}
      >
        {text}
      </div>
    );
  }

  if (type === ColumnType.toolTip) {
    return (
      <Tooltip
        placement="right"
        componentsProps={{
          tooltip: {
            sx: {
              bgcolor: "transparent",
            },
          },
        }}
        title={title}
      >
        {text}
      </Tooltip>
    );
  }
};

export default GenerateCell;