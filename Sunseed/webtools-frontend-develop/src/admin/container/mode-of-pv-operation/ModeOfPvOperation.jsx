import { Box } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { modeOfPvOperationFormData } from "./modeOfPvOperationFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminModeOfPvOperation, saveAdminModeOfPvOperation, updateAdminModeOfPvOperation } from "../../../api/admin/modeOfPvOperation";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const ModeOfPvOperationForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader,setLoader] = useState(false);
  const pageHeading = id ? "Edit Mode Of PV Operation" : "Add Mode Of PV Operation";
  const buttonLabel = id ? "Update" : "Save";

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    modeOfOperation: "",
  });

  useEffect(() => {
    if(id){
      setLoader(true)
      getAdminModeOfPvOperation(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setDefaultValues({
            id: id,
            modeOfOperation: data.modeOfOperation,
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
      name: data.modeOfOperation,
      hide: !!data.hide,
    }
    const apiFunction = id ? updateAdminModeOfPvOperation : saveAdminModeOfPvOperation;
    const apiParameters = id ? [id, payload] : [payload];
    
    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE);
  };

  return (
    <Container>
      <AdminBackNavigation title="Back to Mode Of PV Operation" onClick={handleCancel} />
      <Box className="wrapper">
      <Box className="title">{pageHeading}</Box>
      <Box className="formContainer">
        { loader ? <CustomLinearProgress /> : 
          <CustomFormContainer
            formData={modeOfPvOperationFormData()}
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

export default ModeOfPvOperationForm;
