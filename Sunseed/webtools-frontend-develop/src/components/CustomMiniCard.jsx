import { Box, Button, IconButton } from "@mui/material";
import { GRAPH_DOWNLOAD_STATUS } from "../utils/constant";
import {
  Download,
  CheckCircleRounded,
  DeleteForever,
} from "@mui/icons-material";
import { useTheme } from "styled-components";

const CustomMiniCard = ({
  title,
  status,
  percentDownloaded,
  handleViewGraph,
  handleDelete,
}) => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        background: theme.palette.background.secondary,
        color: "#000",
        boxSizing: "border-box",
        borderRadius: "1em",
        border: `1px solid ${theme.palette.border.main}`,
        marginBottom: "1em",
      }}
    >
      <h5 style={{ padding: "0 1em", fontSize: "1em", fontWeight: "500" }}>
        {title}
      </h5>
      <Box
        sx={{
          borderTop: `1px solid ${theme.palette.border.main}`,
          boxSizing: "border-box",
          padding: "0.25em 1em",
          background:
            status === GRAPH_DOWNLOAD_STATUS.downloading
              ? theme.palette.miniCard.secondary
              : theme.palette.miniCard.main,
          borderBottomLeftRadius: "1em",
          borderBottomRightRadius: "1em",
        }}
      >
        {status === GRAPH_DOWNLOAD_STATUS.toDownload && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <IconButton
              aria-label="download"
              size="small"
              sx={{ color: theme.palette.secondary.main }}
            >
              <Download />
            </IconButton>
            <span>Download</span>
          </Box>
        )}
        {status === GRAPH_DOWNLOAD_STATUS.downloading && (
          <Box sx={{ display: "flex", justifyContent: "center" }}>
            <span>{percentDownloaded || ""}% Downloading...</span>
          </Box>
        )}
        {status === GRAPH_DOWNLOAD_STATUS.downloaded && (
          <Box sx={{ display: "flex", justifyContent: "space-between" }}>
            <Box sx={{ display: "flex", alignItems: "center" }}>
              <CheckCircleRounded
                sx={{ color: theme.palette.secondary.main }}
              />
              <span style={{ display: "inline-block", marginLeft: ".25em", fontStyle: 'italic', color: theme.palette.text.secondary }}>
                Downloaded
              </span>
            </Box>
            <Box sx={{ display: "flex" }}>
              <Button
                size="medium"
                sx={{
                  color: theme.palette.secondary.main,
                  fontWeight: "bold",
                  textTransform: "capitalize",
                }}
              >
                View Graph
              </Button>
              <IconButton
                aria-label="delete"
                size="large"
                sx={{ color: theme.palette.secondary.main }}
              >
                <DeleteForever />
              </IconButton>
            </Box>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default CustomMiniCard;
