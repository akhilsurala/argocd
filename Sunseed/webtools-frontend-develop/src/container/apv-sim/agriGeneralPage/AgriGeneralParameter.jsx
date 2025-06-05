import React, { useEffect, useState } from "react";
import { styled, useTheme } from "styled-components";

import ProjectCard from "../ProjectCard";
import CustomFormContainer from "../../../components/CustomFormContainer";
import { projectForm } from "../../../utils/formData/projectForm";
import { pvGeometry } from "../../../utils/formData/pvGeometry";
import {
  Button,
  Card,
  FormHelperText,
  Grid,
  IconButton,
  MenuItem,
  Select,
  TextField,
  Tooltip,
} from "@mui/material";
import HorizontalStepper from "../../../components/HorizontalStepper";
import { agriGeneral } from "../../../utils/formData/agriGeneral";
import SelectWithInput from "./component/SelectWithInput";
import { CustomSvgIconForToolTip } from "../../dashboard/CustomSvgIcon";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import CustomSelect from "./component/CustomSelect";
import CustomInputField from "./component/CustomInputField";
import CustomSwitch from "./component/CustomSwitch";
import {
  addAgriGeneralParameter,
  addProject,
  getProjectsOfAgriGeneralParameters,
  getProjectsOfAgriGeneralParametersMasterData,
  updateAgriGeneralParameter,
} from "../../../api/userProfile";
import { Navigate, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setAgriGeneralParametersInRedux } from "../../../redux/action/agriGeneralParameter";
import messages from "./messages";
import { useIntl } from "react-intl";
import { useImmer } from "use-immer";
import { setApvToggle } from "../../../redux/action/preProcessorAction";
import { getThetaValue } from "../../../utils/dynamicValidation";

