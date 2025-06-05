// Navigation.js
import React from "react";
import { Tabs, Tab, Box, Typography } from "@mui/material";
import ArticleIcon from "@mui/icons-material/Article";
import award from "../../assets/award.svg";
import {
  CustomSvgIconForAward,
  CustomSvgIconForBook,
  CustomSvgIconForMyApp,
  CustomSvgIconForSupport,
  CustomSvgIconForTutorial,
} from "./CustomSvgIcon";
import styled, { useTheme } from "styled-components";
import MyApps from "./MyApps";
import Documentation from "./Documentation";

const CustomTabs = ({ selectedIndex, onItemClick }) => {
  const theme = useTheme();
  const [value, setValue] = React.useState(0);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

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
              boxSizing: "border-box",

              textAlign: "-webkit-center",
            }}
          >
            {children}
          </Box>
        )}
      </div>
    );
  }

  const tabs = [<MyApps />, <Documentation />];

  return (
    <Box
      sx={{
        width: "100%",
        // marginTop:'65px',
        top: "65px",
        position: "fixed",
        zIndex: 100,
      }}
    >
      <Tabs
        value={value}
        onChange={handleChange}
        aria-label="icon position tabs example"
        sx={{
          "& .MuiButtonBase-root": { fontFamily: "Montserrat" },
          "& .MuiTabs-indicator": {
            backgroundColor: theme.palette.primary.main,
          },
          "& .MuiButtonBase-root.Mui-selected": {
            color: theme.palette.primary.main,
            fontWeight: "700",
          },
          "& :focus": {
            outline: 0,
          },
          boxSizing: "border-box",
          paddingLeft: "6em",
          paddingRight: "6em",
          backgroundColor: "#fff",
        }} // Change selected color to red
      >
        <Tab
          key={1}
          icon={<CustomSvgIconForMyApp />}
          iconPosition="start"
          label="my apps"
          sx={{ textTransform: "capitalize" }} // Convert label to lowercase mixed with uppercase
        />
        {/* <Tab

          key={2}
          icon={<CustomSvgIconForAward />}
          iconPosition="start"
          label="license management"
          sx={{ textTransform: 'capitalize' }} // Convert label to lowercase mixed with uppercase
        /> */}
        <Tab
          key={3}
          icon={<CustomSvgIconForBook />}
          iconPosition="start"
          label="documentation"
          sx={{ textTransform: "capitalize" }} // Convert label to lowercase mixed with uppercase
        />
        {/* <Tab

          key={4}
          icon={<CustomSvgIconForTutorial />}
          iconPosition="start"
          label="tutorial"
          sx={{ textTransform: 'capitalize' }} // Convert label to lowercase mixed with uppercase
        />
        <Tab

          key={5}
          icon={<CustomSvgIconForSupport />}
          iconPosition="start"
          label="support"
          sx={{ textTransform: 'capitalize' }} // Convert label to lowercase mixed with uppercase
        /> */}
      </Tabs>
      {tabs.map((tab, index) => (
        <CustomTabPanel key={index} value={value} index={index}>
          {tab}
        </CustomTabPanel>
      ))}
    </Box>
  );
};

export default CustomTabs;
