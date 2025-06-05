import * as React from 'react';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { Box, Button, Grid, Input, InputAdornment, TextField, Typography } from '@mui/material';
import styled, { useTheme } from 'styled-components';
import { Person } from '@mui/icons-material';
import { CustomSvgIconForAddIcon, CustomSvgIconForDelete, CustomSvgIconForDropdown } from '../../../dashboard/CustomSvgIcon';
import { red } from '@mui/material/colors';
/**
 * Renders a select input with an associated text field. The select input is populated with options from the `selectComponentData` prop.
 *
 * @param {Object} props - The props object containing the following properties:
 *   - `selectComponentData` (Array): An array of objects representing the options for the select input.
 *   - `fields` (Array): An array of objects representing the selected values for the select input and text field.
 *   - `register` (Function): A function from the React Hook Form library used for registering form inputs.
 *   - `append` (Function): A function from the React Hook Form library used for appending new fields to the form.
 *   - `remove` (Function): A function from the React Hook Form library used for removing fields from the form.
 *   - `errors` (Object): An object containing error messages for the form inputs.
 * @return {JSX.Element} A JSX element representing the rendered select input and text field.
 */
const handleBlur = (event) => {
    const value = event.target.value;

    // Ensure we round only if it's a valid number
    if (value && !isNaN(value)) {
        if (value.includes(".")) {
            event.target.value = parseFloat(value).toFixed(2);  // Round to one decimal place
        }
    }
};
const handleKeyDown = (event) => {
    if (['e', 'E', '-', '+'].includes(event.key)) {
        event.preventDefault();
    }
    // Block all alphabet letters (a-z and A-Z)
    if (/^[a-zA-Z]$/.test(event.key)) {
        event.preventDefault();
    }

};

const handleInputChange = (event) => {
    let inputValue = event.target.value;

    // Regular expression to allow numbers with up to 2 decimal places
    const decimalRegex = /^\d*\.?\d{0,2}$/;

    // Test if the input value matches the pattern
    if (decimalRegex.test(inputValue)) {
        setValue(inputValue); // Set the state only if input is valid
    }
};
function checkHeight(objects, value, indexToIgnore) {

    const tolerance = 0.01; // 10mm tolerance in meters
    let message = ""; // Initialize message

    objects.forEach((obj, index) => {
        if (index === indexToIgnore) {
            // Ignore this index
            return;
        }

        const objHeight = parseFloat(obj.height); // Convert height to float

        // Check if the height is within ±10mm of the value
        if (Math.abs(objHeight - value) <= tolerance) {
            message = `Height at index ${index} is within ±10mm of the provided value.`;
        }
    });

    if (message) {
        return "Height should vary by 10mm";
    }
    return ""; // Return message or default success message
}

