import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { AppRoutesPath, changeDateFormat, editModeOfPvOperation, editModuleConfiguration, editPvModule, editTypeOfIrrigation } from "../../../utils/constant";
import dayjs from "dayjs";
import { getAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import { getAdminModuleConfiguration, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import { getAdminTypeOfIrrigation, updateAdminTypeOfIrrigation } from "../../../api/admin/typeOfIrrigation";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const TypeOfIrrigationViewPage = () => {
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
    const payload = {
      id: responseData.id,
      name: responseData.irrigationType,
      hide: !responseData.hide,
    }
    updateAdminTypeOfIrrigation(responseData.id, payload)
      .then((response) => {
        setIsHide(response?.data?.data?.hide)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editTypeOfIrrigation(id), {state: responseData});
  }


  const createDataSet = (data)=>{
  return [
    {"Name": data["irrigationType"]},
    {"Created At": data["createdAt"] ? changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess())) : null },
    {"Updated At": data["updatedAt"] ? changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess())) : null },
  ]

}

  useEffect(() => {
    if(id){
      setLoader(true)
      
      getAdminTypeOfIrrigation(id)
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
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Type Of Irrigation" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >Irrigation Type</Box>

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

export default TypeOfIrrigationViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.secondary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`
