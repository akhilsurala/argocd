import React, { useCallback, useEffect, useState } from "react";
import { styled, useTheme } from "styled-components";

import ProjectCard from "./ProjectCard";
import CustomFormContainer from "../../components/CustomFormContainer";
import { projectForm } from "../../utils/formData/projectForm";
import { pvGeometry } from "../../utils/formData/pvGeometry";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Button,
  Card,
  Grid,
  IconButton,
  InputAdornment,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import HorizontalStepper from "../../components/HorizontalStepper";
import { useFieldArray, useForm } from "react-hook-form";
import CustomSelect from "./agriGeneralPage/component/CustomSelect";
import CustomSwitch from "./agriGeneralPage/component/CustomSwitch";
import CustomDashedLine from "../../components/CustomDashedLine";
import CustomInputField from "./agriGeneralPage/component/CustomInputField";
import CustomTableWithTextField from "../../components/CustomTableWithTextField";
import { CustomSvgIconForDelete, CustomSvgIconForToolTip } from "../dashboard/CustomSvgIcon";
import {
  addEconominParameters,
  getEconominParameters,
  updateEconominParameters,
} from "../../api/economicParameters";
import { AppRoutesPath, runManagerRoute } from "../../utils/constant";
import { useNavigate } from "react-router-dom";
import { ExpandMore } from "@mui/icons-material";
import { useSelector } from "react-redux";


