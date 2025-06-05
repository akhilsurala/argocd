import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { AppRoutesPath, changeDateFormat, editModeOfPvOperation, editModuleConfiguration, editPvModule, editTypeOfIrrigation, editUser } from "../../../utils/constant";
import dayjs from "dayjs";
import { getAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import { getAdminModuleConfiguration, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import { getAdminTypeOfIrrigation, updateAdminTypeOfIrrigation } from "../../../api/admin/typeOfIrrigation";
import { blockAdminUser, getAdminUser } from "../../../api/admin/user";
import blockUser from "../../../assets/icons/blockUser.svg"
import unBlockUser from "../../../assets/icons/unBlockUser.svg"
import AdminBackNavigation from "../../components/AdminBackNavigation";

const UserDatabaseViewPage = () => {
    const navigate = useNavigate();
  const { id } = useParams();
  const [isHide,setIsHide] = useState(false);
  const [loader,setLoader] = useState(false);
  const [responseData,setResponseData] = useState();
  const [data,setData] = useState();

  
  const BlockIcon = ({ row }) => (
    !isHide ? 
        <img src={blockUser} alt="icon" className="icons" onClick={(e) => handleBlockItem(e, row) } /> :
      <img src={unBlockUser} alt="icon" className="icons" onClick={(e) => handleBlockItem(e, row) } />
  )


  
  const handleBlockItem = (e) => {
    const payload = {
      id: responseData.id,
      isActive: !responseData.isActive,
    }
    blockAdminUser(responseData.id, payload)
      .then((response) => {
        setIsHide(response?.data?.data?.isActive)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editUser(id), {state: responseData});
  }


  const createDataSet = (data)=>{
  return [
    {"First Name": data["firstName"]},
    {"Last Name": data["lastName"]},
    {"Email": data["emailId"]},
    {"Type": data["roles"][0]},
    {"Created At": data["createdAt"] ? changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess())) : null },
    {"Updated At": data["updatedAt"] ? changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess())) : null },
  ]

}

  useEffect(() => {
    if(id){
      setLoader(true)
      
      getAdminUser(id)
        .then((response) => {
          setLoader(false);
        setData(createDataSet(response?.data?.data));
        setResponseData(response?.data?.data);
        setIsHide(response?.data?.data?.isActive);
      })
      .catch((error) => {
        console.log(error);
      });
    }
  }, [id,isHide]);

  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_USER_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Users" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >User</Box>
        <BlockIcon />
        <BorderColorIcon className="icons" onClick={(e) => handleEditItem(e)} /> 
        </Box>

        <Box>
            <CustomTableForView  data={data} />
        </Box>
      </Wrapper>
    </Container>
  );
};

export default UserDatabaseViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.secondary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`