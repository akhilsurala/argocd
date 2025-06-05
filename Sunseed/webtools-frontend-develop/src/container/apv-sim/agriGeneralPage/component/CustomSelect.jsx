import { useTheme } from 'styled-components';
import { FormControl, InputLabel, MenuItem, Select } from '@mui/material'
import React from 'react'
import { Controller } from 'react-hook-form'

export default function CustomSelect({ name,
    control,
    errors,
    data,
    rules,
    palceHolder,
    disabledAllField,
    backgroundColor = 'inherit',
    required }) {

    const theme = useTheme();
    const Error = ({ children, backgroundColor }) => <p style={{ backgroundColor: backgroundColor, color: "red", margin: '0px' }}>{children}</p>;

    return (
        <section>
            <Controller
                name={name}
                control={control}
                disabled={disabledAllField}
                rules={{
                    required: required === 'none' ? false : "Select Value",
                    validate: {
                        ...rules?.validate,
                    }
                }
                }
                defaultValue=""
                render={({ field }) => (
                    <FormControl fullWidth size='small'>
                        <InputLabel id="demo-simple-select-label" sx={{ color: disabledAllField && "rgba(0, 0, 0, 0.38)", opacity: disabledAllField ? 0.5 : 0.7 }}>Select type</InputLabel>
                        <Select
                            {...field}
                            labelId="demo-simple-select-label"
                            id="demo-simple-select"
                            label="Select type"
                            data-testid="id"
                            error={!!errors[name]}
                            // data-testid={testId}
                            // disabled={disabled}
                            style={{
                                width: '100%'
                            }}
                            sx={{

                                backgroundColor: disabledAllField && theme.palette.background.faded,
                                width: "-webkit-fill-available",
                                "& fieldset": {
                                    borderColor: theme.palette.border.main,
                                    // backgroundColor: disabled && theme.palette.background.faded,
                                },
                                "& .MuiSvgIcon-root": {
                                    color: theme.palette.border.main,
                                },
                            }}
                            MenuProps={{
                                PaperProps: {
                                    sx: {
                                        maxHeight: 150,

                                        "&::-webkit-scrollbar": {
                                            width: 6,
                                            // backgroundColor: theme.palette.background.secondary,
                                        },
                                        "&::-webkit-scrollbar-track": {
                                            boxShadow: `#D5D5D5`,
                                        },
                                        "&::-webkit-scrollbar-thumb": {
                                            backgroundColor: theme.palette.primary.main,

                                            borderRadius: "8px",
                                        },
                                    },
                                },
                            }}
                        >
                            {data.map(({ value, label }) => (
                                <MenuItem
                                    key={value}
                                    value={value}
                                    sx={{
                                        color: theme.palette.text.main,
                                        fontSize: "14px",
                                        lineHeight: "26px",
                                        fontFamily: theme.palette.fontFamily.main,
                                        fontWeight: 500
                                    }}
                                >
                                    {label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    // <Select
                    //     size="small"

                    //     placeholder="Age"
                    //     labelId="demo-simple-select-label"
                    //     id="demo-simple-select"

                    //     {...field}
                    //     data-testid="id"
                    //     error={!!errors[name]}
                    //     // data-testid={testId}
                    //     // disabled={disabled}

                    //     sx={{

                    //         width: "-webkit-fill-available",
                    //         "& fieldset": {
                    //             borderColor: theme.palette.border.main,
                    //             // backgroundColor: disabled && theme.palette.background.faded,
                    //         },
                    //         "& .MuiSvgIcon-root": {
                    //             color: theme.palette.border.main,
                    //         },
                    //     }}
                    //     MenuProps={{
                    //         PaperProps: {
                    //             sx: {
                    //                 maxHeight: 150,

                    //                 "&::-webkit-scrollbar": {
                    //                     width: 6,
                    //                     // backgroundColor: theme.palette.background.secondary,
                    //                 },
                    //                 "&::-webkit-scrollbar-track": {
                    //                     boxShadow: `#D5D5D5`,
                    //                 },
                    //                 "&::-webkit-scrollbar-thumb": {
                    //                     backgroundColor: theme.palette.primary.main,

                    //                     borderRadius: "8px",
                    //                 },
                    //             },
                    //         },
                    //     }}
                    // >
                    //     {data.map(({ value, label }) => (
                    //         <MenuItem
                    //             key={value}
                    //             value={value}
                    //             sx={{
                    //                 color: theme.palette.text.main,
                    //                 fontSize: "14px",
                    //                 lineHeight: "26px",
                    //                 fontFamily: theme.palette.fontFamily.main,
                    //                 fontWeight: 500
                    //             }}
                    //         >
                    //             {label}
                    //         </MenuItem>
                    //     ))}
                    // </Select>
                )}
            >

            </Controller>
            {errors[name] && <Error backgroundColor={backgroundColor}> {errors[name].message}</Error>}
        </section >
    )
}
