import { useTheme } from "styled-components";

export const cropParameters = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "CroppingCycle",
        label: "Cropping Cycle",
        //   handleChange: handleChange,
        isRequired: true,
        placeHolder: "Select Cycle",
        dataSet: ["Rabi", "Kharif", "Zaid", "Rabi-Kharif"],
        name: "CroppingCycle",
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
        testId: "croppingCycle",
      },
    ],
    [
      {
        key: "bedType",
        label: "Bed Type",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "",
        dataSet: ["Inter Bed", "Intra Bed"],
        name: "bedType",
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
        componentType: "radioButton",
        maxLength: {},
        testId: "bedType",
      },
    ],
    [
      {
        key: "addCrops",
        label: "Add Crops",
        //   handleChange: handleChange,
        isRequired: true,
        placeHolder: "Select Crop",
        dataSet: [
          "Tomato Akra Rakshak",
          "Tomato Akra Vikas",
          "Capsicum Annum L.",
          "Capsicum Frutenses L.",
        ],
        name: "addCrops",
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
        testId: "addCrops",
      },
      {
        key: "spacing",
        label: "",
        isRequired: true,
        placeHolder: "Enter Spacing",
        name: "spacing",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message:
            "Please enter an integer value within the range of 0 to 10 for Spacing",
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
        requiredMessage: "Spacing is required",
        testId: "spacing",
        min: {
          value: 0,
          message:
            "Please enter an integer value within the range of 0 to 10 for Spacing",
        },
        max: {
          value: 10,
          message:
            "Please enter an integer value within the range of 0 to 10 for Spacing",
        },
      },
    ],
    [
      {
        key: "dateOfSowing",
        label: "Date of Sowing",
        isRequired: true,
        placeHolder: "Enter Pattern",
        name: "dateOfSowing",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message:
            "Please enter an integer value within the range of -90 to 90 for Tilt if FT",
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
        componentType: "datePicker",
        testId: "dateOfSowing",
      },
    ],
    [
      {
        key: "croppingPattern",
        label: "Cropping Pattern",
        isRequired: true,
        placeHolder: "Enter Pattern",
        name: "croppingPattern",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Please enter an integer value within the range of 0 to 10",
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
        testId: "croppingPattern",
        min: {
          value: 0,
          message: "Please enter an integer value within the range of 0 to 10",
        },
        max: {
          value: 10,
          message: "Please enter an integer value within the range of 0 to 10",
        },
      },
    ],
    [
      {
        key: "startPointOffset",
        label: "Start Point Offset",
        isRequired: true,
        placeHolder: "Enter Point Offset",
        name: "startPointOffset",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Please enter an integer value within the range of 0 to 10000",
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

        testId: "startPointOffset",
        min: {
          value: 0,
          message: "Please enter an integer value within the range of 0 to 10000",
        },
        max: {
          value: 10000,
          message: "Please enter an integer value within the range of 0 to 10000",
        },
      },
    ],
    [
      {
        key: "dashedLine",
        label: "Start Point Offset",
        isRequired: false,
        name: "dashedLine",

        type: "text",
        componentType: "dashedLine",
      },
    ],
    [
      {
        key: "bedCC",
        label: "No. of Bed CC",
        isRequired: true,
        placeHolder: "Enter Value",
        name: "bedCC",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Please enter an integer value within the range of 0 to 10",
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
        testId: "bedCC",
        min: {
          value: 1,
          message: "Please enter an integer value within the range of 1 to 10",
        },
        max: {
          value: 10,
          message: "Please enter an integer value within the range of 1 to 10",
        },
      },
      {
        key: "bedAzimuth",
        label: "Bed Azimuth",
        isRequired: true,
        placeHolder: "Enter Value",
        name: "bedAzimuth",
        validate: {},
        pattern: {
          value: /^-?(0|[1-9]\d*)(\.\d+)?$/,
          message: "Please enter an integer value within the range of 0 to 360",
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
        testId: "bedAzimuth",
        min: {
          value: 0,
          message: "Please enter an integer value within the range of 0 to 360",
        },
        max: {
          value: 360,
          message: "Please enter an integer value within the range of 0 to 360",
        },
      },
    ],
  ];
};
