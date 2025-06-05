import * as React from "react";
import PropTypes from "prop-types";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import { useTheme } from "styled-components";

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
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

export default function CustomTabs({ activeTab, handleChange, tabs }) {
  const theme = useTheme();

  return (
    <Box sx={{ width: "100%", boxSizing: "border-box", paddingBottom: "4em" }}>
      <Box sx={{ borderBottom: "none", background: "white" }}>
        <Tabs
          value={activeTab}
          onChange={(event, newValue) => handleChange(newValue)}
          aria-label="basic tabs example"
          indicatorColor="transparent"
        >
          {tabs.map((tab, index) => (
            <Tab
              icon={tab.icon}
              key={index}
              iconPosition="start"
              label={tab.label}
              {...a11yProps(index)}
              sx={
                index === activeTab
                  ? {
                    color: `${theme.palette.primary.main} !important`,
                    borderBottom: `3px solid ${theme.palette.primary.main} !important`,
                    outline: "none !important",
                    textTransform: "capitalize",
                    fontWeight: "bold",
                  }
                  : {
                    borderBottom: `3px solid white !important`,
                    outline: "none !important",
                    textTransform: "capitalize",
                  }
              }
            />
          ))}
        </Tabs>
      </Box>
      {tabs.map((tab, index) => (
        <CustomTabPanel key={index} value={activeTab} index={index}>
          {tab.component}
        </CustomTabPanel>
      ))}
    </Box>
  );
}
