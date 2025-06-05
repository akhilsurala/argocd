import React, { useEffect, useState } from 'react';
import styled, { useTheme } from 'styled-components';
import { Box } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import CustomTextField from '../components/CustomTextField';
import CustomButton from '../components/CustomCard/CustomButton';
import OpenStreetMap from './osm/OpenStreetMap';
import { addProject } from '../api/userProfile';
import { useNavigate } from 'react-router-dom';

export default function CreateProjectPage() {
    const theme = useTheme();
    const navigate = useNavigate();

    const [message, setMessage] = useState('');
    const [polygonCoordinate, setPolygonCoordinate] = useState([]);
    const [loader, setLoader] = useState(false);
    const [area, setArea] = useState(0);

    const handleProjectNameValidation = (val) => {
        if (val?.length < 5) return "Minimum 5 characters are allowed";
        if (val?.length > 50) return "Maximum 50 characters are allowed";
        const regex = /^[a-zA-Z]/;
        if (!regex.test(val)) return "First character must be an alphabet"
    }

    const { handleSubmit, control, setValue, trigger, watch, formState: { errors } } = useForm({
        defaultValues: {
            projectName: '',
            lat: '',
            lng: '',
        },
    });

    const onSubmit = (data) => {
        if (polygonCoordinate.length === 0) {
            setMessage('Please draw a valid polygon.');
            return;
        }
        if (message) {
            return;
        }


        const payload = {
            projectName: data.projectName,
            latitude: data.lat,
            longitude: data.lng,
            polygonCoordinates: polygonCoordinate,
            area,
        };

        setLoader(true);
        addProject(payload)
            .then(() => navigate(-1))
            .catch((error) => {
                console.error(error);
                alert(error.response?.data?.errorMessages?.[0] || 'Something went wrong.');
            })
            .finally(() => setLoader(false));
    };

    useEffect(() => {
        if (polygonCoordinate.length === 0) {
            setValue('lat', '');
            setValue('lng', '');
            setMessage('');
        }
    }, [polygonCoordinate, setValue]);

    return (
        <Container>
            <div style={{ width: '100%', height: '100%', boxSizing: 'border-box' }}>
                <div className="title">General Input</div>
                <Box
                    className="formContent"
                    sx={{
                        padding: '20px',
                        height: '100%',
                        overflow: 'auto',
                        boxSizing: 'border-box',
                        "&::-webkit-scrollbar": {
                            width: 5,
                        },
                        "&::-webkit-scrollbar-thumb": {
                            backgroundColor: theme.palette.primary.main,
                            borderRadius: '8px',
                        },
                    }}
                >
                    <form onSubmit={handleSubmit(onSubmit)} noValidate>
                        <div className="subWrapper">
                            {/* Project Name Field */}
                            <Controller
                                name="projectName"
                                control={control}
                                rules={{
                                    validate: { handleProjectNameValidation },
                                    required: 'Project Name is required.',
                                    minLength: {
                                        value: 5,
                                        message: 'Minimum 5 characters are required.',
                                    },
                                    maxLength: {
                                        value: 50,
                                        message: 'Maximum 50 characters are allowed.',
                                    },
                                    pattern: {
                                        value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/,
                                        message: 'Project Name must contain letters, can include spaces, and cannot include special characters or only numbers.',
                                    },
                                }}
                                render={({ field }) => (
                                    <CustomTextField
                                        value={field.value}
                                        key="Project Name"
                                        name="projectName"
                                        label="Project Name"
                                        errors={errors}
                                        placeHolder="Project Name"
                                        textFieldType="textField"
                                        onChange={(e) => {
                                            field.onChange(e);
                                            trigger('projectName'); // Trigger validation on every keystroke
                                        }}
                                    />
                                )}
                            />
                            <div style={{ display: 'flex', gap: '10px' }}>
                                {/* Latitude (N) Field */}
                                <Controller
                                    name="lat"
                                    control={control}
                                    render={({ field }) => (
                                        <CustomTextField
                                            value={field.value}
                                            key="lat"
                                            name="lat"
                                            label="Latitude"
                                            errors={errors}
                                            placeHolder="Latitude"
                                            inputProps={{ readOnly: true }} // Make it read-only
                                        />
                                    )}
                                />
                                {/* Longitude (E) Field */}
                                <Controller
                                    name="lng"
                                    control={control}
                                    render={({ field }) => (
                                        <CustomTextField
                                            value={field.value}
                                            key="lng"
                                            name="lng"
                                            label="Longitude"
                                            errors={errors}
                                            placeHolder="Longitude"
                                            inputProps={{ readOnly: true }} // Make it read-only
                                        />
                                    )}
                                />
                            </div>
                            {/* Buttons */}
                            <div style={{ display: 'flex', gap: '10px', alignSelf: 'flex-end' }}>
                                <CustomButton
                                    name="cancel"
                                    label="Cancel"
                                    variant="contained"
                                    sx={{
                                        borderRadius: '6px',
                                        backgroundColor: 'transparent',
                                        color: theme.palette.text.main,
                                        ':hover': { backgroundColor: 'transparent' },
                                    }}
                                    onClick={() => navigate(-1)}
                                />
                                <CustomButton
                                    name="submit"
                                    label="Submit"
                                    variant="contained"
                                    sx={{
                                        borderRadius: '6px',
                                        backgroundColor: theme.palette.secondary.main,
                                        ':hover': { backgroundColor: theme.palette.secondary.main },
                                    }}
                                    type="submit"
                                    isLoading={loader}
                                />
                            </div>
                        </div>
                        {/* Map Component */}

                    </form>
                    <div className="map-box">
                        <p style={{ color: 'red' }}>{message}</p>
                        <OpenStreetMap
                            value={watch(['lat', 'lng'])}
                            handleChange={(coords) => {
                                setValue('lat', coords.lat);
                                setValue('lng', coords.lng);
                            }}
                            setMessage={setMessage}
                            polygonCoordinate={polygonCoordinate}
                            setPolygonCoordinate={setPolygonCoordinate}
                            area={area}
                            setArea={setArea}
                        />
                    </div>
                </Box>
            </div>
        </Container>
    );
}

const Container = styled.div`
  background: ${({ theme }) => theme.palette.background.secondary};
  width: 100%;
  height: 100%;
  border-radius: 18px;
  padding: 20px;
  box-sizing: border-box;

  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    color: ${({ theme }) => theme.palette.text.main};
  }

  .subWrapper {
    display: flex;
    gap: 20px;
    justify-content: space-between;
  }

  .map-box {
    height: inherit;
  }
`;
