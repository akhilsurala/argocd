import { useTheme } from "styled-components";
import { ADMIN_SOIL } from "../../../utils/constant";

export const soilFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "soilName",
        label: ADMIN_SOIL["soilName"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter name",
        name: "soilName",
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
