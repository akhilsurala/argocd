import { useTheme } from "styled-components";

export const agriGeneral = (disableFields) => {
  const theme = useTheme();
  return [
    [
      {
        key: "azimuth",
        label: "Azimuth",
        isRequired: !disableFields,
        disabled: disableFields,
        placeHolder: "Enter Value",
        name: "azimuth",
        validate: {},
        pattern: {
          value: /^(0|[1-9]\d*)(\.\d+)?$/,
          message:
            "Please enter an integer value within the range of 0 to 360.",
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
        maxLength: {
          value: 30,
          message: "Maximum 30 characters are allowed",
        },
        testId: "azimuth",
        min: {
          value: 0,
          message:
            "Please enter an integer value within the range of 0 to 360.",
        },
        max: {
          value: 360,
          message:
            "Please enter an integer value within the range of 0 to 360.",
        },
      },
    ],
    [
      {
        key: "lengthOfOneRow",
        label: "Length Of One Row",
        isRequired: !disableFields,
        disabled: disableFields,
        placeHolder: "Enter Value",
        name: "lengthOfOneRow",
        validate: {},
        pattern: {
          value: /^(0|[1-9]\d*)(\.\d+)?$/,
          message:
            "Please enter an integer value within the range of 1 to 1000.",
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
        maxLength: {
          value: 30,
          message: "Maximum 30 characters are allowed",
        },
        testId: "lengthOfOneRow",
        min: {
          value: 1,
          message:
            "Please enter an integer value within the range of 1 to 1000.",
        },
        max: {
          value: 1000,
          message:
            "Please enter an integer value within the range of 1 to 1000.",
        },
      },
    ],
    [
      {
        key: "moduleConfiguration",
        label: "Module Configuration",
        //   handleChange: handleChange,
        isRequired: !disableFields,
        disabled: disableFields,
        placeHolder: "Select Configuration",
        dataSet: [
          "P1",
          "P2",
          "P3",
          "P1-P1",
          "P2-P2",
          "P3-P3",
          "L1",
          "L2, L3",
          "L1-L1",
          "L2-L2",
          "L3-L3",
        ],
        name: "moduleConfiguration",
        validate: {},
        pattern: {},
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        componentType: "selectDropdown",
        maxLength: {},
      },
    ],
  ];
};