const AgriGeneralParameter = ({ setActiveStep, projectId, runId }) => {
  const theme = useTheme();
  const watchList = ["agriApv"];
  const [watchState, setWatchState] = useState();
  const [disableFields, setDisableFields] = useState(false);

  const [isVarient, setIsVarient] = useState(false);
  const [agriPvProtectionHeights, setAgriPvProtectionHeights] = useState(null);
  const navigate = useNavigate();
  const [id, setId] = useState(null);

  // const pvHeight = useSelector(
  //   (state) => state.preProcessor.pvParameters.height
  // );
  const toggle = useSelector((state) => state.preProcessor.apvToggle);
  const disabledAllField = toggle === "Only PV" ? true : false;
  const disabledOnlyAgriField = toggle === "Only Agri" ? true : false;
  const pitch = useSelector(
    (state) => state.preProcessor.pvParameters.pitchOfRow
  );

  // const width = 1;
  const [triggerField, setTriggerField] = useState("");

  const pvParameters = useSelector((state) => state.preProcessor.pvParameterDetails);
  let length = pvParameters?.pvModule?.length / 1000;
  let width = pvParameters?.pvModule?.width / 1000;
  let lengthOfOneRow = pvParameters?.preProcessorToggles?.lengthOfOneRow;
  let pitchOfRow = pvParameters?.preProcessorToggles?.pitchOfRows;
  let azimuth = pvParameters?.preProcessorToggles?.azimuth;
  let pvHeight = pvParameters?.height;
  let n = pvParameters?.moduleConfigs?.[0]?.numberOfModules;
  let moduleType = pvParameters?.moduleConfigs?.[0]?.typeOfModule;
  let gap = pvParameters.gapBetweenModules / 1000;
  let sinTheta =
    pvParameters?.modeOfOperationId?.modeOfOperation === "Fixed Tilt"
      ? getThetaValue(pvParameters.tiltIfFt, "sin")
      : getThetaValue(pvParameters.maxAngleOfTracking, "sin");

  let l = moduleType === "P" || moduleType === "P'" ? length : width;

  const getProtectionHeight = () => {
    return toggle === "Only Agri" ? [0.35, 10] : [0, +(parseFloat(pvHeight) - (sinTheta * ((l * n) + ((n - 1) * gap)) / 2)).toFixed(2)];
  };
  const defaultObjMinMax = {
    protectionHeight: getProtectionHeight(),
    bedWidth: [100, 2000],
    bedHeight: [0, 1000],
    bedcc: [1, 5],
  };
  const [minMaxRange, setMinMaxRange] = useImmer({ ...defaultObjMinMax });

  const dispatch = useDispatch();

  const intl = useIntl();

  const [protectionLayer, setProtectionLayer] = useState([
    {
      value: "-99",
      label: "None",
    },
  ]);
  const [irrigation, setIrrigation] = useState([]);
  const [irrigationFromAPI, setIrrigationFromAPI] = useState([]);
  const [soil, setSoil] = useState([]);
  const [restApiValue, setRestApiValue] = useState({});
  const {
    handleSubmit,
    control,
    watch,
    register,
    reset,
    trigger,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      agriPvProtectionHeight: [
        {
          height: "",
          protectionId: "-99",
        },
      ],

      isMulching: false,
    },
  });
  const {
    fields,
    update,
    append,
    prepend,
    remove,
    swap,
    move,
    insert,
    replace,
  } = useFieldArray({
    control, // control props comes from useForm (optional: if you are using FormContext)
    name: "agriPvProtectionHeight", // unique name for your Field Array
  });

  const watchFields = watch(["tempControl", "minTemp", "maxTemp"]); // you can also target specific fields by their
  const watchListAgri = watch("agriPvProtectionHeight");
  // console.log("watchListAgri", watchListAgri)
  useEffect(() => {
    if (triggerField) {
      trigger(triggerField);
      setTriggerField("");
    }
  }, [minMaxRange, triggerField]);

  const changeBedWidthValidation = (formValues) => {
    const bedAngle = formValues.bedAngle || 0;
    const bedHeight = formValues.bedHeight || 0;
    const numberOfBeds = formValues.bedcc || 1;

    const tanTheta = getThetaValue(bedAngle, "tan");
    const h = parseFloat(bedHeight);
    const x = bedAngle == 0 || bedHeight == 0 ? 0 : h / tanTheta;

    const maxWidth = (pitch * 1000) / numberOfBeds - 2 * x;
    // console.log("max", maxWidth);
    return [100, maxWidth.toFixed(2)];
  };

  const changeNumberOfBedValidation = (formValues) => {
    const bedAngle = formValues.bedAngle || 0;
    const bedHeight = formValues.bedHeight || 0;
    const bedWidth = formValues.bedWidth || 100;

    const tanTheta = getThetaValue(bedAngle, "tan");
    const w = parseFloat(bedWidth);
    const h = parseFloat(bedHeight);
    const bedBottomWidth =
      bedAngle == 0 || bedHeight == 0 ? w : w + 2 * (h / tanTheta);
    const bedMaxVal = (pitch * 1000) / bedBottomWidth;
    // console.log("num", bedMaxVal)
    return [1, parseInt(bedMaxVal)];
  };
  // console.log("num", error)

  React.useEffect(() => {
    const subscription = watch((value, { name, type }) => {
      // console.log(name, type)
      if (type === "click") return;
      if (type === undefined && "agriPvProtectionHeight" === name) return;
      // if (type == 'change') {

      if (
        name == "bedcc" ||
        name == "bedAngle" ||
        name == "bedHeight" ||
        name == "bedWidth"
      ) {
        if (
          value.bedHeight &&
          value.bedAngle &&
          value.bedcc &&
          value.bedWidth
        ) {
          setMinMaxRange((draft) => {
            draft.bedWidth = changeBedWidthValidation(value);
          });
        } else {
          setMinMaxRange((draft) => {
            draft.bedWidth = [100, 2000];
          });
        }

        if (value.bedWidth) setTriggerField("bedWidth");
      }
      if (name == "bedWidth" || name == "bedHeight" || name == "bedAngle") {
        if (value.bedWidth && value.bedAngle && value.bedHeight) {
          const tanTheta = getThetaValue(value.bedAngle, "tan");
          const height = (value.bedWidth * tanTheta) / 2;
          setMinMaxRange((draft) => {
            draft.bedHeight = [0, height.toFixed(2)];
          });
        } else {
          setMinMaxRange((draft) => {
            draft.bedHeight = [0, 1000];
          });
        }

        setTimeout(() => {
          if (value.bedHeight) setTriggerField("bedHeight");
        }, 200);
      }

      if (
        !disabledOnlyAgriField &&
        (name == "bedWidth" ||
          name == "bedHeight" ||
          name == "bedAngle" ||
          name == "bedcc")
      ) {
        if (
          value.bedcc &&
          value.bedWidth &&
          value.bedAngle &&
          value.bedHeight
        ) {
          setMinMaxRange((draft) => {
            draft.bedcc = changeNumberOfBedValidation(value);
          });
        } else {
          setMinMaxRange((draft) => {
            draft.bedcc = [1, 5];
          });
        }

        setTimeout(() => {
          if (value.bedcc) {
            setTriggerField("bedcc");
          }
        }, 50);
      }

      trigger(name);
      // }
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  useEffect(() => {
    reset();
  }, [handleSubmit]);
  const saveProjectAgriGeneralParameters = (data) => {
    addAgriGeneralParameter(data, null, projectId, runId)
      .then((response) => {
        if (response.data.httpStatus === "CREATED") {
          setActiveStep((active) => active + 1);
        }
      })
      .catch((error) => {
        console.log(error);

        alert(error.response.data.errorMessages[0]);
      })
      .finally(() => {
        // setLoader(false);
      });
  };

  const findMissingProtectionIds = (a, b) => {
    // Create a set of protection IDs from array b
    const bProtectionIds = new Set(
      b?.map((item) => item.agriPvProtectionHeightId)
    );

    // Filter the protection IDs in array a that are not in array b
    const missingProtectionIds = a
      .filter((item) => !bProtectionIds.has(item.agriPvProtectionHeightId))
      .map((item) => item.agriPvProtectionHeightId);

    return missingProtectionIds;
  };
  const updateProjectAgriGeneralParameters = (data) => {
    const missingIds = findMissingProtectionIds(
      agriPvProtectionHeights,
      data?.agriPvProtectionHeight
    );
    data.protectionDelete = missingIds;
    updateAgriGeneralParameter(data, null, projectId, id, runId)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          setActiveStep((active) => active + 1);
        }
      })
      .catch((error) => {
        console.log(error);

        alert(error.response.data.errorMessages[0]);
      })
      .finally(() => {
        // setLoader(false);
      });
  };

  const updateFieldArray = (agriPvProtectionHeight) => {
    // const updatedData = agriPvProtectionHeight.map((item) => {
    //   console.log("mmm", item, protectionLayer)
    //   const existsInMaster = protectionLayer.some((master) => master.id === item.protectionId);
    //   return {
    //     ...item,
    //     protectionId: existsInMaster ? item.protectionId : "-99",
    //     height: existsInMaster ? item.height : "",
    //   };
    // });

    // console.log("protectionLayer")
    if (agriPvProtectionHeight?.length > 0) {
      reset({
        agriPvProtectionHeight: [],
      });

      replace(agriPvProtectionHeight);
      return;
    }
    reset({
      agriPvProtectionHeight: [{
        height: "",
        protectionId: "-99",
      },],
    });



  };

  useEffect(() => {
    if (irrigation && irrigationFromAPI && irrigation.some((item) => item.value === irrigationFromAPI))
      setValue("irrigationTypeId", irrigationFromAPI);
  }, [irrigation, irrigationFromAPI])
  function callGetProjectApiForAgriGeneralParameters() {
    // setLoader(true);

    getProjectsOfAgriGeneralParameters(null, projectId, runId)
      .then((response) => {
        if (response.data.httpStatus === "OK" && response.data.data) {
          const { agriPvProtectionHeight, bedParameter, ...rest } =
            response.data.data;
          setAgriPvProtectionHeights(agriPvProtectionHeight);

          dispatch(setAgriGeneralParametersInRedux(response.data.data));
          // console.log("response", response?.data?.data.isMaster)
          if (
            response?.data?.data?.isMaster !== undefined &&
            response?.data?.data?.isMaster !== null
          )
            setIsVarient(!response?.data?.data.isMaster);
          // for (let i = 0; i < agriPvProtectionHeight.length; i++) {
          //   reset({
          //     agriPvProtectionHeight: [],
          //   });
          //   replace(agriPvProtectionHeight);
          // }
          updateFieldArray(agriPvProtectionHeight)
          for (const [key, value] of Object.entries(bedParameter)) {
            // console.log(`${key}: ${value}`);
            if (disabledOnlyAgriField && key === "startPointOffset") {
              continue;
            }
            setValue(key, value);
          }

          setRestApiValue(rest);
          setValue("isMulching", rest.isMulching);
          setIrrigationFromAPI(rest.irrigationType);
          // setValue('soilId', rest.soilId);
          setValue("tempControl", rest.tempControl);
          setValue("maxTemp", rest.maxTemp);
          setValue("minTemp", rest.minTemp);
          setValue("trail", rest?.trail ? rest?.trail : "");
          setId(rest.id);

          setMinMaxRange((draft) => {
            draft = { ...defaultObjMinMax };
          });

          // console.log("hey", agriPvProtehow can i manage the updation, deletion and creation of nested data  between frontend and backend? can u share the payload that will be needed from frontend to backendctionHeight, bedParameter, rest)
          // setData(getProjectDto(response.data.data))
        }
      })
      .catch((error) => {
        // alert(error?.response?.data?.errorMessages?.[0])
        console.log(error);
      })
      .finally(() => {
        // setLoader(false);
      });
  }
  function callGetProjectApiForAgriGeneralParametersForMaster() {
    // setLoader(true);
    getProjectsOfAgriGeneralParametersMasterData(null, projectId, runId)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          const { protectionLayers, typeOfIrrigations, soils } =
            response.data.data;
          const transformedArray = protectionLayers.map((item) => ({
            value: item.id,
            label: item.name,
          }));

          const irrigationArray = typeOfIrrigations.map((item) => ({
            value: item.id,
            label: item.name,
          }));
          const soilArray = soils.map((item) => ({
            value: item.id,
            label: item.name,
          }));
          setProtectionLayer([
            {
              value: "-99",
              label: "None",
            },
            ...transformedArray,
          ]);
          setIrrigation(irrigationArray);
          setSoil(soilArray);

          // setProtectionLayer(ProtectionLayer.map((obj => return { value: obj.id, label: objname }));
          // setIrrigation(Irrigation)
          // setData(getProjectDto(response.data.data))
        } else {
          console.log("something went wront", response);
        }
      })
      .catch((error) => {
        alert(error?.response?.data?.errorMessages[0]);
        console.log(error);
      })
      .finally(() => {
        // setLoader(false);
      });
  }

  useEffect(() => {
    if (disabledAllField) {
      return;
    }
    callGetProjectApiForAgriGeneralParametersForMaster();

    callGetProjectApiForAgriGeneralParameters();
  }, []);

  // const onSubmit = (data) => {
  //     setActiveStep(1);
  // };
  function transformData(inputData) {
    // Directly map input to output without any conversions or transformations
    const outputData = {
      agriPvProtectionHeight: inputData.agriPvProtectionHeight.map((item) => ({
        agriPvProtectionHeightId: item.agriPvProtectionHeightId,
        height: item.height,
        protectionId: item.protectionId,
        protectionLayerName: item.protectionLayerName,
      })),
      bedParameter: {
        bedAngle: inputData.bedAngle,
        bedAzimuth: null,
        bedcc: inputData.bedcc,
        bedHeight: inputData.bedHeight,
        bedWidth: inputData.bedWidth,
        id: inputData.id,
        startPointOffset: null,
      },
      cloneId: null,
      id: inputData.id,
      irrigationType: inputData.irrigationTypeId,
      isMaster: true,
      isMulching: inputData.isMulching,
      maxTemp: inputData.maxTemp,
      minTemp: inputData.minTemp,
      projectId: 2, // Assuming projectId is a constant in the output
      runId: 84, // Assuming runId is a constant in the output
      // soilId: inputData.soilId,
      status: "created",
      tempControl: inputData.tempControl,
      trail: inputData.trail ? inputData.trail : null,
    };

    return outputData;
  }

  const getAgriPvProtectionHeight = (data) => {
    return data.filter((item) => item.protectionId !== "-99");
  };
  const tempControlData = [
    { value: "none", label: "None" },
    { value: "Absolute Min Max", label: "Absolute min max" },
    { value: "Trail Min Max", label: "Trail min max" },
  ];
  // const soilData = [{ value: 1, label: 'Slit' }, { value: 2, label: 'clay' }, { value: 3, label: 'Sandy' }, { value: 4, label: 'alluvial' }]
  const onSubmit = (data) => {
    dispatch(setAgriGeneralParametersInRedux(transformData(data)));
    const agriData = getAgriPvProtectionHeight(data.agriPvProtectionHeight);
    // const localData = { ...data, soilId: data.soilId ? data.soilId : restApiValue.soilId, agriPvProtectionHeight: agriData?.length ? agriData : null }
    const localData = {
      ...data,
      agriPvProtectionHeight: agriData?.length ? agriData : null,
    };
    if (disabledAllField) {
      setActiveStep((active) => active + 1);
      return;
    }
    if (id) {
      updateProjectAgriGeneralParameters(localData);
    } else {
      saveProjectAgriGeneralParameters(localData);
    }
  };

  const getTitleWithToolTip = (label, msg) => {
    return (
      <>
        <div style={{ display: "flex" }}>
          <div style={{ marginRight: "5px" }}>{label}</div>
          {msg && (
            <Tooltip
              title={<div dangerouslySetInnerHTML={{ __html: msg }} />}
              placement="right-end"
              componentsProps={{
                tooltip: {
                  sx: {
                    color: "#53988E",
                    backgroundColor: "#F2F7F6",
                    borderRadius: "8px",
                    border: "1px solid #53988E",
                  },
                },
              }}
            >
              <IconButton sx={{ padding: "0px" }}>
                <CustomSvgIconForToolTip style={{ color: "#53988E" }} />
              </IconButton>
            </Tooltip>
          )}
        </div>
        <Gap />
      </>
    );
  };

  const getProtectionLayerBlock = () => {
    return (
      <>
        {getTitleWithToolTip("Protection Layer")}

        <SelectWithInput
          register={register}
          trigger={trigger}
          append={append}
          selectComponentData={protectionLayer}
          fields={fields}
          remove={remove}
          watchListAgri={watchListAgri}
          errors={errors}
          watch={watch}
          minMaxRange={minMaxRange}
          disabledAllField={disabledAllField}
          setValue={setValue}
        />
      </>
    );
  };

  const getHeight = () => {
    const name = "bedHeight";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Height")}
        <CustomInputField
          name={name}
          type={"number"}
          control={control}
          errors={errors}
          disabled={disabledAllField}
          endLabel="millimeters"
          rules={{
            required: "This field is required",
            max: {
              value: minMaxRange.bedHeight[1],
              message: `Enter a numerical value within the range of ${minMaxRange.bedHeight[0]} to ${minMaxRange.bedHeight[1]}`, // JS only: <p>error message</p> TS only support string
            },
            min: {
              value: minMaxRange.bedHeight[0],
              message: `Enter a numerical value within the range of ${minMaxRange.bedHeight[0]} to ${minMaxRange.bedHeight[1]}`, // JS only: <p>error message</p> TS only support string
            },
          }}
        />
      </>
    );
  };

  const getStartingPointOffset = () => {
    const name = "startPointOffset";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Start Point Offset")}
        <CustomInputField
          name={name}
          type={"number"}
          control={control}
          errors={errors}
          disabled={disabledAllField || disabledOnlyAgriField}
          endLabel="millimeters"
          rules={{
            required: "This field is required",
            max: {
              value: (pitch / 2) * 1000,
              message: `Enter a numerical value within the range of ${-1000 * (pitch / 2)} to ${(pitch / 2) * 1000
                }`, // JS only: <p>error message</p> TS only support string
            },
            min: {
              value: -1000 * (pitch / 2),
              message: `Enter a numerical value within the range of ${-1000 * (pitch / 2)} to ${(pitch / 2) * 1000
                }`, // JS only: <p>error message</p> TS only support string
            },
          }}
        />
      </>
    );
  };
  const getWidth = () => {
    const name = "bedWidth";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Width")}
        <CustomInputField
          name={name}
          type={"number"}
          control={control}
          errors={errors}
          disabled={disabledAllField}
          endLabel="millimeters"
          rules={{
            required: "This field is required",
            max: {
              value: minMaxRange.bedWidth[1],
              message: `Enter a numerical value within the range of ${minMaxRange.bedWidth[0]} to ${minMaxRange.bedWidth[1]}`, // JS only: <p>error message</p> TS only support string
            },
            min: {
              value: minMaxRange.bedWidth[0],
              message: `Enter a numerical value within the range of ${minMaxRange.bedWidth[0]} to ${minMaxRange.bedWidth[1]}`, // JS only: <p>error message</p> TS only support string
            },
          }}
        />
      </>
    );
  };
  const getAngle = () => {
    const name = "bedAngle";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Angle of Bed")}
        <CustomInputField
          name={name}
          type={"number"}
          control={control}
          endLabel="degree"
          errors={errors}
          disabled={disabledAllField}
          rules={{
            required: "This field is required",
            max: {
              value: 75,
              message: "Enter a numerical value within the range of 0-75", // JS only: <p>error message</p> TS only support string
            },
            min: {
              value: 0,
              message: "Enter a numerical value within the range of 0-75", // JS only: <p>error message</p> TS only support string
            },
          }}
        />
      </>
    );
  };

  // console.log("error", errors)

  //Error Component
  const getSoilType = () => {
    const name = "soilId";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Soil Type")}
        <CustomSelect
          name={name}
          control={control}
          errors={errors}
          data={soil}
          disabledAllField={disabledAllField || isVarient}
        />
      </>
    );
  };

  const getTempratureControl = () => {
    const name = "tempControl";
    const min = "minTemp";
    const max = "maxTemp";
    const trail = "trail";
    return (
      <>
        <Gap />
        {getTitleWithToolTip(
          "Temperature Control",
          intl.formatMessage({ ...messages.tempControl })
        )}
        <CustomSelect
          name={name}
          control={control}
          errors={errors}
          data={tempControlData}
          disabledAllField={disabledAllField}
        />

        {(watchFields[0] === "Absolute Min Max" ||
          watchFields[0] === "Trail Min Max") && (
            <div>
              <Gap />
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  gap: "20px",
                }}
              >
                <div>
                  <CustomInputField
                    name={min}
                    type={"number"}
                    control={control}
                    errors={errors}
                    disabled={disabledAllField}
                    rules={{
                      required: "Input Required",

                      max: {
                        value: 50,
                        message:
                          "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                      },
                      min: {
                        value: 1,
                        message:
                          "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                      },
                      validate: {
                        lessThanTen: (v) =>
                          parseInt(v) <= watchFields[2] ||
                          !watchFields[2] ||
                          "Min temp cannot be higher than the Max temp",
                      },
                    }}
                    placeholder={"Min Value"}
                  />
                </div>
                <div>
                  <CustomInputField
                    name={max}
                    type={"number"}
                    control={control}
                    errors={errors}
                    disabled={disabledAllField}
                    rules={{
                      required: "Input Required",
                      max: {
                        value: 50,
                        message:
                          "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                      },
                      min: {
                        value: 1,
                        message:
                          "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                      },
                      validate: {
                        lessThanTen: (v) =>
                          parseInt(v) >= watchFields[1] ||
                          !watchFields[1] ||
                          "Max temp cannot be lower than the Min temp ",
                      },
                    }}
                    placeholder={"max Value"}
                  />
                </div>
              </div>
              <Gap />
              {watchFields[0] === "Trail Min Max" && (
                <CustomInputField
                  name={trail}
                  type={"number"}
                  control={control}
                  disabled={disabledAllField}
                  errors={errors}
                  rules={{
                    required: "Input Required",
                    max: {
                      value: 50,
                      message:
                        "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                    },
                    min: {
                      value: 1,
                      message:
                        "Enter an integer value within the range of 1 to 50", // JS only: <p>error message</p> TS only support string
                    },
                  }}
                  placeholder={"Enter Trail Value"}
                />
              )}
            </div>
          )}
      </>
    );
  };
  const bedAzimuthList = [
    {
      value: 0,
      label: "ALONG",
    },
    {
      value: 1,
      label: "ACROSS",
    },
  ];
  const getBedCCAndAzumat = () => {
    const name = "bedcc";
    const name1 = "bedAzimuth";
    return (
      <>
        <Gap />
        <div
          style={{
            display: "flex",
            gap: "20px",
            justifyContent: "space-between",
          }}
        >
          <div style={{ flexGrow: "1" }}>
            {getTitleWithToolTip("No. of beds/pitch")}
            <CustomInputField
              name={name}
              type={"number"}
              noFlotingValue={true}
              control={control}
              errors={errors}
              disabled={disabledAllField}
              rules={{
                required: "This field is required",
                max: {
                  value: minMaxRange.bedcc[1],
                  message:
                    "Only accepts values ranging from 1 to " +
                    minMaxRange.bedcc[1], // JS only: <p>error message</p> TS only support string
                },
                min: {
                  value: minMaxRange.bedcc[0],
                  message:
                    "Only accepts values ranging from 1 to " +
                    minMaxRange.bedcc[1], // JS only: <p>error message</p> TS only support string
                },
              }}
            />
          </div>
          <div style={{ flexGrow: "1" }}>
            {getTitleWithToolTip(
              "Bed Azimuth",
              intl.formatMessage({ ...messages.bedAzimuth })
            )}
            {/* <CustomInputField name={name1} type={'number'} control={control} errors={errors} disabled={disabledAllField || disabledOnlyAgriField} rules={{
                        required: "This field is required", max: {
                            value: 360,
                            message: 'Enter an integer value within the range of 0 to 360' // JS only: <p>error message</p> TS only support string
                        },
                        min: {
                            value: 0,
                            message: 'Enter an integer value within the range of 0 to 360' // JS only: <p>error message</p> TS only support string
                        }
                    }} /> */}

            <CustomSelect
              name={name1}
              control={control}
              errors={errors}
              data={bedAzimuthList}
              disabledAllField={disabledAllField}
            />
          </div>
        </div>

        <Gap />
      </>
    );
  };

  const getTypeOfIrrigation = () => {
    const name = "irrigationTypeId";
    return (
      <>
        <Gap />
        {getTitleWithToolTip("Type of irrigation")}
        <CustomSelect
          name={name}
          control={control}
          errors={errors}
          data={irrigation}
          disabledAllField={disabledAllField}
        />
      </>
    );
  };

  return (
    <Container>
      <Grid container spacing={4}>
        <Grid item md={12} className="leftSection">
          <Card
            sx={{
              borderRadius: 5,
              marginBottom: "50px",
            }}
          >
            <div className="title">Agri General Parameters</div>

            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <div className="formContent">
                {getProtectionLayerBlock()}
                {getTypeOfIrrigation()}
                {/* {getSoilType()} */}

                {getTempratureControl()}

                <Gap />
                <CustomSwitch
                  name="isMulching"
                  control={control}
                  label="Will mulching be used?"
                  disabled={disabledAllField}
                />
                <Gap />
                <div
                  style={{ borderBottom: "1px dashed #E0E0E0", height: "1px" }}
                ></div>

                <Gap />
                <p
                  style={{
                    fontSize: "16px",
                    font: "Montserrat",
                    fontWeight: "600",
                  }}
                >
                  Bed Parameters
                </p>

                {getStartingPointOffset()}
                {getHeight()}
                {getWidth()}
                {getAngle()}
                {getBedCCAndAzumat()}
                <div style={{ display: "flex", justifyContent: "flex-end" }}>
                  <Button
                    variant="contained"
                    className="btn"
                    data-testid="submitButton"
                    onClick={() => setActiveStep((active) => active - 1)}
                    sx={{
                      fontFamily: "Open Sans",
                      fontSize: "14px",
                      fontWeight: "700",
                      textTransform: "capitalize",
                      background: "transparent",
                      color: "grey",
                      "&:hover": {
                        backgroundColor: "transparent",
                      },

                      alignSelf: "flex-end",
                      width: "140px",
                    }}
                  >
                    Previous
                  </Button>

                  <Button
                    type="submit"
                    variant="contained"
                    className="btn"
                    data-testid="submitButton"
                    sx={{
                      fontFamily: "Open Sans",
                      fontSize: "14px",
                      fontWeight: "700",

                      marginLeft: "20px",
                      textTransform: "capitalize",
                      background: theme.palette.secondary.main,
                      "&:hover": {
                        backgroundColor: theme.palette.secondary.main,
                      },

                      alignSelf: "flex-end",
                      width: "140px",
                    }}
                  >
                    Save & Next
                  </Button>
                </div>
              </div>
            </form>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default AgriGeneralParameter;

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
  font-weight: 500;

  /* .leftSection {
    background: ${({ theme }) => theme.palette.background.secondary};
  } */

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
  .label {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
    color: #474f50;
    margin-bottom: 6px;
  }
  .emptyLabel {
    height: 26px;
  }
`;
const Gap = styled.div`
  padding: 10px;
`;
