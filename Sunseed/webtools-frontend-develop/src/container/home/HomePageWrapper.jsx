import React, { useEffect } from "react";

import { Box } from "@mui/material";

import Footer from "./Footer";
import Headers from "./Header";
import SideMenu from "./SideMenu";
import Dashboard from "./Dashboard";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";
import RecentProjectScreen from "../apv-sim/RecentProjectScreen";
import ProjectForm from "../apv-sim/ProjectForm";

const HomePageWrapper = (props) => {
  const navigate = useNavigate();

  useEffect(() => {
    const userId = localStorage.getItem("apiToken");
    if (!userId) {
      navigate(AppRoutesPath.LOGIN);
    }
  }, []);

  return (
    <Box sx={{ height: "100vh", display: "flex", flexDirection: "column" }}>
      <Box sx={{ display: "flex" }}>
        <SideMenu />
        <Box sx={{ flex: 1 }}>
          <Headers />
        </Box>
      </Box>
      <Box sx={{ flex: 1, backgroundColor: "#F4F5F7" }}>
        <Box sx={{ width: "calc(100% - 242px)", ml: "242px", mt: 8, height: "calc(100% - 100px)" }}>
          {props.children}
        </Box>
      </Box>
      <Box>
        <Footer />
      </Box>
    </Box>
  );
};

export default HomePageWrapper;
