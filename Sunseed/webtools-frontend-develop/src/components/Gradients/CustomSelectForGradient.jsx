import React, { useState } from "react";
import { Select, MenuItem } from "@mui/material";
import styled from "styled-components";
import GradientBox from "./GradientBox"; // Assuming you have GradientBox as a separate component



// Gradient options


export default function CustomSelectForGradient({ gradientStops, setSelectedGradient, selectedGradient, disabled = false, gradientOptions = [] }) {

    const handleChange = (event) => {
        setSelectedGradient(event.target.value);
    };
    // React.useEffect(() => {
    //     if (disabled) setSelectedGradient("")
    // }, [disabled]);

    return (
        <SelectContainer>
            <CustomSelect disabled={disabled} size="small" value={selectedGradient} onChange={handleChange} displayEmpty>
                {gradientOptions.map((option, index) => (
                    <MenuItem key={index} value={option}>
                        <GradientMenuItem>
                            <span style={{ marginRight: "20px" }}>{option.label}</span>
                            {/* Render the gradient preview */}
                            <GradientBox stops={option.stops} height="20px" width="100%" />
                        </GradientMenuItem>
                    </MenuItem>
                ))}
            </CustomSelect>

            {/* Display the selected gradient preview outside the select */}
            {/* <SelectedGradientPreview>
                <GradientBox stops={selectedGradient.stops} height="20px" width="100%" />
            </SelectedGradientPreview> */}
        </SelectContainer>
    );
}

// Styled Components
const SelectContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 400px;
`;

const CustomSelect = styled(Select)`
`;

const GradientMenuItem = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
`;

const SelectedGradientPreview = styled.div`
  margin-top: 10px;
  width: 100%;
`;
