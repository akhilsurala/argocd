import React from 'react';
import KeyboardBackspaceIcon from '@mui/icons-material/KeyboardBackspace';

const AdminBackNavigation = ({title,onClick}) => {
    return (
        <div className="navigationWrapper"  onClick={onClick} >
        <KeyboardBackspaceIcon />
        <div className="text">{title}</div>
      </div>
    );
}

export default AdminBackNavigation;
