import React from 'react';
import { styled } from '@mui/system';
import Switch from '@mui/material/Switch';
import { Stack, TextField, Typography } from '@mui/material';
import { Controller } from 'react-hook-form';



const AntSwitch = styled(Switch)(() => ({
    width: 28,
    height: 16,
    padding: 0,
    display: 'flex',

    '&:active': {
        '& .MuiSwitch-thumb': {
            width: 20,

        },
        '& .MuiSwitch-switchBase.Mui-checked': {
            transform: 'translateX(9px)',
        },

    },
    '& .MuiSwitch-switchBase': {
        padding: 2,
        '&.Mui-checked': {
            transform: 'translateX(12px)',
            color: '#fff',

            '& + .MuiSwitch-track': {
                backgroundColor: '#53988E',


                opacity: 1,
                // backgroundColor: theme.palette.mode === 'dark' ? '#177ddc' : '#1890ff',
            },
        },
    },
    '& .MuiSwitch-thumb': {
        boxShadow: '0 2px 4px 0 rgb(0 35 11 / 20%)',
        width: 12,
        height: 12,
        borderRadius: 6,

        backgroundColor: 'white',
        // transition: theme.transitions.create(['width'], {
        //     duration: 200,
        // }),
    },
    '& .MuiSwitch-track': {
        borderRadius: 16 / 2,
        opacity: 1,
        // backgroundColor:
        //     theme.palette.mode === 'dark' ? 'rgba(255,255,255,.35)' : 'rgba(0,0,0,.25)',
        boxSizing: 'border-box',

        backgroundColor: 'grey',
    },
}));

const CustomSwitch = ({ name, control, label, disabled,onChange }) => {
    return (
        <section>
            <Controller
                name={name}
                control={control}
                disabled={disabled}
                render={({ field }) => (
                    <div style={{
                        display: 'flex', justifyContent: 'space-between',//styleName: Body2;

                    }}>
                        <p>{label}</p>
                        <Stack direction="row" spacing={1} alignItems="center">
                            <Typography style={{
                                fontFamily: 'Montserrat',
                                fontSize: '14px',
                                fontWeight: '500',
                            }}>No</Typography>
                            <AntSwitch {...field} 
                            onChange={(e) => {
                                field.onChange(e.target.checked); // Updates react-hook-form state
                                if (onChange) {
                                  onChange(e.target.checked); // Calls the custom onChange function
                                }
                              }}
                             checked={field.value} inputProps={{ 'aria-label': 'ant design' }} />
                            <Typography style={{
                                fontFamily: 'Montserrat',
                                fontSize: '14px',
                                fontWeight: '500',
                            }}>Yes</Typography>
                        </Stack>
                    </div >

                )}
            />

        </section>)
}

export default CustomSwitch;