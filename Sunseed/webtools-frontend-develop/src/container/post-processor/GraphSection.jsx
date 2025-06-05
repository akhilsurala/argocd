import { Box, Button } from "@mui/material";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import styled, { useTheme } from "styled-components";
import CustomButton from "../../components/CustomButton";
import CustomDropDown from "../../components/CustomDropDown";
import postProcessorGraph from "../../assets/postProcessorGraph.svg";
import CustomLinearProgress from "../../components/CustomLinearProgress";
import { getLinksForScenes } from "../../api/analysisManager";
import { useDispatch, useSelector } from "react-redux";
import { setUrlDetails, setUrlDetailsWeeksTime, updateMinMaxFor3dView } from "../../redux/action/postProcessorAction";
import ObjDatViewer from "../threeJS/ThreeDViewer";
import { BASE_URL_FOR_DOWNLOAD } from "../../api/config";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";

// function getHourAndMinuteFromUTC(dateString) {
//   const date = new Date(dateString);

//   // Use the user's local time zone and format to 12-hour with AM/PM
//   const options = {
//     hour: "numeric",
//     minute: "numeric",
//     hour12: true,
//   };

//   // Format the date to local time using the specified options
//   return date.toLocaleString(undefined, options);
// }

function convertToISO(dateString) {
  // Replace the space between date and time with 'T' and 'UTC' with 'Z'
  const isoString = dateString.replace(" ", "T").replace(" UTC", "Z");
  return isoString;
}
function formatToDDMM(dateString) {
  // Ensure the date is in ISO 8601 format and create a Date object
  const date = new Date(dateString);

  if (isNaN(date)) {
    throw new Error("Invalid date format.");
  }

  // Extract the hours and minutes in local time
  const options = { hour: "numeric", minute: "numeric", hour12: true };
  return date.toLocaleString(undefined, options).toLowerCase(); // Ensure "am/pm" is lowercase
}
function getHourAndMinuteFromUTC(dateString) {
  // Ensure the date string is ISO 8601 format
  // console.log("date", dateString)
  let date = new Date(dateString);

  if (isNaN(date)) {
    date = convertToISO(dateString);
    return formatToDDMM(date);
  }


  return date.toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
    timeZone: 'UTC',
  });
}

