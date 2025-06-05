import React, { useEffect, useState } from "react";
import { styled, useTheme } from "styled-components";

import ProjectCard from "./ProjectCard";
import CustomFormContainer from "../../components/CustomFormContainer";
import { projectForm } from "../../utils/formData/projectForm";
import { addProject } from "../../api/userProfile";
import axios from "axios";

const CancelToken = axios.CancelToken;
let cancelTokenForProject;

const ProjectForm = () => {
  const theme = useTheme();
  const watchList = [];
  const [watchState, setWatchState] = useState();
  const [options, setOptions] = useState({
    label: "Area (acres)",
    placeHolder: "Enter Land Size",
    key: "fixedLand",
  });

  const onCardBtnClick = () => {
    console.log("pressed");
  };

  const defaultValues = {
    latitude: "",
    longitude: "",
    projectName: "",
    googleMap: "",
    // calculationApproach: "Fixed Land",
    // fixedLand: "",
    // fixedPvCapacity: "",
  };


  const callCreateProjectApi = (data) => {
    if (cancelTokenForProject !== undefined) {
      cancelTokenForProject();
    }
    const payload = {
      projectName: data.projectName,
      latitude: data.latitude,
      longitude: data.longitude,
      calculationApproach: "Fixed Land",
      dcInKw: "1",
      area: "1",
    };
    addProject(payload, {
      cancelToken: new CancelToken((c) => {
        cancelTokenForProject = c;
      }),
    })
      .then((response) => {
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const onSubmit = (data) => {
    // console.log(data);
    setTimeout(() => {
      callCreateProjectApi(data);
    }, 1000);
  };

  const handleChange = (e) => {
    if (e.target.value === "Fixed Land") {
      setOptions({
        label: "Area (acres)",
        placeHolder: "Enter Land Size",
        key: "fixedLand",
      });
    } else {
      setOptions({
        label: "DC (kw)",
        placeHolder: "Enter kw Value",
        key: "fixedPvCapacity",
      });
    }
  };

  return (
    <Container>
      <div style={{ width: "100%", padding: "20px" }}>
        <div className="title">New Project General Parameters</div>
        <div className="formContent">
          <CustomFormContainer
            formData={projectForm(options, handleChange, pinCoordinates, setPinCoordinates)}
            defaultValues={defaultValues}
            setWatchState={setWatchState}
            watchList={watchList}
            onFormSubmit={onSubmit}
            buttonLabel="Create Run"
            buttonPosition="right"
          />
        </div>
      </div>
    </Container>
  );
};

export default ProjectForm;

const Container = styled.div`
  margin: 80px 20px 20px 20px;
  background: ${({ theme }) => theme.palette.background.secondary};
  height: 100%;
  overflow: hidden;

  .title {
    padding: 20px;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
  }
  .formContent {
    padding: 40px 20px 20px 20px;
  }
`;
