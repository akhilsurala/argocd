import { ExpandCircleDown, ExpandMoreOutlined } from "@mui/icons-material";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  Divider,
} from "@mui/material";
import { useTheme } from "styled-components";
import { CustomSvgIconForExpand } from "../container/dashboard/CustomSvgIcon";

const ClearButton = ({ actionText, handleClick, disabled = false }) => {
  const theme = useTheme();
  return (
    <Button
      variant="text"
      style={{ outline: 0 }}
      onClick={handleClick}
      disabled={disabled}
      sx={{
        color: `${theme.palette.secondary.main}`,
        padding: "1em 0",
        margin: "0",
        marginLeft: "1em",
        borderRadius: ".5em",
        boxShadow: "none",
        textTransform: "capitalize",
        fontWeight: "bold",
        "&:hover": {
          color: `${theme.palette.secondary.main}`,
          background: "transparent",
        },
      }}
    >
      {actionText}
    </Button>
  );
};

const GraphListCrad = ({ headerText, list, width = "100%" }) => {
  const theme = useTheme();

  return (
    <Box
      sx={{
        flexBasis: width,
        boxSizing: "border-box",
        border: "1px solid #eee",
        padding: "1em",
        borderRadius: "8px",
        margin: "0 1em",
      }}
    >
      {headerText && (
        <>
          <Box
            sx={{
              width: "100%",
              boxSizing: "border-box",
              padding: "1em",
              borderRadius: "8px",
              background: "#f2f4f5",
              fontWeight: 600,
              color: theme.palette.text.main,
            }}
          >
            {headerText}
          </Box>
        </>
      )}
      {list.map((item, index) => (
        <>
          <Box
            sx={{
              width: "100%",
              boxSizing: "border-box",
              padding: "0 1em",
            }}
          >
            {index ? <Divider /> : null}
            <>
              {item.type === "NESTED" ? (
                <Accordion sx={{ boxShadow: "none" }}>
                  <AccordionSummary
                    expandIcon={
                      <CustomSvgIconForExpand
                        sx={{
                          transform: "rotate(180deg)",
                          transition: "transform 0.3s",
                          "&.Mui-expanded": { transform: "rotate(0deg)" },
                        }}
                      />
                    }
                    sx={{
                      padding: "0",
                      margin: "0",
                      position: "relative",
                      boxSizing: "border-box",
                      paddingLeft: "2.5em",
                      "& .MuiAccordionSummary-content": { margin: "0" },
                      "& .MuiAccordionSummary-expandIconWrapper": {
                        position: "absolute",
                        left: 0,
                      },
                    }}
                  >
                    <Box
                      sx={{
                        width: "100%",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                      }}
                    >
                      <span
                        style={{
                          fontWeight: 500,
                          color: theme.palette.text.main,
                        }}
                      >
                        {item.text}
                      </span>
                      <Box sx={{ display: "flex" }}>
                        {item.buttons.map((btn) => (
                          <ClearButton
                            actionText={btn.actionText}
                            handleClick={btn.handleClick}
                            disabled={btn.disabled}
                          />
                        ))}
                      </Box>
                    </Box>
                  </AccordionSummary>
                  <AccordionDetails>
                    {item.elements.map((element, internalIndex) => (
                      <>
                        {internalIndex ? <Divider /> : null}
                        <Box
                          sx={{
                            width: "100%",
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          {/* <span
                            style={{
                              fontWeight: 500,
                              color: theme.palette.text.main,
                            }}
                          >
                            {element.text}
                          </span> */}
                          <span style={{ fontWeight: 500, color: theme.palette.text.main }}>
                            {element.text.split('\n').map((line, i) => (
                              <>
                                {line}
                                <br />
                              </>
                            ))}
                          </span>
                          <Box sx={{ display: "flex" }}>
                            {element.buttons.map((btn) => (
                              <ClearButton
                                actionText={btn.actionText}
                                handleClick={btn.handleClick}
                                disabled={btn.disabled}
                              />
                            ))}
                          </Box>
                        </Box>
                      </>
                    ))}
                  </AccordionDetails>
                </Accordion>
              ) : (
                <Box
                  sx={{
                    width: "100%",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    boxSizing: "border-box",
                  }}
                >
                  {/* <span
                    style={{ fontWeight: 500, color: theme.palette.text.main }}
                  >
                    {item.text}
                  </span> */}
                  <span style={{ fontWeight: 500, color: theme.palette.text.main }}>
                    {item.text.split('\n').map((line, i) => (
                      <>
                        {line}
                        <br />
                      </>
                    ))}
                  </span>
                  <Box sx={{ display: "flex" }}>
                    {item.buttons.map((btn) => (
                      <ClearButton
                        actionText={btn.actionText}
                        handleClick={btn.handleClick}
                        disabled={btn.disabled}
                      />
                    ))}
                  </Box>
                </Box>
              )}
            </>
          </Box>
        </>
      ))}
    </Box>
  );
};

export default GraphListCrad;
