import { Button, Stack } from "@mui/material";
import React, {
  useEffect,
  useImperativeHandle,
  forwardRef,
  useRef,
} from "react";
import { Controller, useForm } from "react-hook-form";
import CustomTextField from "./CustomTextField";
import { Container } from "../container/login/CustomContainer";
import { useTheme } from "styled-components";
import CustomOtpField from "./CustomOtpField";
import CustomRadioButtons from "./CustomRadioButtons";
import CustomDropDown from "./CustomDropDown";
import CustomDatePicker from "./CustomDatePicker";
import CustomDashedLine from "./CustomDashedLine";
import { useDispatch, useSelector } from "react-redux";
import { setUpdateFormValue } from "../redux/action/preProcessorAction";

const CustomFormContainer = forwardRef(
  (
    {
      updateMinMax = () => { },
      formData,
      defaultValues,
      initialValues = {},
      watchList = [],
      onFormSubmit,
      buttonLabel,
      buttonPosition,
      showPreviousButton,
      previousButtonLabel,
      handlePreviousButton,
      handleFormError = () => { },
      formRef,
      getFormStateRef,
      dependencies = {},
    },
    ref
  ) => {
    const dispatch = useDispatch();
    const theme = useTheme();
    const updateFormValue = useSelector(
      (state) => state.preProcessor.updateFormValue
    );
    const onSubmit = (data) => {
      onFormSubmit(data);
    };

    const {
      handleSubmit,
      register,
      formState: { errors },
      control,
      watch,
      reset,
      setValue,
      getValues,
      trigger,
    } = useForm({
      mode: "all",
      defaultValues: defaultValues,
    });

    const allFields = watch(watchList);
    const callResetForm = (updatedFormState) => {
      reset({ ...updatedFormState });
    };

    const getCurrentFormData = () => {
      return getValues();
    };

    useImperativeHandle(getFormStateRef, () => getCurrentFormData);
    useImperativeHandle(formRef, () => callResetForm);

    useEffect(() => {
      if (updateFormValue) {
        const data = getValues();
        Object.keys(data).forEach((key) => {
          let foundObject;
          formData.forEach((subArr) => {
            subArr.forEach((obj) => {
              if (obj.name === key) {
                foundObject = obj;
              }
            });
          });
          const val = foundObject?.disabled ? "" : data[key];
          setValue(key, val, {
            shouldValidate: foundObject?.disabled,
          });
        });
      }
      dispatch(setUpdateFormValue({ updateFormValue: false }));
    }, [updateFormValue, setValue]);

    useEffect(() => {
      handleFormError(errors);
    }, [errors, handleFormError]);

    const previousValues = initialValues;
    const watchedFieldsValues = watch(watchList);
    useEffect(() => {
      if (watchedFieldsValues) {
        watchList?.forEach((field, index) => {
          const currentValue = watchedFieldsValues[index];
          const previousValue = previousValues[field];
          // console.log(`${field}`, previousValue, currentValue );
          if (currentValue !== previousValue) {
            updateMinMax(field, getValues());
            const dependentFields = dependencies[field] || [];
            if (dependentFields.length > 0) {
              dependentFields.map((field) => {
                if (getValues(field)) {
                  trigger(field);
                }
              });
            }
            previousValues[field] = currentValue;
          }
        });
      }
    }, [watchedFieldsValues, trigger, dependencies, watchList]);

    const defaultDataSet = {
      label: "",
      handleChange: () => { },
      isRequired: false,
      placeHolder: "",
      register: () => { },
      errors: {},
      name: "",
      pattern: {},
      inputProps: {},
      type: "text",
      validate: () => { },
      testId: "",
      suffix: "",
      textFieldType: "textual",
      showToolTip: false,
      toolTipMessage: "",
      maxLength: 100,
    };

    const callRenderComponent = (innerIndex, props, value, onChange) => {
      const customDataSet = {
        label: props.label || defaultDataSet.label,
        handleChange: props.handleChange || defaultDataSet.handleChange,
        isRequired: props.isRequired || defaultDataSet.isRequired,
        placeHolder: props.placeHolder || defaultDataSet.placeHolder,
        register: register,
        errors: errors,
        name: props.name || defaultDataSet.name,
        pattern: props.pattern || defaultDataSet.pattern,
        inputProps: props.inputProps || defaultDataSet.inputProps,
        type: props.type || defaultDataSet.props,
        validate: props.validate || defaultDataSet.validate,
        value: value === null || value === undefined ? "" : value,
        onChange: onChange,
        dataSet: props.dataSet,
        testId: props.testId || defaultDataSet.testId,
        disabled: props.disabled,
        suffix: props.suffix || defaultDataSet.suffix,
        textFieldType: props.textFieldType || defaultDataSet.textFieldType,
        showToolTip: props.showToolTip || defaultDataSet.showToolTip,
        toolTipMessage: props.toolTipMessage || defaultDataSet.toolTipMessage,
        maxLength: props?.maxLength?.value || defaultDataSet.maxLength,
      };

      switch (props.componentType) {
        case "textField":
          return <CustomTextField key={innerIndex} {...customDataSet} />;
        case "otpField":
          return <CustomOtpField key={innerIndex} {...customDataSet} />;
        case "radioButton":
          return <CustomRadioButtons key={innerIndex} {...customDataSet} />;
        case "selectDropdown":
          return <CustomDropDown key={innerIndex} {...customDataSet} />;
        case "datePicker":
          return <CustomDatePicker key={innerIndex} {...customDataSet} />;
        case "dashedLine":
          return <CustomDashedLine key={innerIndex} {...customDataSet} />;
        default:
          return <div />; // by default return empty div
      }
    };

    const findWidth = (innerArrayLength) => {
      if (innerArrayLength === 1) return "100%";
      else return "45%";
    };

    const renderButton = () => {
      return showPreviousButton ? (
        <div
          style={{
            display: "flex",
            justifyContent: "right",
            gap: "10px",
            marginTop: "40px",
          }}
        >
          {showPreviousButton && (
            <Button
              type="reset"
              className="prevBtn"
              data-testid="previousButton"
              variant="outlined"
              onClick={(e) => {
                e.preventDefault();
                handlePreviousButton();
              }}
              sx={{
                "&:hover": {
                  backgroundColor: theme.palette.background.secondary,
                  color: "#C7C9CA",
                  borderColor: "#C7C9CA",
                },
                ...(buttonPosition === "right" && {
                  alignSelf: "flex-end",
                  width: "140px",
                  color: "#474F5080",
                  borderColor: "#C7C9CA",
                  height: "40px",
                  fontWeight: 600,
                }),
              }}
            >
              {previousButtonLabel}
            </Button>
          )}
          <Button
            type="submit"
            className="btn"
            data-testid="submitButton"
            sx={{
              "&:hover": {
                backgroundColor: theme.palette.secondary.main,
              },
              ...(buttonPosition === "right" && {
                alignSelf: "flex-end",
                width: "140px",
              }),
            }}
          >
            {buttonLabel}
          </Button>
        </div>
      ) : (
        <Button
          type="submit"
          className="btn"
          data-testid="submitButton"
          sx={{
            "&:hover": {
              backgroundColor: theme.palette.secondary.main,
            },
            ...(buttonPosition === "right" && {
              alignSelf: "flex-end",
              width: "140px",
            }),
          }}
        >
          {buttonLabel}
        </Button>
      );
    };

    return (
      <Container>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <Stack spacing={2}>
            {formData.map((innerArray, index) => (
              <div
                key={index}
                className={`${innerArray.length > 1 ? "subWrapper" : ""}`}
              >
                {innerArray.map((props, innerIndex) => (
                  <div
                    style={{ width: findWidth(innerArray.length) }}
                    key={innerIndex}
                  >
                    <Controller
                      key={innerIndex}
                      name={props.name}
                      control={control}
                      rules={{
                        required:
                          props.isRequired &&
                          (props.requiredMessage
                            ? props.requiredMessage
                            : `${props.label} is required`),
                        pattern: props.pattern,
                        validate: props.validate,
                        minLength: props.minLength,
                        maxLength: props.maxLength,
                        min: props.min && props.min,
                        max: props.max && props.max,
                      }}
                      render={({
                        field: { onChange, value },
                        fieldState: { error },
                        formState,
                      }) => (
                        <>
                          {callRenderComponent(
                            innerIndex,
                            props,
                            value,
                            onChange
                          )}
                        </>
                      )}
                    />
                  </div>
                ))}
              </div>
            ))}
            {renderButton()}
          </Stack>
        </form>
      </Container>
    );
  }
);

export default CustomFormContainer;
