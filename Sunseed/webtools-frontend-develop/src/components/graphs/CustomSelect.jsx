import { MenuItem, Select } from "@mui/material";

const CustomSelect = ({
  id,
  value,
  options,
  onChange,
  width = "15em",
  height = "2.5em",
}) => {
  return (
    <Select
      id={id}
      label=""
      value={value}
      onChange={onChange}
      sx={{
        width: width,
        height: height,
        minHeight: height,
      }}
    >
      {options.map((option) => (
        <MenuItem key={option.value} value={option.value}>
          {option.label}
        </MenuItem>
      ))}
    </Select>
  );
};

export default CustomSelect;
