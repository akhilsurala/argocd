import { Box, Button } from "@mui/material";
import { useTheme } from "styled-components";

const GraphListCradContainer = (props) => {
  const { title, actionText, handleClick, disabled = false } = props;
  const theme = useTheme();

  return (
    <Box
      sx={{
        background: theme.palette.background.secondary,
        color: "#000",
        boxSizing: "border-box",
        padding: "0 2em 2em 2em",
        borderRadius: "1em",
        fontSize: "14px",
        fontWeight: 500,
        lineHeight: "27px",
        display: "flex",
        flexDirection: "column",
        marginBottom: "2em",
        height: "95%",
      }}
    >
      <h3
        style={{
          fontSize: "18px",
          color: theme.palette.text.main,
          fontWeight: 700,
          marginLeft: "1em",
        }}
      >
        {title}
      </h3>
      <Box sx={{ display: "flex" }}>{props.children}</Box>
      <div style={{ flexGrow: 1 }}></div>
      {actionText && (
        <Box
          sx={{
            background: theme.palette.background.secondary,
            boxSizing: "border-box",
            padding: "1em",
            marginTop: "1em",
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
                boxShadow: "none",
              },
              textTransform: "capitalize",
              fontWeight: "bold",
            }}
          >
            {actionText}
          </Button>
        </Box>
      )}
    </Box>
  );
};

export default GraphListCradContainer;
