export const signUpPage = (
  showPassword,
  showConfirmPassword,
  inputProps,
  confirmInputProps,
  validatePassword
) => {
  return [
    [
      {
        key: "firstName",
        label: "First Name",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter First Name",
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
        type: "text",
        componentType: "textField",
        maxLength: {
          value: 20,
          message: "Maximum 20 characters are allowed"
        }
      },
      {
        key: "lastName",
        label: "Last Name",
        // handleChange: handleChange,
        isRequired: false,
        placeHolder: "Enter Last Name",
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
        key: "email",
        label: "Email",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter Email",
        // register: register,
        // errors: errors,
        name: "email",
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
          message: "Maximum 50 characters are allowed",
        },
      },
    ],
    [
      {
        key: "password",
        label: "Password",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter Password",
        name: "password",
        pattern: {},
        inputProps: inputProps,
        type: showPassword ? "text" : "password",
        componentType: "textField",
        maxLength: {
          value:30,
          message: "Maximum 30 characters are allowed",
        },
        validate: {},
        pattern: {
          value: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/,
          message:
            "Password must contain one digit, one lowercase letter, one uppercase letter, and one special character, and it must be 8-16 characters long.",
        },
      },
    ],
    [
      {
        key: "confirmPassword",
        label: "Confirm Password",
        // handleChange: handleChange,
        isRequired: true,
        placeHolder: "Enter Confirm Password",
        name: "confirmPassword",
        pattern: {},
        inputProps: confirmInputProps,
        type: showConfirmPassword ? "text" : "password",
        componentType: "textField",
        maxLength: {
          value:30,
          message: "Maximum 30 characters are allowed",
        },
        validate: validatePassword,
        requiredMessage:"Enter password again"
      },
    ],
  ];
};