export default function SelectWithInput({ watch, selectComponentData, trigger, fields, register, append, remove, errors, minMaxRange, disabledAllField, watchListAgri, setValue }) {
    // console.log("selectComponentData", selectComponentData);

    const theme = useTheme();
    const [height, setHeight] = React.useState({})
    const Error = ({ children }) => <p style={{ color: "red", margin: '0px' }}>{children}</p>;
    const { agriPvProtectionHeight } = errors;

    // console.log("pph", agriPvProtectionHeight)
    const getSingleRow = (val, index) => {
        let errorType = null
        if (agriPvProtectionHeight && agriPvProtectionHeight[index]) {
            errorType = agriPvProtectionHeight[index];
        }

        const theme = useTheme();


        // Inside getSingleRow:
        let protectionHeight = watch(`agriPvProtectionHeight.${index}.height`);
        let protectionId = watch(`agriPvProtectionHeight.${index}.protectionId`);
        const preserveHeight = (value, index) => {

            const result = checkHeight(watchListAgri, value, index);
            return result ? result : null;
        };

        // console.log("before", protectionId)
        protectionId = selectComponentData.some((item) => item.value == protectionId) ? protectionId : '-99';

        // console.log("before1", protectionId)
        // protectionHeight = protectionId !== '-99' ? protectionHeight : '';




        return (
            <div key={val.id}>

                <Container >
                    <div className='parent'>

                        <FormControl fullWidth size='small' disabled={disabledAllField}  >
                            <InputLabel id="demo-simple-select-label" sx={{ color: disabledAllField && "rgba(0, 0, 0, 0.38)", opacity: disabledAllField ? 0.5 : 0.7 }} >Select type</InputLabel>
                            <Select

                                value={protectionId}
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                {...register(`agriPvProtectionHeight.${index}.protectionId`, {
                                    disabled: disabledAllField,
                                    // required: "This field is required"
                                })}
                                onChange={(e) => {
                                    trigger(`agriPvProtectionHeight.${index}.protectionId`);

                                    setValue(`agriPvProtectionHeight.${index}.protectionId`, e.target.value);
                                    if (e.target.value === "-99") {

                                        trigger(`agriPvProtectionHeight.${index}`);
                                        setValue(`agriPvProtectionHeight.${index}.height`, "");
                                    }

                                }}
                                sx={{
                                    "& fieldset": {
                                        border: 'none', // Remove the border
                                        backgroundColor: disabledAllField && theme.palette.background.faded,
                                    },
                                }}
                                // disableUnderline={true}
                                label="Select Value"
                                defaultValue={protectionId ? protectionId : "-99"}
                            >
                                {selectComponentData?.map((data) => {
                                    return (<MenuItem key={data.value} value={data.value}>
                                        {data.label}
                                    </MenuItem>)

                                })}
                            </Select>
                        </FormControl>
                        <Box sx={{
                            height: '24px',
                            borderRight: '2px solid #E8E2E0',
                            alignSelf: 'center',

                        }}></Box>

                        <Box>
                            <TextField
                                size="small"

                                value={protectionHeight}

                                {...register(`agriPvProtectionHeight.${index}.height`, {
                                    required: protectionId == '-99' || disabledAllField ? false : "This field is required",

                                    validate: (val) => preserveHeight(val, index),
                                    max: {
                                        value: minMaxRange.protectionHeight[1],
                                        message: 'Enter an integer value within the range of ' + minMaxRange.protectionHeight[0] + ' to ' + minMaxRange.protectionHeight[1] // JS only: <p>error message</p> TS only support string
                                    },
                                    min: {
                                        value: minMaxRange.protectionHeight[0],
                                        message: 'Enter an integer value within the range of ' + minMaxRange.protectionHeight[0] + ' to ' + minMaxRange.protectionHeight[1] // JS only: <p>error message</p> TS only support string
                                    }

                                },)}
                                onChange={(e) => {
                                    let inputValue = e.target.value;

                                    // Regex to allow numbers with up to 2 decimal places
                                    const decimalRegex = /^\d*\.?\d{0,2}$/;

                                    // If it doesn't match, truncate the input to two decimal places
                                    if (!decimalRegex.test(inputValue)) {
                                        if (inputValue.includes(".")) {
                                            // Split the value into integer and decimal parts
                                            const [integerPart, decimalPart] = inputValue.split(".");
                                            // Truncate the decimal part to 2 digits
                                            const truncatedDecimal = decimalPart.substring(0, 2);
                                            // Rebuild the value with the truncated decimal
                                            inputValue = `${integerPart}.${truncatedDecimal}`;
                                        }
                                    }

                                    // Set the valid input value

                                    setValue(`agriPvProtectionHeight.${index}.height`, inputValue);
                                }}
                                placeholder="Height"
                                fullWidth
                                className="[&::-webkit-inner-spin-button]:appearance-none"


                                defaultValue={protectionHeight !== '-99' ? protectionHeight : ""}

                                InputProps={{
                                    // disableUnderline: true,

                                    onKeyDown: handleKeyDown, // Only prevent unwanted key inputs here
                                    // onBlur: handleBlur,       // Apply rounding only when the field loses focus
                                    endAdornment: <InputAdornment position="end"> <Typography sx={{
                                        fontFamily: 'Montserrat',
                                        fontSize: '14px',
                                        fontStyle: 'italic',
                                        fontWeight: '500',
                                        color: '#53988E80'
                                    }}>meters</Typography> </InputAdornment>,


                                }}

                                disabled={disabledAllField || protectionId === "-99"}

                                sx={{
                                    "& fieldset": { border: 'none' },

                                    backgroundColor: disabledAllField && theme.palette.background.faded,
                                }}


                            />
                        </Box>


                    </div>
                    {index === 0 ?

                        <Box sx={{ minWidth: '5%', alignSelf: 'center' }}>
                            {/* <Button onClick={() => setArr((ar) => [...ar, { key: arr.length + 1, obj: {} }])} > */}
                            <Button
                                style={{ outline: 'none' }}
                                disabled={disabledAllField || fields.length === 3} onClick={() => append(
                                    {
                                        "protectionId": "-99",
                                        "height": ""

                                    })} >
                                <CustomSvgIconForAddIcon sx={{ color: theme.palette.secondary.main }} />
                            </Button>
                        </Box> :
                        <Box sx={{ minWidth: '5%', alignSelf: 'center' }}>
                            <Button
                                style={{ outline: 'none' }} onClick={() => remove(index)} >
                                <CustomSvgIconForDelete sx={{ color: '#C7C9CA' }} />
                            </Button>
                        </Box>
                    }

                </Container >
                {/* {errorType && console.log("ssserror", errorType, errorType['height'].message, errorType['protectionId'].type)} */}
                <Grid container spacing={0}>

                    <Grid item xs={8}>
                        {errorType && errorType['protectionId']?.message && <Error> {errorType['protectionId'].message}</Error>}
                    </Grid>
                    <Grid item xs={4}>
                        {errorType && errorType?.['height']?.message && <Error> {errorType['height']?.message}</Error>}
                    </Grid>
                </Grid>
            </div>)
    }

    // React.useEffect(() => {
    //     fields.map((val, index) => setHeight({ ...height, [val.height]: index }));
    // }, [fields])

    return <Block>
        {fields.map((val, index) => getSingleRow(val, index))}
    </Block>;
}

const Container = styled.div`
display: flex;

    .parent {
        font-family: "Montserrat";
        display: flex;
        flex-grow: 1;
        gap: 0px;
        border-radius: 6px;
        border: 1px solid #E8E2E0;

    }
`

const Block = styled.div`
    display: flex;
    flex-direction: column;
    gap: 10px;
`
