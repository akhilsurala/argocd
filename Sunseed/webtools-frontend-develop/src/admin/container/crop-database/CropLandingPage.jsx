import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { getAdminCrops, updateAdminCrop } from "../../../api/admin/crop";
import PvModuleForm from "./CropForm";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_CROP, AppRoutesPath, editCrop, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const CropLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_CROP

  const callcropListApi = () => {
    getAdminCrops(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_CROP).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callcropListApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_CROP_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editCrop(row.id), { state: data });
  };
  const handleHideItem = (e, row) => {
    console.log(row);
    const requestDto = {
      name: row?.name,
      vcMax: row?.farquharParameter?.vcMax,
      jmax: row?.farquharParameter?.jmax,
      cjMax: row?.farquharParameter?.cjMax,
      hajMax: row?.farquharParameter?.haJMax,
      alpha: row?.farquharParameter?.alpha,
      rd25: row?.farquharParameter?.rd25,
      em: row?.stomatalParameter?.em,
      io: row?.stomatalParameter?.io,
      k: row?.stomatalParameter?.k,
      b: row?.stomatalParameter?.b,
      requiredDLI: row?.requiredDLI,
      requiredPPFD: row?.requiredPPFD,
      harvestDays: row?.harvestDays,
      f1: row?.f1,
      f2: row?.f2,
      f3: row?.f3,
      f4: row?.f4,
      f5: row?.f5,
      minStage: row?.minStage,
      maxStage: row?.maxStage,
      duration: row?.duration,
      hasPlantActualDate: !!row?.hasPlantActualDate,
      plantActualStartDate: !!row?.hasPlantActualDate && row?.plantActualStartDate ? row?.plantActualStartDate : null,
      plantMaxAge: row?.plantMaxAge,
      maxPlantsPerBed: row?.maxPlantsPerBed,
      hide: !row.hide,
      opticalProperties: {
        reflectance_PAR: row?.opticalProperty?.reflectionPAR,
        reflectance_NIR: row?.opticalProperty?.reflectionNIR,
        transmissivity_PAR: row?.opticalProperty?.transmissionPAR,
        transmissivity_NIR: row?.opticalProperty?.transmissionNIR
      }
    };
    const formData = new FormData();

    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles', null); // Add the file


    updateAdminCrop(row.id, formData)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, row) => {
    navigate(viewStaticPage("crop", row.id));
  };
  const handleDeleteItem = (e, row) => {
    navigate(AppRoutesPath.ADMIN_CROP_DATABASE_NEW);
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
        title="Crops"
        createButtonLabel={"Add Crop"}
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

export default CropLandingPage;

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
