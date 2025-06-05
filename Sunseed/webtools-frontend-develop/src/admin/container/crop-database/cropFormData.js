import { useTheme } from "styled-components";
import { ADMIN_CROP } from "../../../utils/constant";

export const cropFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "name",
        label: ADMIN_CROP["name"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter name",
        name: "name",
        validate: {},
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/i,
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
  ];
};
