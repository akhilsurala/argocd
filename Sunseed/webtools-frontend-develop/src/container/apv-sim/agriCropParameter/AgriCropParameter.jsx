import React, { useEffect, useRef, useState } from "react";
import { styled, useTheme } from "styled-components";


import { Accordion, AccordionDetails, AccordionSummary, Box, Button, Card, CssBaseline, FormHelperText, Grid, IconButton, MenuItem, Select, Stack, TextField, Tooltip } from "@mui/material";
import { useForm } from "react-hook-form";
import CustomInputField from "../agriGeneralPage/component/CustomInputField";
import { CustomSvgIconForCalender, CustomSvgIconForDelete, CustomSvgIconForGoArrow, CustomSvgIconForLeaf, CustomSvgIconForToolTip } from "../../dashboard/CustomSvgIcon";
import { ExpandMore } from "@mui/icons-material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import CustomSelect from "../agriGeneralPage/component/CustomSelect";
import AddRemoveBlocks from "./AddRemoveBlock";
import DefineBed from "./DefineBed";
import AddedBedBlock from "./AddedBedBlock";
import { useImmer } from "use-immer";
import TabeForBedDetails from "./TabeForBedDetails";
import { addCropParameter, getCropMasterDataParameter, getCropParameter, updateCropParameter } from "../../../api/userProfile";
import { useDispatch, useSelector } from "react-redux";
import { setAgriCropsType } from "../../../redux/action/agriCropParameters";


