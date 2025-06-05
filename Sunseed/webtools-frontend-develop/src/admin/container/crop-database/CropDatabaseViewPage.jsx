import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { AppRoutesPath, changeDateFormat, editCrop, editModeOfPvOperation, editPvModule } from "../../../utils/constant";
import dayjs from "dayjs";
import { getAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import { getAdminCrop, updateAdminCrop } from "../../../api/admin/crop";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const CropDatabaseViewPage = () => {
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
      name: responseData?.name,
      vcMax: responseData?.farquharParameter?.vcMax,
      jmax: responseData?.farquharParameter?.jmax,
      cjMax: responseData?.farquharParameter?.cjMax,
      hajMax: responseData?.farquharParameter?.haJMax,
      alpha: responseData?.farquharParameter?.alpha,
      rd25: responseData?.farquharParameter?.rd25,
      em: responseData?.stomatalParameter?.em,
      io: responseData?.stomatalParameter?.io,
      k: responseData?.stomatalParameter?.k,
      b: responseData?.stomatalParameter?.b,
      requiredDLI: responseData?.requiredDLI,
      requiredPPFD: responseData?.requiredPPFD,
      harvestDays: responseData?.harvestDays,
      f1: responseData?.f1,
      f2: responseData?.f2,
      f3: responseData?.f3,
      f4: responseData?.f4,
      f5: responseData?.f5,
      minStage: responseData?.minStage,
      maxStage: responseData?.maxStage,
      duration: responseData?.harvestDays,
      hasPlantActualDate: !!responseData?.hasPlantActualDate,
      plantActualStartDate: !!responseData?.hasPlantActualDate && responseData?.plantActualStartDate ? responseData?.plantActualStartDate : null,
      plantMaxAge: responseData?.plantMaxAge,
      maxPlantsPerBed: responseData?.maxPlantsPerBed,
      hide: !responseData.hide,
      opticalProperties: {
        reflectance_PAR: 0.0,
        reflectance_NIR: responseData?.opticalProperty?.reflectionNIR,
        transmissivity_PAR: 0.0,
        transmissivity_NIR: responseData?.opticalProperty?.transmissionNIR
      }
    };
    const formData = new FormData();
    
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles',  null); // Add the file
    
    
    updateAdminCrop(responseData.id, formData)
      .then((response) => {
        setIsHide(response?.data?.data?.hide)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editCrop(id), {state: responseData});
  }

  const createDataSet = (data) => {
    return [
      {"Crop Name": data["name"]},
      {"Crop Label": data["cropLabel"]},
      {"Required DLI": data["requiredDLI"]},
      {"Required PPFD": data["requiredPPFD"]},
      {"Harvest Days": data["harvestDays"]},
      {"F1": data["f1"]},
      {"F2": data["f2"]},
      {"F3": data["f3"]},
      {"F4": data["f4"]},
      {"Min S1(Spacing b/w crops)": data["f5"]},
      {"Min Stage": data["minStage"]},
      {"Max Stage": data["maxStage"]},
      // {"Duration": data["harvestDays"]},
      {"Is Active": data["isActive"]},
      {"Hide": data["hide"]},
      {"Created At": changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess()))},
      {"Updated At": changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess()))},
      
      // Optical Properties
      {"Transmission NIR": data["opticalProperty"]?.["transmissionNIR"]},
      {"Reflection NIR": data["opticalProperty"]?.["reflectionNIR"]},
      {"Transmission PAR": data["opticalProperty"]?.["transmissionPAR"]},
      {"Reflection PAR": data["opticalProperty"]?.["reflectionPAR"]},
      {"Optical Property File": data["opticalProperty"]?.["opticalPropertyFile"]},
      
      // Stomatal Parameters
      {"Em": data["stomatalParameter"]?.["em"]},
      {"Io": data["stomatalParameter"]?.["io"]},
      {"K": data["stomatalParameter"]?.["k"]},
      {"B": data["stomatalParameter"]?.["b"]},
      
      // Farquhar Parameters
      {"VcMax": data["farquharParameter"]?.["vcMax"]},
      {"CjMax": data["farquharParameter"]?.["cjMax"]},
      {"HaJMax": data["farquharParameter"]?.["haJMax"]},
      {"Alpha": data["farquharParameter"]?.["alpha"]},
      {"Rd25": data["farquharParameter"]?.["rd25"]},
      {"Jmax": data["farquharParameter"]?.["jmax"]},

      {"Plant has Actual Start Date?": data["hasPlantActualDate"]},
      ...(data["hasPlantActualDate"] ? [{ "Plant Start Date": data["plantActualStartDate"] }] : []),
      {"Max Plant Age": data["plantMaxAge"]},
      {"Min Plants count in a bed": data["maxPlantsPerBed"]},
    ];
  };
  


  useEffect(() => {
    if(id){
      setLoader(true)
      
      getAdminCrop(id)
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
    navigate(AppRoutesPath.ADMIN_CROP_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Crop" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >Crops</Box>

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

export default CropDatabaseViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`
