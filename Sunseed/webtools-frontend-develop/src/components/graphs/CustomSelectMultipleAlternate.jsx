import * as React from "react";
import MenuItem from "@mui/material/MenuItem";
import ListItemText from "@mui/material/ListItemText";
import Select from "@mui/material/Select";
import Checkbox from "@mui/material/Checkbox";
import { useTheme } from "styled-components";
import { Box, ListSubheader } from "@mui/material";

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

export default function CustomSelectMultipleAlternate({
  id,
  name,
  value,
  options,
  onChange,
  width = "10em",
  height = "3em",
  subHeader = "Select",
}) {
  const theme = useTheme();
  return (
    <Select
      id={id}
      multiple
      value={value}
      onChange={onChange}
      displayEmpty
      renderValue={(selected) => {
        // let label = "";
        // selected.forEach((id) => {
        //   options.forEach((option, index) => {
        //     if (option.value === id)
        //       label += `${option.label}${selected.length > 1 ? ", " : ""}`;
        //   });
        // });

        // return label;

        if (!selected?.length)
          return <span style={{ color: "#aaa" }}>{subHeader}</span>;

        return (
          <Box>
            <span
              style={{
                display: "inline-block",
                boxSizing: "border-box",
                padding: ".25em 1em",
                border: `2px solid ${theme.palette.primary.main}`,
                borderRadius: "10em",
                background: "#fcf1e9",
              }}
            >{`${selected?.length} ${name} Selected`}</span>
          </Box>
        );
      }}
      MenuProps={{
        autoFocus: false,
        PaperProps: {
          sx: {
            maxHeight: 300,

            "&::-webkit-scrollbar": {
              width: 6,
            },
            "&::-webkit-scrollbar-track": {
              boxShadow: `#D5D5D5`,
            },
            "&::-webkit-scrollbar-thumb": {
              backgroundColor: theme.palette.primary.secondary,

              borderRadius: "8px",
            },
          },
        },
      }}
      sx={{
        width: width,
        height: height,
        minHeight: height,
        background: "white",
        borderRadius: "6px",
      }}
    >
      <ListSubheader
        sx={{
          fontSize: "1em",
          color: theme.palette.text.main,
          paddingLeft: "1.75em",
          lineHeight: "32px",
          // backgroundColor: "transparent",
        }}
      >
        {subHeader}
      </ListSubheader>
      {options?.map((option) => (
        <MenuItem
          key={option.label}
          value={option.value}
          sx={{
            background: "white !important",
            paddingTop: 0,
            paddingBottom: 0,
            "&:hover": {
              backgroundColor: "#eff2fc !important", // Change background to aliceblue on hover
            },
            height: "2em",
          }}
        >
          <Checkbox
            checked={value.indexOf(option.value) > -1}
            sx={{
              borderRadius: "12px !important", // Make checkbox curvy
              color: theme.palette.primary.main,
              "&.Mui-checked": {
                color: theme.palette.primary.main,
              },
            }}
          />
          <ListItemText
            sx={{
              "&:hover": {
                backgroundColor: "#eff2fc !important", // Change background to aliceblue on hover
              },
            }}
            primary={option.label}
          />
        </MenuItem>
      ))}
    </Select>
  );
}
