import { Box } from "@mui/material";

const Legend = ({ label, color }) => {
    return (
        <Box
            sx={{
                display: "flex",
                alignItems: "center",
                marginRight: "1em",
            }}
        >
            <h5 style={{ color: "black", marginRight: "1em" }}>{label}</h5>
            <Box
                sx={{
                    width: "1em",
                    height: "1em",
                    background: color,
                    border: "2px solid black",
                }}
            />
        </Box>
    );
};

export default Legend;
