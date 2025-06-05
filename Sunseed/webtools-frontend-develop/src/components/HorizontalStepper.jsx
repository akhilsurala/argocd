import React from 'react';
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import PropTypes from "prop-types";
import { memo } from 'react';
import { useTheme, styled } from 'styled-components';

// import { styled } from '@mui/material/styles';
import StepConnector, { stepConnectorClasses } from '@mui/material/StepConnector';
import { StepLabel } from '@mui/material';
import CustomStepIcon from './CustomStepIcon';


const QontoConnector = styled(StepConnector)(({ theme }) => ({

    [`&.${stepConnectorClasses.alternativeLabel}`]: {
        top: 6,
        left: 'calc(-50%)',
        right: 'calc(50% )',
        fontFamily: 'Montserrat',
        fontSize: '12px',
        fontWeight: '700',
        lineHeight: '10px',
        textAlign: 'center',


    },
    [`&.${stepConnectorClasses.active}`]: {
        [`& .${stepConnectorClasses.line}`]: {
            borderColor: theme.palette.secondary.main,
        },
    },
    [`&.${stepConnectorClasses.completed}`]: {
        [`& .${stepConnectorClasses.line}`]: {
            borderColor: theme.palette.secondary.main,
        },

    },

    [`& .${stepConnectorClasses.line}`]: {
        // borderColor: theme.palette.background.stepper,

        borderColor: '#53988E1F',

        borderTopWidth: '14px',
        borderRadius: '20px',
        borderBottomWidth: '14px',
    },
}));

const HorizontalStepper = ({ steps, activeStep }) => {
    //      Need to pass parameter 
    //    const [activeStep, setActiveStep] = useState(0);
    //    const steps = [
    //     'PV Generation Parameter',
    //     'PV Geometry Parameters',
    //     'Crop Parameters',
    //     "Agri General Parameters",
    //     "Economic Parameters"
    //   ]

    const theme = useTheme();
    const [completed, setCompleted] = React.useState({});

    React.useEffect(() => {
        const obj = {}
        for (let i = 0; i < activeStep; i++) {
            obj[i] = true;
        }
        setCompleted(obj);
    }, [activeStep])






    return (
        <Box sx={{
            width: '100%',

        }}>
            <Stepper nonLinear alternativeLabel activeStep={activeStep} connector={<QontoConnector />} sx={{
                '& .MuiStepIcon-root.Mui-completed': {
                    color: theme.palette.secondary.main
                },
                '& .MuiStepIcon-root.Mui-active': {
                    color: theme.palette.secondary.main
                },
                '& .MuiStepLabel-alternativeLabel': {
                    fontFamily: 'Montserrat',
                    fontSize: '12px',
                    fontWeight: '600',
                    lineHeight: '10px',
                    textAlign: 'center'

                },

                '& .MuiStepIcon-root': {
                    backgroundImage: 'url("data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'24\' height=\'24\' viewBox=\'0 0 24 24\' fill=\'none\'%3E%3Ccircle cx=\'12\' cy=\'12\' r=\'12\' fill=\'none\'/%3E%3Ccircle cx=\'12\' cy=\'12\' r=\'11\' stroke=\'%2353988E\' stroke-opacity=\'0.5\' strokeWidth=\'2\'/%3E%3C/svg%3E")',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center',
                },

                '& .MuiSvgIcon-root': {
                    color: theme.palette.secondary.main
                },



            }}>
                {steps.map((label, index) => (
                    <Step key={label} completed={completed[index]}>
                        <StepLabel StepIconComponent={CustomStepIcon}>

                            {label}
                        </StepLabel>
                    </Step>
                ))}
            </Stepper>

        </Box >
    );
}

export default memo(HorizontalStepper)

HorizontalStepper.proptypes = {
    steps: PropTypes.array.isRequired,
    activeStep: PropTypes.number.isRequired,
}



