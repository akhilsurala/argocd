import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  FormControl,
  FormHelperText,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
  Tooltip,
  duration,
} from "@mui/material";
import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import {
  CustomSvgIconForCalender,
  CustomSvgIconForDelete,
  CustomSvgIconForLeaf,
  CustomSvgIconForToolTip,
} from "../../dashboard/CustomSvgIcon";
import styled, { useTheme } from "styled-components";
import CustomSelect from "../agriGeneralPage/component/CustomSelect";
import CustomInputField from "../agriGeneralPage/component/CustomInputField";
import { Controller, useForm } from "react-hook-form";
import { ExpandMore } from "@mui/icons-material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

import dayjs from "dayjs";
import AddedBedBlock from "./AddedBedBlock";
import AddRemoveBlocks from "./AddRemoveBlock";
import CustomDatePicker from "../../../components/CustomDatePicker";
import { useSelector } from "react-redux";
import { current, original } from "immer";
import { getCropMasterDataParameter } from "../../../api/userProfile";
import { useIntl } from "react-intl";

import messages from "./messages";
import { useImmer } from "use-immer";
import validationCrop from "./validationCrop";

const months = [
  { value: "default", label: "Select Month" },
  { value: 0, label: "January" },
  { value: 1, label: "February" },
  { value: 2, label: "March" },
  { value: 3, label: "April" },
  { value: 4, label: "May" },
  { value: 5, label: "June" },
  { value: 6, label: "July" },
  { value: 7, label: "August" },
  { value: 8, label: "September" },
  { value: 9, label: "October" },
  { value: 10, label: "November" },
  { value: 11, label: "December" },
];

const Error = ({ children }) => (
  <p style={{ color: "red", margin: "0px" }}>{children}</p>
);

const changeS1Validation = (formValues) => {
  if (
    formValues.bedWidth === null ||
    formValues.bedWidth === undefined ||
    formValues.bedWidth === "" ||
    formValues.o2 === null ||
    formValues.o2 === undefined ||
    formValues.o2 === ""
  ) {
    return 10000;
  }
  const L = formValues.lengthOfOneRow * 1000;
  const maxonBedWidth = formValues.bedWidth * 10;
  const maxonLengthOfOneRow = (L - 2 * formValues.o2) / 2;
  return Math.min(maxonBedWidth, maxonLengthOfOneRow);
};

