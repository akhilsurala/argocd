import React, { useEffect, useRef, useState } from "react";
import { styled, useTheme } from "styled-components";

import ProjectCard from "./ProjectCard";
import CustomFormContainer from "../../components/CustomFormContainer";
import { projectForm } from "../../utils/formData/projectForm";
import { pvGeometry } from "../../utils/formData/pvGeometry";
import { Card, Grid } from "@mui/material";
import {
  UpdatePvParameters,
  addPvParameters,
  getPvParameters,
  getPvParametersDropDownData,
} from "../../api/pvParameters";
import { getLocalStorageData } from "../../utils/localStorage";
import CircularProgress from "@mui/material/CircularProgress";
import { useSelector, useDispatch } from "react-redux";
import {
  setApvToggle,
  setPvParameterDetails,
  setPvParameters,
  setRunId,
  setUpdateFormValue,
} from "../../redux/action/preProcessorAction";
import { getApvToggle } from "../../utils/constant";
import { myDebounce } from "../../utils/debounce";

const PvGeometry = ({
  setActiveStep,
  projectId,
  runId,
  setCurrentRunId,
  isCloned,
  setIsCloned,
  setIsVarient,
  pvModuleList,
  setPvModuleList,
  modeOfPvOperationList,
  setModeOfPvOperationList,
  moduleConfigurationList,
  setModuleConfigurationList,
  soilTypeList,
  setSoilTypeList,
}) => {
  const formRef = useRef();
  const getFormStateRef = useRef();
  const dispatch = useDispatch();
  const theme = useTheme();
  const watchList = [
    "agriApv",
    "pvModule",
    "modeOfPvGeneration",
    "moduleConfiguration",
    "moduleMaskPattern",
    "tiltIfFt",
    "maxAnglesOfTracking",
    "height",
    "lengthOfOneRow",
    "gapBetweenModules",
    "pitchOfRow",
    "azimuth",
    "XCoordinate",
    "YCoordinate",
  ];
  const [loader, setLoader] = useState(false);
  const [isMaster, setIsMaster] = useState(false);
  // const runId = useSelector((state) => state.preProcessor.runId);
  const apvToggleValue = useSelector((state) => state.preProcessor.apvToggle);
  const [responseData, setResponseData] = useState({});
  const [pvParametersId, setPvParametersId] = useState();
  // const [selectedToggle,setSelectedToggle] = useState();
  const [disableFields, setDisableFields] = useState({
    agriApv: true,
    pvModule: true,
    modeOfPvGeneration: true,
    tiltIfFt: true,
    maxAnglesOfTracking: true,
    moduleMaskPattern: true,
    gapBetweenModules: true,
    height: true,
    pitchOfRow: true,
    azimuth: true,
    lengthOfOneRow: true,
    moduleConfiguration: true,
    soilId: true,
    XCoordinate: false,
    YCoordinate: false,
  });
  const [defaultValues, setDefaultValues] = useState({
    runName: "",
    agriApv: "APV",
    pvModule: "",
    modeOfPvGeneration: "",
    tiltIfFt: "",
    maxAnglesOfTracking: "",
    moduleMaskPattern: "",
    gapBetweenModules: "",
    height: "",
    pitchOfRow: "",
    azimuth: "",
    lengthOfOneRow: "",
    moduleConfiguration: "",
    soilId: "",
    XCoordinate: "",
    YCoordinate: "",
  });
  
  const onSubmit = (data) => {
    const payload = {
      ...data,
      tiltIfFt: data.tiltIfFt,
      maxAngleOfTracking: data.maxAnglesOfTracking,
      moduleMaskPattern: data.moduleMaskPattern,
      gapBetweenModules: data.gapBetweenModules,
      height: data.height,
      pitchOfRows: data.pitchOfRow,
      azimuth: data.azimuth,
      lengthOfOneRow: data.lengthOfOneRow,
      pvModuleId: data.pvModule,
      modeOfOperationId: data.modeOfPvGeneration,
      moduleConfigId: [data.moduleConfiguration],
      soilId: data.soilId,
    };
    const requestData = { payload, projectId, apvToggle: data.agriApv };
    {
      pvParametersId && !isCloned
        ? myDebounce(callUpdatePvParametersId, requestData, 500)
        : myDebounce(callAddPvParametersId, requestData, 500);
    }
  };

  const callAddPvParametersId = ({ payload, projectId, apvToggle }) => {
    setIsMaster(!isCloned);
    addPvParameters(payload, projectId, apvToggle, runId, isCloned, isMaster)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          if (isCloned) {
            setCurrentRunId(response?.data?.data?.runId);
            setIsCloned(false);
          }
          dispatch(
            setPvParameterDetails(
              { 
                pvParameterDetails: response?.data?.data
              }
            )
          );
          setActiveStep((active) => active + 1);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  const callUpdatePvParametersId = ({ payload, projectId, apvToggle }) => {
    UpdatePvParameters(payload, projectId, pvParametersId, apvToggle, runId)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          dispatch(
            setPvParameterDetails(
              { 
                pvParameterDetails: response?.data?.data
              }
            )
          );
          setActiveStep((active) => active + 1);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // function callGetPVParameters() {
  //   // console.log("callGetPVParameters", projectId)
  //   // setLoader(true);
  //   getPVParameters(projectId)
  //     .then((response) => {
  //       if (response.data.httpStatus === "OK" && response.data.data) {
  //         const defaultValues = response.data.data;
  //         let modconfig = ""
  //         if (defaultValues?.moduleConfigs?.length > 0)
  //           modconfig = defaultValues?.moduleConfigs[0]?.id || "";
  //         const data = {
  //           ...defaultValues,
  //           XCoordinate: defaultValues.XCoordinate,
  //           YCoordinate: defaultValues.YCoordinate,
  //           runName: defaultValues.preProcessorToggle.runName,
  //           lengthOfOneRow: defaultValues.preProcessorToggle.lengthOfOneRow,
  //           azimuth: defaultValues.preProcessorToggle.azimuth,
  //           pitchOfRow: defaultValues.preProcessorToggle.pitchOfRows,
  //           maxAnglesOfTracking: defaultValues.maxAngleOfTracking,
  //           tiltIfFt: defaultValues.tiltIfFt,
  //           modeOfPvGeneration: defaultValues?.modeOfOperationId?.id || "",
  //           moduleConfiguration: modconfig,
  //           pvModule: defaultValues?.pvModule?.id || "",
  //           agriApv: defaultValues?.preProcessorToggle?.toggle || "APV"

  //         }

  //         dispatch(setApvToggle({ apvToggle: data.agriApv }));
  //         handleDisableFields(defaultValues?.preProcessorToggle?.toggle, response?.data?.data);
  //         setDefaultValues(data);
  //         setTriggerReset(true)
  //         setId(defaultValues.id)

  //       } else {
  //         console.log("something went wront", response)
  //       }

  //     })
  //     .catch((error) => {

  //       // alert(error?.response?.data?.errorMessages?.[0])
  //       console.log(error);
  //     })
  //     .finally(() => {
  //       // setLoader(false);
  //     });
  //   getPvParametersDropDownData()
  //     .then((res) => {
  //       setModeOfPvOperationList(res.data.data.modeOfOperations);
  //       setPvModuleList(res.data.data.pvModules);
  //       setModuleConfigurationList(res.data.data.moduleConfigurations);
  //     })
  //     .catch((err) => {
  //       console.log(err);
  //     });
  // }
  // useEffect(() => {
  //   // callGetProjectApiForAgriGeneralParametersForMaster();
  //   callGetPVParameters();
  // }, [])

  const callMasterDataApi = (mode) => {
    getPvParametersDropDownData(mode)
      .then((res) => {
        setModeOfPvOperationList(res.data.data.modeOfOperations);
        setPvModuleList(res.data.data.pvModules);
        setModuleConfigurationList(res.data.data.moduleConfigurations);
        setSoilTypeList(res.data.data.soils);
        // setSoilTypeList([
        //   {
        //     "id": 14,
        //     "name": "Brown Sandy Loam Soil",
        //     "soilPicturePath": null
        //   },
        //   {
        //     "id": 16,
        //     "name": "Coco Peat Soil",
        //     "soilPicturePath": null
        //   },
        //   {
        //     "id": 15,
        //     "name": "Red Soil",
        //     "soilPicturePath": null
        //   }
        // ]);
      })
      .catch((err) => {
        console.error("Error fetching PV parameters master data:", err);
      });
  };

  const callGetParametersApi = () => {
    setTimeout(() => {
      setLoader(true);
      getPvParameters(projectId, runId, isCloned)
        .then((res) => {
          setResponseData(res?.data?.data);
          setLoader(false);
          setPvParametersId(res?.data?.data?.id);
          const toggle = getApvToggle(
            res?.data?.data?.preProcessorToggle?.toggle
          );
          if (
            res?.data?.data?.isMaster !== undefined &&
            res?.data?.data?.isMaster !== null
          )
            setIsVarient(!res?.data?.data?.isMaster);
          // dispatch(setPvParameters({ pvParameters: res?.data?.data }));

          // console.log("loggggg", res?.data?.data)
          dispatch(
            setPvParameters({
              pvParameters: {
                ...res?.data?.data,
                simulationTime: res?.data?.data?.simulationTime?.toISOString(), // Convert Day.js object to ISO string
              },
            })
          );
          dispatch(setApvToggle({ apvToggle: toggle }));
          dispatch(setRunId({ runId: res?.data?.data?.runId }));
        })
        .catch((err) => {
          console.log(err);
          setLoader(false);
        });
    }, 500);
  };

  useEffect(() => {
    if(responseData && 
      Object.keys(responseData).length > 0 && 
      pvModuleList.length &&
      modeOfPvOperationList.length &&
      moduleConfigurationList.length &&
      soilTypeList.length){
        handleDisableFields(apvToggleValue, responseData);
    }
  }, [responseData]);

  useEffect(() => {
    callGetParametersApi();
  }, []);

  // useEffect(() => {
  //   callMasterDataApi(responseData?.modeOfOperationId?.modeOfOperation);
  // }, [responseData]);

  const handleModeOfPvGeneration = (value) => {
    if (!value) {
      // callMasterDataApi();
      setDisableFields((prevState) => ({
        ...prevState,
        tiltIfFt: true,
        maxAnglesOfTracking: true,
      }));
      return;
    }
    dispatch(setUpdateFormValue({ updateFormValue: true }));

    // if single axis tracking(in Mode of PV Operations) is selected then disable tiltIfFt field
    if (value === 2) {
      callMasterDataApi("Single Axis Tracking");
      setDefaultValues({
        ...defaultValues,
        tiltIfFt: "",
        modeOfPvGeneration: value,
      });
      setDisableFields((prevState) => ({
        ...prevState,
        tiltIfFt: true,
        maxAnglesOfTracking: false,
      }));
    } else {
      callMasterDataApi("Fixed Tilt");
      setDisableFields((prevState) => ({
        ...prevState,
        tiltIfFt: false,
        maxAnglesOfTracking: true,
      }));
      setDefaultValues({
        ...defaultValues,
        modeOfPvGeneration: value,
        maxAnglesOfTracking: "",
      });
    }
    if (getFormStateRef.current) {
      const formValues = getFormStateRef.current();
      if (formRef.current) {
        formRef.current({
          ...formValues,
          moduleConfiguration: "",
          modeOfPvGeneration: value,
        });
      }
    }
  };

  const enableFieldsOnlyAgri = ["azimuth", "pitchOfRow", "lengthOfOneRow", "soilId"];

  const handleDisableFields = (toggleValue, data) => {
    let updatedFormState = {};
    if (toggleValue === "Only Agri") {
      setDisableFields((prevState) => ({
        ...prevState,
        ...Object.fromEntries(
          Object.keys(prevState).map((key) => [
            key,
            enableFieldsOnlyAgri.includes(key) ? false : true,
          ])
        ),
      }));
      setDefaultValues({
        ...defaultValues,
        runName: data.preProcessorToggle.runName,
        agriApv: "Only Agri",
        pvModule: "",
        modeOfPvGeneration: "",
        tiltIfFt: "",
        maxAnglesOfTracking: "",
        moduleMaskPattern: "",
        gapBetweenModules: "",
        height: "",
        pitchOfRow: data?.preProcessorToggle?.pitchOfRows,
        azimuth: data?.preProcessorToggle?.azimuth,
        lengthOfOneRow: data?.preProcessorToggle?.lengthOfOneRow,
        moduleConfiguration: "",
        soilId: data?.preProcessorToggle?.soilType?.id,
      });
      updatedFormState = {
        ...defaultValues,
        runName: data.preProcessorToggle.runName,
        agriApv: "Only Agri",
        pvModule: "",
        modeOfPvGeneration: "",
        tiltIfFt: "",
        maxAnglesOfTracking: "",
        moduleMaskPattern: "",
        gapBetweenModules: "",
        height: "",
        pitchOfRow: data?.preProcessorToggle?.pitchOfRows,
        azimuth: data?.preProcessorToggle?.azimuth,
        lengthOfOneRow: data?.preProcessorToggle?.lengthOfOneRow,
        moduleConfiguration: "",
        soilId: data?.preProcessorToggle?.soilType?.id,
      };
    } else {
      setDisableFields((prevState) => ({
        ...prevState,
        ...Object.fromEntries(
          Object.keys(prevState).map((key) => [key, false])
        ),
      }));
      setDefaultValues({
        ...defaultValues,
        XCoordinate: data.XCoordinate,
        YCoordinate: data.YCoordinate,
        runName: data.preProcessorToggle.runName,
        agriApv: toggleValue,
        tiltIfFt: data?.tiltIfFt,
        maxAnglesOfTracking: data?.maxAngleOfTracking,
        moduleMaskPattern: data?.moduleMaskPattern,
        gapBetweenModules: data?.gapBetweenModules,
        height: data?.height,
        pitchOfRow: data?.preProcessorToggle?.pitchOfRows,
        azimuth: data?.preProcessorToggle?.azimuth,
        lengthOfOneRow: data?.preProcessorToggle?.lengthOfOneRow,
        pvModule: data.pvModule ? getSelectedListData(
          data?.pvModule?.id,
          pvModuleList
        )?.id : null,
        modeOfPvGeneration: data.modeOfOperationId ? getSelectedListData(
          data?.modeOfOperationId?.id,
          modeOfPvOperationList
        )?.id : null,
        moduleConfiguration: data?.moduleConfigs ? getSelectedListData(
          data?.moduleConfigs?.[0]?.id,
          moduleConfigurationList
        )?.id : null,
        soilId: data?.preProcessorToggle ? getSelectedListData(
          data?.preProcessorToggle?.soilType?.id,
          soilTypeList
        )?.id : null,
      });
      updatedFormState = {
        ...defaultValues,
        XCoordinate: data.XCoordinate,
        YCoordinate: data.YCoordinate,
        runName: data.preProcessorToggle.runName,
        agriApv: toggleValue,
        tiltIfFt: data?.tiltIfFt,
        maxAnglesOfTracking: data?.maxAngleOfTracking,
        moduleMaskPattern: data?.moduleMaskPattern,
        gapBetweenModules: data?.gapBetweenModules,
        height: data?.height,
        pitchOfRow: data?.preProcessorToggle?.pitchOfRows,
        azimuth: data?.preProcessorToggle?.azimuth,
        lengthOfOneRow: data?.preProcessorToggle?.lengthOfOneRow,
        pvModule:data.pvModule ? getSelectedListData(
          data?.pvModule?.id,
          pvModuleList
        )?.id : null,
        modeOfPvGeneration: data?.modeOfOperationId?.id ? getSelectedListData(
          data?.modeOfOperationId?.id,
          modeOfPvOperationList
        )?.id : null,
        moduleConfiguration: data?.moduleConfigs?.[0]?.id ? getSelectedListData(
          data?.moduleConfigs?.[0]?.id,
          moduleConfigurationList
        )?.id : null,
        soilId: data?.preProcessorToggle?.soilType?.id ? getSelectedListData(
          data?.preProcessorToggle?.soilType?.id,
          soilTypeList
        )?.id : null,
      };

      // disable tiltFt and maxAngleOfTracking fields based on mode of PV operation
      if (data?.modeOfOperationId?.id === 2) {
        setDisableFields((prevState) => ({
          ...prevState,
          tiltIfFt: true,
          maxAnglesOfTracking: false,
        }));
      } else if (data?.modeOfOperationId?.id === 1) {
        setDisableFields((prevState) => ({
          ...prevState,
          tiltIfFt: false,
          maxAnglesOfTracking: true,
        }));
      } else {
        setDisableFields((prevState) => ({
          //disable both fields by default
          ...prevState,
          tiltIfFt: true,
          maxAnglesOfTracking: true,
        }));
      }
    }
    if (formRef.current) {
      formRef.current(updatedFormState);
    }
  };

  const getThetaValue = (theta, thetaType) => {
    // Convert theta from degrees to radians
    const angle = Math.abs(theta) * (Math.PI / 180);

    // Calculate the angle (90 degrees - theta) in radians
    // const angle = Math.PI / 2 - thetaRadians;

    // Calculate the sine of the angle
    const thetaValue = thetaType === "sin" ? Math.sin(angle) : Math.cos(angle);

    // Define a small threshold for floating-point comparison
    const threshold = 1e-10;

    // Normalize the result to zero if it's within the threshold
    if (Math.abs(thetaValue) < threshold) {
      return 0;
    }

    return thetaValue;
  };

  const initialValues = {
    agriApv: "APV",
    pvModule: "",
    modeOfPvGeneration: "",
    tiltIfFt: "",
    maxAnglesOfTracking: "",
    moduleMaskPattern: "",
    gapBetweenModules: "",
    height: "",
    pitchOfRow: "",
    azimuth: "",
    lengthOfOneRow: "",
    moduleConfiguration: "",
    soilId: "",
  };

  const minMax = {
    height: [1, 10],
    lengthOfOneRow: [1, 500],
    gapBetweenModules: [0, 100],
    pitchOfRow: [0, 176],
  };

  const getSelectedListData = (id, list) => {
    const data = list.find((data) => data.id === id);
    if (data) {
      return data;
    }
    return null;
  };

  const changeHeightValidation = (formValues) => {
    if (
      !formValues.pvModule ||
      !formValues.moduleConfiguration ||
      !formValues.modeOfPvGeneration ||
      formValues.gapBetweenModules === null ||
      formValues.gapBetweenModules === undefined ||
      formValues.gapBetweenModules === ""
    ) {
      minMax.height = [1, 10];
      return;
    }
    if (
      (formValues.tiltIfFt === null ||
        formValues.tiltIfFt === undefined ||
        formValues.tiltIfFt === "") &&
      !formValues.maxAnglesOfTracking
    ) {
      minMax.height = [1, 10];
      return;
    }

    const selectedModuleData = getSelectedListData(
      formValues.pvModule,
      pvModuleList
    );
    const length = +selectedModuleData?.length / 1000;
    const width = +selectedModuleData?.width / 1000;
    const moduleField = getSelectedListData(
      formValues.moduleConfiguration,
      moduleConfigurationList
    );
    const moduleType = moduleField?.typeOfModule;
    const n = moduleField?.numberOfModules;
    let gap = formValues.gapBetweenModules / 1000;

    const sinTheta =
      formValues.modeOfPvGeneration === 1
        ? getThetaValue(formValues.tiltIfFt, "sin")
        : getThetaValue(formValues.maxAnglesOfTracking, "sin");

    if (moduleType === "P" || moduleType === "P'") {
      const minHeight = (sinTheta * (length * n + (n - 1) * gap)) / 2;
      minMax.height = [minHeight.toFixed(2), 10];
    } else {
      const minHeight = (sinTheta * (width * n + (n - 1) * gap)) / 2;
      minMax.height = [minHeight.toFixed(2), 10];
    }
  };

  const changeLengthOfOneRowValidation = (formValues) => {
    if (!formValues.pvModule || !formValues.moduleConfiguration) {
      minMax.lengthOfOneRow = [1, 500];
      return;
    }

    const selectedModuleData = getSelectedListData(
      formValues.pvModule,
      pvModuleList
    );
    const length = +selectedModuleData?.length / 1000;
    const width = +selectedModuleData?.width / 1000;

    const moduleField = getSelectedListData(
      formValues.moduleConfiguration,
      moduleConfigurationList
    );
    const moduleType = moduleField?.typeOfModule;

    if (moduleType === "P" || moduleType === "P'") {
      minMax.lengthOfOneRow = [width, 500];
    } else minMax.lengthOfOneRow = [length, 500];
  };

  const changeGapBetweenModulesValidation = (formValues) => {
    if (
      !formValues.lengthOfOneRow ||
      !formValues.pvModule ||
      !formValues.moduleConfiguration ||
      !formValues.modeOfPvGeneration ||
      !formValues.pitchOfRow
    ) {
      minMax.gapBetweenModules = [0, 100];
      return;
    }
    if (
      (formValues.tiltIfFt === null ||
        formValues.tiltIfFt === undefined ||
        formValues.tiltIfFt === "") &&
      !formValues.maxAnglesOfTracking
    ) {
      minMax.gapBetweenModules = [0, 100];
      return;
    }
    const selectedModuleData = getSelectedListData(
      formValues.pvModule,
      pvModuleList
    );
    const length = +selectedModuleData?.length / 1000;
    const width = +selectedModuleData?.width / 1000;
    const lengthOfOneRow = formValues.lengthOfOneRow * 1000;
    const moduleField = getSelectedListData(
      formValues.moduleConfiguration,
      moduleConfigurationList
    );
    const moduleType = moduleField?.typeOfModule;
    const n = moduleField?.numberOfModules;
    let l = 0; // length of module
    let lengthSideValidation = 0;
    let L = formValues.pitchOfRow;
    const cosTheta =
      formValues.modeOfPvGeneration === 1
        ? getThetaValue(formValues.tiltIfFt, "cos")
        : getThetaValue(formValues.maxAnglesOfTracking, "cos");

    {
      moduleType === "P" || moduleType === "P'" ? (l = length) : (l = width);
    }
    {
      moduleType === "P" || moduleType === "P'" ? (lengthSideValidation = width) : (lengthSideValidation = length);
    }
    if (moduleType === "P" || moduleType === "L") {
      if (n === 1) {
        minMax.gapBetweenModules = [0, ((lengthOfOneRow - lengthSideValidation * 1000))];
      }
      if (n > 1) {
        const maxGap = ((L - cosTheta * n * l) / (cosTheta * (n - 1))) * 1000;
        minMax.gapBetweenModules = [0, +maxGap.toFixed(2)];
      }
    } else {
      const maxGap =
        ((L - cosTheta * 2 * n * l) / (cosTheta * (2 * n - 1))) * 1000;
      minMax.gapBetweenModules = [0, +maxGap.toFixed(2)];
    }
  };

  const changePitchOfRowsValidation = (formValues) => {
    if (
      !formValues.pvModule ||
      !formValues.moduleConfiguration ||
      formValues.gapBetweenModules === null ||
      formValues.gapBetweenModules === undefined ||
      formValues.gapBetweenModules === "" ||
      !formValues.modeOfPvGeneration
    ) {
      minMax.pitchOfRow = [0, 176];
      return;
    }

    const selectedModuleData = getSelectedListData(
      formValues.pvModule,
      pvModuleList
    );
    const length = +selectedModuleData?.length / 1000;
    const width = +selectedModuleData?.width / 1000;
    const moduleField = getSelectedListData(
      formValues.moduleConfiguration,
      moduleConfigurationList
    );
    const moduleType = moduleField?.typeOfModule;
    const n = moduleField?.numberOfModules;
    let l = 0; // length of module
    let gap = formValues.gapBetweenModules / 1000;
    let cosTheta = 1;
    //   formValues.modeOfPvGeneration === 1
    //     ? getThetaValue(formValues.tiltIfFt, "cos")
    //     : getThetaValue(formValues.maxAnglesOfTracking, "cos");

    // {
    //   cosTheta === 0 ? (cosTheta = 0.01) : (cosTheta = cosTheta);
    // }

    {
      moduleType === "P" || moduleType === "P'" ? (l = length) : (l = width);
    }
    if (moduleType === "P" || moduleType === "L") {
      const minVal = cosTheta * (l * n + (n - 1) * gap);
      const maxVal = 10 * (cosTheta * (l * n + (n - 1) * gap));
      minMax.pitchOfRow = [
        Math.abs(minVal.toFixed(2)),
        Math.abs(maxVal.toFixed(2)),
      ];
    } else {
      const minVal = cosTheta * (l * 2 * n + (2 * n - 1) * gap);
      const maxVal = 10 * (cosTheta * (l * 2 * n + (2 * n - 1) * gap));
      minMax.pitchOfRow = [
        Math.abs(minVal.toFixed(2)),
        Math.abs(maxVal.toFixed(2)),
      ];
    }
  };

  const handleApvToggle = (e) => {
    const toggleValue = e.target.value;
    dispatch(setApvToggle({ apvToggle: toggleValue }));
    handleDisableFields(toggleValue, responseData);
  };

  const dependencies = {
    pvModule: ["height", "lengthOfOneRow", "gapBetweenModules", "pitchOfRow"],
    moduleConfiguration: [
      "height",
      "lengthOfOneRow",
      "gapBetweenModules",
      "pitchOfRow",
    ],
    tiltIfFt: ["height", "gapBetweenModules", "pitchOfRow"],
    maxAnglesOfTracking: ["height", "gapBetweenModules", "pitchOfRow"],
    lengthOfOneRow: ["gapBetweenModules", "pitchOfRow"],
    gapBetweenModules: ["pitchOfRow", "height"],
    modeOfPvGeneration: ["pitchOfRow", "gapBetweenModules"],
    pitchOfRow: ["gapBetweenModules"],
  };

  const validateHeight = (val) => {
    if (!val) return true;
    const value = parseFloat(val);
    const [min, max] = minMax.height;
    if (min > max) {
      if (value > max) {
        return `height cannot be greater than ${max}`;
      }
      return `Tip of module intersecting ground.`;
    }
    if (value < min) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }
    if (value > max) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }

    return true;
  };
  const validateLengthOfOneRow = (value) => {
    if (!value) return true;
    const [min, max] =
      apvToggleValue === "Only Agri" ? [2, 500] : minMax.lengthOfOneRow;
    if (min > max) {
      return `length of one row cannot be greater than ${max}`;
    }
    if (value < min) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }
    if (value > max) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }

    return true;
  };
  const validateGapBetweenModules = (value) => {
    if (!value) return true;
    const [min, max] = minMax.gapBetweenModules;
    if (min > max) {
      return `Reduce Gap - pitch distance exceeded with current value.`;
    }
    if (value < min) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }
    if (value > max) {
      return `Only accepts values ranging from ${min} to ${max}`;
    }

    return true;
  };
  const validatePitchOfRow = (value) => {
    if (!value) return true;
    let [min, max] =
      apvToggleValue === "Only Agri" ? [2, 176] : minMax.pitchOfRow;

    max = Math.min(max, 176);
    if (min > max) {
      if (value < max) {
        return `Pitch too less, module intersection detected. Should be greater than ${max}}`;
      }
      if (value >= max) {
        return `Min GCR 10% required. Reduce Pitch.  Should be less than ${max}}`;
      }
    }
    if (value < min) {
      return `Pitch too less, module intersection detected. range[${min}, ${max}]`;
    }
    if (value > max) {
      return `Min GCR 10% required. Reduce Pitch. range[${min}, ${max}]`;
    }

    return true;
  };

  // for dynamic validations
  const updateMinMax = (field, formValues) => {
    // console.log("logggggsss", formValues)
    dispatch(
      setPvParameters({
        pvParameters: {
          ...formValues,
          simulationTime: formValues.simulationTime?.toISOString(), // Convert Day.js object to ISO string
        },
      })
    );
    // dispatch(setPvParameters({ pvParameters: formValues }));
    if (field === "pvModule" || field === "agriApv") {
      changeHeightValidation(formValues);
      changeLengthOfOneRowValidation(formValues);
      changeGapBetweenModulesValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "moduleConfiguration") {
      changeHeightValidation(formValues);
      changeLengthOfOneRowValidation(formValues);
      changeGapBetweenModulesValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "tiltIfFt") {
      changeHeightValidation(formValues);
      changeGapBetweenModulesValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "maxAnglesOfTracking") {
      changeHeightValidation(formValues);
      changeGapBetweenModulesValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "modeOfPvGeneration") {
      changeHeightValidation(formValues);
      changeGapBetweenModulesValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "lengthOfOneRow") {
      changeGapBetweenModulesValidation(formValues);
      changeHeightValidation(formValues);
      changePitchOfRowsValidation(formValues);
    }
    if (field === "gapBetweenModules") {
      changePitchOfRowsValidation(formValues);
      changeHeightValidation(formValues);
    }
    if (field === "pitchOfRow") {
      changeGapBetweenModulesValidation(formValues);
    }
  };

  const handleRunNameValidation = (val) => {
    if (val?.length < 5) return "Minimum 5 characters are allowed";
    if (val?.length > 20) return "Maximum 20 characters are allowed";
    const regex = /^[a-zA-Z]/;
    if (!regex.test(val)) return "First character must be an alphabet"
  }

  return (
    <Container>
      {loader ? (
        <CircularProgress
          sx={{
            position: "absolute",
            left: "50%",
            top: "50%",
            transform: "translate(-50%, -50%)",
          }}
        />
      ) : (
        <Grid item className="leftSection">
          <Card
            sx={{
              borderRadius: 5,
              marginBottom: "100px",
            }}
          >
            <div className="title">PV Parameters</div>
            <div className="formContent">
              <CustomFormContainer
                formRef={formRef}
                getFormStateRef={getFormStateRef}
                formData={pvGeometry(
                  handleRunNameValidation,
                  validateHeight,
                  disableFields,
                  pvModuleList,
                  modeOfPvOperationList,
                  moduleConfigurationList,
                  soilTypeList,
                  handleModeOfPvGeneration,
                  handleApvToggle,
                  validateLengthOfOneRow,
                  validateGapBetweenModules,
                  validatePitchOfRow,
                  minMax
                )}
                dependencies={dependencies}
                updateMinMax={updateMinMax}
                minMax={minMax}
                defaultValues={defaultValues}
                initialValues={initialValues}
                watchList={watchList}
                onFormSubmit={onSubmit}
                buttonLabel="Save & Next"
                buttonPosition="right"
              />
            </div>
          </Card>
        </Grid>
      )}
    </Container>
  );
};

export default PvGeometry;

const Wrapper = styled.div`
  overflow: hidden;
  .stepperContainer {
    background-color: ${({ theme }) => theme.palette.background.secondary};
    padding: 16px 0px;
  }
`;

const Container = styled.div`
  margin: 20px;
  height: 85vh;
  overflow: auto;
  &::-webkit-scrollbar {
    height: 4px;
    width: 4px;
  }

  &::-webkit-scrollbar-track {
    box-shadow: #d5d5d5;
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.palette.primary.main};
    border-radius: 8px;
  }
  .leftSection {
    /* overflow: auto; */
    /* background: ${({ theme }) => theme.palette.background.secondary}; */
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
