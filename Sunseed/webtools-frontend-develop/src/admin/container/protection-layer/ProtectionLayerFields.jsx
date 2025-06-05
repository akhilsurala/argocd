import { TextField } from "@mui/material";
import { downloadURL, sampleOpticsFileUrl } from "../../../utils/constant";
import CustomInputField from "../../../container/apv-sim/agriGeneralPage/component/CustomInputField";

export const ProtectionLayerName = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Name</div>
    <CustomInputField
      name="protectionLayerName"
      type="text"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter protection layer name",
        validate: {
          noWhiteSpaces: (value) => value.trim().length > 11 || 'Minimum 12 characters are required',
        },
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 _]+$/i,
          message: "Only accepts alphabets and integer characters",
        },
        minLength: {
          value: 12,
          message: "Minimum 12 characters are required",
        },
        maxLength: {
          value: 60,
          message: "Maximum 60 characters are allowed",
        },
      }}
    />
  </div>
);

export const Texture = ({ control, errors, soilOpticsErrorMessage, isSoilOpticsError, handleSoilOpticsFileUpload, textureImage }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>
      Texture
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
    {textureImage && <a href={downloadURL(textureImage)}
      download rel="noopener noreferrer">
      {textureImage?.split('/').pop()}
    </a>}
  </div>
);

export const DiffuseFraction = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Diffuse Fraction</div>
    <CustomInputField
      name="diffuseFraction"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter diffuse fraction",
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1",
        },
        max: {
          value: 1,
          message: "Only accepts value ranging 0 to 1",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const TransmissionPercentage = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Transmission Percentage</div>
    <CustomInputField
      name="transmissionPercentage"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter transmission Percentage",
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 100",
        },
        max: {
          value: 100,
          message: "Only accepts value ranging 0 to 100",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const VoidPercentage = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Void Percentage</div>
    <CustomInputField
      name="voidPercentage"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter Void Percentage",
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 100",
        },
        max: {
          value: 100,
          message: "Only accepts value ranging 0 to 100",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const PARTransmissivity = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>PAR Transmissivity</div>
    <CustomInputField
      name="parTransmissivity"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter PAR transmissivity",
        validate: {
          validate: checkSumForOpticalProperties
        },
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1",
        },
        max: {
          value: 1,
          message: "Only accepts value ranging 0 to 1",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const PARReflectivity = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>PAR Reflectivity</div>
    <CustomInputField
      name="parReflectivity"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter PAR reflectivity",
        validate: {
          validate: checkSumForOpticalProperties
        },
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1",
        },
        max: {
          value: 1,
          message: "Only accepts value ranging 0 to 1",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const NIRTransmissivity = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>NIR Transmissivity</div>
    <CustomInputField
      name="nirTransmissivity"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter NIR transmissivity",
        validate: {
          validate: checkSumForOpticalProperties
        },
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1",
        },
        max: {
          value: 1,
          message: "Only accepts value ranging 0 to 1",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const NIRReflectivity = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>NIR Reflectivity</div>
    <CustomInputField
      name="nirReflectivity"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter NIR reflectivity",
        validate: {
          validate: checkSumForOpticalProperties
        },
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1",
        },
        max: {
          value: 1,
          message: "Only accepts value ranging 0 to 1",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const Fval1 = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>f_val1</div>
    <CustomInputField
      name="f_val1"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 0 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const Fval2 = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>f_val2</div>
    <CustomInputField
      name="f_val2"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 0 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const Fval3 = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>f_val3</div>
    <CustomInputField
      name="f_val3"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 0 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const Fval4 = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>f_val4</div>
    <CustomInputField
      name="f_val4"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 0 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const TransmissonReflectancePAR = ({ control, errors, soilOpticsErrorMessage, isSoilOpticsError, handleSoilOpticsFileUpload, fileUrl }) => (
  <div>
    {/* Header & Download Link */}
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "4px" }}>
      <div style={{ fontWeight: 500, color: "#474F50" }}>Transmission & Reflectance in PAR Band</div>

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