import { Box, Button, Stack } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { protectionLayerFormData } from "./protectionLayerFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminProtectionLayer, saveAdminProtectionLayer, updateAdminProtectionLayer } from "../../../api/admin/protectionLayer";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import { DiffuseFraction, Fval1, Fval2, Fval3, Fval4, NIRReflectivity, NIRTransmissivity, PARReflectivity, PARTransmissivity, ProtectionLayerName, Texture, TransmissionPercentage, TransmissonReflectancePAR, VoidPercentage } from "./ProtectionLayerFields";
import { useForm } from "react-hook-form";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const ProtectionLayerForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader, setLoader] = useState(false);
  const pageHeading = id ? "Edit Shade Net" : "Add Shade Net";
  const buttonLabel = id ? "Update" : "Save";

  const [soilOpticsValue, setSoilOpticsValue] = useState();
  const [isSoilOpticsError, setIsSoilOpticsError] = useState(false);
  const [soilOpticsErrorMessage, setSoilOpticsErrorMessage] = useState("");
  const [textureImage, setTextureImage] = useState();
  const [protectionLayerValue, setProtectionLayerValue] = useState();
  const [protectionLayerError, setProtectionLayerError] = useState(false);
  const [protectionLayerErrorMessage, setProtectionLayerErrorMessage] = useState("");
  const [protectionLayerFileUrl, setProtectionLayerFileUrl] = useState();

  const {
    handleSubmit,
    control,
    watch,
    register,
    setValue,
    getValues,
    formState: { errors },
  } = useForm({
    mode: "all",
    defaultValues: {},
  });

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    protectionLayerName: "",
  });
  useEffect(() => {
    if (id) {
      setLoader(true)
      getAdminProtectionLayer(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setFromValueFromResponse(data);
          setTextureImage(data?.linkToTexture);
          setProtectionLayerFileUrl(data?.opticalProperty?.opticalPropertyFile);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);

  const onSubmit = (data) => {

    if (isSoilOpticsError || protectionLayerError) return;
    if (!id && !soilOpticsValue) {
      setSoilOpticsErrorMessage("Required field.");
      setIsSoilOpticsError(true);
      return;
    }
    // if (!id && !protectionLayerValue) {
    //   setProtectionLayerErrorMessage("Required field.");
    //   setProtectionLayerError(true);
    //   return;
    // }
    const requestDto = {
      name: data.protectionLayerName,
      polysheets: "PolysheetTypeA",
      linkToTexture: "http://example.com/texture.png",
      diffusionFraction: data.diffuseFraction,
      transmissionPercentage: data.transmissionPercentage,
      voidPercentage: data.voidPercentage,
      opticalProperties: {
        reflectance_PAR: data.parReflectivity,
        reflectance_NIR: data.nirReflectivity,
        transmissivity_PAR: data.parTransmissivity,
        transmissivity_NIR: data.nirTransmissivity
      },
      f1: data.f_val1,
      f2: data.f_val2,
      f3: data.f_val3,
      f4: data.f_val4,
      hide: !!data.hide,
    };


    const formData = new FormData();
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles', protectionLayerValue ? protectionLayerValue : null);
    formData.append('texture', soilOpticsValue ? soilOpticsValue : null);


    const apiFunction = id ? updateAdminProtectionLayer : saveAdminProtectionLayer;
    const apiParameters = id ? [id, formData] : [formData];

    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE);
  };

  const handleSoilOpticsFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const validFileType = ["png", "jpg"];
      const maxSizeInMB = 2; // Maximum file size (2MB)
      const fileExtension = file.name.split(".").pop().toLowerCase();
      const fileName = file.name.replace(/\.[^/.]+$/, ""); // Extract file name without extension
      const validFileNameRegex = /^[a-zA-Z0-9 _]+$/;

      // Check file type
      if (!validFileType.includes(fileExtension)) {
        setSoilOpticsErrorMessage("Only png and jpg files are allowed.");
        setIsSoilOpticsError(true);
        return;
      }
      // Check file name validity
      if (!validFileNameRegex.test(fileName)) {
        setSoilOpticsErrorMessage("File name can only contain letters, numbers, spaces, and underscores.");
        setIsSoilOpticsError(true);
        return;
      }

      setSoilOpticsValue(file);
      setSoilOpticsErrorMessage("");
      setIsSoilOpticsError(false);
    }
  };

  const handleProtectionLayerFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const validFileType = ["xls", "xlsx"];
      const maxSizeInMB = 2; // Maximum file size (2MB)
      const fileExtension = file.name.split(".").pop().toLowerCase();

      // Check file type
      if (!validFileType.includes(fileExtension)) {
        setProtectionLayerErrorMessage("Only xls and xlsx files are allowed.");
        setProtectionLayerError(true);
        return;
      } else {
        setProtectionLayerValue(file);
        setProtectionLayerErrorMessage("");
        setProtectionLayerError(false);
      }
    }
  };

  const checkSumForPAR = () => {
    const transmissionPAR = parseFloat(getValues("parTransmissivity")) || 0;
    const reflectancePAR = parseFloat(getValues("parReflectivity")) || 0;

    if (transmissionPAR + reflectancePAR > 1) {
      return "Transmission PAR + Reflectance PAR must be ≤ 1";
    } else {
      return true
    }
  }

  const checkSumForNIR = () => {
    const transmissionNIR = parseFloat(getValues("nirTransmissivity")) || 0;
    const reflectanceNIR = parseFloat(getValues("nirReflectivity")) || 0;

    if (transmissionNIR + reflectanceNIR > 1) {
      return "Transmission NIR + Reflectance NIR must be ≤ 1";
    } else {
      return true
    }
  }

  const setFromValueFromResponse = (data) => {
    setValue("protectionLayerName", data.protectionLayerName);
    setValue("polysheets", data.polysheets);
    setValue("diffuseFraction", data.diffusionFraction);
    setValue("transmissionPercentage", data.transmissionPercentage);
    setValue("voidPercentage", data.voidPercentage);
    setValue("f_val1", data.f1);
    setValue("f_val2", data.f2);
    setValue("f_val3", data.f3);
    setValue("f_val4", data.f4);
    setValue("parReflectivity", data.opticalProperty.reflectionPAR);
    setValue("nirReflectivity", data.opticalProperty.reflectionNIR);
    setValue("parTransmissivity", data.opticalProperty.transmissionPAR);
    setValue("nirTransmissivity", data.opticalProperty.transmissionNIR);

    setValue("hide", data?.hide);
  }

  return (
    <Container>
      <AdminBackNavigation title="Back to Shade Net" onClick={handleCancel} />
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer">
          {loader ? (
            <CustomLinearProgress />
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={2}>
                <ProtectionLayerName control={control} errors={errors} />
                <TransmissonReflectancePAR
                  soilOpticsErrorMessage={protectionLayerErrorMessage}
                  isSoilOpticsError={protectionLayerError}
                  handleSoilOpticsFileUpload={handleProtectionLayerFileUpload}
                  control={control}
                  errors={errors}
                  fileUrl={protectionLayerFileUrl}
                />
                <Texture
                  soilOpticsErrorMessage={soilOpticsErrorMessage}
                  isSoilOpticsError={isSoilOpticsError}
                  handleSoilOpticsFileUpload={handleSoilOpticsFileUpload}
                  control={control}
                  errors={errors}
                  textureImage={textureImage}
                />
                <div
                  style={{
                    borderTop: "1px dashed",
                    borderBottom: "1px dashed",
                    padding: "10px 0px",
                  }}
                >
                  <div
                    style={{
                      marginRight: "5px",
                      marginBottom: "4px",
                      marginBottom: "6px",
                      fontWeight: 700,
                      color: '#474F50'
                    }}
                  >
                    PAR Radiation
                  </div>
                  <Stack spacing={2}>
                    <DiffuseFraction control={control} errors={errors} />
                    <TransmissionPercentage control={control} errors={errors} />
                  </Stack>
                </div>
                <VoidPercentage control={control} errors={errors} />
                <div
                  style={{
                    borderTop: "1px dashed",
                    borderBottom: "1px dashed",
                    padding: "10px 0px",
                  }}
                >
                  <div
                    style={{
                      marginRight: "5px",
                      marginBottom: "4px",
                      marginBottom: "6px",
                      fontWeight: 700,
                      color: '#474F50'
                    }}
                  >
                    Material Optical Properties
                  </div>
                  <Stack spacing={2}>
                    <PARReflectivity control={control} errors={errors} checkSumForOpticalProperties={checkSumForPAR} />
                    <PARTransmissivity control={control} errors={errors} checkSumForOpticalProperties={checkSumForPAR} />
                    <NIRReflectivity control={control} errors={errors} checkSumForOpticalProperties={checkSumForNIR} />
                    <NIRTransmissivity control={control} errors={errors} checkSumForOpticalProperties={checkSumForNIR} />
                  </Stack>
                </div>
                <Fval1 control={control} errors={errors} />
                <Fval2 control={control} errors={errors} />
                <Fval3 control={control} errors={errors} />
                <Fval4 control={control} errors={errors} />
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

export default ProtectionLayerForm;

