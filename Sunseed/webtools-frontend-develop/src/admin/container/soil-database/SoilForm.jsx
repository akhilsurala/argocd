import { Box, Button, Stack } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { soilFormData } from "./soilFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import {
  getAdminSoil,
  saveAdminSoil,
  updateAdminSoil,
} from "../../../api/admin/soil";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import { useForm } from "react-hook-form";
import { SoilOptics, SoilTypeField } from "./SoilDatabaseFields";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const SoilForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader, setLoader] = useState(false);
  const pageHeading = id ? "Edit Soil" : "Add Soil";
  const buttonLabel = id ? "Update" : "Save";

  const [soilOpticsValue, setSoilOpticsValue] = useState();
  const [isSoilOpticsError, setIsSoilOpticsError] = useState(false);
  const [soilOpticsErrorMessage, setSoilOpticsErrorMessage] = useState("");
  const [fileUrl, setFileUrl] = useState();

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    soilName: "",
  });

  const {
    handleSubmit,
    control,
    watch,
    register,
    setValue,
    formState: { errors },
  } = useForm({
    mode: "all",
    defaultValues: {},
  });

  useEffect(() => {
    if (id) {
      setLoader(true);
      getAdminSoil(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setValue("soilType", data.soilName);
          setValue("hide", data.hide);
          setFileUrl(data?.opticalProperty?.opticalPropertyFile)
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);

  const onSubmit = (data) => {
    if (isSoilOpticsError) return;
    if (!id && !soilOpticsValue) {
      setSoilOpticsErrorMessage("Required field.");
      setIsSoilOpticsError(true);
      return;
    }
    const requestDto = {
      name: data.soilType,
      hide: !!data.hide,
      opticalProperties: {
        reflectance_PAR: 0.0,
        reflectance_NIR: 0.0,
        transmissivity_PAR: 0.0,
        transmissivity_NIR: 0.0
      }
    };

    const formData = new FormData();
    formData.append('requestDto', JSON.stringify(requestDto));
    formData.append('opticalFiles', soilOpticsValue ? soilOpticsValue : null);

    const apiFunction = id ? updateAdminSoil : saveAdminSoil;
    const apiParameters = id ? [id, formData, data.soilType, data.hide] : [formData];


    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_SOIL_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    navigate(AppRoutesPath.ADMIN_SOIL_DATABASE);
  };

  const handleSoilOpticsFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const validFileType = ["xls", "xlsx"];
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

  return (
    <Container>
      <AdminBackNavigation title="Back to Soil" onClick={handleCancel} />
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer">
          {loader ? (
            <CustomLinearProgress />
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={2}>
                <SoilTypeField control={control} errors={errors} />
                <SoilOptics
                  soilOpticsErrorMessage={soilOpticsErrorMessage}
                  isSoilOpticsError={isSoilOpticsError}
                  handleSoilOpticsFileUpload={handleSoilOpticsFileUpload}
                  control={control}
                  errors={errors}
                  fileUrl={fileUrl}
                />
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

export default SoilForm;
