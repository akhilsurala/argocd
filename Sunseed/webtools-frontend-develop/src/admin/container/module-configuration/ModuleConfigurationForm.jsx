import { Box } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { moduleConfigurationFormData } from "./moduleConfigurationFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminModuleConfiguration, saveAdminModuleConfiguration, updateAdminModuleConfiguration } from "../../../api/admin/moduleConfiguration";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const ModuleConfigurationForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader,setLoader] = useState(false);
  const pageHeading = id ? "Update Module Configuration" : "Add Module Configuration";
  const buttonLabel = id ? "Update" : "Save";

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    name: "",
    numberOfModules: "",
    typeOfModule: "",
    ordering: ""
  });

  useEffect(() => {
    if(id){
      setLoader(true)
      getAdminModuleConfiguration(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setDefaultValues({
            id: id,
            name: data.name,
            numberOfModules: data.numberOfModules,
            typeOfModule: data.typeOfModule,
            ordering: data.ordering,
          });
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);
  
  const onSubmit = (data) => {
    const payload = {
      ...(id && { id: id }),
      name: data.name,
      numberOfModules: data.numberOfModules,
      typeOfModule: data.typeOfModule,
      ordering: data.ordering,
      hide: !!data.hide,
    }

    const apiFunction = (id) ? updateAdminModuleConfiguration : saveAdminModuleConfiguration;
    const apiParameters = id ? [id, payload] : [payload];
    
    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  
  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Module Configuration" onClick={handleCancel} />
      <Box className="wrapper">
      <Box className="title">{pageHeading}</Box>
      <Box className="formContainer">
        { loader ? <CustomLinearProgress /> : 
          <CustomFormContainer
            formData={moduleConfigurationFormData()}
            defaultValues={defaultValues}
            onFormSubmit={onSubmit}
            buttonLabel={buttonLabel}
            buttonPosition="right"
            showPreviousButton={true}
            previousButtonLabel="Cancel"
            handlePreviousButton={handleCancel}
          />}
      </Box>
      </Box>
    </Container>
  );
};

export default ModuleConfigurationForm;

