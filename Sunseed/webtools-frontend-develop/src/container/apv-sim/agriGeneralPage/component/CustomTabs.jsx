import * as React from "react";
import PropTypes from "prop-types";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import Legend from "./Legends";

function CustomTabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box
          sx={{
            p: 3,
          }}
        >
          {children}
        </Box>
      )}
    </div>
  );
}

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

export default function CustomTabs({ tabs, internal, legends }) {
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  return (
    <Box
      key={tabs.label}
      sx={{ width: "100%", background: internal ? "#e8deda" : "#e8ecf7" }}
    >
      <Box sx={internal && { borderBottom: 1, borderColor: "divider" }}>
        {legends?.length && (
          <Box
            sx={{
              width: "100%",
              display: "flex",
              justifyContent: "flex-end",
              background: "#e8ecf7",
            }}
          >
            {legends.map((legend) => (
              <Legend
                label={legend.label}
                key={legend.label}
                color={legend.color}
              />
            ))}
          </Box>
        )}
        <Tabs
          variant={internal ? "standard" : "fullWidth"}
          value={value}
          onChange={handleChange}
          indicatorColor={!internal && "transparent"}
        >
          {tabs.map((tab, index) => (
            <Tab
              key={index}
              label={tab.label}
              style={
                internal
                  ? index === value
                    ? {
                        color: "#000",
                        fontWeight: "bold",
                        outline: "none",
                        textTransform: "capitalize",
                      }
                    : {
                        color: "#888",
                        fontWeight: "bold",
                        outline: "none",
                        textTransform: "capitalize",
                      }
                  : index === value
                  ? {
                      background: "#53988E",
                      color: "#fff",
                      fontWeight: "bold",
                      outline: "none",
                      borderRadius: "8px",
                      textTransform: "capitalize",
                    }
                  : {
                      background: "#53988E1F",
                      color: "#a0989e",
                      fontWeight: "bold",
                      outline: "none",
                      borderRadius: "8px",
                      textTransform: "capitalize",
                    }
              }
            />
          ))}
        </Tabs>
      </Box>

      {tabs.map((tab, index) => (
        <CustomTabPanel value={value} key={index} index={index}>
          {tab.component}
        </CustomTabPanel>
      ))}
    </Box>
  );
}
