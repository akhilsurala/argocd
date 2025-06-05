import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { AppRoutesPath, changeDateFormat, editModeOfPvOperation, editModuleConfiguration, editProtectionLayer, editPvModule, editSoil } from "../../../utils/constant";
import dayjs from "dayjs";
import { getAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import { getAdminModuleConfiguration, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import { getAdminProtectionLayer, updateAdminProtectionLayer } from "../../../api/admin/protectionLayer";
import { getAdminSoil, updateAdminSoil } from "../../../api/admin/soil";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const SoilDatabaseViewPage = () => {
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
      name: responseData.soilName,
      hide: !responseData.hide,
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

    updateAdminSoil(responseData.id, formData)
      .then((response) => {
        setIsHide(response?.data?.data?.hide)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editSoil(id), {state: responseData});
  }


  const createDataSet = (data) => {
    return [
      {"Name": data["soilName"]},  
      {"Created At": data["createdAt"] ? changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess())) : null },
      {"Updated At": data["updatedAt"] ? changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess())) : null },
  
      // Optical Properties
      {"Transmission NIR": data?.["opticalProperty"]?.["transmissionNIR"]},
      {"Reflection NIR": data?.["opticalProperty"]?.["reflectionNIR"]},
      {"Transmission PAR": data?.["opticalProperty"]?.["transmissionPAR"]},
      {"Reflection PAR": data?.["opticalProperty"]?.["reflectionPAR"]},
      {"Optical Property File": data?.["opticalProperty"]?.["opticalPropertyFile"]},
      
      {"Is Active": data["isActive"]},
      {"Hide": data["hide"]},
    ];
  };
  

  useEffect(() => {
    if(id){
      setLoader(true)
      
      getAdminSoil(id)
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
    navigate(AppRoutesPath.ADMIN_SOIL_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Soil" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >Soil</Box>

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

export default SoilDatabaseViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`
