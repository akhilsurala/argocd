import { Stack, TextField } from "@mui/material";
import { downloadURL, sampleOpticsFileUrl } from "../../../utils/constant";
import CustomInputField from "../../../container/apv-sim/agriGeneralPage/component/CustomInputField";
import CustomSelect from "../../../container/apv-sim/agriGeneralPage/component/CustomSelect";



export const ManufacturerName = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Manufacturer Name</div>
    <CustomInputField
      name="manufacturerName"
      type="text"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter manufacturer name",
        validate: {
          noWhiteSpaces: (value) => value.trim().length > 1 || 'Minimum 2 characters are required',
        },
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 _\.]+$/i,
          message: "Only accepts alphabets and integer characters",
        },
        minLength: {
          value: 2,
          message: "Minimum 2 characters are required",
        },
        maxLength: {
          value: 30,
          message: "Maximum 30 characters are allowed",
        },
      }}
    />
  </div>
);
export const ModuleTypeField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>PV Module</div>
    <CustomInputField
      name="moduleType"
      type="text"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter pv module",
        validate: {
          noWhiteSpaces: (value) => value.trim() !== '' || 'Enter pv module',
        },
        pattern: {

        },
        minLength: {
          value: 5,
          message: "Minimum 5 characters are required",
        },
        maxLength: {
          value: 50,
          message: "Maximum 50 characters are allowed",
        },
      }}
    />
  </div>
);
export const ShortCode = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Short Code</div>
    <CustomInputField
      name="shortCode"
      type="text"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter short code",
        validate: {
          noWhiteSpaces: (value) => value.trim() !== '' || 'Enter short code',
        },
        pattern: {

        },
        minLength: {
          value: 3,
          message: "Minimum 3 characters are required",
        },
        maxLength: {
          value: 5,
          message: "Maximum 5 characters are allowed",
        },
      }}
    />
  </div>
);

