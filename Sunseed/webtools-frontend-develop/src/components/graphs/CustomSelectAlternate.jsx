import { MenuItem, Select } from "@mui/material";

const CustomSelectAlternate = ({
  id,
  value,
  options,
  onChange,
  width = "15em",
  height = "3em",
  placeholder = "Select",
}) => {
  return (
    <>
      <Select
        id={id}
        label=""
        value={value}
        onChange={onChange}
        displayEmpty
        sx={{
          width: width,
          height: height,
          minHeight: height,
          borderRadius: "6px",
        }}
        renderValue={(value) => {
          if (!value)
            return <span style={{ color: "#aaa" }}>{placeholder}</span>;
          return options.filter((obj) => obj.value === value)[0]?.label;
        }}
      >
        <MenuItem value="" disabled>
          {placeholder}
        </MenuItem>
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value} disabled={option.disabled}>
            {option.label}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

export default CustomSelectAlternate;
