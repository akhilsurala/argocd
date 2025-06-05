import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminSoils, updateAdminSoil } from "../../../api/admin/soil";
import PvModuleForm from "./SoilForm";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_SOIL, AppRoutesPath, editSoil, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const SoilLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_SOIL

  const callSoilsListApi = () => {
    getAdminSoils(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_SOIL).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callSoilsListApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_SOIL_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editSoil(row.id), {state: data});
  };
  // const handleHideItem = (e, row) => {
  //   const payload = {
  //     // id: row.id,
  //     name: row.soilName,
  //     hide: !row.hide,
  //   }
  //   updateAdminSoil(row.id, payload)
  //     .then((response) => {
  //       setReload(!reload);
  //     })
  //     .catch((error) => {
  //       console.log(error);
  //     });
  // };
  const handleHideItem = (e, row) => {
    const requestDto = {
      name: row.soilName,
      hide: !row.hide,
      opticalProperties: {
        reflectance_PAR: 0.0,
        reflectance_NIR: 0.0,
        transmissivity_PAR: 0.0,
        transmissivity_NIR: 0.0
      }
    };
  
    const formData = new FormData();
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles',  null); 
    updateAdminSoil(row.id, formData)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, row) => {
    navigate(viewStaticPage("soil",row.id))
  };
  const handleDeleteItem = (e, id) => {
    navigate(AppRoutesPath.ADMIN_SOIL_DATABASE_NEW);
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
        title="Soils"
        createButtonLabel={"Add Soil"}
        showEyeIcon={true}
        showEditIcon={true}
        showRedirectIcon={true}
        showDeleteIcon={false}
        data={data}
        headers={headers}
        labels={labels}
        handleNewItem={handleNewItem}
        handleEditItem={handleEditItem}
        handleHideItem={handleHideItem}
        handleViewItem={handleViewItem}
        handleDeleteItem={handleDeleteItem}
        searchText={searchText}
        setSearchText={setSearchText}
      />
      {/* <PvModuleForm /> */}
      {/* <CustomSuccessPage
        title="PV Module Added"
        text="Successfully added new PV module."
        buttonLabel="Move to PV Module"
      /> */}
    </Container>
  );
};

export default SoilLandingPage;

const Container = styled.div`
  min-height: calc(100vh - 200px);

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
