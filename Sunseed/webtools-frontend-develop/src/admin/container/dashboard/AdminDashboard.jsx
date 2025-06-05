import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminDashboardData, getAdminDashboardCount } from "../../../api/admin/dashboard";
import { ADMIN_PV_MODULE, AppRoutesPath } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [simulatedRuncount, setSimulatedRunCount] = useState(0);
  const [headers, setHeaders] = useState([]);

  const labels = ADMIN_PV_MODULE

  useEffect(() => {
    getAdminDashboardData()
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(response?.data?.data[0]));
      })
      .catch((error) => {
        console.log(error);
      });
    getAdminDashboardCount()
      .then((response) => {
        setSimulatedRunCount(response.data.data.simulatedRuns);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  const handleNewItem = () => {
    navigate(AppRoutesPath.HOME);
  };

  const handleViewUsers = () => {
    navigate(AppRoutesPath.ADMIN_USER_DATABASE);
  }

  const redirectToHomePage = () => {
    navigate(AppRoutesPath.HOME);
    setActiveTab("Dashboard");
  };

  const CustomCard = ({ title, count, showUser }) => {
    return (
      <Box className="cardContainer">
        <Box className="title">{title}</Box>
        <Box className="count">{count}</Box>
        {showUser && <Box className="userWrapper" onClick={ handleViewUsers }> View Users </Box>}
      </Box>
    );
  };

  return (
    <Container
      style={{
        padding: "20px",
        display: "flex",
        flexDirection: "column",
        gap: "20px",
      }}
    >
      <CustomTable
        title = "Active Projects"
        showAddButton={false}
        showEyeIcon={false}
        showEditIcon={false}
        showRedirectIcon={false}
        showDeleteIcon={true}
        data={data}
        headers={headers}
        labels={labels}
      />
      <Grid container spacing={4}>
        <Grid item md={6}>
          <CustomCard title="Total User" count="142" showUser={true} />
        </Grid>
        <Grid item md={6}>
          <CustomCard title="No. Of Simulations" count={simulatedRuncount} showUser={false} />
        </Grid>
      </Grid>
    </Container>
  );
};

export default AdminDashboard;

const Container = styled.div`
  .cardContainer {
    background-color: #ffffff;
    height: 320px;
    top: 556px;
    left: 262px;
    gap: 0px;
    border-radius: 16px;
    opacity: 0px;
    padding: 40px 20px 0px 20px;
    display: flex;
    flex-direction: column;
  }
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
  }
  .count {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 80px;
    font-weight: 700;
    line-height: 97.52px;
    flex: 1;
    margin: 50px auto;
  }
  .userWrapper {
    text-align: center;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 12px;
    font-weight: 600;
    line-height: 22px;
    padding: 8px;
    border-top: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.light};
    cursor: pointer;
  }
`;
