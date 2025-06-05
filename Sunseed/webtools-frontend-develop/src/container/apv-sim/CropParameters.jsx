import React, { useEffect, useState } from "react";
import { styled, useTheme } from "styled-components";

import ProjectCard from "./ProjectCard";
import CustomFormContainer from "../../components/CustomFormContainer";
import { projectForm } from "../../utils/formData/projectForm";
import { Grid } from "@mui/material";
import { cropParameters } from "../../utils/formData/cropParameters";
import dayjs from "dayjs";

const CropParameters = () => {
  const theme = useTheme();
  const watchList = ["CroppingCycle"];
  const [watchState, setWatchState] = useState();
  const [disabletiltIfFt, setDisabletiltIfFt] = useState(false);

  const defaultValues = {
    CroppingCycle: "",
    bedType: "Inter Bed",
    addCrops: "",
    spacing: "",
    dateOfSowing: dayjs(new Date()),
    croppingPattern: "",
    startPointOffset: "",
    bedCC: "",
    bedAzimuth: "",
  };

  const onSubmit = (data) => {
    console.log(data);
  };

  useEffect(() => {
    console.log(watchState);
  }, [watchState?.CroppingCycle]);

  return (
    <Container>
      <Grid container spacing={4}>
        <Grid item md={6} className="leftSection">
          <div className="title">Crop Parameters</div>
          <div className="formContent">
            <CustomFormContainer
              formData={cropParameters()}
              defaultValues={defaultValues}
              setWatchState={setWatchState}
              watchList={watchList}
              onFormSubmit={onSubmit}
              buttonLabel="Next"
              buttonPosition="right"
            />
          </div>
        </Grid>
        <Grid item md={6}>
          <div
            style={{
              backgroundColor: "#53988E14",
              height: "100vh",
              width: "100%",
            }}
          >
            Map Section
          </div>
        </Grid>
      </Grid>
    </Container>
  );
};

export default CropParameters;

const Container = styled.div`
  margin: 20px;
  height: 100%;
  overflow: hidden;
  
  .leftSection {
      background: ${({ theme }) => theme.palette.background.secondary};
      padding: 20px;
  }

  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
    padding: 20px 0px 0px 20px;
  }
  .formContent {
    padding: 40px 20px 20px 20px;
  }
`;