const GraphSection = ({
  selectedQuantityAvailable,
  setSelectedQuantityAvailable,
  fileRelatedInfo,
  gradientStops,
  minVal,
  maxVal,
  receivedData,
}) => {
  const theme = useTheme();
  const [selectedTime, setselectedTime] = useState("");
  const [defaultValues, setDefaultValues] = useState([0, 0]);
  const [loader, setLoader] = useState(true);
  const [currentObjFileName, setCurrentObjFileName] = useState("");
  const [currentMtlFileName, setCurrentMtlFileName] = useState("");
  const [currentDatFileName, setCurrentDatFileName] = useState("");
  const dataIsNull = false;
  const {
    isTracking: singleAxisTilt,
    simulationGroundArea: simulationGroundArea
  } = useSelector((state) => state.postProcessor.linkDetailsAsPerHours);


  const currentlySelectedWeekTime = useSelector(
    (state) => state.postProcessor.currentlySelectedWeekTime
  );


  const dispatch = useDispatch();
  const [objectValue, setObjectValue] = useState({});
  const [contourControl, setContourControl] = useState([]);
  const navigate = useNavigate();
  const [urlsForFile, setUrlsForFile] = useState({});

  // useEffect(() => {
  //   console.log("currentObjFileName", currentObjFileName, currentMtlFileName, currentDatFileName)
  // }, [currentObjFileName, currentMtlFileName, currentDatFileName])
  useEffect(() => {
    if (defaultValues.length > 0)
      dispatch(updateMinMaxFor3dView(defaultValues));
  }, [defaultValues])


  useEffect(() => {
    // console.log("receivedData.projectId", receivedData)
    getLinksForScenes(receivedData.projectId, receivedData.runId)
      .then((response) => {
        if (response?.data?.httpStatus === "OK") {
          if (response?.data?.data?.scenes?.length === 0) {
            navigate(AppRoutesPath.PAGE_NOT_FOUND);
          } else {
            console.log("response?.data?.data", response?.data?.data)
            dispatch(setUrlDetails(response?.data?.data));
          }
        }
      })
      .catch((error) => {
        console.log(error);
      });

    return () => {
      dispatch(setUrlDetailsWeeksTime([]))
      dispatch(setUrlDetails({}))
    };
  }, []);




  useEffect(() => {
    if (currentlySelectedWeekTime?.length) {

      const obj = {};
      const arr = currentlySelectedWeekTime.map((item) => {
        obj[item.startTime] = item;
        return {
          id: item.startTime,
          name: getHourAndMinuteFromUTC(item.startTime),
        };
      });
      setObjectValue(obj);
      setContourControl(arr);
      // console.log("hees, ", obj, arr)
      setselectedTime(arr[0].id);
    }
  }, [currentlySelectedWeekTime, selectedQuantityAvailable]);

  // const contourControl = [
  //   {
  //     id: 0,
  //     name: "Plants",
  //   },
  //   {
  //     id: 1,
  //     name: "Shade Nets",
  //   },
  // ];
  const contourControlError = {
    Plants: false,
    "Shade Nets": false,
  };

  const handleTime = useCallback((val) => {
    // console.log("fdsafsd", val);
    setselectedTime(val);
  });

  useEffect(() => {
    if (dataIsNull) {
      return;
    }

    const getData = setTimeout(() => {
      // console.log("selected", selectedQuantityAvailable)
      // Set OBJ and MTL file names
      if (singleAxisTilt) {
        setCurrentObjFileName(objectValue?.[selectedTime]?.geometry?.url);
      } else {

        // console.log("hi", currentlySelectedWeekTime)
        setCurrentObjFileName(currentlySelectedWeekTime?.[0]?.geometry?.url);
      }

      if (selectedQuantityAvailable === 3) {
        setCurrentMtlFileName(objectValue?.[selectedTime]?.material?.url);
      }

      // Set DAT file name and default values based on contour and quantity
      const setDataFromSource = (source) => {
        setDefaultValues([source?.min, source?.max]);
        setCurrentDatFileName(source?.url);
      };
      let source;
      if (selectedQuantityAvailable === 1) {
        source = objectValue?.[selectedTime]?.carbon_assimilation;
      } else if (selectedQuantityAvailable === 2) {
        source = objectValue?.[selectedTime]?.temperature;
      } else if (selectedQuantityAvailable === 4) {
        // source = objectValue?.[selectedTime]?.radiation; // Adjust this as needed
        source =
          currentlySelectedWeekTime?.[currentlySelectedWeekTime.length - 1]?.dli_output; // Adjust this as needed
      }
      // console.log("source", source, selectedQuantityAvailable)
      setDataFromSource(source);
    }, 50);

    return () => clearTimeout(getData);
  }, [selectedTime, selectedQuantityAvailable, objectValue, currentlySelectedWeekTime]);

  const getDatName = () => {
    switch (selectedQuantityAvailable) {
      case 1:
        setDefaultValues([-10, 30]);
        return `https://gizstg.navyuginfo.com/photoSynthesis.dat`;
      default:
        setDefaultValues([275, 330]);
        return `https://gizstg.navyuginfo.com/data.dat`;
    }
  };

  function formatDate(dateString) {
    const date = new Date(dateString); // Convert the string to a Date object
    const day = String(date.getDate()).padStart(2, "0"); // Ensure two digits for day
    const month = String(date.getMonth() + 1).padStart(2, "0"); // Ensure two digits for month
    const year = date.getFullYear(); // Get the full year

    return `${day}-${month}-${year}`; // Return in dd-mm-yyyy format
  }

  useEffect(() => {
    const getData = setTimeout(() => {
      if (dataIsNull) {
        setUrlsForFile({
          objFileUrl: "https://gizstg.navyuginfo.com/scene.obj",
          mtlFileUrl: "https://gizstg.navyuginfo.com/helios.mtl",
          datFileUrl: `${getDatName()}`,
          objFileKey:
            "obj-" + fileRelatedInfo.projectId + "-" + fileRelatedInfo.runId,
          mtlFileKey:
            "mtl-" + fileRelatedInfo.projectId + "-" + fileRelatedInfo.runId,
          datFileKey:
            "dat-" +
            fileRelatedInfo.projectId +
            "-" +
            fileRelatedInfo.runId +
            "-" +
            selectedQuantityAvailable,
        });
      } else {
        if (currentObjFileName) {
          // Create the base object
          console.log("heeee", selectedTime);
          const createOBj = {
            objFileUrl: BASE_URL_FOR_DOWNLOAD + currentObjFileName,
            objFileKey:
              "obj/" +
              fileRelatedInfo.projectId +
              "/" +
              fileRelatedInfo.runId +
              "/" +
              formatDate(selectedTime),
          };

          if (singleAxisTilt) {
            createOBj.objFileKey =
              "obj/" +
              fileRelatedInfo.projectId +
              "/" +
              fileRelatedInfo.runId +
              "/" +
              selectedTime;
          }

          // Add MTL-related keys if available
          if (currentMtlFileName) {
            createOBj.mtlFileUrl = BASE_URL_FOR_DOWNLOAD + currentMtlFileName;
            createOBj.mtlFileKey =
              "mtl/" +
              fileRelatedInfo.projectId +
              "/" +
              fileRelatedInfo.runId +
              "/" +
              selectedTime;
          }

          // Add DAT-related keys if available
          if (currentDatFileName) {
            createOBj.datFileUrl = BASE_URL_FOR_DOWNLOAD + currentDatFileName;
            createOBj.datFileKey =
              "dat/" +
              fileRelatedInfo.projectId +
              "/" +
              fileRelatedInfo.runId +
              "/" +
              selectedTime +
              "/" +
              selectedQuantityAvailable;
          }

          // Update the state with the new URLs
          setUrlsForFile((obj) => ({
            ...obj,
            ...createOBj,
          }));
        }
      }
    }, 200);
    return () => clearTimeout(getData);
  }, [currentObjFileName, currentMtlFileName, currentDatFileName]);

  return (
    <Container>
      <Box className="headerWrapper">
        <Box className="title">3d View</Box>

        <Box className="buttonSection">
          {selectedQuantityAvailable !== 4 && (
            <div style={{ minWidth: "200px", marginTop: "-28px" }}>
              <CustomDropDown
                label=""
                placeHolder="Select Time"
                value={selectedTime}
                onChange={() => { }}
                handleChange={handleTime}
                disabled={false}
                error={[]}
                dataSet={contourControl}
                errors={contourControlError}
                noneNotRequire={true}
              />
            </div>
          )}
          {/* <Button
            type="reset"
            className="prevBtn"
            onClick={(e) => {}}
            sx={{
              "&:hover": {
                backgroundColor: theme.palette.background.secondary,
              },
            }}
          >
            Save HD IMG
          </Button> */}
          {/* <CustomButton label="Download CSV" onClick={() => { }} /> */}
        </Box>
      </Box>
      {loader ? <CustomLinearProgress /> : <div className="customBorder" />}
      <Box className="imageWrapper"></Box>

      <ObjDatViewer
        setLoader={setLoader}
        loader={loader}
        mode={selectedQuantityAvailable === 3 ? "" : "dat"}
        urlsForFile={urlsForFile}
        fileRelatedInfo={fileRelatedInfo}
        gradientStops={gradientStops.stops}
        minVal={minVal}
        maxVal={maxVal}
        defaultValues={defaultValues}
        simulationGrounArea={simulationGroundArea}
      />

      {/* <img src={postProcessorGraph} alt="post processor graph" className="img" /> */}
    </Container>
  );
};

export default GraphSection;

const Container = styled.div`
  background-color: #ffffff;
  padding: 26px;
  border-radius: 16px;
  .headerWrapper {
    display: flex;
    align-items: center;
  }
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    color: ${({ theme }) => theme.palette.text.main};
    font-size: 16px;
    font-weight: 600;
    line-height: 26px;
    text-align: left;
    flex: 1;
  }
  .prevBtn {
    height: 44px;
    padding: 12px 20px 12px 20px;
    border-radius: 8px;
    gap: 10px;
    color: #25272759;
    background-color: ${({ theme }) => theme.palette.background.secondary};
    text-transform: capitalize;
    border: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.main};
    font-weight: 700;
  }
  .buttonSection {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .img {
    width: 100%;
    margin-top: 20px;
  }
`;
