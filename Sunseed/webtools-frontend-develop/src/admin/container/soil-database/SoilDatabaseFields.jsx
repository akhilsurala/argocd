import React from 'react';
import { TextField } from '@mui/material';
import CustomInputField from '../../../container/apv-sim/agriGeneralPage/component/CustomInputField';
import { downloadURL, sampleOpticsFileUrl } from '../../../utils/constant';

export const SoilTypeField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Soil Type</div>
    <CustomInputField
      name="soilType"
      type="text"
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter soil type",
        validate: {
          noWhiteSpaces: (value) => value.trim().length > 3 || 'Minimum 4 characters are required',
        },
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 _]+$/i,
          message: "Only accepts alphabets and integer characters",
        },
        minLength: {
          value: 4,
          message: "Minimum 4 characters are required",
        },
        maxLength: {
          value: 32,
          message: "Maximum 32 characters are allowed",
        },
      }}
    />
  </div>
);
export const SoilOptics = ({ control, errors, soilOpticsErrorMessage, isSoilOpticsError, handleSoilOpticsFileUpload, fileUrl }) => (
  <div>
    {/* Header & Download Link */}
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "4px" }}>
      <div style={{ fontWeight: 500, color: "#474F50" }}>Soil Optics</div>

      {/* Sample File Download Link (Right-Aligned) */}
      <a 
        href={sampleOpticsFileUrl} 
        download 
        rel="noopener noreferrer"
        style={{ fontSize: "13px", color: "#115293", textDecoration: "none", fontWeight: "500" }}
      >
        Download Sample
      </a>
    </div>
    <TextField
      type="file"
      onChange={handleSoilOpticsFileUpload}
      error={isSoilOpticsError ? "Enter Field" : false}
      helperText={soilOpticsErrorMessage}
      sx={{
        borderColor: 'E0E0E0',
        width: "-webkit-fill-available",
        "& input[type='file']": {
          backgroundColor: "white", // Explicitly set background for dark mode
          color: "black", // Explicitly set text color
          border: "1px solid #E0E0E0", // Add border to ensure visibility
          padding: "6px 12px", // Padding for better appearance
          zIndex: 1,
        },
        "& input[type='file']::file-selector-button": {
          backgroundColor: "#DB8C47", // Button background color
          color: "white", // Button text color
          border: "none", // Remove default border
          borderRadius: "4px", // Button corner rounding
          cursor: "pointer", // Pointer cursor for better UX
        },
        "& input[type='file']::file-selector-button:hover": {
          backgroundColor: "#115293", // Button hover color
        },
        "& :focus": {
          outline: 0,
        },
      }}
      variant="outlined"
      autoComplete="off"
      size="small"
    />
    {fileUrl && <a href={downloadURL(fileUrl)}
      download rel="noopener noreferrer">
      {fileUrl?.split('/').pop()}
    </a>}
  </div>
);
