import React from 'react';
import { useTheme } from 'styled-components';

const CustomStepIcon = (props) => {
    const { active, completed, icon } = props;
    const theme = useTheme();

    const iconStyle = {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: '22px',
        border: `2px solid `,
        height: '22px',
        borderRadius: '100%',
        fontFamily: 'Montserrat',

        color: active ? theme.palette.background.secondary : theme.palette.secondary.main,
        backgroundColor: active ? theme.palette.secondary.main : theme.palette.background.secondary,

        zIndex: 1,
        fontWeight: 'bold',

    };

    return <div style={iconStyle}>{icon}</div>;
};

export default CustomStepIcon;
