import { useTheme } from "styled-components";
import { ADMIN_USER } from "../../../utils/constant";

export const userFormData = (roleDataset) => {
  const theme = useTheme();
  return [
    [
      {
        key: "firstName",
        label: "First Name",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter first name",
        // register: register,
        // errors: errors,
        name: "firstName",
        validate: {},
        pattern: {
          value : /^[A-Za-z]+$/i,
          message : "Invalid first name"},
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: "#E1D6C8",
            },
          },
        },
        requiredMessage:"Enter first name",
        type: "text",
        componentType: "textField",
        maxLength: {
          value: 20,
          message: "Maximum 20 characters are allowed"
        }
      }
    ],
    [
      {
        key: "lastName",
        label: "Last Name",
        // handleChange: handleChange,
        isRequired: false,
        placeHolder: "Enter last name",
        // register: register,
        // errors: errors,
        name: "lastName",
        validate: {},
        pattern: {
          value : /^[A-Za-z]+$/i,
          message : "Invalid last name"
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: "#E1D6C8",
            },
          },
        },
        type: "text",
        componentType: "textField",
        maxLength: {
          value: 20,
          message: "Maximum 20 characters are allowed"
        }
      },
    ],
    [
      {
        key: "emailId",
        label: "Email",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter email",
        // register: register,
        // errors: errors,
        requiredMessage:"Enter email",
        name: "emailId",
        validate: {},
        pattern: {
          value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
          message: "Invalid email format",
        },
        inputProps: {
          sx: {
            borderRadius: "6px",
            "& fieldset": {
              borderColor: "#E1D6C8",
            },
          },
        },
        type: "text",
        componentType: "textField",
        maxLength: {
          value: 50,
          message: "Maximum 50 characters are allowed"
        }
      },
    ],
    [
      {
         key: "type",
        label: "Type",
        //   handleChange: handleChange,
        isRequired: true,
        disabled: false,
        placeHolder: "Select type",
        dataSet: roleDataset,
        name: "type",
        requiredMessage: "Select type",
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
        testId: "type",
      },
    ],
  ];
};