export const NumCellXField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Num Cell X</div>
    <CustomInputField
      name="numCellX"
      type="number"
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter num cell x",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 24",
        },
        max: {
          value: 24,
          message: "Only accepts value ranging 1 to 24",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const NumCellYField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Num Cell Y</div>
    <CustomInputField
      name="numCellY"
      type='number'
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter num cell y",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 100",
        },
        max: {
          value: 100,
          message: "Only accepts value ranging 1 to 100",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const LongerSideField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>longer Side</div>
    <CustomInputField
      name="longerSide"
      type={'number'}
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="mm"

      rules={{
        required: "Enter longer side",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 4000",
        },
        max: {
          value: 4000,
          message: "Only accepts value ranging 1 to 4000",
        },

        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const ShorterSideField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Shorter Side</div>
    <CustomInputField
      name="shorterSide"
      type="number"
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="mm"
      rules={{
        required: "Enter shorter side",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 2000",
        },
        max: {
          value: 2000,
          message: "Only accepts value ranging 1 to 2000",
        },

        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const ThicknessField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Thickness</div>
    <CustomInputField
      name="thickness"
      type="number"
      noFlotingValue={true}
      control={control}
      errors={errors}
      endLabel="mm"
      disabled={false}
      rules={{
        required: "Enter thickness",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 50",
        },
        max: {
          value: 50,
          message: "Only accepts value ranging 1 to 50",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const VoidRatioField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Void Ratio</div>
    <CustomInputField
      name="voidRatio"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="percentage"
      rules={{
        required: "Enter void ratio",
        pattern: {

        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 99",
        },
        max: {
          value: 99,
          message: "Only accepts value ranging 1 to 99",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const Idc0Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>IDC 0</div>
    <CustomInputField
      name="idc0"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter idc 0",
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const Pdc0Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>PDC0</div>
    <CustomInputField
      name="pdc0"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      endLabel="watts"
      disabled={false}
      rules={{
        required: "Enter pdc0",
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 2000",
        },
        max: {
          value: 2000,
          message: "Only accepts value ranging 0 to 2000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const AlphaScField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Alpha Sc</div>
    <CustomInputField
      name="alphaSc"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="%/°C"
      rules={{
        required: "Enter alpha sc",
        min: {
          value: 0.001,
          message: "Only accepts value ranging 0.001 to 0.999",
        },
        max: {
          value: 0.999,
          message: "Only accepts value ranging 0.001 to 0.999",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const BetaVocField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Beta Voc</div>
    <CustomInputField
      name="betaVoc"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="%/°C"
      rules={{
        required: "Enter beta voc",
        min: {
          value: -1,
          message: "Only accepts value ranging -1 to 0",
        },
        max: {
          value: 0,
          message: "Only accepts value ranging -1 to 0",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const GammaPdcField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Gamma Pdc</div>
    <CustomInputField
      name="gammaPdc"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="%/°C"
      rules={{
        required: "Enter gamma pdc",
        pattern: {

        },
        min: {
          value: -1,
          message: "Only accepts value ranging -1 to 0",
        },
        max: {
          value: 0,
          message: "Only accepts value ranging -1 to 0",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const TemRefField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Temp Ref</div>
    <CustomInputField
      name="temRef"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="C"
      rules={{
        required: "Enter temp ref",
        pattern: {

        },
        min: {
          value: 5,
          message: "Only accepts value ranging 5 to 35",
        },
        max: {
          value: 35,
          message: "Only accepts value ranging 5 to 35",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const RadSunField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Rad Sun</div>
    <CustomInputField
      name="radSun"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter rad sun",
        pattern: {

        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 10",
        },
        max: {
          value: 10,
          message: "Only accepts value ranging 0 to 10",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const F1Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>F1</div>
    <CustomInputField
      name="f1"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {

        },
        min: {
          value: -1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const F2Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>F2</div>
    <CustomInputField
      name="f2"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {

        },
        min: {
          value: -1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const F3Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>F3</div>
    <CustomInputField
      name="f3"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {

        },
        min: {
          value: -1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const F4Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>F4</div>
    <CustomInputField
      name="f4"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {

        },
        min: {
          value: -1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const F5Field = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>F5</div>
    <CustomInputField
      name="f5"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {

        },
        min: {
          value: -1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging -1000 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

// export const HideField = ({ control, errors }) => (
//   <div>
//     <div style={{ marginRight: '5px', fontWeight:500}} >Hide</div>
//   <CustomInputField
//     name="hide"
//     type="checkbox"
//     noFlotingValue={false}
//     control={control}
//     errors={errors}
//     disabled={false}
//     rules={{
//       required: "Enter hide",
//       pattern: {
//         value : /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/,
//         message : "Invalid format"},
//       maxLength: {
//         value: 100,
//         message: "Maximum 100 characters are allowed"
//       }
//     }}
//     />
// </div>
// );

export const XCellField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>X Cell</div>
    <CustomInputField
      name="xcell"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      endLabel="mm"
      disabled={false}
      rules={{
        required: "Enter xcell",
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const YCellField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Y Cell</div>
    <CustomInputField
      name="ycell"
      type="number"
      noFlotingValue={false}
      control={control}
      endLabel="mm"
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter ycell",
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const XCellGapField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>X Cell Gap</div>
    <CustomInputField
      name="xcellGap"
      type="number"
      noFlotingValue={false}
      endLabel="mm"
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter xcell gap",
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const YCellGapField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Y Cell Gap</div>
    <CustomInputField
      name="ycellGap"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      endLabel="mm"
      disabled={false}
      rules={{
        required: "Enter ycell gap",
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const VMapField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>V mp</div>
    <CustomInputField
      name="vmap"
      type="number"
      noFlotingValue={false}
      control={control}
      endLabel="volts"
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter v mp",
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 200",
        },
        max: {
          value: 200,
          message: "Only accepts value ranging 0 to 200",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const IMapField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>I mp</div>
    <CustomInputField
      name="imap"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="amps"
      rules={{
        required: "Enter i mp",
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

export const NEffectiveField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>N Effective</div>
    <CustomInputField
      name="neffective"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="percentage"
      rules={{
        required: "",
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 100",
        },
        max: {
          value: 100,
          message: "Only accepts value ranging 0 to 100",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const IscField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", marginBottom: "4px", fontWeight: 500, color: '#474F50' }}>ISC</div>
    <CustomInputField
      name="isc"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      endLabel="amps"
      disabled={false}
      rules={{
        required: "Enter ISC",
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 100",
        },
        max: {
          value: 100,
          message: "Only accepts value ranging 0 to 100",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const VocField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>VOC</div>
    <CustomInputField
      name="voc"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="volts"
      rules={{
        required: "Enter VOC",
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 200",
        },
        max: {
          value: 200,
          message: "Only accepts value ranging 0 to 200",
        },
        pattern: {

        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const ModuleTech = ({ control, errors, dataSet }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Module Tech</div>
    <CustomSelect
      name="moduleTech"
      control={control}
      errors={errors}
      data={dataSet}
      disabledAllField={false}
    />
  </div>
);

export const OpticalPropertiesFields = ({
  handleFrontFileUpload,
  handleBackFileUpload,
  control,
  errors,
  frontReflectanceError,
  frontReflectanceErrorMessage,
  backReflectanceError,
  backReflectanceErrorMessage,
  frontFileUrl,
  backFileUrl
}) => {
  return (
    <div style={{ borderTop: '1px dashed', borderBottom: '1px dashed', padding: '10px 0px' }} >
      <div style={{ marginRight: "5px", marginBottom: "4px", marginBottom: '6px', fontWeight: 700, color: '#474F50' }}>
        Optical Properties
      </div>
      <Stack spacing={2}   >
        <div>
          {/* Header & Download Link */}
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "4px" }}>
            <div style={{ fontWeight: 500, color: "#474F50" }}>PAR Front Reflectance and Transmission</div>

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
            onChange={handleFrontFileUpload}
            error={frontReflectanceError ? "Enter Field" : false}
            helperText={frontReflectanceErrorMessage}

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
          {/* {console.log("front", frontFileUrl)} */}
          {frontFileUrl && <a href={downloadURL(frontFileUrl)}
            download rel="noopener noreferrer">
            {frontFileUrl?.split('/')?.pop()}
          </a>}
        </div>
        <div>
          <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>
            NIR Front Reflectance
          </div>
          <CustomInputField
            name="frontReflectance"
            type="number"
            noFlotingValue={false}
            control={control}
            errors={errors}
            disabled={false}
            rules={{
              required: "",
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
        <div>
          {/* Header & Download Link */}
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "4px" }}>
            <div style={{ fontWeight: 500, color: "#474F50" }}>PAR Back Reflectance and Transmission</div>

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
            onChange={handleBackFileUpload}
            error={backReflectanceError ? "Enter Field" : false}
            helperText={backReflectanceErrorMessage}
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
          {backFileUrl && <a href={downloadURL(backFileUrl)}
            download rel="noopener noreferrer">
            {backFileUrl?.split('/')?.pop()}
          </a>}
        </div>
        <div>
          <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>
            NIR Back Reflectance
          </div>
          <CustomInputField
            name="backReflectance"
            type="number"
            noFlotingValue={false}
            control={control}
            errors={errors}
            disabled={false}
            rules={{
              required: "",
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
      </Stack>
    </div>
  );
};
