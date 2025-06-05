import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminPvModules, hideAdminPvModule, updateAdminPvModule } from "../../../api/admin/pvModule";
import PvModuleForm from "./PvModuleForm";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_PV_MODULE, AppRoutesPath, editPvModule, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const AdminPvDatabase = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState("");

  const labels = ADMIN_PV_MODULE

  const callPvPModuleListApi = () => {
    getAdminPvModules(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(
          Object.keys(ADMIN_PV_MODULE).filter((key) => key !== "action")
        );
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callPvPModuleListApi, {}, 500);
  }, [reload, searchText]);


  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_PV_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editPvModule(row.id), {state: data});
  };
  const handleHideItem = (e, row) => {
    
    const requestDto = {
      name: row.moduleType,
      length: row.longerSide,
      width: row.shorterSide,
      hide: !row.hide,
      manufacturerName: row.manufacturerName,
      moduleName: row.moduleName,
      shortcode: row.shortcode,
      moduleTech: row.moduleTech,
      linkToDataSheet: "http://example.com/datasheet",
      numCellX: row.numCellX,
      numCellY: row.numCellY,
      longerSide: row.longerSide,
      shorterSide: row.shorterSide,
      thickness: row.thickness,
      voidRatio: row.voidRatio,
      xcell: row.xcell,
      ycell: row.ycell,
      xcellGap: row.xcellGap,
      ycellGap: row.ycellGap,
      vmap: row.vmap,
      imap: row.imap,
      idc0: row.idc0,
      pdc0: row.pdc0,
      neffective: row.neffective,
      voc: row.voc,
      isc: row.isc,
      alphaSc: row.alphaSc,
      betaVoc: row.betaVoc,
      gammaPdc: row.gammaPdc,
      temRef: row.temRef,
      radSun: row.radSun,
      opticalProperties: [
        {
          type: "front",
          reflectance_PAR: 0.0,
          reflectance_NIR: row?.frontOpticalProperty?.reflectionNIR,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        },
        {
          type: "back",
          reflectance_PAR: 0.0,
          reflectance_NIR: row.backOpticalProperty?.reflectionNIR,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        }
      ],
      f1: row.f1,
      f2: row.f2,
      f3: row.f3,
      f4: row.f4,
      f5: row.f5,
    };
    const formData = new FormData();

    formData.append("opticalFiles",  null);
    formData.append('opticalFiles',  null);
    formData.append('requestDto', JSON.stringify(requestDto));
    updateAdminPvModule(row.id, formData)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, data) => {
    navigate(viewStaticPage("pv-module", data.id));
  };
  const handleDeleteItem = (e, id) => {
    navigate(AppRoutesPath.ADMIN_PV_DATABASE_NEW);
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
        title="PV Modules"
        createButtonLabel={"Add Module"}
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
    </Container>
  );
};

export default AdminPvDatabase;

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
