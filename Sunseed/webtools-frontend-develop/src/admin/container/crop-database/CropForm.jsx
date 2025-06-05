import { Box, Button, Stack } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";

import dayjs from "dayjs";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { cropFormData } from "./cropFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import {
  getAdminCrop,
  saveAdminCrop,
  updateAdminCrop,
} from "../../../api/admin/crop";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import {
  Alpha,
  BField,
  CJMax,
  CropLabel,
  CropName,
  Duration,
  Em,
  Fval1,
  Fval2,
  Fval3,
  Fval4,
  Fval5,
  HaJMax,
  HarvestLifespan,
  HasPlantStartDateSwitch,
  I0,
  JMax,
  KField,
  MaxStage,
  MinStage,
  PlantMaxAge,
  PlantPerBed,
  PlantStartDate,
  Rd,
  ReflectanceNIR,
  ReflectancePAR,
  RequiredDLI,
  RequiredPPFD,
  TransmissionNIR,
  TransmissionPAR,
  TransmissonReflectancePAR,
  VcMax,
} from "./CropParametersFields";
import { useForm } from "react-hook-form";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const CropForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader, setLoader] = useState(false);
  const pageHeading = id ? "Edit Crop" : "Add Crop";
  const buttonLabel = id ? "Update" : "Save";

  const [soilOpticsValue, setSoilOpticsValue] = useState();
  const [isSoilOpticsError, setIsSoilOpticsError] = useState(false);
  const [soilOpticsErrorMessage, setSoilOpticsErrorMessage] = useState("");
  const [fileUrl, setFileUrl] = useState();
  const [startDateVal, setStartDateValue] = useState("");

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    name: "",
  });

  const {
    handleSubmit,
    control,
    watch,
    register,
    setValue,
    getValues,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm({
    mode: "all",
    defaultValues: {},
  });

  const watchFields = watch(["hasPlantActualDate"]);

  useEffect(() => {
    if (id) {
      setLoader(true);
      getAdminCrop(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setFormValuesFromResponse(data);
          setFileUrl(data?.opticalProperty?.opticalPropertyFile)
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);

  const onSubmit = (data) => {
    if (!!data?.hasPlantActualDate && !dayjs(startDateVal).isValid()) {
      setError("plantActualStartDate", { type: "manual", message: "Invalid Date" });
      return;
    }

    if (isSoilOpticsError) return;
    if (!id && !soilOpticsValue) {
      setSoilOpticsErrorMessage("Required field.");
      setIsSoilOpticsError(true);
      return;
    }
    const requestDto = {
      name: data?.cropName,
      cropLabel: data?.cropLabel,
      vcMax: data?.vcMax,
      jmax: data?.jMax,
      cjMax: data?.cjMax,
      hajMax: data?.hajMax,
      alpha: data?.alpha,
      rd25: data?.rd,
      em: data?.em,
      io: data?.i0,
      k: data?.k,
      b: data?.b,
      requiredDLI: data?.requiredDLI,
      requiredPPFD: data?.requiredPPFD,
      harvestDays: data?.harvestLifespan,
      f1: data?.f_val1,
      f2: data?.f_val2,
      f3: data?.f_val3,
      f4: data?.f_val4,
      f5: data?.f_val5,
      minStage: data?.minStage,
      maxStage: data?.maxStage,
      duration: data?.harvestLifespan,
      hasPlantActualDate: !!data?.hasPlantActualDate,
      plantActualStartDate: !!data?.hasPlantActualDate && startDateVal ? dayjs(startDateVal).format("MMMM D") : null,
      plantMaxAge: data?.plantMaxAge,
      maxPlantsPerBed: data?.maxPlantsPerBed,
      hide: !!data.hide,
      opticalProperties: {
        reflectance_PAR: data?.reflectancePAR,
        reflectance_NIR: data?.reflectanceNIR,
        transmissivity_PAR: data?.transmissionPAR,
        transmissivity_NIR: data?.transmissionNIR
      }
    };
    const formData = new FormData();

    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles', soilOpticsValue ? soilOpticsValue : null); // Add the file



    const apiFunction = id ? updateAdminCrop : saveAdminCrop;
    const apiParameters = id ? [id, formData] : [formData];

    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_CROP_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    navigate(AppRoutesPath.ADMIN_CROP_DATABASE);
  };

  const handleSoilOpticsFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const validFileType = ["xlsx", "xls"];
      const maxSizeInMB = 2; // Maximum file size (2MB)
      const fileExtension = file.name.split(".").pop().toLowerCase();

      // Check file type
      if (!validFileType.includes(fileExtension)) {
        setSoilOpticsErrorMessage("Only xls and xlsx files are allowed.");
        setIsSoilOpticsError(true);
        return;
      } else {
        setSoilOpticsValue(file);
        setSoilOpticsErrorMessage("");
        setIsSoilOpticsError(false);
      }
    }
  };

  const checkSumForPAR = () => {
    const transmissionPAR = parseFloat(getValues("transmissionPAR")) || 0;
    const reflectancePAR = parseFloat(getValues("reflectancePAR")) || 0;

    if (transmissionPAR + reflectancePAR > 1) {
      return "Transmission PAR + Reflectance PAR must be ≤ 1";
    } else {
      return true
    }
  }

  const checkSumForNIR = () => {
    const transmissionNIR = parseFloat(getValues("transmissionNIR")) || 0;
    const reflectanceNIR = parseFloat(getValues("reflectanceNIR")) || 0;

    if (transmissionNIR + reflectanceNIR > 1) {
      return "Transmission NIR + Reflectance NIR must be ≤ 1";
    } else {
      return true
    }
  }

  const setFormValuesFromResponse = (data) => {
    setValue("cropName", data?.name);
    setValue("cropLabel", data?.cropLabel);
    setValue("requiredDLI", data?.requiredDLI);
    setValue("requiredPPFD", data?.requiredPPFD);
    setValue("harvestLifespan", data?.harvestDays);
    setValue("f_val1", data?.f1);
    setValue("f_val2", data?.f2);
    setValue("f_val3", data?.f3);
    setValue("f_val4", data?.f4);
    setValue("f_val5", data?.f5);
    setValue("minStage", data?.minStage);
    setValue("maxStage", data?.maxStage);
    setValue("duration", data?.harvestDays);

    setValue("isActive", data?.isActive);
    setValue("hide", data?.hide);
    setValue("createdAt", data?.createdAt);
    setValue("updatedAt", data?.updatedAt);

    // Setting values for nested objects (opticalProperty, stomatalParameter, farquharParameter)
    setValue("transmissionNIR", data?.opticalProperty?.transmissionNIR);
    setValue("reflectanceNIR", data?.opticalProperty?.reflectionNIR);
    setValue("transmissionPAR", data?.opticalProperty?.transmissionPAR);
    setValue("reflectancePAR", data?.opticalProperty?.reflectionPAR);
    setValue("opticalPropertyFile", data?.opticalProperty?.opticalPropertyFile);

    setValue("em", data?.stomatalParameter?.em);
    setValue("i0", data?.stomatalParameter?.io);
    setValue("k", data?.stomatalParameter?.k);
    setValue("b", data?.stomatalParameter?.b);

    setValue("vcMax", data?.farquharParameter?.vcMax);
    setValue("cjMax", data?.farquharParameter?.cjMax);
    setValue("hajMax", data?.farquharParameter?.haJMax);
    setValue("alpha", data?.farquharParameter?.alpha);
    setValue("rd", data?.farquharParameter?.rd25);
    setValue("jMax", data?.farquharParameter?.jmax);


    setValue("hasPlantActualDate", data?.hasPlantActualDate);
    setValue("plantActualStartDate", dayjs(data?.plantActualStartDate, "MMMM D"));
    setStartDateValue(dayjs(data?.plantActualStartDate, "MMMM D"));
    setValue("plantMaxAge", data?.plantMaxAge);
    setValue("maxPlantsPerBed", data?.maxPlantsPerBed);
  }

  return (
    <Container>
      <AdminBackNavigation title="Back to Crop" onClick={handleCancel} />
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer">
          {loader ? (
            <CustomLinearProgress />
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={2}>
                <CropName control={control} errors={errors} />
                <CropLabel control={control} errors={errors} />
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
                      color: "#474F50"
                    }}
                  >
                    Farquhar Parameters
                  </div>
                  <Stack spacing={2}>
                    <VcMax control={control} errors={errors} />
                    <JMax control={control} errors={errors} />
                    <CJMax control={control} errors={errors} />
                    <HaJMax control={control} errors={errors} />
                    <Alpha control={control} errors={errors} />
                    <Rd control={control} errors={errors} />
                  </Stack>
                </div>
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
                    Stomatal Model Parameters
                  </div>
                  <Stack spacing={2}>
                    <Em control={control} errors={errors} />
                    <I0 control={control} errors={errors} />
                    <KField control={control} errors={errors} />
                    <BField control={control} errors={errors} />
                  </Stack>
                </div>
                <TransmissonReflectancePAR
                  soilOpticsErrorMessage={soilOpticsErrorMessage}
                  isSoilOpticsError={isSoilOpticsError}
                  handleSoilOpticsFileUpload={handleSoilOpticsFileUpload}
                  control={control}
                  errors={errors}
                  fileUrl={fileUrl}
                />
                <TransmissionNIR control={control} errors={errors} checkSumForOpticalProperties={checkSumForNIR} />
                <ReflectanceNIR control={control} errors={errors} checkSumForOpticalProperties={checkSumForNIR} />
                <TransmissionPAR control={control} errors={errors} checkSumForOpticalProperties={checkSumForPAR} />
                <ReflectancePAR control={control} errors={errors} checkSumForOpticalProperties={checkSumForPAR} />
                <RequiredDLI control={control} errors={errors} />
                <RequiredPPFD control={control} errors={errors} />
                <HarvestLifespan control={control} errors={errors} />
                <Fval1 control={control} errors={errors} />
                <Fval2 control={control} errors={errors} />
                <Fval3 control={control} errors={errors} />
                <Fval4 control={control} errors={errors} />
                <Fval5 control={control} errors={errors} />
                <MinStage control={control} errors={errors} />
                <MaxStage control={control} errors={errors} />
                <HasPlantStartDateSwitch control={control} errors={errors} />
                { watchFields[0] && 
                  <PlantStartDate
                    control={control}
                    errors={errors}
                    startDateVal={startDateVal}
                    setStartDateValue={setStartDateValue}
                    setError={setError}
                    clearErrors={clearErrors}
                  />
                }
                <PlantMaxAge control={control} errors={errors} />
                <PlantPerBed control={control} errors={errors} />
                {/* <Duration control={control} errors={errors} /> */}
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

export default CropForm;
