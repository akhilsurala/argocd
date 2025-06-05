import { useTheme } from "styled-components";
import { ADMIN_MODULE_CONFIGURATION, typeOfModules } from "../../../utils/constant";

export const moduleConfigurationFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "name",
        label: ADMIN_MODULE_CONFIGURATION["name"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter name",
        name: "name",
        validate: {},
        // pattern: {
        //   value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/i,
        //   message: "Only accepts alphabets and integer characters",
        // },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "textField",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],[
      {
        key: "numberOfModules",
        label: ADMIN_MODULE_CONFIGURATION["numberOfModules"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter Module Numbers",
        name: "numberOfModules",
        validate: {},
        // pattern: {
        //   value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/i,
        //   message: "Only accepts alphabets and integer characters",
        // },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "textField",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],[
      {
        key: "typeOfModule",
        label: ADMIN_MODULE_CONFIGURATION["typeOfModule"],
        isRequired: true,
        disabled: false,
        placeHolder: "Select Type OF Module",
        dataSet: typeOfModules,
        name: "typeOfModule",
        validate: {},
        // pattern: {
        //   value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/i,
        //   message: "Only accepts alphabets and integer characters",
        // },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "selectDropdown",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],[
      {
        key: "ordering",
        label: ADMIN_MODULE_CONFIGURATION["ordering"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter Order",
        name: "ordering",
        validate: {},
        // pattern: {
        //   value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/i,
        //   message: "Only accepts alphabets and integer characters",
        // },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "textField",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],
  ];
};
