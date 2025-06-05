import { TextField } from "@mui/material";
import { downloadURL, sampleOpticsFileUrl } from "../../../utils/constant";
import CustomInputField from "../../../container/apv-sim/agriGeneralPage/component/CustomInputField";
import CustomSwitch from "../../../container/apv-sim/agriGeneralPage/component/CustomSwitch";

import dayjs from "dayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import CustomDatePicker from "../../components/CustomDatePicker";

export const CropName = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Crop Name</div>
    <CustomInputField
      name="cropName"
      type="text"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter crop name",
        validate: {
          noWhiteSpaces: (value) => value.trim().length > 4 || 'Minimum 5 characters are required',
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

export const CropLabel = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Crop Label</div>
    <CustomInputField
      name="cropLabel"
      type="text"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter crop label",
        validate: {
          noWhiteSpaces: (value) => value.trim().length > 4 || 'Minimum 5 characters are required',
        },
        pattern: {
          value: /^(?=.*[a-zA-Z])[a-zA-Z0-9 _]+$/i,
          message: "Only accepts alphabets and integer characters",
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

export const VcMax = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>VCMax25</div>
    <CustomInputField
      name="vcMax"
      type="number"
      control={control}
      errors={errors}
      disabled={false}
      endLabel="umol/m2-s"
      rules={{
        required: "Enter VCmax",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 1 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const JMax = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>JMax25</div>
    <CustomInputField
      name="jMax"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      endLabel="umol/m2-s"
      rules={{
        required: "Enter jmax",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 1 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const CJMax = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>CJMax</div>
    <CustomInputField
      name="cjMax"
      type="number"
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 1 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const HaJMax = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>HaJMax</div>
    <CustomInputField
      name="hajMax"
      type="number"
      control={control}
      errors={errors}
      disabled={false}
      endLabel="mol/j"
      rules={{
        required: "Enter hajmax",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000",
        },
        max: {
          value: 1000,
          message: "Only accepts value ranging 1 to 1000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const Alpha = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>α</div>
    <CustomInputField
      name="alpha"
      type="number"
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter alpha",
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
export const Rd = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Rd25</div>
    <CustomInputField
      name="rd"
      type="number"
      control={control}
      errors={errors}
      endLabel="umol/m2-s"
      disabled={false}
      rules={{
        required: "Enter rd25",
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
export const Em = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Em</div>
    <CustomInputField
      name="em"
      type="number"
      control={control}
      errors={errors}
      endLabel="mmol/m2-s"
      disabled={false}
      rules={{
        required: "Enter Em",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000000",
        },
        max: {
          value: 1000000,
          message: "Only accepts value ranging 1 to 1000000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const I0 = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>i0</div>
    <CustomInputField
      name="i0"
      type="number"

      control={control}
      errors={errors}
      endLabel="mol/m2-s"
      disabled={false}
      rules={{
        required: "Enter I0",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000000",
        },
        max: {
          value: 1000000,
          message: "Only accepts value ranging 1 to 1000000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const KField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>K</div>
    <CustomInputField
      name="k"
      type="number"

      control={control}
      errors={errors}
      endLabel="mol/m-2s-1 mmol mol-1"
      disabled={false}
      rules={{
        required: "Enter K",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000000000",
        },
        max: {
          value: 1000000000,
          message: "Only accepts value ranging 1 to 1000000000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const BField = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>B</div>
    <CustomInputField
      name="b"
      type="number"

      control={control}
      errors={errors}
      endLabel="mmol/mol"
      disabled={false}
      rules={{
        required: "Enter B",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 1000000",
        },
        max: {
          value: 1000000,
          message: "Only accepts value ranging 1 to 1000000",
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

export const TransmissionNIR = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Transmission NIR</div>
    <CustomInputField
      name="transmissionNIR"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter transmissionNIR",
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
export const ReflectanceNIR = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Reflectance NIR</div>
    <CustomInputField
      name="reflectanceNIR"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter reflectanceNIR",
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

export const TransmissionPAR = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Transmission PAR</div>
    <CustomInputField
      name="transmissionPAR"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter transmissionPAR",
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

export const ReflectancePAR = ({ control, errors, checkSumForOpticalProperties }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Reflectance PAR</div>
    <CustomInputField
      name="reflectancePAR"
      type="number"
      noFlotingValue={false}
      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter reflectancePAR",
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

export const RequiredDLI = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Required DLI</div>
    <CustomInputField
      name="requiredDLI"
      type="number"

      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="mol/m2/perday"
      rules={{
        required: "Enter requiredDLI",
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
export const RequiredPPFD = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Required PPFD</div>
    <CustomInputField
      name="requiredPPFD"
      type="number"

      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="umol/m2/s"
      rules={{
        required: "Enter requiredPPFD",
        pattern: {
        },
        min: {
          value: 10,
          message: "Only accepts value ranging 10 to 1500",
        },
        max: {
          value: 1500,
          message: "Only accepts value ranging 10 to 1500",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const HarvestLifespan = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Harvest Lifespan</div>
    <CustomInputField
      name="harvestLifespan"
      type="number"

      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="days"
      rules={{
        required: "Enter harvest lifespan",
        pattern: {
        },
        min: {
          value: 10,
          message: "Only accepts value ranging 10 to 365",
        },
        max: {
          value: 365,
          message: "Only accepts value ranging 10 to 365",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const Fval1 = ({ control, errors }) => (
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
export const Fval5 = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Min S1(Spacing b/w crops)</div>
    <CustomInputField
      name="f_val5"
      type="number"

      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel="mm"
      rules={{
        required: "",
        pattern: {
        },
        min: {
          value: 500,
          message: "Only accepts value ranging 500 to 10000",
        },
        max: {
          value: 10000,
          message: "Only accepts value ranging 500 to 10000",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);
export const MinStage = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Min Stage</div>
    <CustomInputField
      name="minStage"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter min stage",
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
export const MaxStage = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Max Stage</div>
    <CustomInputField
      name="maxStage"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter max stage",
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
export const Duration = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Duration</div>
    <CustomInputField
      name="duration"
      type="number"

      control={control}
      errors={errors}
      disabled={false}
      rules={{
        required: "Enter duration",
        pattern: {
        },
        min: {
          value: 1,
          message: "Only accepts value ranging 1 to 365",
        },
        max: {
          value: 365,
          message: "Only accepts value ranging 1 to 365",
        },
        maxLength: {
          value: 100,
          message: "Maximum 100 characters are allowed",
        },
      }}
    />
  </div>
);

export const HasPlantStartDateSwitch = ({ control, errors }) => (
  <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>
    <CustomSwitch
      name="hasPlantActualDate"
      control={control}
      label="Plant has Actual Start Date?"
      disabled={false}
    />
  </div>
);

export const PlantStartDate = ({ control, errors, startDateVal, setStartDateValue, setError, clearErrors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Plant Start Date</div>
      <CustomDatePicker
        control={control}
        errors={errors}
        onChange={setStartDateValue}
        value={startDateVal}
        setError={setError}
        clearErrors={clearErrors}
        name="plantActualStartDate"
        label="Plant Start Date"
        pastYearRange={0}
        futureYearRange={0}
        firstSelectedDate={dayjs("2025-01-01")}
        previouslySelectedDate={null} // New prop for previously selected date
        page="definedBed"
        daysCount={365}
      />
  </div>
);

export const PlantMaxAge = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Max Plant Age</div>
    <CustomInputField
      name="plantMaxAge"
      type="number"
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel=""
      rules={{
        required: "Enter Max Age of Plant",
        pattern: {
        },
        min: {
          value: 0,
          message: "Only accepts value ranging 0 to 5000",
        },
        max: {
          value: 5000,
          message: "Only accepts value ranging 0 to 5000",
        },
        maxLength: {
          value: 4,
          message: "Maximum 4 characters are allowed",
        },
      }}
    />
  </div>
);


export const PlantPerBed = ({ control, errors }) => (
  <div>
    <div style={{ marginRight: "5px", marginBottom: "4px", fontWeight: 500, color: "#474F50" }}>Min Plants count in a bed</div>
    <CustomInputField
      name="maxPlantsPerBed"
      type="number"
      noFlotingValue={true}
      control={control}
      errors={errors}
      disabled={false}
      endLabel=""
      rules={{
        required: "Enter max plant count per bed",
        pattern: {
        },
        min: {
          value: 3,
          message: "Only accepts value ranging 3 to 50",
        },
        max: {
          value: 50,
          message: "Only accepts value ranging 3 to 50",
        },
        maxLength: {
          value: 2,
          message: "Maximum 2 characters are allowed",
        },
      }}
    />
  </div>
);