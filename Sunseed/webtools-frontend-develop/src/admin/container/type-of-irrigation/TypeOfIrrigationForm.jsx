import { Box } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { typeOfIrrigationFormData } from "./typeOfIrrigationFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminTypeOfIrrigation, saveAdminTypeOfIrrigation, updateAdminTypeOfIrrigation } from "../../../api/admin/typeOfIrrigation";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const TypeOfIrrigationForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader,setLoader] = useState(false);
  const pageHeading = id ? "Edit Irrigation Type" : "Add Irrigation Type";
  const buttonLabel = id ? "Update" : "Save";

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    irrigationType: "",
  });

  useEffect(() => {
    if(id){
      setLoader(true)
      getAdminTypeOfIrrigation(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setDefaultValues({
            id: id,
            irrigationType: data.irrigationType,
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
      name: data.irrigationType,
      hide: !!data.hide,
    }
    const apiFunction = id ? updateAdminTypeOfIrrigation : saveAdminTypeOfIrrigation;
    const apiParameters = id ? [id, payload] : [payload];
    
    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Type Of Irrigation" onClick={handleCancel} />
      <Box className="wrapper">
      <Box className="title">{pageHeading}</Box>
      <Box className="formContainer">
        { loader ? <CustomLinearProgress /> : 
          <CustomFormContainer
            formData={typeOfIrrigationFormData()}
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

export default TypeOfIrrigationForm;

