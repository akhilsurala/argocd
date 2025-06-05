import { useTheme } from "styled-components";
import messages from "../../container/apv-sim/messages";
import { useIntl } from "react-intl";
import { ModuleMaskPatternDropdown } from "../constant";

export const pvGeometry = (
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
) => {
  const theme = useTheme();
  const intl = useIntl();
  return [
    [
      {
        key: "runName",
        label: "Run Name",
        placeHolder: "Enter Name",
        name: "runName",
        validate: {handleRunNameValidation},
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/,
          message:
            "Run name must contain letters, can include spaces, and cannot include special characters or be composed of only numbers."
        },
        isRequired: true,
        requiredMessage: "Enter run name",
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
          value: 20,
          message: "Maximum 20 characters are allowed",
        },
        testId: "runName",
      },
    ],
    [
      {
        key: "agriApv",
        label: "",
        handleChange: handleApvToggle,
        isRequired: true,
        placeHolder: "",
        dataSet: [ "APV","Only Agri","Only PV"],
        name: "agriApv",
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
        testId: "agriApv",
      },
    ],
    [
      {
        key: "pvModule",
        label: "PV Module",
        //   handleChange: handleChange,
        isRequired: !disableFields.pvModule,
        disabled: disableFields.pvModule,
        placeHolder: "Select module",
        dataSet: pvModuleList,
        name: "pvModule",
        validate: {},
        pattern: {},
        requiredMessage: "Select PV module",
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
        testId: "pvModule",
      },
    ],
    [
      {
        key: "modeOfPvGeneration",
        label: "Mode Of PV Operation",
        //   handleChange: handleChange,
        isRequired: !disableFields.modeOfPvGeneration,
        disabled: disableFields.modeOfPvGeneration,
        placeHolder: "Select modes",
        dataSet: modeOfPvOperationList,
        name: "modeOfPvGeneration",
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
        requiredMessage: "Select mode of PV operation",
        componentType: "selectDropdown",
        maxLength: {},
        handleChange: handleModeOfPvGeneration,
        testId: "modeOfPvGeneration",
      },
    ],
    [
      {
        key: "moduleConfiguration",
        label: "Module Configuration",
        //   handleChange: handleChange,
        isRequired: !disableFields.moduleConfiguration,
        disabled: disableFields.moduleConfiguration,
        placeHolder: "Select configuration",
        requiredMessage: "Select module configuration",
        dataSet: moduleConfigurationList,
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
        testId: "moduleConfiguration",
      },
    ],
    [
      {
        key: "soilId",
        label: "Soil Type",
        //   handleChange: handleChange,
        isRequired: !disableFields.soilType,
        disabled: disableFields.soilType,
        placeHolder: "Select type",
        dataSet: soilTypeList,
        name: "soilId",
        validate: {},
        pattern: {},
        requiredMessage: "Select soil type",
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
        testId: "soilId",
      },
    ],
    [
      {
        key: "moduleMaskPattern",
        label: "Module Mask Pattern",
        //   handleChange: handleChange,
        isRequired:false,
        disabled: disableFields.moduleMaskPattern,
        placeHolder: "Select pattern",
        dataSet: ModuleMaskPatternDropdown,
        name: "moduleMaskPattern",
        validate: {},
        pattern: {},
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.moduleMaskPattern}),
        requiredMessage: "Select module mask pattern",
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
        testId: "moduleMaskPattern",
      },
    ],
    // [
    //   {
    //     key: "moduleMaskPattern",
    //     label: "Module Mask Pattern",
    //     isRequired: false,
    //     disabled: disableFields.moduleMaskPattern,
    //     placeHolder: "Enter value",
    //     name: "moduleMaskPattern",
    //     requiredMessage: "Enter module mask pattern",
    //     validate: {},
    //     pattern: {
    //       value: /^(?=.*0)(?=.*1)[01]+$/,
    //       message:
    //         "Only accepts combination of '0' and '1'",
    //     },

    //     showToolTip : true,
    //     toolTipMessage : intl.formatMessage({...messages.moduleMaskPattern}),
    //     inputProps: {
    //       sx: {
    //         borderRadius: "6px",
    //         "& fieldset": {
    //           borderColor: theme.palette.border.main,
    //         },
    //       },
    //     },
    //     type: "text",
    //     componentType: "textField",
    //     textFieldType : "numeric",
    //     maxLength: {
    //       value: 10,
    //       message: "Maximum 10 bits are allowed",
    //     },
    //     testId: "moduleMaskPattern",
    //   },
    // ],
    [
      {
        key: "tiltIfFt",
        label: "Tilt Angle",
        placeHolder: "Enter value",
        name: "tiltIfFt",
        validate: {},
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message:
            "Invalid Tilt Angle",
        },
        requiredMessage: "Enter value in Tilt Angle",
        isRequired: !disableFields.tiltIfFt,
        disabled: disableFields.tiltIfFt,
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        suffix:"degree",
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.tiltIfFt}),
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "tiltIfFt",
        min: {
          value: -90,
          message:
            "Only accepts values ranging from -90 to 90",
        },
        max: {
          value: 90,
          message:
            "Only accepts values ranging from -90 to 90",
        },
      },
    ],
    [
      {
        key: "maxAnglesOfTracking",
        label: "Max Angle Of Tracking",
        isRequired: !disableFields.maxAnglesOfTracking,
        disabled: disableFields.maxAnglesOfTracking,
        placeHolder: "Enter value",
        name: "maxAnglesOfTracking",
        validate: {},
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message:
            "Invalid Max Angle Of Tracking",
        },
        requiredMessage: "Enter value in Ft Max Angles Of Tracking",
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.maxAnglesOfTracking}),
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        suffix:"degree",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "maxAnglesOfTracking",
        min: {
          value: -60,
          message:
            "Only accepts values ranging from -60 to 60",
        },
        max: {
          value: 60,
          message:
            "Only accepts values ranging from -60 to 60",
        },
      },
    ],
    [
      {
        key: "height",
        label: "Height",
        isRequired: !disableFields.height,
        disabled: disableFields.height,
        placeHolder: "Enter value",
        name: "height",
        requiredMessage: "Enter the height",
        validate: validateHeight,
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message: `Invalid Height`,
        },
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.height}),
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        suffix:"meters",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "height",
      },
    ],
    [
      {
        key: "lengthOfOneRow",
        label: "Length Of One Row",
        isRequired: !disableFields.lengthOfOneRow,
        disabled: disableFields.lengthOfOneRow,
        placeHolder: "Enter value",
        name: "lengthOfOneRow",
        validate: validateLengthOfOneRow,
        requiredMessage: "Enter the length of one row",
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message:
          `Invalid Length of One Row`,
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        suffix:"meters",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "lengthOfOneRow",
      },
    ],
    [
      {
        key: "gapBetweenModules",
        label: "Gap Between Modules",
        isRequired: !disableFields.gapBetweenModules,
        disabled: disableFields.gapBetweenModules,
        placeHolder: "Enter value",
        name: "gapBetweenModules",
        requiredMessage: "Enter the gap between modules",
        validate: validateGapBetweenModules,
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message: "Invalid Gap Between Modules",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.gapBetweenModules}),
        suffix:"millimeters",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "gapBetweenModules",
      },
    ],
    
    [
      {
        key: "pitchOfRow",
        label: "Pitch Of Rows",
        isRequired: !disableFields.pitchOfRow,
        disabled: disableFields.pitchOfRow,
        placeHolder: "Enter value",
        name: "pitchOfRow",
        requiredMessage: "Enter pitch of rows",
        validate: validatePitchOfRow,
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message: "Invalid Pitch of Rows",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.pitchOfRow}),
        suffix:"meters",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "pitchOfRow",
      },
    ],
    [
      {
        key: "azimuth",
        label: "Azimuth",
        isRequired: !disableFields.azimuth,
        disabled: disableFields.azimuth,
        placeHolder: "Enter value",
        name: "azimuth",
        validate: {},
        requiredMessage: "Enter Azimuth",
        showToolTip : true,
        toolTipMessage : intl.formatMessage({...messages.azimuth}),
        pattern: {
          value: /^(-?\d+(\.\d*)?)$|^(-?\.\d+)$/,
          message:
            "Invalid Azimuth",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: theme.palette.border.main,
            },
          },
        },
        suffix:"degree",
        type: "text",
        componentType: "textField",
        textFieldType : "numeric",
        maxLength: {
          value:12,
          message: "Maximum 12 characters are allowed",
        },
        testId: "azimuth",
        min: {
          value: 0,
          message:
            "Only accepts values ranging from 0 to 360",
        },
        max: {
          value: 360,
          message:
            "Only accepts values ranging from 0 to 360",
        },
      },
    ],
    
    
  ];
};
