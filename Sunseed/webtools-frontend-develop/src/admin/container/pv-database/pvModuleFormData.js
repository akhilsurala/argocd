import { useTheme } from "styled-components";
import { ADMIN_PV_MODULE } from "../../../utils/constant";

export const pvModuleFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "moduleType",
        label: ADMIN_PV_MODULE["moduleType"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter module name",
        name: "moduleType",
        validate: {},
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ._]+$/,
          message: "Only accepts alphabets and integer characters",
        },
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
    [
      {
        key: "length",
        label: ADMIN_PV_MODULE["length"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter length",
        name: "length",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Only accepts a decimal value",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "number",
        componentType: "textField",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],
    [
      {
        key: "width",
        label: ADMIN_PV_MODULE["width"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter width",
        name: "width",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Only accepts decimal value",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "number",
        componentType: "textField",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],
  ];
};
