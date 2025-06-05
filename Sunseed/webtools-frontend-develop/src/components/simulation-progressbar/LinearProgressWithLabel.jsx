import * as React from "react";
import PropTypes from "prop-types";
import LinearProgress from "@mui/material/LinearProgress";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { useTheme } from "styled-components";

function LinearProgressWithLabel(props) {
    const theme = useTheme();
    return (
        <Box
            sx={{
                display: "flex",
                alignItems: "left",
                flexDirection: "column",
                gap: "8px",
            }}
        >
            <Box
                sx={{
                    minWidth: 35,
                }}
            >
                <Typography variant="body2" color="text.secondary" sx={{
                    color: '#474F50',
                    fontFamily: theme.palette.fontFamily.main,
                    fontWeight: 600,
                    fontSize: "14px"
                }}  >{`${Math.round(
                    props.value
                )}% Simulating...`}</Typography>
            </Box>
            <Box sx={{ width: "100%", mr: 1 }}>
                <LinearProgress variant="determinate" {...props} sx={{
                    backgroundColor: '#C4E0EE',
                    height: '6px',
                    borderRadius: '6px',
                    "& .MuiLinearProgress-bar": {
                        backgroundColor: '#DB8C47',
                    },
                }} />
            </Box>
        </Box>
    );
}

LinearProgressWithLabel.propTypes = {
    /**
     * The value of the progress indicator for the determinate and buffer variants.
     * Value between 0 and 100.
     */
    value: PropTypes.number.isRequired,
};

export default function CustomLinearProgressWithLabel({ progress }) {
    // const [progress, setProgress] = React.useState(progress);

    //   React.useEffect(() => {
    //     const timer = setInterval(() => {
    //       setProgress((prevProgress) =>
    //         prevProgress >= 100 ? 10 : prevProgress + 10
    //       );
    //     }, 800);
    //     return () => {
    //       clearInterval(timer);
    //     };
    //   }, []);

    return (
        <Box sx={{ width: "100%" }}>
            <LinearProgressWithLabel value={progress} />
        </Box>
    );
}