const Error = ({ children }) => <p style={{ color: "red", margin: '0px' }}>{children}</p>;
const AgriCropParameter = ({ setActiveStep, projectId, runId, isCloned,
    isVarient, setIsVarient, }) => {
    const theme = useTheme();
    const {
        handleSubmit,
        control,
        watch, register,
        reset,
        setValue,
        formState: { errors, },
    } = useForm();

    const [id, setId] = useState(null);
    const [disabledCreatCycle, setDisabledCreateCycle] = useState(false);
    const [trigger, setTrigger] = useState(false);
    const toggle = useSelector((state) => state.preProcessor.apvToggle);
    const disabledAllField = toggle === 'Only PV' ? true : false

    const [cycleNames, setCycleNames] = useState(new Set());
    const [showErrorMessage, setShowErrorMessage] = useState("");
    const [tableForBedDetails, setTableForBedDetails] = useState([]);

    const [agriCropParameterData, setAgriCropParameterData] = useImmer(
        []);
    const [intialStateData, setIntialStateData] = useImmer(
        [])
    const [count, setCount] = useState(0);

    const [deletedCycle, setDeletedCycle] = useState([]);
    const onSubmit = (data) => {
        reset({ cycleName: '' })
        setAgriCropParameterData((draft) => {
            draft.push({
                "cycleName": data.cycleName,
                "cycleBedDetails": [], // Add your desired initial values for cycleBedDetails if needed
                "interBedPattern": [], // Add your desired initial values for interBedPattern if needed,
                "cycleStartDate": "",
                "deletedBedDetailsId": []
            });
        });
    };
    const dispatch = useDispatch();

    const getCycleNames = (cycles) => {
        const cycleNamesSet = new Set();
        cycles.forEach(cycle => {
            cycleNamesSet.add((cycle.cycleName).toLowerCase());
        });
        return cycleNamesSet;
    };



    useEffect(() => {
        const cycleNameSet = getCycleNames(agriCropParameterData);
        setCycleNames(cycleNameSet); // Set the state with the cycle names
    }, [agriCropParameterData])


    // console.log("loclloc", agriCropParameterData);

    function updateBedNames(cycles) {
        return cycles.map(cycle => {
            const updatedCycle = { ...cycle };
            updatedCycle.cycleBedDetails = cycle.cycleBedDetails.map((bedDetail, index) => {
                return {
                    ...bedDetail,
                    bedName: `Bed ${index + 1}`  // Updating the bedName by appending the index
                };
            });
            return updatedCycle;
        });
    }
    const callPostApi = () => {
        const data = {
            "cycles": updateBedNames(agriCropParameterData)
        }
        addCropParameter(data, null, projectId, runId)
            .then((response) => {
                if (response.data.httpStatus === "CREATED") {
                    setActiveStep(active => active + 1)
                }
            })
            .catch((error) => {
                console.log(error);

                // alert(error.response.data.errorMessages[0])
            })
            .finally(() => {
                // setLoader(false);
            });
    }
    const callPutApi = () => {
        const data = {
            "id": id,
            "cycles": updateBedNames(agriCropParameterData),
            "deletedCyclesId": deletedCycle,
        }
        updateCropParameter(data, null, projectId, id, runId)
            .then((response) => {
                if (response.data.httpStatus === "OK") {

                    setActiveStep(active => active + 1)
                }
            })
            .catch((error) => {
                console.log(error);

                // alert(error.response.data.errorMessages[0])
            })
            .finally(() => {
                // setLoader(false);
            });
    }

    function getCycleNamesForInterbed(cycles) {
        const result = [];
        cycles.forEach(cycle => {
            // Check if cycleBedDetails exists and has more than one item
            // and interBedPattern exists and is empty
            if (
                Array.isArray(cycle.cycleBedDetails) &&
                cycle.cycleBedDetails.length > 1 &&
                Array.isArray(cycle.interBedPattern) &&
                cycle.interBedPattern.length === 0
            ) {
                result.push(cycle.cycleName);
            }
        });
        return result;
    }
    const validationMessage = async () => {

        // console.log("agriCropParameterData", getCycleNamesForInterbed(agriCropParameterData));
        // console.log("agriCropParameterData", agriCropParameterData);

        // console.log("here", agriCropParameterData, tableForBedDetails);
        if (agriCropParameterData.length === 0) {
            return "Create atleast one cycle "
        }
        if (agriCropParameterData.length > tableForBedDetails?.cycles?.length) {
            return "Please complete cycle first"
        }
        const anyError = await callChildFunction();
        if (anyError) {
            return "Please save bed crop details"
        }
        if (JSON.stringify(agriCropParameterData) !== JSON.stringify(intialStateData)) {
            return "Some changes are pending please save or discard"
        }

        if (getCycleNamesForInterbed(agriCropParameterData).length > 0) {
            return "Interbed pattern reset in all cycles please update"
        }

        return ""
    }

    const updateIntialState = () => {
        setTrigger(true)

    }

    useEffect(() => {
        if (trigger) {
            setIntialStateData(agriCropParameterData);
            setCount(count => count + 1)
            setTrigger(false)

            // console.log("tgring here", agriCropParameterData, trigger);
        }


    }, [agriCropParameterData, trigger])



    const saveProjectAgriCropParameters = async () => {

        if (disabledAllField) {
            setActiveStep(active => active + 1)
            return;
        }

        setShowErrorMessage("");
        const msg = await validationMessage()
        if (msg !== "") {
            setShowErrorMessage(msg);
            return
        }

        if (id) {
            callPutApi()
        } else {
            callPostApi()
        }
    }

    // console.log("isVarient", isVarient)
    const getAgriCropParameters = () => {

        getCropParameter(projectId, runId)
            .then((response) => {
                if (response.data.success) {
                    // console.log("resop", response.data.data.cycles)
                    if (response.data.data) {
                        setId(response.data.data.id)
                        setAgriCropParameterData(response.data.data.cycles)
                        setIntialStateData(response.data.data.cycles);

                        setCount((count) => count + 1);

                        // console.log("dataaaaaaaaaa", response.data)

                        if (response?.data?.data?.isMaster !== undefined && response?.data?.data?.isMaster !== null)
                            setIsVarient(!(response?.data?.data?.isMaster))
                    }
                }
            })
            .catch((error) => {
                console.log(error);

                // alert(error.response.data.errorMessages[0])
            })
            .finally(() => {
                // setLoader(false);
            });
    }


    useEffect(() => {
        if (disabledAllField) {
            return;
        }

        getAgriCropParameters()


    }, [])



    useEffect(() => {

        if (count) {
            setTable()
        }

    }, [count])
    // console.log("agriCrop", agriCropParameterData)

    const checkIfCycleExists = (cycleName) => {
        if (cycleNames instanceof Set) {
            return cycleNames.has(cycleName.toLowerCase()); // Use the .has() method
        }
        return false
    };

    const createCycleBlock = () => {
        const name = "cycleName"
        return (
            <section style={{ marginBottom: '20px', }}>
                <Stack style={{ border: '1px solid #E0E0E0', borderRadius: '8px', padding: '10px 20px' }}>
                    <p>Create Cycle</p>
                    <Box style={{
                        display: 'flex',
                    }}>
                        <div style={{ flexGrow: '1' }}>
                            <CustomInputField
                                name={name}

                                control={control}
                                errors={errors}
                                disabled={disabledCreatCycle || disabledAllField || isVarient}
                                placeholder="Enter Name"

                                rules={{
                                    required: "This field is required",
                                    validate: {
                                        sameNameChecking: (value) => {
                                            if (checkIfCycleExists(value)) {
                                                return "Same name cycle exist"
                                            }
                                            return null
                                        },
                                        noOnlySpaces: (value) => value.trim() !== "" || "Input cannot contain only spaces",
                                        maxLength: (value) => value.length <= 25 || "Maximum character length is 25"
                                    }
                                }} />
                        </div>
                        <IconButton
                            type="submit"

                            disabled={disabledCreatCycle || disabledAllField || isVarient}
                            sx={{
                                ':focus': {
                                    outline: 0
                                }
                            }}
                            style={{
                                color: 'white',
                                marginLeft: '10px',
                                padding: '0px'
                            }} >
                            <CustomSvgIconForGoArrow sx={{ height: '35px', width: '35px' }} />
                        </IconButton>

                    </Box>
                    {/* <p style={{ backgroundColor: "#DB8C471F", padding: '12px', borderRadius: '6px' }}><strong>Note: </strong>you can add upto 3 crop cycles. Remaining cycles <strong>{3 - agriCropParameterData.length} </strong></p> */}
                </Stack >
            </section>
        )
    }




    const transformData = (data) => {
        // console.log("daata", data)
        // Define the pattern length based on the number of interBedPattern items
        // Create the output structure
        const output = {
            cycles: []
        };
        // Iterate over the cycles in the input data
        // data.forEach(cycle => {
        for (let i = 0; i < data.length; i++) {
            // Create a new cycle object

            // const patternLength = data[i].interBedPattern.length;
            const cycle = data[i]
            const newCycle = {
                startDate: data[i].cycleStartDate,
                cycleName: cycle.cycleName,
                beds: [],
                pattern: data[i].interBedPattern
            };

            // Set the startDate
            // newCycle.startDate = new Date(cycle.cycleBedDetails[0].startDate).toLocaleDateString('en-GB').replace(/\//g, '/');

            // Copy the bed details
            newCycle.beds = cycle.cycleBedDetails.map(bed => ({
                ...bed,
                cropId1: bed.cropId1,
                o1: bed.o1,
                s1: bed.s1,
                o2: bed.o2,
                optionalCropType: bed.optionalCropType,
                optionalO1: bed.optionalO1,
                optionalS1: bed.optionalS1,
                optionalO2: bed.optionalO2,

            }));

            // Add the new cycle to the output
            output.cycles.push(newCycle);
        }

        return output;
    };

    const setTable = () => {
        if (agriCropParameterData.length) {
            const transformedData = transformData(agriCropParameterData);
            setTableForBedDetails(transformedData)
        } else {
            setTableForBedDetails([])
        }

    }

    const defineBedRef = useRef();

    const callChildFunction = async () => {

        if (defineBedRef.current) {

            const isValid = await defineBedRef?.current?.trigger(); // Call the child function
            return !isValid
        }
        return false;
    };
    return (
        <Container>
            <Grid container spacing={4}>
                <Grid item md={12} className="leftSection">
                    <Card sx={
                        {
                            borderRadius: 5,
                            marginBottom: '100px'
                        }
                    }>

                        <div className="title">Agri Crop Parameters</div>

                        <div className="formContent">

                            <form onSubmit={handleSubmit(onSubmit)} noValidate>
                                {createCycleBlock()}
                            </form>
                            {agriCropParameterData.map((obj, index) => {
                                return <DefineBed key={obj.cycleName}
                                    obj={obj}
                                    isVarient={isVarient}
                                    setCount={setCount}
                                    objCycle={obj.cycleName}
                                    objIndex={index}
                                    agriCropParameterData={agriCropParameterData}
                                    setAgriCropParameterData={setAgriCropParameterData}
                                    setDeletedCycle={setDeletedCycle}
                                    updateIntialState={updateIntialState}
                                    accordianOpen={(agriCropParameterData.length - 1) === index ? true : false}
                                    checkValidation={setShowErrorMessage}

                                    ref={defineBedRef}
                                />
                            })}



                            {showErrorMessage !== "" && <Error>{showErrorMessage}</Error>}

                            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>


                                <Button
                                    variant="contained"
                                    className="btn"
                                    data-testid="submitButton"
                                    onClick={() => setActiveStep(active => active - 1)}

                                    sx={{
                                        fontFamily: 'Open Sans',
                                        fontSize: '14px',
                                        fontWeight: '700',
                                        textTransform: 'capitalize',
                                        background: 'transparent',
                                        color: 'grey',
                                        "&:hover": {
                                            backgroundColor: 'transparent',
                                        },

                                        alignSelf: "flex-end",
                                        width: "140px",

                                    }}
                                >Previous
                                </Button>

                                <Button
                                    onClick={saveProjectAgriCropParameters}
                                    variant="contained"
                                    className="btn"
                                    data-testid="submitButton"

                                    sx={{
                                        fontFamily: 'Open Sans',
                                        whiteSpace: 'nowrap',
                                        fontSize: '14px',
                                        fontWeight: '700',

                                        marginLeft: '20px',
                                        textTransform: 'capitalize',
                                        background: theme.palette.secondary.main,
                                        "&:hover": {
                                            backgroundColor: theme.palette.secondary.main,
                                        },

                                        alignSelf: "flex-end",
                                        width: "140px",

                                    }}
                                >Save & Next
                                </Button>
                            </div>
                            <Gap />
                            <div style={{ borderBottom: '1px dashed #E0E0E0', height: '1px' }}></div>
                            <Gap />
                            {tableForBedDetails.length !== 0 && <TabeForBedDetails data={tableForBedDetails} />}
                        </div>
                    </Card>
                </Grid>

            </Grid>
        </Container >
    );
};

export default AgriCropParameter;

const Wrapper = styled.div`
  overflow: hidden;
  .stepperContainer {
    background-color: ${({ theme }) => theme.palette.background.secondary};
    padding: 16px 0px;
  }
`;

const Container = styled.div`
  margin: 20px;  
  height: 85vh;
  overflow: auto;
  &::-webkit-scrollbar {
    height: 4px;
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    box-shadow: #D5D5D5;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.palette.primary.main};
    border-radius: 8px;
  }
  font-weight: 500;

  /* .leftSection {
    background: ${({ theme }) => theme.palette.background.secondary};
  } */

  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
    padding: 20px 0px 0px 20px;
  }
  .formContent {
    
    padding: 40px 20px 20px 20px;
  }
  .label {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
    color: #474f50;
    margin-bottom: 6px;
  }
  .emptyLabel {
    height: 26px;
  }
`;
const Gap = styled.div`
    padding: 10px;
`
