import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { AppRoutesPath, changeDateFormat, editModeOfPvOperation, editModuleConfiguration, editProtectionLayer, editPvModule } from "../../../utils/constant";
import dayjs from "dayjs";
import { getAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import { getAdminModuleConfiguration, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import { getAdminProtectionLayer, updateAdminProtectionLayer } from "../../../api/admin/protectionLayer";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const ProtectionLayerViewPage = () => {
    const navigate = useNavigate();
  const { id } = useParams();
  const [isHide,setIsHide] = useState(false);
  const [loader,setLoader] = useState(false);
  const [responseData,setResponseData] = useState();
  const [data,setData] = useState();

  const EyeIcon = ({ row }) => (
    isHide ? <VisibilityOffIcon className="icons"  onClick={(e) => handleHideItem(e, row)} /> :
      <VisibilityIcon className="icons"  onClick={(e) => handleHideItem(e, row)} />
  )



  const handleHideItem = (e) => {
    const requestDto = {
      name: responseData.protectionLayerName,
      polysheets: "PolysheetTypeA",
      linkToTexture: "http://example.com/texture.png",
      diffusionFraction: responseData.diffusionFraction,
      transmissionPercentage: responseData.transmissionPercentage,
      voidPercentage: responseData.voidPercentage,
      opticalProperties: {
        reflectance_PAR: responseData?.opticalProperty?.reflectionPAR,
        reflectance_NIR: responseData?.opticalProperty?.reflectionNIR,
        transmissivity_PAR: responseData?.opticalProperty?.transmissionPAR,
        transmissivity_NIR: responseData?.opticalProperty?.transmissionNIR
      },
      f1: responseData.f1,
      f2: responseData.f2,
      f3: responseData.f3,
      f4: responseData.f4,
      hide: !responseData?.hide
    };
  
    
    const formData = new FormData();
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles', null); 
    formData.append('texture', null); 
    updateAdminProtectionLayer(id, formData)
      .then((response) => {
        setIsHide(response?.data?.data?.hide)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editProtectionLayer(id), {state: responseData});
  }


  const createDataSet = (data) => {
    return [
      {"Name": data["protectionLayerName"]},
      {"Polysheets": data["polysheets"]},
      {"Link To Texture": data["linkToTexture"]},
      {"Diffusion Fraction": data["diffusionFraction"]},
      {"Transmission Percentage": data["transmissionPercentage"]},
      {"Void Percentage": data["voidPercentage"]},
      
      // Optical Properties
      {"Transmission NIR": data["opticalProperty"]?.["transmissionNIR"]},
      {"Reflection NIR": data["opticalProperty"]?.["reflectionNIR"]},
      {"Transmission PAR": data["opticalProperty"]?.["transmissionPAR"]},
      {"Reflection PAR": data["opticalProperty"]?.["reflectionPAR"]},
      {"Optical Property File": data["opticalProperty"]?.["opticalPropertyFile"]},
      
      {"F1": data["f1"]},
      {"F2": data["f2"]},
      {"F3": data["f3"]},
      {"F4": data["f4"]},
      {"Is Active": data["isActive"]},
      {"Hide": data["hide"]},
      
      // Date fields
      {"Created At": data["createdAt"] ? changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess())) : null},
      {"Updated At": data["updatedAt"] ? changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess())) : null},
    ];
  };
  

  useEffect(() => {
    if(id){
      setLoader(true)
      
      getAdminProtectionLayer(id)
        .then((response) => {
          setLoader(false);
        setData(createDataSet(response?.data?.data));
        setResponseData(response?.data?.data);
        setIsHide(response?.data?.data?.hide);
      })
      .catch((error) => {
        console.log(error);
      });
    }
  }, [id,isHide]);

  const handleCancel = () => {
    navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Shade Net" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >Shade Net</Box>

         <EyeIcon  />
        <BorderColorIcon className="icons" onClick={(e) => handleEditItem(e)} /> 
        </Box>

        <Box>
            <CustomTableForView  data={data} />
        </Box>
      </Wrapper>
    </Container>
  );
};

export default ProtectionLayerViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`
