import { useTheme } from "styled-components";
import { ADMIN_SOIL, typeOfPVConfigurations } from "../../../utils/constant";

export const modeOfPvOperationFormData = () => {
  const theme = useTheme();
  return [
    [
      {
        key: "modeOfOperation",
        label: ADMIN_SOIL["modeOfOperation"],
        isRequired: true,
        disabled: false,
        placeHolder: "Select name",
        dataSet: typeOfPVConfigurations,
        name: "modeOfOperation",
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
        componentType: "selectDropdown",
        suffix: "",
        //   textFieldType: "numeric",
        requiredMessage: "required field",
        testId: "model",
      },
    ],
  ];
};
