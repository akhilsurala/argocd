import { InputAdornment, TextField, Typography } from "@mui/material";
import React from "react";
import { Controller } from "react-hook-form";
import { useTheme } from "styled-components";

export default function CustomInputField({ prefixArray = '', name, control, errors, rules, placeholder, type, inputProps, disabled = false, endLabel, noFlotingValue = false,
    backgroundColor = 'inherit' }) {
    const theme = useTheme();

    const getInputProps = (label) => {
        return {

            endAdornment: (
                <InputAdornment position="end">
                    <Typography sx={{
                        fontFamily: 'Montserrat',
                        fontSize: '14px',
                        fontStyle: 'italic',
                        fontWeight: '500',
                        color: '#53988E80'
                    }}>{label}</Typography>
                </InputAdornment>
            ),
        };
    };

    const validateDecimalPlaces = (value) => {
        if (type === 'number' && value) {
            const regex = /^-?\d+(\.\d{0,3})?$/;
            return regex.test(value) || 'Only three decimal places are allowed';
        }
        return true;
    };
    const handleKeyDown = (event) => {
        // Prevent invalid characters for numeric inputs
        if (type === "number" && ["e", "E", "+"].includes(event.key)) {
            event.preventDefault();
        }
        
        // Prevent `.` if floating values are disallowed
        if (type === "number" && noFlotingValue && event.key === ".") {
            event.preventDefault();
        }
    };

    const handleBlur = (event) => {
        if (type === "number" && !noFlotingValue) {
            const value = event.target.value;
            if (value.includes(".")) {
                event.target.value = parseFloat(value).toFixed(2); // Allow up to two decimal places
            }
        }
    };
    const Error = ({ children }) => <p style={{ backgroundColor: backgroundColor, color: "red", margin: '0px' }}>{children}</p>;
    return (
        <section>
            <Controller
                name={prefixArray + name}
                control={control}
                rules={{
                    ...rules,
                    validate: {
                        ...rules.validate,
                        validateDecimalPlaces
                    },
                }}
                disabled={disabled}
                defaultValue=""

                render={({ field }) => (
                    <TextField
                        {...field}
                        type={type ? type : ""}

                        // inputProps={{
                        //     step: type === 'number' ? "0.1" : null,
                        // }}
                        error={!!errors[name]}
                        style={{
                            width: '100%'
                        }}
                        sx={{

                            borderColor: 'E0E0E0',
                            width: "-webkit-fill-available",
                            "& :focus": {
                                outline: 0,
                            },
                            backgroundColor: disabled && theme.palette.background.faded,
                        }}
                        InputProps={{
                            ...inputProps,
                            autoComplete: "off",
                            onKeyDown: handleKeyDown,
                            ...endLabel && getInputProps(endLabel)
                        }}
                        variant="outlined"
                        placeholder={placeholder ? placeholder : "Enter value"}
                        autoComplete="off"
                        size="small"

                    />

                )}
            />

            {errors[name] && <Error backgroundColor={backgroundColor}>{errors[name].message}</Error>}
        </section>
    )
}
