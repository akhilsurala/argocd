import { Box } from "@mui/material";
import React from "react";
import Footer from "../../container/home/Footer";
import Headers from "../../container/home/Header";
import AdminSideMenu from "./AdminSideMenu";

const AdminWrapper = (props) => {
  
  return (
    <Box>
      <Box>
        <AdminSideMenu />
        <Box sx={{backgroundColor:"#f5f7fc"}}>
          <Headers />
          <Box
            sx={{
              marginTop: "64px",
              marginLeft: "242px",
              paddingBottom: "50px",
            }}
          >
            {props.children}
          </Box>
        </Box>
      </Box>
      <Box>
        <Footer />
      </Box>
    </Box>
  );
};

export default AdminWrapper;
