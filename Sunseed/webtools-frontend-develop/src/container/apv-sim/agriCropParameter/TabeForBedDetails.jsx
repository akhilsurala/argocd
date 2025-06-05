import React, { useState } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Box } from '@mui/material';
import { useTheme } from 'styled-components';
import { CustomSvgIconForDelete } from '../../dashboard/CustomSvgIcon';
import { useSelector } from 'react-redux';
import CustomModal from '../../../components/CustomModal';
import BedPatternWithChip from '../runManager/BedPatternWithChip';



const createBedRows = (beds, getCropNameById) => (
    beds.map((bed, index) => (
        <React.Fragment key={index}>

            Bed {index + 1}
            <div style={{ whiteSpace: 'nowrap' }}>
                {`[ ${getCropNameById(bed.cropId1)} | ${bed.o1} |  ${bed.s1} | ${bed.o2} | ${bed.stretch} ]`}
            </div>
            {bed.optionalCropType !== null && bed.optionalCropType !== "" &&
                <div style={{ whiteSpace: 'nowrap' }}>
                    {`[ ${getCropNameById(bed.optionalCropType)} | ${bed.optionalO1} |  ${bed.optionalS1} | ${bed.optionalO2} | ${bed.optionalStretch} ]`}
                </div>
            }



        </React.Fragment>
    ))
);



const TabeForBedDetails = ({ data }) => {

    const crops = useSelector((state) => state.preProcessor.agriCropParameterReducer.crops);

    function getCropNameById(id) {
        const crop = crops.find(crop => crop.id === id);
        return crop ? crop.name : null;
    }
    const [interBedPatternData, setInterBedPatternData] = useState(['hey', 'bey']);

    const [openBedsModal, setOpenBedsModal] = useState(false);
    const handleInterBedPattern = (value) => {
        if (value.length === 0) return
        setInterBedPatternData(value);
        setOpenBedsModal((prev) => !prev);
    };
    const theme = useTheme();
    return (
        <Box sx={{ width: '100%', overflowX: 'auto' }}>
            <TableContainer component={Paper}

                sx={{

                    "&::-webkit-scrollbar": {
                        height: 8,
                        // backgroundColor: theme.palette.background.secondary,
                    },
                    "&::-webkit-scrollbar-track": {
                        boxShadow: `#D5D5D5`,
                    },
                    "&::-webkit-scrollbar-thumb": {
                        backgroundColor: theme.palette.primary.main,

                        borderRadius: "8px",
                    },
                }}

            ><CustomModal
                    openModal={openBedsModal}
                    title="Crop Cycle"
                    handleClose={handleInterBedPattern}
                    children={<BedPatternWithChip data={interBedPatternData} removeCross={true} />}
                />
                <Table aria-label="responsive table">
                    <TableHead  >
                        <TableRow sx={{ bgcolor: 'grey.100', borderRadius: '10px', }}>
                            <TableCell style={{ padding: '10px', borderBottom: 'none', fontWeight: '600', fontFamily: 'Montserrat', fontSize: '14px' }}>Parameter</TableCell>
                            {data?.cycles?.map((cycle, index) => (
                                <TableCell style={{ padding: '10px', borderBottom: 'none', fontWeight: '600', fontFamily: 'Montserrat', fontSize: '14px' }} key={index}>{`${cycle.cycleName}`}</TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <TableRow >
                            <TableCell style={{ padding: '13px', borderBottom: 'none', whiteSpace: 'nowrap', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px' }}>Start Date</TableCell>
                            {data?.cycles?.map((cycle, index) => (
                                <TableCell style={{ padding: '13px', borderBottom: 'none', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px' }} key={index}>{cycle.startDate}</TableCell>
                            ))}
                        </TableRow>
                        <TableRow sx={{ bgcolor: 'grey.100' }}>
                            <TableCell style={{ padding: '13px', borderBottom: 'none', whiteSpace: 'nowrap', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px', verticalAlign: 'top' }}>Beds</TableCell>
                            {data?.cycles?.map((cycle, index) => (
                                <TableCell style={{ padding: '13px', borderBottom: 'none', whiteSpace: 'nowrap', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px', verticalAlign: 'top' }} key={index} >
                                    {createBedRows(cycle.beds, getCropNameById)}


                                </TableCell>
                            ))}
                        </TableRow>
                        <TableRow>
                            <TableCell style={{ padding: '13px', whiteSpace: 'nowrap', borderBottom: 'none', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px' }}>Inter Bed Pattern</TableCell>
                            {data?.cycles?.map((cycle, index) => (
                                <TableCell style={{ padding: '13px', whiteSpace: 'nowrap', borderBottom: 'none', fontWeight: '400', fontFamily: 'Montserrat', fontSize: '14px' }} key={index}><div style={{ cursor: 'pointer', color: '#53988E' }} onClick={() => handleInterBedPattern(cycle.pattern)}>{`${cycle.pattern.length} Bed pattern`}</div></TableCell>
                            ))}
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </Box >
    );
};

export default TabeForBedDetails;
