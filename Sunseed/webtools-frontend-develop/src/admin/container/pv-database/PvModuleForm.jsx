import { Box, Button, Stack } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled, { useTheme } from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { pvModuleFormData } from "./pvModuleFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminPvModule, saveAdminPvModule, updateAdminPvModule } from "../../../api/admin/pvModule";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import { useForm } from "react-hook-form";
import AdminBackNavigation from "../../components/AdminBackNavigation";
import {
  AlphaScField,
  BetaVocField,
  F1Field,
  F2Field,
  F3Field,
  F4Field,
  F5Field,
  GammaPdcField,
  IMapField,
  Idc0Field,
  IscField,
  LongerSideField,
  ManufacturerName,
  ModuleTech,
  ModuleTypeField,
  NEffectiveField,
  NumCellXField,
  NumCellYField,
  OpticalPropertiesFields,
  Pdc0Field,
  RadSunField,
  ShortCode,
  ShorterSideField,
  TemRefField,
  ThicknessField,
  VMapField,
  VocField,
  VoidRatioField,
  XCellField,
  XCellGapField,
  YCellField,
  YCellGapField,
} from "./PvModuleFields";

const PvModuleForm = () => {
  const theme = useTheme();
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader, setLoader] = useState(false);
  const pageHeading = id ? "Edit PV Module" : "Add PV Module";
  const buttonLabel = id ? "Update" : "Save";

  const [data, setData] = useState();
  const [opticalPropertyFrontEndValue, setOpticalPropertyFrontEndValue] =
    useState();
  const [opticalPropertyBackEndValue, setOpticalPropertyBackEndValue] =
    useState();
  const [frontReflectanceError, setFrontReflectanceError] = useState(false);
  const [frontReflectanceErrorMessage, setFrontReflectanceErrorMessage] =
    useState("");
  const [backReflectanceError, setBackReflectanceError] = useState(false);
  const [backReflectanceErrorMessage, setBackReflectanceErrorMessage] =
    useState("");
  const [frontFileUrl, setFrontFileUrl] = useState();
  const [backFileUrl, setBackFileUrl] = useState();


  const {
    handleSubmit,
    control,
    watch,
    register,
    setValue,
    formState: { errors },
  } = useForm({
    mode: "all",
    defaultValues: {
      fileUpload: "",
    },
  });

  const handleFrontFileUpload = (e) => {
    const file = e.target.files[0];
    const fileExtension = file.name.split('.')[1];

    const renamedFile = new File([file], `front.${fileExtension}`, { type: file.type });
    if (renamedFile) {
      const validFileType = ["xlsx", "xls"];
      const maxSizeInMB = 2; // Maximum renamedFile size (2MB)
      const fileExtension = renamedFile.name.split(".").pop().toLowerCase();

      // Check renamedFile type
      if (!validFileType.includes(fileExtension)) {
        setFrontReflectanceErrorMessage("Only xls and xlsx files are allowed.");
        setFrontReflectanceError(true);
        return;
      } else {
        setOpticalPropertyFrontEndValue(renamedFile);
        setFrontReflectanceErrorMessage("");
        setFrontReflectanceError(false);
      }
    }
  };
  const handleBackFileUpload = (e) => {
    const file = e.target.files[0];
    const fileExtension = file.name.split('.')[1];

    const renamedFile = new File([file], `back.${fileExtension}`, { type: file.type });
    if (renamedFile) {
      const validFileType = ["xlsx", "xls"];
      const maxSizeInMB = 2; // Maximum file size (2MB)
      const fileExtension = renamedFile.name.split(".").pop().toLowerCase();

      // Check file type
      if (!validFileType.includes(fileExtension)) {
        setBackReflectanceErrorMessage("Only xls and xlsx files are allowed.");
        setBackReflectanceError(true);
        return;
      } else {
        setOpticalPropertyBackEndValue(renamedFile);
        setBackReflectanceErrorMessage("");
        setBackReflectanceError(false);
      }
    }
  };

  useEffect(() => {
    if (id) {
      setLoader(true);
      getAdminPvModule(id)
        .then((response) => {
          const data = response.data.data;
          createDataSet(data);
          setFrontFileUrl(data?.frontOpticalProperty?.opticalPropertyFile)
          setBackFileUrl(data?.backOpticalProperty?.opticalPropertyFile)
          setLoader(false);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);

  const createDataSet = (response) => {
    setValue("manufacturerName", response.manufacturerName);
    setValue("shortCode", response.shortcode);
    setValue("moduleType", response.moduleType);
    setValue("moduleTech", response.moduleTech);
    setValue("numCellX", response.numCellX);
    setValue("numCellY", response.numCellY);
    setValue("longerSide", response.longerSide);
    setValue("shorterSide", response.shorterSide);
    setValue("thickness", response.thickness);
    setValue("voidRatio", response.voidRatio);
    // setValue("idc0", response.idc0);
    setValue("pdc0", response.pdc0);
    setValue("alphaSc", response.alphaSc);
    setValue("betaVoc", response.betaVoc);
    setValue("gammaPdc", response.gammaPdc);
    setValue("temRef", response.temRef);
    setValue("radSun", response.radSun);
    setValue("f1", response.f1);
    setValue("f2", response.f2);
    setValue("f3", response.f3);
    setValue("f4", response.f4);
    setValue("f5", response.f5);
    setValue("xcell", response.xcell);
    setValue("ycell", response.ycell);
    setValue("xcellGap", response.xcellGap);
    setValue("ycellGap", response.ycellGap);
    setValue("vmap", response.vmap);
    setValue("imap", response.imap);
    setValue("neffective", response.neffective);
    setValue("voc", response.voc);
    setValue("isc", response.isc);
    setValue("frontReflectance", response?.frontOpticalProperty?.reflectionNIR);
    setValue("backReflectance", response?.backOpticalProperty?.reflectionNIR);
    setValue("hide", response.hide);
  };

  const onSubmit = (data) => {
    if (frontReflectanceError || backReflectanceError) return;
    if (!id && !opticalPropertyFrontEndValue) {
      setFrontReflectanceErrorMessage("Required field.");
      setFrontReflectanceError(true);
      if (!id && !opticalPropertyBackEndValue) {
        setBackReflectanceErrorMessage("Required field.");
        setBackReflectanceError(true);
      }
      return;
    }
    if (!id && !opticalPropertyBackEndValue) {
      setBackReflectanceErrorMessage("Required field.");
      setBackReflectanceError(true);
      return;
    }


    const requestDto = {
      name: data.moduleType,
      length: data.longerSide,
      width: data.shorterSide,
      hide: !!data?.hide,
      manufacturerName: data.manufacturerName,
      moduleName: data.moduleTech,
      shortcode: data.shortCode,
      moduleTech: data.moduleTech,
      linkToDataSheet: "http://example.com/datasheet",
      numCellX: data.numCellX,
      numCellY: data.numCellY,
      longerSide: data.longerSide,
      shorterSide: data.shorterSide,
      thickness: data.thickness,
      voidRatio: data.voidRatio,
      xcell: data.xcell,
      ycell: data.ycell,
      xcellGap: data.xcellGap,
      ycellGap: data.ycellGap,
      vmap: data.vmap,
      imap: data.imap,
      // idc0: data.idc0,
      pdc0: data.pdc0,
      neffective: data.neffective,
      voc: data.voc,
      isc: data.isc,
      alphaSc: data.alphaSc,
      betaVoc: data.betaVoc,
      gammaPdc: data.gammaPdc,
      temRef: data.temRef,
      radSun: data.radSun,
      opticalProperties: [
        {
          type: "front",
          reflectance_PAR: 0.0,
          reflectance_NIR: data.frontReflectance,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        },
        {
          type: "back",
          reflectance_PAR: 0.0,
          reflectance_NIR: data.backReflectance,
          transmissivity_PAR: 0.0,
          transmissivity_NIR: 0.0,
        }
      ],
      f1: data.f1,
      f2: data.f2,
      f3: data.f3,
      f4: data.f4,
      f5: data.f5,
    };
    const formData = new FormData();

    formData.append("opticalFiles", opticalPropertyFrontEndValue ? opticalPropertyFrontEndValue : null);
    formData.append('opticalFiles', opticalPropertyBackEndValue ? opticalPropertyBackEndValue : null);
    formData.append('requestDto', JSON.stringify(requestDto));

    const apiFunction = id ? updateAdminPvModule : saveAdminPvModule;
    const apiParameters = id ? [id, formData] : [formData];

    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_PV_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    navigate(AppRoutesPath.ADMIN_PV_DATABASE);
  };

  const dataSet = [
    { label: "TOPCON", value: "TOPCON" },
    { label: "HJT", value: "HJT" },
    { label: "PERC", value: "PERC" },
    { label: "IBC", value: "IBC" },
    { label: "OTHER", value: "OTHER" },
    { label: "FUTURETECH1", value: "FUTURETECH1" },
    { label: "FUTURETECH2", value: "FUTURETECH2" },
  ];


  return (
    <Container>
      <AdminBackNavigation title="Back to PV Modules" onClick={handleCancel} />
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer">
          {loader ? (
            <CustomLinearProgress />
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={2}>
                <ManufacturerName control={control} errors={errors} />
                <ModuleTypeField control={control} errors={errors} />
                <ModuleTech control={control} errors={errors} dataSet={dataSet} />
                <ShortCode control={control} errors={errors} />
                <NumCellXField control={control} errors={errors} />
                <NumCellYField control={control} errors={errors} />
                <LongerSideField control={control} errors={errors} />
                <ShorterSideField control={control} errors={errors} />
                <ThicknessField control={control} errors={errors} />
                <VoidRatioField control={control} errors={errors} />
                {/* <Idc0Field control={control} errors={errors} /> */}
                <Pdc0Field control={control} errors={errors} />
                <AlphaScField control={control} errors={errors} />
                <BetaVocField control={control} errors={errors} />
                <GammaPdcField control={control} errors={errors} />
                <TemRefField control={control} errors={errors} />
                <RadSunField control={control} errors={errors} />
                <OpticalPropertiesFields
                  handleFrontFileUpload={handleFrontFileUpload}
                  handleBackFileUpload={handleBackFileUpload}
                  control={control}
                  errors={errors}
                  frontReflectanceError={frontReflectanceError}
                  frontReflectanceErrorMessage={frontReflectanceErrorMessage}
                  backReflectanceError={backReflectanceError}
                  backReflectanceErrorMessage={backReflectanceErrorMessage}
                  frontFileUrl={frontFileUrl}
                  backFileUrl={backFileUrl}
                />
                <F1Field control={control} errors={errors} />
                <F2Field control={control} errors={errors} />
                <F3Field control={control} errors={errors} />
                <F4Field control={control} errors={errors} />
                <F5Field control={control} errors={errors} />
                {/* <HideField control={control} errors={errors} /> */}
                <XCellField control={control} errors={errors} />
                <YCellField control={control} errors={errors} />
                <XCellGapField control={control} errors={errors} />
                <YCellGapField control={control} errors={errors} />
                <VMapField control={control} errors={errors} />
                <IMapField control={control} errors={errors} />
                <NEffectiveField control={control} errors={errors} />
                <IscField control={control} errors={errors} />
                <VocField control={control} errors={errors} />

                <div
                  style={{
                    display: "flex",
                    justifyContent: "flex-end",
                    marginBottom: "26px",
                  }}
                >
                  <Button
                    variant="contained"
                    className="prevButton"
                    data-testid="submitButton"
                    onClick={handleCancel}
                  >
                    Cancel
                  </Button>

                  <Button
                    type="submit"
                    variant="contained"
                    data-testid="submitButton"
                    className="submitButton"
                  >
                    {buttonLabel}
                  </Button>
                </div>
              </Stack>
            </form>
          )}
        </Box>
      </Box>
    </Container>
  );
};

export default PvModuleForm;

const Gap = styled.div`
  padding: 10px;
`;
