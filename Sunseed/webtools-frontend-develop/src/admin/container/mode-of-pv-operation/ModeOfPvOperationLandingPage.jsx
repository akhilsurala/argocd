import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminModeOfPvOperations, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import PvModuleForm from "./ModeOfPvOperation";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_MODE_OF_PV_OPERATION, AppRoutesPath, editModeOfPvOperation, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const ModeOfPvOperationLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_MODE_OF_PV_OPERATION

  const callModeOfPvOperationsListApi = () => {
    getAdminModeOfPvOperations(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_MODE_OF_PV_OPERATION).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callModeOfPvOperationsListApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editModeOfPvOperation(row.id), {state: data});
  };
  const handleHideItem = (e, row) => {
    const payload = {
      id: row.id,
      name: row.modeOfOperation,
      hide: !row.hide,
    }
    updateAdminModeOfPvOperation(row.id, payload)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, row) => {
    navigate(viewStaticPage("mode-of-pv-operation",row?.id));
  };
  const handleDeleteItem = (e, row) => {
    navigate(AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_NEW);
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
        title="Mode OF PV Operation"
        createButtonLabel={"Add Operation"}
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

export default ModeOfPvOperationLandingPage;

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
