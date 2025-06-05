import { Box } from "@mui/material";
import React, { useEffect, useState } from "react";
import styled from "styled-components";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { userFormData } from "./userFormData";
import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import {
  getAdminUser,
  saveAdminUser,
  updateAdminUser,
} from "../../../api/admin/user";
import CustomLinearProgress from "../../../components/CustomLinearProgress";
import { Container } from "../../components/CustomCreatePageCss";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const UserForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { state } = location;
  const { id } = useParams();
  const [loader, setLoader] = useState(false);
  const pageHeading = id ? "Edit User" : "Add User";
  const buttonLabel = id ? "Update" : "Save";

  const [defaultValues, setDefaultValues] = useState({
    id: "",
    firstName: "",
    lastName: "",
    emailId: "",
    active: "",
    type:""
  });

  useEffect(() => {
    if (id) {
      setLoader(true);
      getAdminUser(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setDefaultValues({
            id: id,
            firstName: data.firstName,
            lastName: data.lastName,
            emailId: data.emailId,
            active: data.active,
            type: data.roles[0]
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
      name: data.moduleType,
      firstName: data.firstName,
      lastName: data.lastName,
      emailId: data.emailId,
      roles: [data.type],
      hide: !!data.hide,
    };
    const apiFunction = id ? updateAdminUser : saveAdminUser;
    const apiParameters = id ? [id, payload] : [payload];

    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_USER_DATABASE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleCancel = () => {
    console.log("cancelled");
    navigate(AppRoutesPath.ADMIN_USER_DATABASE);
  };

  const roleDataset = [
    {
      id: "user",
      name: "user",
    },
    {
      id: "admin",
      name: "admin",
    },
  ];

 

  return (
    <Container>
      <AdminBackNavigation title="Back to Users" onClick={handleCancel} />
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer">
          {loader ? (
            <CustomLinearProgress />
          ) : (
            <CustomFormContainer
              formData={userFormData(roleDataset)}
              defaultValues={defaultValues}
              onFormSubmit={onSubmit}
              buttonLabel={buttonLabel}
              buttonPosition="right"
              showPreviousButton={true}
              previousButtonLabel="Cancel"
              handlePreviousButton={handleCancel}
            />
          )}
        </Box>
      </Box>
    </Container>
  );
};

export default UserForm;
