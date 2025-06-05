import { Box, Divider } from "@mui/material";

const Figure = ({ title, image, width = "100%", height = 'auto', margin = '1em 0', align = 'center' }) => {
    return (
        <Box
            sx={{
                width: "100%",
                height: "100%",
                background: "#e8deda",
                display: "flex",
                flexDirection: "column",
                alignItems: align,
                margin: margin,
                paddingBottom: '2em',
                borderRadius: '16px'
            }}
        >{title &&
            <Box
                sx={{
                    width: "100%",
                    boxSizing: "border-box",
                    padding: "1em",
                }}
            >
                {title && <h5 style={{ color: "black" }}>{title}</h5>}
                {title && <Divider />}
            </Box>
            }
            <img src={image} width={width} height={height} />
        </Box>
    );
};

export default Figure;
