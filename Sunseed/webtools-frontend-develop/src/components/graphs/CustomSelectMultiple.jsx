import * as React from "react";
import MenuItem from "@mui/material/MenuItem";
import ListItemText from "@mui/material/ListItemText";
import Select from "@mui/material/Select";
import Checkbox from "@mui/material/Checkbox";
import { useTheme } from "styled-components";

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

export default function CustomSelectMultiple({
  id,
  value,
  options,
  onChange,
  width = "10em",
  height = "2.5em",
}) {
  const theme = useTheme();
  return (
    <Select
      id={id}
      multiple
      value={value}
      onChange={onChange}
      renderValue={(selected) => {
        let label = "";
        selected.forEach((id) => {
          options.forEach((option, index) => {
            if (option.value === id)
              label += `${option.label}${selected.length > 1 ? ", " : ""}`;
          });
        });

        return label;
      }}
      MenuProps={{
        PaperProps: {
          sx: {
            maxHeight: 150,

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
      }}
    >
      {options.map((option) => (
        <MenuItem
          key={option.label}
          value={option.value}
          sx={{ background: "white !important" }}
        >
          <Checkbox
            checked={value.indexOf(option.value) > -1}
            sx={{
              color: theme.palette.primary.main,
              "&.Mui-checked": {
                color: theme.palette.primary.main,
              },
            }}
          />
          <ListItemText sx={{ background: "white" }} primary={option.label} />
        </MenuItem>
      ))}
    </Select>
  );
}
