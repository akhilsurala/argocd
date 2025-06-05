import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminModuleConfigurations, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import PvModuleForm from "./ModuleConfigurationForm";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_MODULE_CONFIGURATION, AppRoutesPath, editModuleConfiguration, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const ModuleConfigurationLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_MODULE_CONFIGURATION

  const callModuleConfigurationsListApi = () => {
    getAdminModuleConfigurations(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_MODULE_CONFIGURATION).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callModuleConfigurationsListApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editModuleConfiguration(row.id), {state: data});
  };
  const handleHideItem = (e, row) => {
    const payload = {
      id: row.id,
      name: row.name,
      numberOfModules: row.numberOfModules,
      typeOfModule: row.typeOfModule,
      ordering: row.ordering,
      hide: !row.hide,
    }
    updateAdminModuleConfiguration(row.id, payload)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, row) => {
    navigate(viewStaticPage("module-configuration",row.id))
  };
  const handleDeleteItem = (e, id) => {
    navigate(AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE);
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
        title="Module Configuration"
        createButtonLabel={"Add Module Configuration"}
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

export default ModuleConfigurationLandingPage;

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
