import * as React from "react";

import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import DeleteIcon from "@mui/icons-material/Delete";

import { Alert, IconButton } from "@mui/material";
import { useTheme } from "styled-components";

import CrownImage from "../../src/assets/icons/crown.svg";
import GenerateCell from "../container/apv-sim/runManager/GenerateCell";
import { ColumnType } from "../container/apv-sim/runManager/utils";
import { statusText } from "../container/apv-sim/runManager/constants";
import {
  Cancel,
  HighlightOff,
  HighlightOffOutlined,
} from "@mui/icons-material";

export default function CustomRunsListCard(props) {
  const theme = useTheme();

  const { runName, type, status } = props.row;

  return (
    <Card
      sx={{
        minWidth: 275,
        marginBottom: ".5em",
        boxShadow: "none",
        background: type === "parent" && "#f8f9f9",
        borderRadius: "10px",
      }}
    >
      <CardContent
        sx={{
          padding: ".25em 0.25em .25em 1em !important",
          display: "flex",
          alignItems: "center",
        }}
      >
        <span style={{ display: "inline-block", marginRight: ".5em" }}>
          {runName}
        </span>
        {type === "parent" && (
          <img src={CrownImage} width="20px" height="20px" />
        )}

        <div style={{ flexGrow: 1 }}></div>

        <GenerateCell
          type={ColumnType.status}
          text={statusText[status]}
          value={status}
        />
        <IconButton
          aria-label="delete"
          size="medium"
          onClick={props.onDelete}
          disabled={props.disabled}
          style={{ color: !props.disabled && theme.palette.primary.secondary }}
        >
          <Cancel fontSize="inherit" />
        </IconButton>
      </CardContent>
    </Card>
  );
}
