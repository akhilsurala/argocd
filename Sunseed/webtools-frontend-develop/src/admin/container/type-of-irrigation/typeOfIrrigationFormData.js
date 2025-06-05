import { useTheme } from "styled-components";
import { ADMIN_SOIL } from "../../../utils/constant";

export const typeOfIrrigationFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "irrigationType",
        label: ADMIN_SOIL["irrigationType"],
        isRequired: true,
        disabled: false,
        placeHolder: "Enter name",
        name: "irrigationType",
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
