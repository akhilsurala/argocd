import React, { useEffect, useState } from "react";
import CustomDropDown from "../../components/CustomDropDown";
import styled from "styled-components";
import { Box, TextField, InputAdornment, Typography } from "@mui/material";
import CustomSelectForGradient from "../../components/Gradients/CustomSelectForGradient";
import { useSelector } from "react-redux";

const FormSection = ({ quantityAvailable, setSelectedQuantityAvailable, selectedQuantityAvailable, selectedGradient, setSelectedGradient, gradientOptions, minVal, setMinVal, maxVal, setMaxVal }) => {
  const [selectedTime, setselectedTime] = useState("");
  const range = useSelector((state) => state?.postProcessor?.minMax)
  const [errorMin, setErrorMin] = useState("");
  const [errorMax, setErrorMax] = useState("");



  const contourControl = [
    {
      id: 0,
      name: "RED",
    },
    {
      id: 1,
      name: "BLUE",
    },
  ];
  const contourControlError = {
    RED: false,
    BLUE: false,
  };

  const quantityAvailableError = {
    "Bificial gain / day": false,
    // "Albedo / day": false,
    "Temperature control": false,
    // "Day Light Integral": false,
  };

  const handleQuantityAvailable = (val) => {
    setSelectedQuantityAvailable(val);
  };
  const [minRange, maxRange] = range; // Destructure range

  const [debouncedMinVal, setDebouncedMinVal] = useState(minVal);
  const [debouncedMaxVal, setDebouncedMaxVal] = useState(maxVal);
  
  const [currentUnitForSuffix, setCurrentUnitSuffix] = useState("");

  // Handle min input change
  const handleMinChange = (e) => {

    setDebouncedMinVal(e.target.value);

  };

  // Handle max input change
  const handleMaxChange = (e) => {

    setDebouncedMaxVal(e.target.value);

  };

  useEffect(() => {
    const handler = setTimeout(() => {

      if (!debouncedMinVal) {

        setErrorMin("");
        setMinVal("")
        return;
      };
      if (isNaN(debouncedMinVal)) {
        setErrorMin("Input should be number");
        return;
      };

      const minValNumber = parseFloat(debouncedMinVal); // Convert to number
      const maxValNumber = parseFloat(debouncedMaxVal); // Convert to number
      const [minRange, maxRange] = range; // Destructure range

      // Check if debouncedMinVal is a valid number and not empty

      if (minValNumber < minRange || minValNumber > maxRange) {
        setErrorMin(`Value should be between [${minRange}, ${maxRange}]`);
      }
      else if (!isNaN(maxValNumber) && minValNumber > maxValNumber) {
        setErrorMin("Min value should be less than Max value");
      }
      else {
        setErrorMin(""); // Clear error message
        setMinVal(debouncedMinVal); // Set debounced value after delay
      }
    }, 100); // 500ms debounce delay

    return () => {
      clearTimeout(handler); // Clear timeout if value changes before delay ends
    };
  }, [debouncedMinVal, debouncedMaxVal, range]); // Add range to dependency array

  function isNumeric(num) {
    return !isNaN(num)
  }
  useEffect(() => {
    const handler = setTimeout(() => {


      if (!debouncedMinVal) {

        setErrorMax("");
        setMaxVal("")
        return;
      }
      if (isNaN(debouncedMaxVal)) {
        setErrorMax("Input should be number");
        return;
      };
      const maxValNumber = parseFloat(debouncedMaxVal); // Convert to number
      const minValNumber = parseFloat(debouncedMinVal); // Convert to number
      const [minRange, maxRange] = range; // Destructure range
      console.log("input should be", isNaN(maxValNumber), maxValNumber)
      // Check if debouncedMaxVal is a valid number and not empty


      if (maxValNumber < minRange || maxValNumber > maxRange) {
        setErrorMax(`Value should be between [${minRange}, ${maxRange}]`);
      }
      else if (!isNaN(minValNumber) && maxValNumber < minValNumber) {
        setErrorMax(`Max value should be greater than Min value`);
      }
      else {
        setErrorMax("");
        setMaxVal(debouncedMaxVal); // Set debounced value after delay
      }
    }, 100); // 500ms debounce delay

    return () => {
      clearTimeout(handler); // Clear timeout if value changes before delay ends
    };
  }, [debouncedMaxVal, debouncedMinVal, range]); // Add range to dependency array




  useEffect(() => {
    setDebouncedMinVal(minRange);
    setDebouncedMaxVal(maxRange);
    setErrorMax("")
    setErrorMin("")
  }, [selectedQuantityAvailable, range]);
  useEffect(() => {

    console.log("selectedGradient", selectedGradient);
  }, [selectedGradient]);

  useEffect(() => {
    if (selectedQuantityAvailable === 1) {
      setCurrentUnitSuffix("μ moles/ m²");
    } else if (selectedQuantityAvailable === 2) {
      setCurrentUnitSuffix("K");
    } else if (selectedQuantityAvailable === 3) {
      setCurrentUnitSuffix("");
    } else if (selectedQuantityAvailable === 4) {
      setCurrentUnitSuffix("moles / m² / day");
    }
  }, [selectedQuantityAvailable]);

  return (
    <Container>
      <Box className="fieldsSection">
        <div>

          <span className="label">Contour Control </span>
          <CustomSelectForGradient setSelectedGradient={setSelectedGradient} selectedGradient={selectedGradient} disabled={selectedQuantityAvailable === ""} gradientOptions={gradientOptions} />
        </div>
        <CustomDropDown
          label="Quantity Available"
          placeHolder="Select type"
          value={selectedQuantityAvailable}
          onChange={() => { }}
          handleChange={(val) => handleQuantityAvailable(val)}
          disabled={false}
          error={[]}
          dataSet={quantityAvailable}
          errors={quantityAvailableError}
          noneNotRequire={true}
          showToolTip={selectedQuantityAvailable === 2}
          toolTipMessage="Leaf temperatures from the simulations are expected to be reasonably accurate. However, temperatures of other elements such as the ground or modules should not be referenced from these simulations."
        />
      </Box>
      <Box className="textFieldSection">
        <TextField
          id="outlined-basic"
          size="small"
          value={debouncedMinVal ? debouncedMinVal : ""}
          disabled={selectedQuantityAvailable === 3}
          placeholder={'min value'}
          onChange={handleMinChange}
          error={errorMin} // Error state
          helperText={errorMin} // Error message
          InputProps={{
            endAdornment:
              <InputAdornment position="end">
                <Typography sx={{
                  fontFamily: 'Montserrat',
                  fontSize: '14px',
                  fontStyle: 'italic',
                  fontWeight: '500',
                  color: '#53988E80'
                }}>{currentUnitForSuffix}</Typography>
              </InputAdornment>,
          }}
        />
        <TextField
          id="outlined-basic"
          size="small"

          value={debouncedMaxVal ? debouncedMaxVal : ""}
          disabled={selectedQuantityAvailable === 3}
          placeholder={'max value'}
          error={errorMax} // Error state
          helperText={errorMax} // Error message
          onChange={handleMaxChange}
          InputProps={{
            endAdornment:
              <InputAdornment position="end">
                <Typography sx={{
                  fontFamily: 'Montserrat',
                  fontSize: '14px',
                  fontStyle: 'italic',
                  fontWeight: '500',
                  color: '#53988E80'
                }}>{currentUnitForSuffix}</Typography>
              </InputAdornment>,
          }}
        />
      </Box>
    </Container>
  );
};

export default FormSection;

const Container = styled.div`
  background-color: #ffffff;
  padding: 26px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  .fieldsSection {
    display: grid;
    grid-template-columns: auto auto;
    gap: 20px;
  }
  .textFieldSection {
    display: grid;
    grid-template-columns: auto auto auto auto;
    gap: 20px;
  }
  .label {
    font-size: 16px;
    font-weight: 500;
    line-height: 26px;
    letter-spacing: 0em;
    text-align: left;
    color: #474f50;
    margin-bottom: 0px;
  }
`;
