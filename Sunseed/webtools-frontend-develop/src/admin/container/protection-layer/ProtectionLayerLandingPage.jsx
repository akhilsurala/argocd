import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import styled from "styled-components";
import { getAdminProtectionLayers, updateAdminProtectionLayer } from "../../../api/admin/protectionLayer";
import { ADMIN_PROTECTION_LAYER, AppRoutesPath, editProtectionLayer, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const ProtectionLayerLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_PROTECTION_LAYER

  const callProtectionLayersListApi = () => {
    getAdminProtectionLayers(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_PROTECTION_LAYER).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    myDebounce(callProtectionLayersListApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editProtectionLayer(row.protectionLayerId), {state: data});
  };
  const handleHideItem = (e, row) => {
    console.log(row);
    const requestDto = {
      name: row.protectionLayerName,
      polysheets: "PolysheetTypeA",
      linkToTexture: "http://example.com/texture.png",
      diffusionFraction: row.diffusionFraction,
      transmissionPercentage: row.transmissionPercentage,
      voidPercentage: row.voidPercentage,
      opticalProperties: {
        reflectance_PAR: row?.opticalProperty?.reflectionPAR,
        reflectance_NIR: row?.opticalProperty?.reflectionNIR,
        transmissivity_PAR: row?.opticalProperty?.transmissionPAR,
        transmissivity_NIR: row?.opticalProperty?.transmissionNIR
      },
      f1: row.f1,
      f2: row.f2,
      f3: row.f3,
      f4: row.f4,
      hide: !row?.hide
    };
  
    
    const formData = new FormData();
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles',  null); 

    updateAdminProtectionLayer(row.protectionLayerId, formData)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, row) => {
    navigate(viewStaticPage("protection-layer",row.protectionLayerId));
  };
  const handleDeleteItem = (e, row) => {
    navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_NEW);
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
        title="Shade Nets"
        createButtonLabel={"Add Shade Net"}
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

export default ProtectionLayerLandingPage;

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
