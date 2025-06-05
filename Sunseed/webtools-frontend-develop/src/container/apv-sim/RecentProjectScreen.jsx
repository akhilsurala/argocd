import React, { useEffect, useState } from "react";
import styled, { useTheme } from "styled-components";
import { TextField, buttonBaseClasses } from "@mui/material";
import dayjs from "dayjs";
// import OpenStreetMap from './osm/OpenStreetMap';
import CustomTextField from "../../components/CustomTextField";
import CustomButton from "../../components/CustomCard/CustomButton";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath, changeDateFormat } from "../../utils/constant";
import TableWithReactQueryProvider from "../../components/custom-table/CustomTable";
import CustomTable from "../../components/custom-table/CustomTable";
import { getProjects, updateProject } from "../../api/userProfile";

export default function RecentProjectScreen() {
  const [searchBoxValue, setSearchBoxValue] = useState("");
  const [loader, setLoader] = useState(false);
  const [data, setData] = useState(null);
  const [apiErrorMsg, setApiErrorMsg] = useState("");

  const [editedUsers, setEditedUsers] = useState({});

  useEffect(() => {
    // Set a timer to update `debouncedSearchValue` after a delay
    const handler = setTimeout(() => {
      callGetProjectApi(searchBoxValue);
    }, 300); // 300ms debounce time

    // Cleanup function to clear the previous timer if searchBoxValue changes
    return () => {
      clearTimeout(handler);
    };
  }, [searchBoxValue]);
  const getProjectDto = (data) => {
    return data.map((singleData) => {
      return {
        comments: singleData.comments ? singleData.comments : "",
        createdOn: changeDateFormat(
          dayjs.utc(singleData.createdOn).tz(dayjs.tz.guess())
        ),
        // lastEdited: changeDateFormat(dayjs.utc(singleData.lastEdited).tz(dayjs.tz.guess())),
        location: singleData.location ? singleData.location : "",
        numberOfRuns: singleData.numberOfRuns,
        latitude: singleData.latitude,
        longitude: singleData.longitude,
        projectId: singleData.projectId,
        projectName: singleData.projectName,
      };
    });
  };

  function callGetProjectApi(searchBoxValue) {
    setLoader(true);
    getProjects(null, searchBoxValue)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          // console.log("hey", response.data.httpStatus, response.data.data)
          setData(getProjectDto(response.data.data));
        } else {
          console.log("something went wront", response);
        }
      })
      .catch((error) => {
        alert(error.response.data.errorMessages[0]);
        console.log(error);
      })
      .finally(() => {
        setLoader(false);
      });
  }

  function getObjectByProjectId(projectId) {
    for (const obj of data) {
      if (obj.projectId === projectId) {
        return obj;
      }
    }
    return null; // Return null if no matching object is found
  }

  function callUpdateProject() {
    setLoader(true);
    const obj = getObjectByProjectId(editedUsers.projectId);
    const localdata = {
      ...obj,
      comments: editedUsers.comments ? editedUsers.comments : obj.comments,
      projectName: editedUsers.projectName
        ? editedUsers.projectName
        : obj.projectName,
    };
    updateProject(localdata, editedUsers.projectId)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          setData(getProjectDto(response.data.data));
        } else {
          setApiErrorMsg(response.data.message);
          // console.log("loo", response.data.message);
        }
      })
      .catch((error) => {
        // alert(error.response.data.errorMessages[0])
        // console.log("loo", error.response.data.message);
        setApiErrorMsg(error.response.data.message);
      })
      .finally(() => {
        setLoader(false);
      });
  }
  useEffect(() => {
    callGetProjectApi();
  }, []);

  useEffect(() => {
    if (Object.keys(editedUsers).length !== 0) {
      // console.log("heyere", editedUsers)
      callUpdateProject();
    }
  }, [editedUsers]);

  return (
    <Container>
      {data && (
        <CustomTable
          setSearchBoxValue={setSearchBoxValue}
          searchBoxValue={searchBoxValue}
          data={data}
          setData={setData}
          setEditedUsers={setEditedUsers}
          apiErrorMsg={apiErrorMsg}
          setApiErrorMsg={setApiErrorMsg}
          callGetProjectApi={callGetProjectApi}
        />
      )}
    </Container>
  );
}

const Container = styled.div``;
