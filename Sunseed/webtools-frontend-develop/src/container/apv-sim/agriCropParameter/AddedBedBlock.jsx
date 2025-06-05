import { IconButton, Stack } from '@mui/material'
import React, { useEffect, useState } from 'react'
import { CustomSvgIconForDelete, CustomSvgIconForEdit } from '../../dashboard/CustomSvgIcon'
import { original } from 'immer'
import { useSelector } from 'react-redux';

const countCrops = (cycleBedDetails) => {
    const cropCount = {};


    cycleBedDetails.forEach(bed => {
        if (bed.cropId1) {
            cropCount[bed.cropId1] = (cropCount[bed.cropId1] || 0) + 1;
        }

        if (bed.optionalCropType) {
            cropCount[bed.optionalCropType] = (cropCount[bed.optionalCropType] || 0) + 1;
        }
    });

    return cropCount;
};
export default function AddedBedBlock({ addedBed, obj: object, setAgriCropParameterData, setDeletedBed, deletedBed, showDeleteIcon, showEditIcon = false, editCycleBlock, isVariant = false }) {

    const crops = useSelector((state) => state.preProcessor.agriCropParameterReducer.crops);
    const [cropCount, setCropCount] = useState(countCrops(addedBed));

    useEffect(() => {
        setCropCount(countCrops(addedBed));
    }, [addedBed])
    function getCropNameById(id) {
        const crop = crops.find(crop => crop.id === id);
        return crop ? crop.name : null;
    }
    const deleteCreatedCycle = (obj, index) => {

        setAgriCropParameterData((draft) => {
            const cycle = draft.find(cycle => cycle.cycleName === object.cycleName);
            // console.log("cycle", original(cycle).cycleBedDetails)
            if (index >= 0 && index < cycle.cycleBedDetails.length) {
                cycle.cycleBedDetails.splice(index, 1);

            }
            if (obj.hasOwnProperty("id")) {
                setDeletedBed((deleteBed) => {
                    const delBed = [...deleteBed, obj.id];
                    cycle['deletedBedDetailsId'] = delBed;

                    return delBed
                });


            }

            cycle['interBedPattern'] = [];

        });

    }



    // const editCycleBlock = (obj, index) => {

    //     // setAgriCropParameterData((draft) => {
    //     //     const cycle = draft.find(cycle => cycle.cycleName === object.cycleName);
    //     //     // console.log("cycle", original(cycle).cycleBedDetails)
    //     //     if (index >= 0 && index < cycle.cycleBedDetails.length) {
    //     //         cycle.cycleBedDetails.splice(index, 1);

    //     //     }
    //     //     if (obj.hasOwnProperty("id")) {
    //     //         setDeletedBed((deleteBed) => {
    //     //             const delBed = [...deleteBed, obj.id];
    //     //             cycle['deletedBedDetailsId'] = delBed;

    //     //             return delBed
    //     //         });


    //     //     }

    //     //     cycle['interBedPattern'] = [];

    //     // });

    // }

    return (
        <section style={{ marginBottom: '20px', }}>
            <Stack style={{ border: '1px solid #E0E0E0', borderRadius: '8px', padding: '10px 20px', gap: 5 }}>
                <div style={{ display: 'flex' }}>
                    <p style={{ fontWeight: '600', font: 'Montserrat', color: '#474F50' }} > Added Beds ( max3 )</p>
                    <p style={{ fontWeight: '600', color: '#474F50', font: 'Montserrat' }} > [crop type | o1 | o2 | s1 | stretch field]</p>
                </div>
                {

                    addedBed?.map((obj, index) => {
                        // console.log("obj", obj.bedName)
                        const isVisibleCount = cropCount[addedBed[index].cropId1];

                        // console.log("addedmap", (isVariant && (isVisibleCount > 1)))
                        return <div key={index} style={{ fontWeight: '500', display: 'flex', padding: '10px 20px', justifyContent: 'space-between', background: '#DFE4E53D', boxSizing: 'border-box', borderRadius: '4px' }}>
                            <div style={{ display: 'flex', flexGrow: '1' }}>
                                <p style={{ fontWeight: '600px', margin: '0px' }} >Bed {index + 1}</p>

                                <p style={{ fontWeight: '600px', color: '#474F5080', margin: '0px', marginLeft: '20px' }} > {`[ ${getCropNameById(obj.cropId1)} | ${obj.o1} | ${obj.o2} | ${obj.s1} | ${obj.stretch} ]`}{obj.optionalCropType !== null && obj.optionalCropType !== '' && `, [ ${getCropNameById(obj.optionalCropType)} | ${obj.optionalO1} | ${obj.optionalO2} | ${obj.optionalS1} | ${obj.optionalStretch}  ]`}</p>
                            </div>
                            {(obj.id && showEditIcon) && <IconButton
                                onClick={(e) => {
                                    e.stopPropagation()
                                    editCycleBlock(obj, index)
                                }} sx={{
                                    padding: '0px',
                                    marginRight: '10px',
                                    ':focus': {
                                        outline: 0,
                                    },

                                }}>
                                <CustomSvgIconForEdit sx={{ color: '#E0E0E0' }} />
                            </IconButton>}

                            {!isVariant && showDeleteIcon && <IconButton
                                onClick={(e) => {
                                    e.stopPropagation()
                                    deleteCreatedCycle(obj, index)
                                }} sx={{
                                    padding: '0px',
                                    ':focus': {
                                        outline: 0,
                                    },

                                }}>
                                <CustomSvgIconForDelete sx={{ color: '#E0E0E0' }} />
                            </IconButton>}

                            {(isVariant && (isVisibleCount > 1)) && <IconButton
                                onClick={(e) => {
                                    e.stopPropagation()
                                    deleteCreatedCycle(obj, index)
                                }} sx={{
                                    padding: '0px',
                                    ':focus': {
                                        outline: 0,
                                    },

                                }}>
                                <CustomSvgIconForDelete sx={{ color: '#E0E0E0' }} />
                            </IconButton>}




                        </div>
                    })
                }

            </Stack >
        </section >
    )
}
