import React, { useState } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Checkbox,
    FormControlLabel,
    Typography,
    IconButton,
    Divider,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

const ConfirmationDialog = ({ open, onClose, onConfirm, title, content }) => {

    const [dontShowAgain, setDontShowAgain] = useState(false);

    const handleCancel = () => {
        onClose(false)
    };

    return (
        <div style={{
            borderRadius: "16px",
            fontFamily: 'Montserrat',
            fontWeight: '600',
            fontSize: '16px',
            lineHeight: '100%',
        }}>
            <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth >
                <DialogTitle sx={{
                    display: "flex", justifyContent: "space-between", alignItems: "center",
                    borderRadius: "16px",
                    fontFamily: 'Montserrat',
                    fontWeight: '600',
                    fontSize: '16px',
                    lineHeight: '100%',
                }}>
                    {title}
                    <IconButton onClick={handleCancel} size="small" style={{
                        outline: 0,
                    }} >
                        <CloseIcon />
                    </IconButton >
                </DialogTitle >
                <Divider sx={{ marginLeft: '20px', marginRight: '20px' }} />
                <DialogContent >
                    <Typography sx={{
                        borderRadius: "16px",
                        fontFamily: 'Montserrat',
                        fontWeight: '500',
                        fontSize: '16px',
                        lineHeight: '26px',
                    }}>
                        {content}
                    </Typography>

                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancel} variant="outlined" style={{
                        outline: 0,
                    }}
                        sx={{
                            color: "black",
                            borderRadius: "6px",
                            fontFamily: 'Montserrat',
                            fontWeight: '600',
                            fontSize: '11px',
                            textTransform: 'capitalize',
                            border: '1px solid #25272759',
                            color: '#25272759',
                            ' :hover': {
                                backgroundColor: "#E3E3E3",

                                border: '1px solid #25272759',
                            }
                        }}>Cancel</Button>
                    <Button onClick={onConfirm} variant="contained"
                        style={{
                            outline: 0,
                        }}
                        sx={{
                            backgroundColor: "#F0C50D", color: "black",
                            borderRadius: "6px",
                            fontFamily: 'Montserrat',
                            fontWeight: '600',
                            fontSize: '11px',
                            textTransform: 'capitalize',
                            color: 'white',
                            ' :hover': {
                                backgroundColor: "#F0C50D",
                            }
                        }}>
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog >
        </div >
    );
};

export default ConfirmationDialog;