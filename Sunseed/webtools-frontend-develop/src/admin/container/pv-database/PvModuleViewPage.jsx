import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container } from "../../components/CustomCreatePageCss";
import { Box } from "@mui/material";
import BorderColorIcon from "@mui/icons-material/BorderColor";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import styled from "styled-components";
import CustomTableForView from "../../components/CustomTableForView";
import { getAdminStaticPage } from "../../../api/admin/staticPage";
import { getAdminPvModule, updateAdminPvModule } from "../../../api/admin/pvModule";
import { AppRoutesPath, changeDateFormat, editPvModule } from "../../../utils/constant";
import dayjs from "dayjs";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const PvModuleViewPage = () => {
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
      name: responseData.moduleType,
      length: responseData.longerSide,
      width: responseData.shorterSide,
      hide: !responseData.hide,
      manufacturerName: responseData.manufacturerName,
      moduleName: responseData.moduleName,
      shortcode: responseData.shortcode,
      moduleTech: responseData.moduleTech,
      linkToDataSheet: "http://example.com/datasheet",
      numCellX: responseData.numCellX,
      numCellY: responseData.numCellY,
      longerSide: responseData.longerSide,
      shorterSide: responseData.shorterSide,
      thickness: responseData.thickness,
      voidRatio: responseData.voidRatio,
      xcell: responseData.xcell,
      ycell: responseData.ycell,
      xcellGap: responseData.xcellGap,
      ycellGap: responseData.ycellGap,
      vmap: responseData.vmap,
      imap: responseData.imap,
      idc0: responseData.idc0,
      pdc0: responseData.pdc0,
      neffective: responseData.neffective,
      voc: responseData.voc,
      isc: responseData.isc,
      alphaSc: responseData.alphaSc,
      betaVoc: responseData.betaVoc,
      gammaPdc: responseData.gammaPdc,
      temRef: responseData.temRef,
      radSun: responseData.radSun,
      opticalProperties: [
        {
          type: "front",
          reflectance_PAR: 0.0,
          reflectance_NIR: responseData?.frontOpticalProperty?.reflectionNIR,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        },
        {
          type: "back",
          reflectance_PAR: 0.0,
          reflectance_NIR: responseData.backOpticalProperty?.reflectionNIR,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        }
      ],
      f1: responseData.f1,
      f2: responseData.f2,
      f3: responseData.f3,
      f4: responseData.f4,
      f5: responseData.f5,
    };
    const formData = new FormData();

    formData.append("opticalFiles",  null);
    formData.append('opticalFiles',  null);
    formData.append('requestDto', JSON.stringify(requestDto));

    updateAdminPvModule(responseData.id, formData)
      .then((response) => {
        setIsHide(response?.data?.data?.hide)
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleEditItem = ()=>{
    navigate(editPvModule(id), {state: responseData});
  }


  const createDataSet = (data) => {
    return [
      {"Module Type": data["moduleType"]},
      // {"Length": data["length"]},
      // {"Width": data["width"]},
      {"Manufacturer Name": data["manufacturerName"]},
      {"Module Name": data["moduleName"]},
      {"Shortcode": data["shortcode"]},
      {"Module Tech": data["moduleTech"]},
      {"Link to Data Sheet": data["linkToDataSheet"]},
      {"Num Cell X": data["numCellX"]},
      {"Num Cell Y": data["numCellY"]},
      {"Longer Side": data["longerSide"]},
      {"Shorter Side": data["shorterSide"]},
      {"Thickness": data["thickness"]},
      {"Void Ratio": data["voidRatio"]},
      {"PDC0": data["pdc0"]},
      {"Alpha Sc": data["alphaSc"]},
      {"Beta Voc": data["betaVoc"]},
      {"Gamma PDC": data["gammaPdc"]},
      {"TEM Ref": data["temRef"]},
      {"Rad Sun": data["radSun"]},
      {"Front Optical Property File": data["frontOpticalProperty"]?.opticalPropertyFile},
      {"Front Optical Property Transmission NIR": data["frontOpticalProperty"]?.transmissionNIR},
      {"Front Optical Property Reflection NIR": data["frontOpticalProperty"]?.reflectionNIR},
      {"Front Optical Property Transmission PAR": data["frontOpticalProperty"]?.transmissionPAR},
      {"Front Optical Property Reflection PAR": data["frontOpticalProperty"]?.reflectionPAR},
      {"Back Optical Property File": data["backOpticalProperty"]?.opticalPropertyFile},
      {"Back Optical Property Transmission NIR": data["backOpticalProperty"]?.transmissionNIR},
      {"Back Optical Property Reflection NIR": data["backOpticalProperty"]?.reflectionNIR},
      {"Back Optical Property Transmission PAR": data["backOpticalProperty"]?.transmissionPAR},
      {"Back Optical Property Reflection PAR": data["backOpticalProperty"]?.reflectionPAR},
      {"F1": data["f1"]},
      {"F2": data["f2"]},
      {"F3": data["f3"]},
      {"F4": data["f4"]},
      {"F5": data["f5"]},
      {"Is Active": data["isActive"]},
      {"Created At": changeDateFormat(dayjs.utc(data["createdAt"]).tz(dayjs.tz.guess()))},
      {"Updated At": changeDateFormat(dayjs.utc(data["updatedAt"]).tz(dayjs.tz.guess()))},
      {"Hide": data["hide"]},
      {"X Cell": data["xcell"]},
      {"Y Cell": data["ycell"]},
      {"X Cell Gap": data["xcellGap"]},
      {"Y Cell Gap": data["ycellGap"]},
      {"V mp": data["vmap"]},
      {"I mp": data["imap"]},
      {"N Effective": data["neffective"]},
      {"VOC": data["voc"]},
      {"ISC": data["isc"]}
    ];
  };
  

  useEffect(() => {
    if(id){
      setLoader(true)
      getAdminPvModule(id)
        .then((response) => {
          const data = createDataSet(response?.data?.data);
          setLoader(false);
          setData(data);
          setResponseData(response?.data?.data);
          setIsHide(response?.data?.data?.hide);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id,isHide]);

  const handleCancel = () => {
    navigate(AppRoutesPath.ADMIN_PV_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to PV Modules" onClick={handleCancel} />
      <Wrapper className="wrapper">
        <Box className="titleWrapper">

        <Box className="title" style={{flex:1}} >PV Module</Box>

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

export default PvModuleViewPage;

const Wrapper = styled.div`
    .icons {
    cursor: pointer;
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 20px;
    margin-right: 14px;
  }
`
