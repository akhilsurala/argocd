import { Box, Button } from "@mui/material";
import { useTheme } from "styled-components";

const CustomCard2 = ({
  title,
  content,
  actionText,
  handleClick,
  paddingRight = "2em",
  disabled = false,
}) => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        background: theme.palette.background.secondary,
        color: "#000",
        boxSizing: "border-box",
        padding: "2em 2em 1em 2em",
        paddingRight: paddingRight,
        borderRadius: "1em",
        fontSize: "14px",
        fontWeight: 500,
        lineHeight: "27px",
        height: "100%",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <h3
        style={{
          fontSize: "18px",
          color: theme.palette.text.main,
          marginTop: 0,
        }}
      >
        {title}
      </h3>
      {content}
      <div style={{ flexGrow: 1 }}></div>
      <Box
        sx={{
          background: theme.palette.background.secondary,
          boxSizing: "border-box",
          padding: "1em 0",
        }}
      >
        <Button
          disabled={disabled}
          variant="contained"
          onClick={handleClick}
          sx={{
            background: `${theme.palette.secondary.main}`,
            padding: "1em 2em",
            borderRadius: ".5em",
            boxShadow: "none",
            "&:hover": {
              background: `${theme.palette.secondary.main}`,
            },
          }}
        >
          {actionText}
        </Button>
      </Box>
    </Box>
  );
};

export default CustomCard2;