const referenceYieldMinCost = "referenceYieldMinCost";
const referenceYieldMaxCost = "referenceYieldMaxCost";
const inputCostOfCropMinCost = "inputCostOfCropMinCost";
const inputCostOfCropMaxCost = "inputCostOfCropMaxCost";
const sellingCostOfCropMinCost = "sellingCostOfCropMinCost";
const sellingCostOfCropMaxCost = "sellingCostOfCropMaxCost";
const allFields = [
  "referenceYieldMinCost",
  "referenceYieldMaxCost",
  "inputCostOfCropMinCost",
  "inputCostOfCropMaxCost",
  "sellingCostOfCropMinCost",
  "sellingCostOfCropMaxCost",
];
const EconomicParameters = ({ setActiveStep, projectId, runId, setIsVarient }) => {
  const theme = useTheme();
  const watchList = ["agriApv"];
  const [watchState, setWatchState] = useState();
  const [disableAllFields, setDisableAllFields] = useState(false);
  const [economicParameterId, setEconomicParameterId] = useState();
  const [triggerField, setTriggerField] = useState("");

  const toggle = useSelector((state) => state.preProcessor.apvToggle);
  const disabledOnlyAgriField = toggle === 'Only Agri' ? true : false;
  const [hourlyDataSet, setHourlyDataSet] = React.useState(Array(24).fill(0));

  const {
    handleSubmit,
    control,
    watch,
    register,
    reset,
    setValue,
    trigger,
    clearErrors,
    getValues,
    formState: { errors },
  } = useForm({
    defaultValues: {
      currency: 1,
      isDefineEconomicParameters: false,
      cropDtoSet: []
    },
  });

  const { fields, append, replace, update, prepend, remove, swap, move, insert } = useFieldArray({
    control, // control props comes from useForm (optional: if you are using FormProvider)
    name: "cropDtoSet", // unique name for your Field Array
  });



  const navigate = useNavigate();
  const watchFields = watch([
    "isDefineEconomicParameters",
    'cropDtoSet'
  ]);

  // console.log("fileds", `${errors}.cropDtoSet.${0}.${referenceYieldMinCost}`);
  function renameCropCostKeys(crops) {
    if (!crops) return []
    return crops?.map(crop => ({
      ...crop,
      minReferenceYieldCost: crop.referenceYieldMinCost,
      maxReferenceYieldCost: 1000000000,
      minInputCostOfCrop: crop.inputCostOfCropMinCost,
      maxInputCostOfCrop: 1000000000,
      minSellingCostOfCrop: crop.sellingCostOfCropMinCost,
      maxSellingCostOfCrop: 1000000000,
      // Remove old keys
      referenceYieldMinCost: undefined,
      referenceYieldMaxCost: undefined,
      inputCostOfCropMinCost: undefined,
      inputCostOfCropMaxCost: undefined,
      sellingCostOfCropMinCost: undefined,
      sellingCostOfCropMaxCost: undefined,
    }));
  }

  function renameCropCostKeysForResponse(crop) {
    return {
      ...crop,
      // Remove old keys
      referenceYieldMaxCost: crop.maxReferenceYieldCost,
      referenceYieldMinCost: crop.minReferenceYieldCost,
      inputCostOfCropMinCost: crop.minInputCostOfCrop,
      inputCostOfCropMaxCost: crop.maxInputCostOfCrop,
      sellingCostOfCropMinCost: crop.minSellingCostOfCrop,
      sellingCostOfCropMaxCost: crop.maxSellingCostOfCrop,

      minReferenceYieldCost: undefined,
      maxReferenceYieldCost: undefined,
      minInputCostOfCrop: undefined,
      maxInputCostOfCrop: undefined,
      minSellingCostOfCrop: undefined,
      maxSellingCostOfCrop: undefined,
    };
  }

  const onSubmit = (data) => {
    // setActiveStep(1);
    const payload = {
      currencyId: data.currency,
      // minReferenceYieldCost: data.referenceYieldMinCost,
      // maxReferenceYieldCost: data.referenceYieldMaxCost,
      // minInputCostOfCrop: data.inputCostOfCropMinCost,
      // maxInputCostOfCrop: data.inputCostOfCropMaxCost,
      // minSellingCostOfCrop: data.sellingCostOfCropMinCost,
      // maxSellingCostOfCrop: data.sellingCostOfCropMaxCost,
      // minSellingPointOfPower: 10,
      // maxSellingPointOfPower: 10,
      hourlySellingRates: hourlyDataSet,
      economicMultiCrop: data?.cropDtoSet[0] ? renameCropCostKeys(data?.cropDtoSet) : [],
      economicParameter: data.isDefineEconomicParameters,
    };
    if (!economicParameterId) callAddEconomicParameters(payload);
    else callUpdateEconomicParameters(payload);
  };

  useEffect(() => {
    if (triggerField) {
      trigger(triggerField);
      setTriggerField("");
    }
  }, [triggerField]);

  const minMaxFieldsMap = {
    referenceYieldMinCost: "referenceYieldMaxCost",
    referenceYieldMaxCost: "referenceYieldMinCost",
    inputCostOfCropMinCost: "inputCostOfCropMaxCost",
    inputCostOfCropMaxCost: "inputCostOfCropMinCost",
    sellingCostOfCropMinCost: "sellingCostOfCropMaxCost",
    sellingCostOfCropMaxCost: "sellingCostOfCropMinCost",
  };

  React.useEffect(() => {
    const subscription = watch((value, { name, type }) => {
      if (type == "change") {
        if (getValues(name) && getValues(minMaxFieldsMap[name])) {
          setTriggerField(minMaxFieldsMap[name]);
        }
        trigger(name);
      }
    });
    return () => subscription.unsubscribe();
  }, [watch]);

  const callGetEconomicParameters = () => {
    getEconominParameters(projectId, runId)
      .then((response) => {
        const data = response.data?.data;

        // if (runId) {
        //   append(
        //     data?.economicMultiCropResponseList.map((item, index) => ({
        //       ...renameCropCostKeysForResponse(item), // This renames the cost keys
        //       ...data?.cropDtoSet[index], // Merge data from the cropDtoSet by index
        //     }))
        //   );
        // } else
        if (data?.economicMultiCropResponseList) {
          replace(data?.economicMultiCropResponseList?.map((item) => ({ ...item, cropId: item?.cropId, name: item?.crop?.name, ...renameCropCostKeysForResponse(item) })));

        }
        if (data?.isMaster !== undefined && data?.isMaster !== null)
          setIsVarient(data.isMaster)
        // Assume fieldArray is already appended, now update the values.


        setEconomicParameterId(data?.economicId);
        setDisableAllFields(!data?.economicParameter);
        setValue("currency", data?.currency?.currencyId)
        setValue("isDefineEconomicParameters", data?.economicParameter);
        // setValue("referenceYieldMinCost", data?.minReferenceYieldCost);
        // setValue("referenceYieldMaxCost", data?.maxReferenceYieldCost);
        // setValue("inputCostOfCropMinCost", data?.minInputCostOfCrop);
        // setValue("inputCostOfCropMaxCost", data?.maxInputCostOfCrop);
        // setValue("sellingCostOfCropMinCost", data?.minSellingCostOfCrop);
        // setValue("sellingCostOfCropMaxCost", data?.maxSellingCostOfCrop);
        setHourlyDataSet(
          data?.hourlySellingRates
            ? data?.hourlySellingRates
            : Array(24).fill(0)
        );
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const callAddEconomicParameters = (payload) => {
    addEconominParameters(payload, projectId, runId)
      .then((response) => {
        console.log(response);
        navigate(runManagerRoute(projectId));
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const callUpdateEconomicParameters = (payload) => {
    updateEconominParameters(payload, projectId, economicParameterId, runId)
      .then((response) => {
        console.log(response);
        navigate(runManagerRoute(projectId));
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    callGetEconomicParameters();
  }, []);

  const getTitleWithToolTip = (label) => {
    return (
      <>
        <div style={{ display: "flex", marginTop: '20px' }}>
          <div className="label">{label}</div>
          {/* <Tooltip title="msg" placement="right-start">
            <IconButton sx={{ padding: "0px" }}>
              <CustomSvgIconForToolTip />
            </IconButton>
          </Tooltip> */}
        </div>
        <Gap />
      </>
    );
  };

  const getCurrencyDropDown = () => {
    const name = "currency";
    // change when get data from master table, currently static data
    const dataSet = [
      { label: "INR", value: 1 },
      { label: "USD", value: 2 },
    ];
    return (
      <>
        <Gap />
        <CustomSelect
          name={name}
          control={control}
          errors={errors}
          data={dataSet}
          disabledAllField={disableAllFields}
        />
      </>
    );
  };

  const getInputProps = (label) => {
    return {
      endAdornment: (
        <InputAdornment position="end">
          <Typography
            sx={{
              fontFamily: "Montserrat",
              fontSize: "14px",
              fontStyle: "italic",
              fontWeight: "500",
              color: "#53988E80",
            }}
          >
            {label}
          </Typography>
        </InputAdornment>
      ),
    };
  };

  const [isAccordionOpen, setIsAccordianOpen] = useState([true, true, true, true, true, true]);
  const handleAccordionToggle = (index) => {
    setIsAccordianOpen([...isAccordionOpen.slice(0, index), !isAccordionOpen[index], ...isAccordionOpen.slice(index + 1)]);
  };

  // useEffect(() => {
  //     setIsAccordianOpen(accordianOpen);
  // }, [accordianOpen])

  const getTextFields = (field, index) => {
    // console.log("watchFIeld", watchFields)
    if (!watchFields[1][index]) return;
    const {
      inputCostOfCropMaxCost: inputCostOfCropMaxCostCurrentValue,
      inputCostOfCropMinCost: inputCostOfCropMinCostCurrentValue,
      sellingCostOfCropMaxCost: sellingCostOfCropMaxCostCurrentValue,
      sellingCostOfCropMinCost: sellingCostOfCropMinCostCurrentValue,
      referenceYieldMaxCost: referenceYieldMaxCostCurrentValue,
      referenceYieldMinCost: referenceYieldMinCostCurrentValue
    } = watchFields[1][index]

    // console.log("heysss", watchFields[1][index]);
    return (
      <div key={index} style={{ marginTop: '0px' }}>

        <Accordion style={{ marginTop: '0px' }} expanded={isAccordionOpen[index]} onChange={() => handleAccordionToggle(index)} >
          <AccordionSummary sx={{
            '& .MuiAccordionSummary-content.MuiAccordionSummary-contentGutters': {

              justifyContent: 'space-between',
            },
            // '& .MuiAccordionSummary-root.Mui-expanded': {
            minHeight: 45,
            maxHeight: 45,
            // }
          }}
            expandIcon={<ExpandMore />}
            aria-controls="panel1-content"
            id="panel1-header"
          >   <div style={{ alignSelf: 'center' }}>
              {field.name}
            </div>
            <IconButton
              onClick={(e) => {
                e.stopPropagation()
              }} sx={{
                ':focus': {
                  outline: 0,
                },

              }}>
            </IconButton>
          </AccordionSummary>
          <AccordionDetails style={
            {
              display: 'flex',
              flexDirection: 'column'
            }
          }>

            <div>
              {getTitleWithToolTip("Reference Yield")}
              <Grid container spacing={4}>
                <Grid item xs={12} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  <CustomInputField
                    name={referenceYieldMinCost}
                    prefixArray={`cropDtoSet.${index}.`}
                    type="number"
                    control={control}
                    // {...register(`cropDtoSet.${index}.${referenceYieldMinCost}`)}
                    errors={errors?.cropDtoSet?.[index] || {}}
                    inputProps={getInputProps("tonne/acre")}
                    placeholder="Enter value"
                    disabled={disableAllFields}
                    rules={{
                      required: !disableAllFields && "required field",
                      max: {
                        value: 1000000000,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      min: {
                        value: 0.1,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      validate: {
                        lessThanTen: (v) => {

                          if (!referenceYieldMaxCostCurrentValue) {
                            return true; // No max value to compare, so it's valid
                          } else if (parseFloat(v) <= referenceYieldMaxCostCurrentValue) {

                            setTriggerField(`cropDtoSet.${index}.${referenceYieldMaxCost}`);
                            return true; // Valid, as value is less than or equal to max
                          } else {

                            setTriggerField(`cropDtoSet.${index}.${referenceYieldMaxCost}`);
                            return "Min cannot be greater than Max Value"; // Error message if value exceeds max
                          }

                        },
                      },

                      pattern: {
                        value: /^(0|[1-9]\d*)(\.\d+)?$/,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={6} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  {false && (
                    <CustomInputField
                      name={referenceYieldMaxCost}

                      prefixArray={`cropDtoSet.${index}.`}
                      // {...register(`cropDtoSet.${index}.${referenceYieldMaxCost}`)}
                      type="number"
                      control={control}
                      errors={errors?.cropDtoSet?.[index] || {}}
                      inputProps={getInputProps("tonne/acre")}
                      placeholder="max"
                      disabled={disableAllFields}
                      rules={{
                        required: !disableAllFields && "required field",
                        max: {
                          value: 1000000000,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        min: {
                          value: 0.1,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        validate: {
                          lessThanTen: (v) => {

                            if (!referenceYieldMinCostCurrentValue) {
                              return true; // No min value to compare, so it's valid
                            } else if (parseFloat(v) >= referenceYieldMinCostCurrentValue) {

                              setTriggerField(`cropDtoSet.${index}.${referenceYieldMinCost}`);
                              return true; // Valid, as value is greater than or equal to min
                            } else {

                              setTriggerField(`cropDtoSet.${index}.${referenceYieldMinCost}`);
                              return "Max cannot be smaller than min value"; // Error message if value is less than min
                            }
                          },
                        },

                        pattern: {
                          value: /^(0|[1-9]\d*)(\.\d+)?$/,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                      }}
                    />
                  )}
                </Grid>
              </Grid>
            </div>
            <div>
              {getTitleWithToolTip("Input Cost of Crop")}

              <Grid container spacing={4} >
                <Grid item xs={12} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  <CustomInputField
                    name={inputCostOfCropMinCost}

                    prefixArray={`cropDtoSet.${index}.`}
                    // {...register(`cropDtoSet.${index}.${inputCostOfCropMinCost}`)}
                    type="number"
                    control={control}
                    errors={errors?.cropDtoSet?.[index] || {}}
                    inputProps={getInputProps("rs/plant")}
                    placeholder="Enter Value"
                    disabled={disableAllFields}
                    rules={{
                      required: !disableAllFields && "required field",
                      max: {
                        value: 1000000000,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      min: {
                        value: 0.1,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      validate: {
                        lessThanTen: (v) => {
                          if (!inputCostOfCropMaxCostCurrentValue) {
                            return true; // No max value to compare, so it's valid
                          } else if (parseFloat(v) <= inputCostOfCropMaxCostCurrentValue) {
                            setTriggerField(`cropDtoSet.${index}.${inputCostOfCropMaxCost}`);
                            return true; // Valid, as value is less than or equal to max
                          } else {
                            setTriggerField(`cropDtoSet.${index}.${inputCostOfCropMaxCost}`);
                            return "Min cannot be greater than Max Value"; // Error message if value exceeds max
                          }
                        },
                      },

                      pattern: {
                        value: /^(0|[1-9]\d*)(\.\d+)?$/,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={6} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  {false && (
                    <CustomInputField
                      name={inputCostOfCropMaxCost}

                      prefixArray={`cropDtoSet.${index}.`}
                      // {...register(`cropDtoSet.${index}.${inputCostOfCropMaxCost}`)}

                      type="number"
                      control={control}
                      errors={errors?.cropDtoSet?.[index] || {}}
                      inputProps={getInputProps("rs/plant")}
                      placeholder="max "
                      disabled={disableAllFields}
                      rules={{
                        required: !disableAllFields && "required field",
                        max: {
                          value: 1000000000,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        min: {
                          value: 0.1,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        validate: {
                          lessThanTen: (v) => {
                            if (!inputCostOfCropMinCostCurrentValue) {
                              return true; // No min value to compare, so it's valid
                            } else if (parseFloat(v) >= inputCostOfCropMinCostCurrentValue) {
                              setTriggerField(`cropDtoSet.${index}.${inputCostOfCropMinCost}`);
                              return true; // Valid, as value is greater than or equal to min
                            } else {
                              setTriggerField(`cropDtoSet.${index}.${inputCostOfCropMinCost}`);
                              return "Max cannot be smaller than min value"; // Error message if value is less than min
                            }
                          },
                        },

                        pattern: {
                          value: /^(0|[1-9]\d*)(\.\d+)?$/,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                      }}
                    />
                  )}
                </Grid>
              </Grid>
            </div>

            <div>
              {getTitleWithToolTip("Selling Price of Crop")}
              <Grid container spacing={4}>
                <Grid item xs={12} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  <CustomInputField

                    prefixArray={`cropDtoSet.${index}.`}
                    name={sellingCostOfCropMinCost}
                    // {...register(`cropDtoSet.${index}.${sellingCostOfCropMinCost}`)}

                    type="number"
                    control={control}
                    errors={errors?.cropDtoSet?.[index] || {}}
                    inputProps={getInputProps("rs/kg")}
                    placeholder="Enter Value"
                    disabled={disableAllFields}
                    rules={{
                      required: !disableAllFields && "required field",
                      max: {
                        value: 1000000000,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      min: {
                        value: 0.1,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                      validate: {
                        lessThanTen: (v) => {
                          if (!sellingCostOfCropMaxCostCurrentValue) {
                            return true; // No max value to compare, so it's valid
                          } else if (parseFloat(v) <= sellingCostOfCropMaxCostCurrentValue) {
                            setTriggerField(`cropDtoSet.${index}.${sellingCostOfCropMaxCost}`);
                            return true; // Valid, as value is less than or equal to max
                          } else {
                            setTriggerField(`cropDtoSet.${index}.${sellingCostOfCropMaxCost}`);
                            return "Min cannot be greater than Max Value"; // Error message if value exceeds max
                          }
                        },
                      },

                      pattern: {
                        value: /^(0|[1-9]\d*)(\.\d+)?$/,
                        message:
                          "Only accepts integer value ranging 0.1 to 1000000000",
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={6} style={{ padding: 0, paddingLeft: '30px', paddingTop: '13px' }}>
                  {false && (
                    <CustomInputField
                      name={sellingCostOfCropMaxCost}
                      // {...register(`cropDtoSet.${index}.${sellingCostOfCropMaxCost}`)}

                      prefixArray={`cropDtoSet.${index}.`}
                      type="number"
                      control={control}
                      errors={errors?.cropDtoSet?.[index] || {}}
                      inputProps={getInputProps("rs/kg")}
                      placeholder="max "
                      disabled={disableAllFields}
                      isRe
                      rules={{
                        required: !disableAllFields && "required field",
                        max: {
                          value: 1000000000,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        min: {
                          value: 0.1,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                        validate: {
                          lessThanTen: (v) => {
                            if (!sellingCostOfCropMinCostCurrentValue) {
                              return true; // No min value to compare, so it's valid
                            } else if (parseFloat(v) >= sellingCostOfCropMinCostCurrentValue) {
                              setTriggerField(`cropDtoSet.${index}.${sellingCostOfCropMinCost}`);
                              return true; // Valid, as value is greater than or equal to min
                            } else {
                              setTriggerField(`cropDtoSet.${index}.${sellingCostOfCropMinCost}`);
                              return "Max cannot be smaller than min value"; // Error message if value is less than min
                            }
                          },
                        },

                        pattern: {
                          value: /^(0|[1-9]\d*)(\.\d+)?$/,
                          message:
                            "Only accepts integer value ranging 0.1 to 1000000000",
                        },
                      }}
                    />
                  )}
                </Grid>
              </Grid>
            </div>
          </AccordionDetails>
        </Accordion>
        <Gap />
      </div>
    );
  };




  useEffect(() => {
    {
      watchFields[0] ? setDisableAllFields(false) : setDisableAllFields(true);
    }
    const subscription = watch((value, { name, type }) => {
      if (
        type == "change" &&
        name === "isDefineEconomicParameters" &&
        !value.isDefineEconomicParameters
      ) {
        allFields.map((val) => {
          setValue(val, "");
          clearErrors(val, "");
        });
        setHourlyDataSet(Array(24).fill(0));
        setValue("currency", "1");
      }
    });
    return () => subscription.unsubscribe();
  }, [watchFields]);

  const handleSwitch = (value) => {
    if (value && economicParameterId) callGetEconomicParameters();
  }

  const getAllFields = () => {

    return fields.map((field, index) => {
      return getTextFields(field, index)
    })

  };

  return (
    <Container>
      <Grid container spacing={4}>
        <Grid item md={12} className="leftSection">
          <Card
            sx={{
              borderRadius: 5,
              marginBottom: "100px",
            }}
          >
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={3} sx={{ margin: "20px 20px 0px 20px" }}>
                <div className="titleWrapper">
                  <div className="title">Economic Parameters</div>
                  {getCurrencyDropDown()}
                </div>
                <Stack spacing={3} style={{ marginRight: "50px" }}>
                  <CustomSwitch
                    name="isDefineEconomicParameters"
                    control={control}
                    label="Want to define the economy parameters?"
                    onChange={handleSwitch}
                  />
                  <CustomDashedLine />
                  {getAllFields()}
                  <CustomDashedLine />
                  <div>
                    {getTitleWithToolTip("Hourly Tariff Rates for PV")}
                    <CustomTableWithTextField
                      disabled={disableAllFields || disabledOnlyAgriField}
                      data={hourlyDataSet}
                      setData={setHourlyDataSet}
                    />
                  </div>
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "flex-end",
                      marginBottom: "26px",
                    }}
                  >
                    <Button
                      variant="contained"
                      className="prevButton"
                      data-testid="submitButton"
                      onClick={() => setActiveStep((active) => active - 1)}
                    >
                      Previous
                    </Button>

                    <Button
                      type="submit"
                      variant="contained"
                      data-testid="submitButton"
                      className="submitButton"
                    >
                      {runId ? "Update Run" : "Create Run"}
                    </Button>
                  </div>
                </Stack>
              </Stack>
            </form>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default EconomicParameters;

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
    flex: 1;
    /* padding: 20px 0px 0px 20px; */
  }
  .label {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
  }
  .formContent {
    padding: 40px 20px 20px 20px;
  }
  .titleWrapper {
    display: flex;
    align-items: center;
  }
  .prevButton {
    font-family: "Open Sans";
    font-size: 14px;
    font-weight: 700;
    text-transform: capitalize;
    background: transparent;
    color: grey;
    &:hover {
      background-color: transparent;
    }
    align-self: flex-end;
    width: 140px;
  }
  .submitButton {
    font-family: "Open Sans";
    font-size: 14px;
    font-weight: 700;

    margin-left: 20px;
    text-transform: capitalize;
    background: ${({ theme }) => theme.palette.secondary.main};
    &:hover {
      background-color: ${({ theme }) => theme.palette.secondary.main};
    }

    align-self: flex-end;
    width: 140px;
  }
`;

const Gap = styled.div`
  padding: 10px;
`;
