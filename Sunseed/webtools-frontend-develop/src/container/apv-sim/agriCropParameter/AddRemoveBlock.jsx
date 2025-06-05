import React, { useState } from 'react';
import { Box, Button, IconButton, Paper, Typography, Chip, Tooltip } from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import styled, { useTheme } from 'styled-components';
import CustomModal from '../../../components/CustomModal';
import BedPatternWithChip from '../runManager/BedPatternWithChip';
import { useSelector } from 'react-redux';
import { useIntl } from 'react-intl';

import messages from "./messages";
import { CustomSvgIconForToolTip } from '../../dashboard/CustomSvgIcon';

const AddRemoveBlocks = ({ addedBed, blocks, setAgriCropParameterData, obj }) => {

    const intl = useIntl();
    const bedCC = useSelector((state) => state.preProcessor.agriGeneralParameters.bedParameter.bedcc)
    const theme = useTheme()
    const handleAddBlock = (type) => {
        setAgriCropParameterData((draft) => {
            const cycle = draft.find(cycle => cycle.cycleName === obj.cycleName);
            if (cycle.interBedPattern.length >= bedCC) {
                return;
            }
            cycle.interBedPattern.push(type);

        });
    };

    const handleRemoveBlock = (index) => {
        setAgriCropParameterData((draft) => {
            const cycle = draft.find(cycle => cycle.cycleName === obj.cycleName);
            if (index >= 0 && index < cycle.interBedPattern.length) {
                cycle.interBedPattern.splice(index, 1);
            }

        });

    };

    const getTitleWithToolTip = (label, tootlTipMsg) => {
        return (
            <>
                <div style={{ display: "flex" }}>
                    <div style={{ marginRight: "5px" }}>{label}</div>
                    {tootlTipMsg && (
                        <Tooltip
                            title={<div dangerouslySetInnerHTML={{ __html: tootlTipMsg }} />}
                            placement="right-end"
                            componentsProps={{
                                tooltip: {
                                    sx: {
                                        color: "#53988E",
                                        backgroundColor: "#F2F7F6",
                                        borderRadius: "8px",
                                        border: "1px solid #53988E",
                                    },
                                },
                            }}
                        >
                            <IconButton style={{ outline: '0px' }} sx={{ padding: "0px" }}>
                                <CustomSvgIconForToolTip style={{ color: "#53988E" }} />
                            </IconButton>
                        </Tooltip>
                    )}
                </div>
                <Gap />
            </>
        );
    };
    return (
        <Box   >
            {getTitleWithToolTip(
                "Inter Bed Pattern",
                intl.formatMessage({ ...messages.interBed })
            )}
            <Gap />
            <Paper sx={{ p: 2 }}>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {blocks.map((block, index) => (
                        <Chip
                            key={index}
                            label={block}
                            onDelete={() => handleRemoveBlock(index)}
                            deleteIcon={<CloseIcon style={{

                                color: theme.palette.secondary.main,
                                height: '12px',
                                width: '12px'
                            }} />}
                            variant="outlined"
                            sx={{
                                fontSize: '11px',
                                fontFamily: 'Montserrat',
                                fontWeight: '600',
                                lineHeight: '22px',
                                color: theme.palette.secondary.main,
                                height: '30px',
                                border: `1px solid ${theme.palette.secondary.main}`, padding: '4px',
                                borderRadius: '8px',
                                background: '#53988E1F'
                            }}
                        />
                    ))}
                </Box>
            </Paper>
            <Box sx={{ display: 'flex', mb: 2, mt: 2 }}>

                {addedBed.map((obj, index) => {
                    return (
                        <Button key={index} variant="outlined" onClick={() => handleAddBlock('Bed ' + (index + 1))}
                            sx={{
                                mx: 1,
                                color: '#474F5080',
                                textTransform: 'capitalize',
                                borderColor: '#474F5080',
                                borderRadius: '6px',
                                ':focus': {
                                    outline: 0,
                                },
                                ':hover': {

                                    borderColor: '#474F5080',
                                }
                            }}>
                            Bed {index + 1}</Button>
                    )
                })}


            </Box>
            <p style={{ backgroundColor: "#DB8C471F", padding: '12px', borderRadius: '6px' }}><strong>Note: </strong>Bed pattern can not be more than {bedCC}.(no of beds between a pitch)</p>

        </Box >
    );
};

export default AddRemoveBlocks;
const Gap = styled.div`
    padding: 10px;
`
