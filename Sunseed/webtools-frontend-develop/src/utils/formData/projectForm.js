import { useTheme } from "styled-components";

export const projectForm = (options,handleChange, pinCoordinates,setPinCoordinates) => {
    const theme = useTheme();
  return [
    [  {
      key: "projectName",
      label: "Project Name",
      isRequired: true,
      placeHolder: "Project Name",
      name: "projectName",
      validate: {},
      pattern: {
        value : /^[A-Za-z]+$/i,
        message : "Invalid project name"},
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
      testId:"projectName"
    },
   
      {
        key: "latitude",
        label: "Project Location",
        isRequired: true,
        disabled: true,
        placeHolder: "N",
        name: "latitude",
        
        value:pinCoordinates.lat,
        validate: {},
        pattern: {},
        inputProps: {
          sx: {

          // width:'50%',
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "textField",
      },
       
      {
        key: "longitude",
        label: "",

        disabled: true,
        isRequired: true,
        placeHolder: "E",
        name: "longitude",
        value:pinCoordinates.lng,
        validate: {},
        pattern: {},
        inputProps: {
          sx: {

          // width:'50%',
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        type: "text",
        componentType: "textField",
        requiredMessage:"Project Location is required"
      },
      

      {


        name: "submit",
        value:pinCoordinates.lng,
        validate: {},
        pattern: {},
        type: "text",
        componentType: "textField",
        requiredMessage:"Project Location is required",
        key: "submitButton",
        componentType: "button",
        type:"submit",
        label: "Submit", // Button label
        variant: "contained", // Button variant
        className: "btn", // Custom class name for button
        testId: "submitButton", // Test ID for testing purposes
        sx: {
          // width: '50%',
          borderRadius: "6px",
            backgroundColor: theme.palette.secondary.main,
          
        },
        onClick: () => {
          // Functionality when button is clicked
          // e.g., handleSubmit() or any other action
        }},
        
      
    ],
   
    [
      {
        key: "googleMap",
        label: "googleMap",
        isRequired: false,
        placeHolder: "googleMap",
        name: "googleMap",
        validate: {},
        pattern: {
          value : /^[A-Za-z]+$/i,
          message : "Invalid project name"},
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        pinCoordinates: pinCoordinates,
        setPinCoordinates: setPinCoordinates,
        type: "text",
        componentType: "googleMap",
        maxLength: {
          value: 30,
          message: "Maximum 30 characters are allowed",
        },
        testId:"googleMap"
      },
    ],
    // [
    //   {
    //     key: "calculationApproach",
    //     label: "Calculation Approach",
    //     handleChange: handleChange,
    //     isRequired: true,
    //     placeHolder: "",
    //     dataSet: ["Fixed Land", "Fixed PV Capacity"],
    //     name: "calculationApproach",
    //     validate: {},
    //     pattern: {},
    //     inputProps: {
    //       sx: {
    //         borderRadius: "6px",
    //         "& fieldset": {
    //           borderColor: theme.palette.border.main,
    //         },
    //       },
    //     },
    //     componentType: "radioButton",
    //     maxLength: {},
    //   },
    // ],
    // [
    //   {
    //     key: options.key,
    //     label: options.label,
    //     handleChange: handleChange,
    //     isRequired: true,
    //     placeHolder: options.placeHolder,
    //     name: options.key,
    //     validate: {},
    //     pattern: {},
    //     inputProps: {
    //       sx: {
    //         borderRadius: "6px",
    //         "& fieldset": {
    //           borderColor: theme.palette.border.main,
    //         },
    //       },
    //     },
    //     componentType: "textField",
    //     maxLength: {},
    //   },
    // ],
  ];
};