const DefineBed = forwardRef(function DefineBed(
  {
    obj,
    setAgriCropParameterData,
    isVarient,
    agriCropParameterData,
    objCycle,
    objIndex,
    disabledCreatCycle,
    setDeletedCycle,
    setCount,
    updateIntialState,
    accordianOpen,
    checkValidation,
  },
  ref // The forwarded ref from the parent
) {
  // console.log("agriCropParameterData", agriCropParameterData, objIndex);
  const intl = useIntl();
  const [deletedBed, setDeletedBed] = useState([]);
  const theme = useTheme();
  const [isAccordionOpen, setIsAccordianOpen] = useState(accordianOpen);
  const [bedButtonName, setBedButtonName] = useState("Add Bed");
  const [currentBedId, setCurrentBedId] = useState("");
  const bedWidth = useSelector(
    (state) =>
      state?.preProcessor?.agriGeneralParameters?.bedParameter?.bedWidth
  ) || 100;
  const [startDateVal, setStartDateValue] = useState(
    obj.cycleStartDate === "" ? "" : dayjs(obj?.cycleStartDate)
  );
  const [lastEditedBedDetails, setLastEditedBedDetails] = useState(null);
  const cropData =
    useSelector(
      (state) =>
        state?.preProcessor?.agriCropParameterReducer?.crops
    ) || 100;

  // console.log("cropData", cropData);
  // console.log("tanTheta", tanTheta, bedAngle, Math.tan(bedAngle), bedBottomWidth);

  const lengthOfRow =
    useSelector(
      (state) =>
        state?.preProcessor?.pvParameters?.lengthOfRow
    ) || 10;
  const [triggerField, setTriggerField] = useState("");

  const getCurrentValueOfF5 = (currentType) => {
    const currentVal = cropData.find(val => val.id == currentType);

    // console.log("curent", currentType, currentVal.meta.f5)
    return currentVal?.meta?.f5;
  }

  const [minMaxRange, setMinMaxRange] = useImmer({
    s1: [

      getCurrentValueOfF5(cropData[0].id),
      changeS1Validation({ bedWidth, o2: null, lengthOfOneRow: lengthOfRow }),
    ],
    optionalS1: [

      getCurrentValueOfF5(cropData[0].id),
      changeS1Validation({ bedWidth, o2: null, lengthOfOneRow: lengthOfRow }),
    ],
  });

  // console.log("minMaxRange", minMaxRange);

  const [showErrorMessage, setShowErrorMessage] = useState("");
  const deleteCreatedCycle = (name) => {
    setAgriCropParameterData((draft) => {
      const index = draft.findIndex((cycle) => cycle.cycleName === name);
      const obj = original(draft[index]);
      if (obj.hasOwnProperty("id")) {
        setDeletedCycle((del) => [...del, obj.id]);
      }

      if (index !== -1) {
        draft.splice(index, 1); // Remove the item at the specified index
      }
    });

    updateIntialState();
  };

  const crops = useSelector(
    (state) => state.preProcessor.agriCropParameterReducer.crops
  );
  const [soil, setSoil] = useState([]);

  const filterCropsFromCycle = (crops, data) => {
    const matchingCropIds = new Set();

    data.cycleBedDetails.forEach((bed) => {
      const { cropId1, optionalCropType } = bed;

      if (cropId1) matchingCropIds.add(cropId1);
      if (optionalCropType) matchingCropIds.add(optionalCropType);
    });

    return crops.filter((crop) => matchingCropIds.has(crop.id));
  };
  useEffect(() => {
    if (isVarient) {
      const filteredCrops = filterCropsFromCycle(
        crops,
        agriCropParameterData[objIndex]
      );
      setSoil(
        filteredCrops.map((item) => ({
          value: item.id,
          label: item.name,
          duration: item.duration,
        }))
      );
    } else {
      setSoil(
        crops.map((item) => ({
          value: item.id,
          label: item.name,
          duration: item.duration,
        }))
      );
    }
  }, [crops]);

  const {
    handleSubmit,
    control,
    watch,
    register,
    reset,
    setValue,
    getValues,
    trigger,
    formState: { errors },
  } = useForm({
    defaultValues: {
      // Use `undefined` or a valid initial date if you have one
    },
  });

  // console.log("lastEditedBedDetails", lastEditedBedDetails)

  useImperativeHandle(ref, () => {
    if (lastEditedBedDetails)
      return {
        trigger,
      };
  });

  // function normalizeValue(value) {
  //     // Treat null, undefined, and "" as the same
  //     return value === null || value === undefined || value === "" ? null : value;
  // }

  // function compareObjects(obj1, obj2) {
  //     if (obj1 === undefined || obj2 === undefined) return false
  //     console.log("obj1", obj1, "obj2", obj2);

  //     // Check that both obj1 and obj2 are defined
  //     if (typeof obj1 !== 'object' || obj1 === null || typeof obj2 !== 'object' || obj2 === null) {
  //         return true; // If either is undefined or not an object, they differ
  //     }

  //     for (const key in obj2) {
  //         // Normalize values to treat null, undefined, and "" as the same
  //         const value1 = normalizeValue(obj1[key]);
  //         const value2 = normalizeValue(obj2[key]);

  //         // Check if the key exists in obj1 and if normalized values are the same
  //         if (!(key in obj1) || value1 !== value2) {
  //             return true; // Values differ or key missing, return true
  //         }
  //     }

  //     return false; // All values in obj2 match with obj1 considering null/undefined/"" as the same
  // }

  // useEffect(() => {

  //     if (compareObjects(lastEditedBedDetails, getValues())) {
  //         // trigger();
  //     }
  // }, [getValues()])

  const optionalCropType = watch("optionalCropType");
  const cropType = watch("cropId1");
  const O1 = watch("o1");
  const optionalO1 = watch("optionalO1");

  useEffect(() => {
    // console.log("o1", O2);
    if (O1)
      setMinMaxRange((draft) => {
        draft.optionalS1 = [
          getCurrentValueOfF5(optionalCropType),
          changeS1Validation({
            bedWidth,
            o2: O1,
            lengthOfOneRow: lengthOfRow,
          }),
        ];
        draft.s1 = [
          getCurrentValueOfF5(cropType),
          changeS1Validation({
            bedWidth,
            o2: O1,
            lengthOfOneRow: lengthOfRow,
          }),
        ];

      });
  }, [O1])

  useEffect(() => {
    if (triggerField) {
      trigger(triggerField);
      setTriggerField("");
    }
  }, [minMaxRange, triggerField]);



  // console.log("minmax", minMaxRange)
  React.useEffect(() => {
    const subscription = watch((value, { name, type }) => {
      // console.log('hey', value, name, type)
      if (type == "change") {
        if (name === "o2") {
          setMinMaxRange((draft) => {
            draft.s1 = [
              getCurrentValueOfF5(value.cropId1),
              changeS1Validation({
                bedWidth,
                o2: value.o2,
                lengthOfOneRow: lengthOfRow,
              }),
            ];
          });
          if (value.s1) setTriggerField("s1");
        }

        if (name === "cropId1") {
          setMinMaxRange((draft) => {
            draft.s1 = [
              getCurrentValueOfF5(value.cropId1),
              changeS1Validation({
                bedWidth,
                o2: value.o2,
                lengthOfOneRow: lengthOfRow,
              }),
            ];
          });
          if (value.s1) setTriggerField("s1");
        }

        if (name === "optionalO2") {
          setMinMaxRange((draft) => {
            draft.optionalS1 = [
              getCurrentValueOfF5(value.optionalCropType),
              changeS1Validation({
                bedWidth,
                o2: value.optionalO2,
                lengthOfOneRow: lengthOfRow,
              }),
            ];
          });
          if (value.optionalS1) setTriggerField("optionalS1");
        }

        if (name === "optionalCropType") {
          setMinMaxRange((draft) => {
            draft.optionalS1 = [
              getCurrentValueOfF5(value.optionalCropType),
              changeS1Validation({
                bedWidth,
                o2: value.optionalO2,
                lengthOfOneRow: lengthOfRow,
              }),
            ];
          });
          if (value.optionalS1) setTriggerField("optionalS1");
        }
        trigger(name);
      }
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  const getTitleWithToolTip = (label, tootlTipMsg) => {
    return (
      <>
        <div style={{ display: "flex" }}>
          <div style={{ marginRight: "5px" }}>{label}</div>
          {tootlTipMsg && (
            <Tooltip
              title={<div dangerouslySetInnerHTML={{ __html: tootlTipMsg }} />}
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
              <IconButton style={{ outline: '0px' }} sx={{ padding: "0px" }}>
                <CustomSvgIconForToolTip style={{ color: "#53988E" }} />
              </IconButton>
            </Tooltip>
          )}
        </div>
        <Gap />
      </>
    );
  };

  // Function to find the duration of a crop by ID
  const findDuration = (cropId, crops) => {
    const crop = crops.find((c) => c.value === cropId);
    return crop ? crop.duration : 0;
  };

  const findDurationForIndividual = (cropId, crops) => {
    const crop = crops.find((c) => c.id === cropId);

    return crop ? crop.duration : 0;
  };

  // Get maximum duration from all bed details
  const getMaxDuration = (cycleBedDetails, crops) => {
    let maxDuration = 0;
    let maxStrechedDuration = -200;

    cycleBedDetails.forEach((bed) => {
      const cropId1Duration = findDuration(bed.cropId1, crops); // Get cropId1 duration
      const optionalCropDuration = findDuration(bed.optionalCropType, crops); // Get optional crop duration

      maxDuration = Math.max(
        maxDuration,
        cropId1Duration,
        optionalCropDuration
      );
      const stretch = bed.stretch ? bed.stretch : 0; // Default to 0 if undefined or null
      const optionalStretch = bed.optionalStretch ? bed.optionalStretch : 0; // Default to 0 if undefined or null

      // Update max stretch duration (in case both stretch and optionalStretch are provided)
      if (optionalStretch !== 0)
        maxStrechedDuration = Math.max(
          maxStrechedDuration,
          stretch,
          optionalStretch
        );
      else maxStrechedDuration = Math.max(maxStrechedDuration, stretch);
    });
    // console.log("maxStretch", maxStrechedDuration)
    // If there is a stretch, calculate the adjusted duration
    if (maxStrechedDuration !== 0 && maxDuration !== 0) {
      // Calculate the stretch factor (percentage)
      const stretchFactor = maxStrechedDuration / 100;

      // Adjust maxDuration by the stretch factor
      const adjustedDuration = maxDuration + maxDuration * stretchFactor;
      // console.log("maxDuration", maxDuration, "value of maxDuration * stretchFactor", (maxDuration * stretchFactor), adjustedDuration)
      return Math.round(adjustedDuration);
    }

    return Math.round(maxDuration); // Return the original max duration if no stretch is applied
  };

  function getResponsiveDatePickers() {
    let previouslySelectedDate = "";
    let firstSelectedDate = dayjs().format("YYYY-MM-DD");
    let daysCount = 2;
    if (objIndex > 0) {
      previouslySelectedDate =
        agriCropParameterData[objIndex - 1].cycleStartDate;
      firstSelectedDate = agriCropParameterData[0].cycleStartDate;

      const maxDuration = getMaxDuration(
        agriCropParameterData[objIndex - 1].cycleBedDetails,
        soil
      );
      // console.log("jjjjj", agriCropParameterData[objIndex - 1].cycleBedDetails)
      daysCount = maxDuration;
    }

    // console.log("daysCount",daysCount, firstSelectedDate, previouslySelectedDate);
    return (
      <div>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <div style={{ marginTop: "20px" }}>
            <CustomDatePicker
              onChange={setStartDateValue}
              value={startDateVal}
              errors={errors}
              name="startDate"
              label="Start Date"
              pastYearRange={0}
              futureYearRange={1}
              firstSelectedDate={firstSelectedDate}
              previouslySelectedDate={previouslySelectedDate} // New prop for previously selected date
              page="definedBed"
              daysCount={daysCount}
            />
          </div>
        </LocalizationProvider>
      </div>
    );
  }

  const addCycleBedDetail = (newBedDetail) => {
    setAgriCropParameterData((draft) => {
      const cycle = draft.find((cycle) => cycle.cycleName === obj.cycleName);
      cycle.cycleBedDetails.push({
        ...newBedDetail,
        bedName: `Bed ${cycle.cycleBedDetails.length + 1}`,
      });
      // cycle.cycleStartDate = newBedDetail.startDate;
    });
  };
  const updateCycleBedDetails = (bedId, updatedBedDetail) => {
    setAgriCropParameterData((draft) => {
      // Find the cycle by cycleName
      const cycle = draft.find((cycle) => cycle.cycleName === obj.cycleName);

      if (cycle) {
        // Find the specific bed by bedId within cycleBedDetails
        const bedDetail = cycle.cycleBedDetails.find((bed) => bed.id === bedId);

        if (bedDetail) {
          // Update the bedDetail with new values
          Object.assign(bedDetail, updatedBedDetail);
        } else {
          console.log(`No bed found with id: ${bedId}`);
        }
      } else {
        console.log(`No cycle found with name: ${cycleName}`);
      }
    });
  };

  const onSubmit = (data) => {
    // console.log(data, "heeeee");
    const formattedData = {
      ...data,
      startDate: data.startDate
        ? dayjs(startDateVal).format("YYYY-MM-DD")
        : null,
    };
    // console.log("currentBedId", currentBedId)
    // setAddedBed((val) => [...val, formattedData]);
    if (currentBedId) {
      updateCycleBedDetails(currentBedId, formattedData);
      setCurrentBedId("");
      setLastEditedBedDetails(null);
    } else {
      addCycleBedDetail(formattedData);
    }
    reset();
    // reset({ startDate: startDateVal });
    // console.log("data", data.startDate)
  };

  useEffect(() => {
    if (startDateVal) {
      setAgriCropParameterData((draft) => {
        if (draft[objIndex]) {
          draft[objIndex].cycleStartDate =
            dayjs(startDateVal).format("YYYY-MM-DD");
          // console.log("Updated object:", draft[objIndex]);
        }
        // if (objs.cycleBedDetails && obj.cycleBedDetails.length > 0) {
        //     objs.cycleBedDetails[objIndex]['cycleStartDate'] = startDateVal
        // }
      });
    }
  }, [startDateVal]);

  const checkCycleDates = (data) => {
    for (let i = 0; i < data.length - 1; i++) {
      const currentCycle = data[i];
      const nextCycle = data[i + 1];
      const currentCycleDate = dayjs(currentCycle.cycleStartDate);
      const nextCycleDate = dayjs(nextCycle.cycleStartDate);

      let maxDuration = 0;

      // Calculate the maximum duration for the current cycle
      currentCycle.cycleBedDetails.forEach((bed) => {
        const primaryDuration = findDurationForIndividual(bed.cropId1, crops);
        const optionalDuration = bed.optionalCropType
          ? findDurationForIndividual(bed.optionalCropType, crops)
          : 0;

        // Store the maximum of primary or optional duration for this bed
        maxDuration = Math.max(maxDuration, primaryDuration, optionalDuration);
      });

      // Calculate the end date of the current cycle based on the max duration
      const currentCycleEndDate = currentCycleDate.add(maxDuration, "days");

      // Check if it crosses the start date of the next cycle
      if (currentCycleEndDate.isAfter(nextCycleDate)) {
        return `Error: Cycle at position ${i + 1} ("${currentCycle.cycleName
          }") should have an adjusted start date.`;
      }
    }

    // No errors found
    return "";
  };

  const validationMessage = async () => {
    if (!startDateVal) {
      return "Please select start date";
    }
    const msg = checkCycleDates(agriCropParameterData);

    if (msg) {
      return msg;
    }

    if (lastEditedBedDetails) {
      const anyError = await trigger();
      if (!anyError) return "Please save bed crop details";
    }
    if (!obj.cycleBedDetails.length) {
      return "Minimum 1 bed Compulsory";
    }
    // console.log("obj", obj);
    if (obj.cycleBedDetails.length >= 2 && obj.interBedPattern.length < 1) {
      return "Minimum 1 Inter Bed Pattern required";
    }


    return "";
  };
  // console.log("obj", agriCropParameterData);
  const handleAddCropCycle = async () => {
    const msg = await validationMessage();
    if (msg !== "") {
      setShowErrorMessage(msg);
      return;
    }
    checkValidation("");
    setShowErrorMessage("");
    updateIntialState();
    handleAccordionToggle();
    setCount((count) => count + 1);
    reset();
  };
  const handleAccordionToggle = () => {
    setIsAccordianOpen((prevState) => !prevState);
  };

  useEffect(() => {
    setIsAccordianOpen(accordianOpen);
  }, [accordianOpen]);

  const editCycleBlock = (objs, index) => {
    console.log("objs", objs);
    setLastEditedBedDetails(objs);
    setValue("bedName", objs.bedName);
    if (crops.some((item) => item.id === objs.cropId1))
      setValue("cropId1", objs.cropId1);
    setValue("cropName", objs.cropName);
    setValue("id", objs.id);
    setValue("o1", objs.o1);
    setValue("o2", objs.o2);

    setValue("stretch", objs.stretch);
    setValue("s1", objs.s1);
    if (objs.optionalCropType) {
      setValue("optionalCropName", objs.optionalCropName);

      if (crops.some((item) => item.id === objs.optionalCropType))
        setValue("optionalCropType", objs.optionalCropType);
      setValue("optionalO1", objs.optionalO1);
      setValue("optionalO2", objs.optionalO2);
      setValue("optionalS1", objs.optionalS1);
      setValue("optionalStretch", objs.optionalStretch);
    }
    setCurrentBedId(objs.id);
  };

  useEffect(() => {
    // console.log("currentBedId", currentBedId)
    if (currentBedId === "") setBedButtonName("Add Bed");
    else setBedButtonName("Update Bed");
  }, [currentBedId]);

  const isDurationCrossCalenderLimit = (value, index) => {
    const maxDurationDate = dayjs(agriCropParameterData[0].cycleStartDate).add(
      365,
      "days"
    );
    const currentLimit = dayjs(
      agriCropParameterData[objIndex]?.cycleStartDate
    ).add(findDurationForIndividual(value, crops), "days");
    return currentLimit.isAfter(maxDurationDate, "day");
  };

  const isDurationCrossNextCycleLimit = (value, index) => {
    // Assuming the start date of the next cycle is available at index + 1 in agriCropParameterData array
    const nextCycleStartDate = agriCropParameterData[index + 1]?.cycleStartDate;

    // If no next cycle exists, return false as there’s no limit to compare
    if (!nextCycleStartDate) return false;

    // Calculate the current crop cycle's end date based on the given duration value
    const currentCycleEndDate = dayjs(
      agriCropParameterData[index].cycleStartDate
    ).add(findDurationForIndividual(value, crops), "days");

    // Return true if the current cycle end date is after the next cycle start date, false otherwise
    return currentCycleEndDate.isAfter(dayjs(nextCycleStartDate), "day");
  };

  const checkValueIsInRange = (value, cropType) => {
    const cropMaxDays = findDurationForIndividual(cropType, crops);
    if (((value * cropMaxDays) / 100) + cropMaxDays > 365) return 'Stretch field crosses the calendar limit';
    else return null;
  }


  // console.log("error", errors)
  return (
    <Container style={{ marginBottom: "20px" }}>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Accordion expanded={isAccordionOpen} onChange={handleAccordionToggle}>
          <AccordionSummary
            sx={{
              "& .MuiAccordionSummary-content.MuiAccordionSummary-contentGutters":
              {
                justifyContent: "space-between",
              },
              // '& .MuiAccordionSummary-root.Mui-expanded': {
              minHeight: 45,
              maxHeight: 45,
              // }
            }}
            expandIcon={<ExpandMore />}
            aria-controls="panel1-content"
            id="panel1-header"
          >
            {" "}
            <div style={{ alignSelf: "center" }}>{obj.cycleName}</div>
            {isVarient ? null : (
              <IconButton
                onClick={(e) => {
                  e.stopPropagation();
                  deleteCreatedCycle(obj.cycleName);
                }}
                sx={{
                  ":focus": {
                    outline: 0,
                  },
                }}
              >
                <CustomSvgIconForDelete sx={{ color: "#E0E0E0" }} />
              </IconButton>
            )}
          </AccordionSummary>
          <AccordionDetails
            style={{
              display: "flex",
              flexDirection: "column",
            }}
          >
            {" "}
            <div
              style={{ borderBottom: "1px dashed #E0E0E0", height: "1px" }}
            ></div>
            {getResponsiveDatePickers()}
            <Gap />
            {(obj.cycleBedDetails.length !== 3 || lastEditedBedDetails !== null) && (
              <div>
                <section
                  style={{ marginBottom: "20px", backgroundColor: "#53988E0F" }}
                >
                  <Stack
                    style={{
                      border: "1px solid #E0E0E0",
                      borderRadius: "8px",
                      padding: "10px 20px",
                    }}
                  >
                    <div
                      style={{
                        boxSizing: "border-box",
                        backgroundColor: "#53988E",
                        color: "white",
                        fontWeight: "600",
                        borderRadius: "4px",
                        padding: "5px 10px",
                      }}
                    >
                      <div style={{ display: "flex" }}>
                        <div style={{ marginRight: "8px" }}>{"Define Bed"}</div>

                      </div>
                    </div>

                    <section
                      style={{
                        display: "flex",
                        justifyContent: "space-around",
                      }}
                    >
                      <Box sx={{ width: "50%" }}>
                        <div
                          style={{
                            padding: "30px 10px",
                          }}
                        >
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            {getTitleWithToolTip("Crop Type", "")}{" "}
                            <CustomSvgIconForLeaf sx={{ color: "#6BAA44" }} />{" "}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomSelect
                              name={"cropId1"}
                              control={control}
                              errors={errors}
                              data={soil}
                              backgroundColor="#53988E0F"
                              rules={{
                                validate: {

                                  cropLimit: (value) => {
                                    const currentSetSize = validationCrop(
                                      agriCropParameterData,
                                      objCycle,
                                      objIndex,
                                      value,
                                      optionalCropType
                                    );
                                    if (
                                      isDurationCrossCalenderLimit(
                                        value,
                                        objIndex
                                      )
                                    )
                                      return "This crop exceed the calender limit";
                                    if (
                                      isDurationCrossNextCycleLimit(
                                        value,
                                        objIndex
                                      )
                                    )
                                      return "Please adjust start date for this cycle";
                                    if (currentSetSize > 6) {
                                      return "Cannot add more than 6 crops";
                                    }
                                  },
                                },
                              }}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            {getTitleWithToolTip(
                              "O1",
                              intl.formatMessage({ ...messages.o1 })
                            )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"o1"}
                              control={control}
                              errors={errors}
                              endLabel="millimeters"
                              rules={{
                                required: "Field required",
                                min: {
                                  value: 0,
                                  message: `Only accepts values ranging from ${0} to ${bedWidth / 2
                                    }`, // JS only: <p>error message</p> TS only support string
                                },
                                max: {
                                  value: bedWidth / 2,
                                  message: `Maximum permissible offset to be half of bed top width. range[${0} , ${bedWidth / 2
                                    }] `, // JS only: <p>error message</p> TS only support string
                                },
                                validate: {
                                  o1Checking: (value) => {
                                    // console.log("o1111111111", O1, optionalO1)
                                    if (
                                      optionalO1 &&
                                      Math.abs(value - optionalO1) < 100
                                    ) {
                                      return "O1 and Optional O1 difference should be greater than 100";
                                    }
                                    return null;
                                  },
                                },
                              }}
                              type="number"
                              disabled={cropType === ""}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >{getTitleWithToolTip(
                            "S1",
                            intl.formatMessage({ ...messages.s1 })
                          )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"s1"}
                              control={control}
                              errors={errors}
                              endLabel="millimeters"
                              rules={{
                                required: "Field required",
                                min: {
                                  value: minMaxRange.s1[0],
                                  message: `s1 to be minimum ${minMaxRange.s1[0]} mm.`,
                                },
                                max: {
                                  value: minMaxRange.s1[1],
                                  message: `Atleast 3 plants to be possible along bed row. Decrease s1. range[${minMaxRange.s1[0]} , ${minMaxRange.s1[1]}]`,
                                },
                              }}
                              type="number"
                              disabled={cropType === ""}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >{getTitleWithToolTip(
                            "O2",
                            intl.formatMessage({ ...messages.o2 })
                          )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"o2"}
                              control={control}
                              errors={errors}
                              endLabel="millimeters"
                              rules={{
                                required: "Field required",
                                min: {
                                  value: 0,
                                  message: ` Maximum permissible offset to be 2 times bed top width. range[${0} , ${bedWidth * 2
                                    }]`,
                                },
                                max: {
                                  value: bedWidth * 2,
                                  message: ` Maximum permissible offset to be 2 times bed top width. range[${0} , ${bedWidth * 2
                                    }]`,
                                },
                              }}
                              disabled={cropType === ""}
                            />
                          </div>

                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            {getTitleWithToolTip(
                              "Stretch field",
                              intl.formatMessage({ ...messages.stretch })
                            )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"stretch"}
                              control={control}
                              errors={errors}
                              endLabel="percentage"
                              type="number"
                              rules={{
                                required: "Field required",
                                min: {
                                  value: -50,
                                  message: `Stretch field should be in range[-50 , 50]`,
                                },
                                max: {
                                  value: 50,
                                  message: `Stretch field should be in range[-50 , 50]`,
                                },
                                validate: {
                                  checkValueIsInRange: (value) => checkValueIsInRange(value, cropType),
                                }
                              }}
                              disabled={cropType === ""}
                            />
                          </div>
                        </div>
                      </Box>
                      <Box
                        sx={{
                          width: "50%",
                          display: "flex",
                          flexDirection: "column",
                        }}
                      >
                        <fieldset style={{ border: "1px dashed #474F5080" }}>
                          <legend style={{ color: "#474F5080" }}>
                            Optional Crop
                          </legend>

                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            {getTitleWithToolTip("Crop Type", "")}{" "}
                            <CustomSvgIconForLeaf sx={{ color: "#776274" }} />{" "}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomSelect
                              backgroundColor="#53988E0F"
                              name={"optionalCropType"}
                              control={control}
                              errors={errors}
                              data={soil}
                              required={"none"}
                              rules={{
                                validate: {
                                  cropLimit: (value) => {
                                    if (isDurationCrossCalenderLimit(value))
                                      return "This crop exceed the calender limit";

                                    const currentSetSize = validationCrop(
                                      agriCropParameterData,
                                      objCycle,
                                      objIndex,
                                      cropType,
                                      value
                                    );
                                    if (currentSetSize > 6) {
                                      return "Cannot add more than 6 crops";
                                    }
                                  },
                                },
                              }}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            {getTitleWithToolTip(
                              "O1",
                              intl.formatMessage({ ...messages.o1 })
                            )}{" "}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"optionalO1"}
                              endLabel="millimeters"
                              type="number"
                              control={control}
                              errors={errors}
                              data={soil}
                              disabled={optionalCropType === ""}
                              rules={{
                                required: "Field required",
                                min: {
                                  value: 0,
                                  message: `Only accepts values ranging from ${0} to ${bedWidth / 2
                                    }`, // JS only: <p>error message</p> TS only support string
                                },
                                max: {
                                  value: bedWidth / 2,
                                  message: `Only accepts values ranging from ${0} to ${bedWidth / 2
                                    }`, // JS only: <p>error message</p> TS only support string
                                },
                                validate: {
                                  o2Checking: (value) => {
                                    // console.log("o11111", O1, value, Math.abs(value - O1))
                                    if (O1 && Math.abs(value - O1) < 100) {
                                      return "O1 and Optional O1 difference should be greater than 100";
                                    }
                                    return null;
                                  },
                                },
                              }}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >{getTitleWithToolTip(
                            "S1",
                            intl.formatMessage({ ...messages.s1 })
                          )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"optionalS1"}
                              endLabel="millimeters"
                              control={control}
                              errors={errors}
                              data={soil}
                              disabled={optionalCropType === ""}
                              rules={{
                                required: "Field required",
                                min: {
                                  value: minMaxRange.optionalS1[0],
                                  message: `s1 to be minimum ${minMaxRange.optionalS1[0]} mm.`,
                                },

                                max: {
                                  value: minMaxRange.optionalS1[1],
                                  message: `Atleast 3 plants to be possible along bed row. Decrease s1. range[${minMaxRange.optionalS1[0]} , ${minMaxRange.optionalS1[1]}]`,
                                },
                              }}
                            />
                          </div>
                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >{getTitleWithToolTip(
                            "O2",
                            intl.formatMessage({ ...messages.o2 })
                          )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"optionalO2"}
                              endLabel="millimeters"
                              control={control}
                              errors={errors}
                              data={soil}
                              disabled={optionalCropType === ""}
                              rules={{
                                required: "Field required",
                                min: {
                                  value: 0,
                                  message: ` Maximum permissible offset to be 2 times bed top width. range[${0} , ${bedWidth * 2
                                    }]`,
                                },
                                max: {
                                  value: bedWidth * 2,
                                  message: ` Maximum permissible offset to be 2 times bed top width. range[${0} , ${bedWidth * 2
                                    }]`,
                                },
                              }}
                            />
                          </div>

                          <Gap />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >  {getTitleWithToolTip(
                            "Stretch field",
                            intl.formatMessage({ ...messages.stretch })
                          )}
                          </div>
                          <Gap />
                          <div style={{ background: "white" }}>
                            <CustomInputField
                              backgroundColor="#53988E0F"
                              name={"optionalStretch"}
                              control={control}
                              errors={errors}
                              endLabel="percentage"
                              rules={{
                                required: "Field required",
                                min: {
                                  value: -50,
                                  message: `Stretch field should be in range[-50 , 50]`,
                                },
                                max: {
                                  value: 50,
                                  message: `Stretch field should be in range[-50 , 50]`,
                                },
                                validate: {
                                  checkValueIsInRange: (value) => checkValueIsInRange(value, optionalCropType),
                                }
                              }}
                              disabled={optionalCropType === ""}
                            />
                          </div>

                        </fieldset>
                        <Gap />
                        <Button
                          type="submit"
                          variant="contained"
                          className="btn"
                          data-testid="submitButton"
                          sx={{
                            fontFamily: "Open Sans",
                            fontSize: "14px",
                            fontWeight: "700",

                            textTransform: "capitalize",
                            background: theme.palette.secondary.main,
                            "&:hover": {
                              backgroundColor: theme.palette.secondary.main,
                            },

                            alignSelf: "flex-end",
                          }}
                        >
                          {bedButtonName}
                        </Button>
                      </Box>
                    </section>
                  </Stack>
                </section>
              </div>
            )}
            {obj.cycleBedDetails.length > 0 && (
              <AddedBedBlock
                addedBed={obj.cycleBedDetails}
                setAgriCropParameterData={setAgriCropParameterData}
                obj={obj}
                setDeletedBed={setDeletedBed}
                deletedBed={deletedBed}
                showDeleteIcon={true}
                showEditIcon={true}
                editCycleBlock={editCycleBlock}
                isVariant={isVarient}
              />
            )}
            {obj.cycleBedDetails.length > 1 && (
              <AddRemoveBlocks
                addedBed={obj.cycleBedDetails}
                blocks={obj.interBedPattern}
                obj={obj}
                setAgriCropParameterData={setAgriCropParameterData}
              />
            )}
            {showErrorMessage !== "" && <Error>{showErrorMessage}</Error>}
            <Button
              onClick={handleAddCropCycle}
              variant="contained"
              className="btn"
              data-testid="submitButton"
              sx={{
                fontFamily: "Open Sans",
                whiteSpace: "nowrap",
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
              Add Crop Cycle
            </Button>
          </AccordionDetails>
        </Accordion>
      </form>
    </Container>
  );
});

export default DefineBed;

const Gap = styled.div`
  padding: 10px;
`;

const Container = styled.section`
  border: 1px solid #e0e0e0;
  border-radius: 4px;
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
