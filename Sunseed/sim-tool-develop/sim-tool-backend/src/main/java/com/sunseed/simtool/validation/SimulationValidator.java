package com.sunseed.simtool.validation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.serviceimpl.EPWServiceImpl.WeatherData;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SimulationValidator {

	private final ObjectMapper objectMapper;
	private final ValidationFormulas simulationFormulas;

	public void validatePayload(Map<String, Object> runPayload, SimulationType simulationType, Boolean withTracking) {
		if (runPayload == null)
			throw new InvalidRequestBodyArgumentException("Run Payload can't be null");

		JsonNode payload = objectMapper.valueToTree(runPayload);

		//Validate in proper sequence
		
		if (!payload.hasNonNull("latitude"))
			throw new InvalidRequestBodyArgumentException("latitude can't be null");
		if (!payload.hasNonNull("longitude"))
			throw new InvalidRequestBodyArgumentException("longitude can't be null");;
		if (!payload.hasNonNull("id"))
			throw new InvalidRequestBodyArgumentException("id can't be null");
		
		validateAzimuth(payload);
		validateLengthOfOneRow(payload);

		if (simulationType.equals(SimulationType.ONLY_PV) || simulationType.equals(SimulationType.APV)) {
			
			if (payload.has("pvParameters") && payload.get("pvParameters").has("modeOfOperationId")) {
		        JsonNode modeOfOperationNode = payload.get("pvParameters").get("modeOfOperationId");
		        String modeOfOperation = modeOfOperationNode.get("modeOfOperation").asText();

		        if ("Single Axis Tracking".equals(modeOfOperation) || withTracking) {
		        	validateMaxAngleOfTracking(payload);
		        	runPayload.put("tracking", true);
		            System.out.println("Mode of Operation is 'Single Axis Tracking'.");
		        } else {
		        	validateTilt(payload);
		        	runPayload.put("tracking", false);
		            System.out.println("Mode of Operation is not 'Single Axis Tracking'.");
		        }
		    } else {
		    	throw new InvalidRequestBodyArgumentException("pvParameters can't be null");
		    }
			
//			if(!withTracking)
//				validateTilt(payload);
//			if(withTracking)
//				validateMaxAngleOfTracking(payload);
			
			validateModuleMaskPattern(payload);
			validateModuleConfig(payload);
			validatePvModule(payload);
			validateGapBetweenModules(payload);
			validateHeight(payload);
		}
		
		validatePitch(payload, simulationType);
		
		if(simulationType.equals(SimulationType.ONLY_AGRI) || simulationType.equals(SimulationType.APV))
		{
			validateBedParameters(payload);
//			validateBedAzimuth(payload);
			validateCropParameters(payload);
			validateCropCycles(payload);
			//validate protection layer
		}
		
		if(simulationType.equals(SimulationType.APV)) {
			validateStartPointOffset(payload);
		}
	}
	
	private void validateAzimuth(JsonNode payload) {
		if (payload.has("preProcessorToggles")) {
			if (payload.get("preProcessorToggles").hasNonNull("azimuth")) {
				Double azimuth = payload.get("preProcessorToggles").get("azimuth").asDouble();
				if (azimuth < 0.0 || azimuth > 360.0)
					throw new InvalidRequestBodyArgumentException("azimuth must be between 0 and 360");
			} else
				throw new InvalidRequestBodyArgumentException("azimuth can't be null");
			
		} else
			throw new InvalidRequestBodyArgumentException("preProcessorToggles can't be null");
	}
	
	private void validateLengthOfOneRow(JsonNode payload) {
		if (payload.has("preProcessorToggles")) {

			if (payload.get("preProcessorToggles").hasNonNull("lengthOfOneRow")) {
				Double lengthOfOneRow = payload.get("preProcessorToggles").get("lengthOfOneRow").asDouble();
				if (lengthOfOneRow < 1.0 || lengthOfOneRow > 500.0) // in metres
					throw new InvalidRequestBodyArgumentException("lengthOfOneRow must be between 1m and 500m");
			} else
				throw new InvalidRequestBodyArgumentException("lengthOfOneRow can't be null");
			
		} else
			throw new InvalidRequestBodyArgumentException("preProcessorToggles can't be null");
	}
	
	private void validatePitch(JsonNode payload, SimulationType simulationType) {
		if (payload.has("preProcessorToggles")) {

			if (payload.get("preProcessorToggles").hasNonNull("pitchOfRows")) {
				Double pitchOfRows = payload.get("preProcessorToggles").get("pitchOfRows").asDouble();

				if (simulationType.equals(SimulationType.ONLY_AGRI)) {
					if (pitchOfRows < 2.0 || pitchOfRows > 176.0)
						throw new InvalidRequestBodyArgumentException("pitchOfRows must be between 2 and 10");
				} else {
					
					/**
					 *  Validate length of one row, module config, length & width of pv module, tilt angle beforehand
					 */
					
					Double gap = payload.get("pvParameters").get("gapBetweenModules").asDouble()/1000.0; //in metres
					
					Double minPitch = simulationFormulas.calculatePitch(payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(0) - '0', 
							payload.get("pvParameters").get("pvModule").get("length").asDouble()/1000.0, payload.get("pvParameters").get("pvModule").get("width").asDouble()/1000.0, 
							payload.get("pvParameters").get("tiltIfFt").asDouble(), 
							payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(1),
							gap, payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().contains("-"));
					
					if(pitchOfRows < minPitch || pitchOfRows > Math.min(10*minPitch, 176.0))
						throw new InvalidRequestBodyArgumentException("pitchOfRows must be between " + minPitch + " and " + (10*minPitch));
					
				}
			} else
				throw new InvalidRequestBodyArgumentException("pitchOfRows can't be null");
		} else
			throw new InvalidRequestBodyArgumentException("preProcessorToggles can't be null");
	}

	private void validateTilt(JsonNode payload) {
		if (payload.has("pvParameters")) {
			if (payload.get("pvParameters").hasNonNull("tiltIfFt")) {
				Double tilt = payload.get("pvParameters").get("tiltIfFt").asDouble();
				if (tilt < -90.0 || tilt > 90.0)
					throw new InvalidRequestBodyArgumentException("tilt angle must be between -90 and 90");
			} else
				throw new InvalidRequestBodyArgumentException("tilt angle can't be null");

		} else
			throw new InvalidRequestBodyArgumentException("pvParameters can't be null");
	}

	private void validateMaxAngleOfTracking(JsonNode payload) {
		if (payload.has("pvParameters")) {

			if (payload.get("pvParameters").hasNonNull("maxAngleOfTracking")) {
				Double maxAngleOfTracking = payload.get("pvParameters").get("maxAngleOfTracking").asDouble();
				if (maxAngleOfTracking < 30.0 || maxAngleOfTracking > 90.0)
					throw new InvalidRequestBodyArgumentException("maxAngleOfTracking must be between 30 and 90");
			} else
				throw new InvalidRequestBodyArgumentException("maxAngleOfTracking can't be null");

		} else
			throw new InvalidRequestBodyArgumentException("pvParameters can't be null");
	}

	private void validateModuleMaskPattern(JsonNode payload) {
		if (payload.has("pvParameters")) {
			if (payload.get("pvParameters").hasNonNull("moduleMaskPattern")) {
				String moduleMaskPattern = payload.get("pvParameters").get("moduleMaskPattern").asText().trim();

				if (moduleMaskPattern.length() > 10)
					throw new InvalidRequestBodyArgumentException("More than 10 bits are not allowed in moduleMaskPattern");

				if (moduleMaskPattern.length() > 0
						&& (!moduleMaskPattern.contains("0") || !moduleMaskPattern.contains("1")))
					throw new InvalidRequestBodyArgumentException("All 0s or 1s are not allowed in moduleMaskPattern");
			} 
//			else
//				throw new InvalidRequestBodyArgumentException("moduleMaskPattern can't be null");

		} else
			throw new InvalidRequestBodyArgumentException("pvParameters can't be null");
	}
	
	private void validateModuleConfig(JsonNode payload) {
		if (payload.has("pvParameters") && payload.get("pvParameters").has("moduleConfigs") && 
				!payload.get("pvParameters").get("moduleConfigs").isEmpty() && 
				payload.get("pvParameters").get("moduleConfigs").get(0).hasNonNull("moduleConfig")) {
			
				String moduleConfig = payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim();

				if(moduleConfig.length() == 2)
				{
					if(moduleConfig.charAt(1) != 'P' && moduleConfig.charAt(1) != 'L')
						throw new InvalidRequestBodyArgumentException("Only P or L configs allowed");
					if(!(Character.isDigit(moduleConfig.charAt(0)) && (moduleConfig.charAt(0) - '0') > 0 && (moduleConfig.charAt(0) - '0') <= 4))
						throw new InvalidRequestBodyArgumentException("Number of modules must be between 1 and 4");
				}
				else if(moduleConfig.length() == 5)
				{
					if(moduleConfig.charAt(1) != 'P' && moduleConfig.charAt(1) != 'L')
						throw new InvalidRequestBodyArgumentException("Only P or L configs allowed");
					if(!(Character.isDigit(moduleConfig.charAt(0)) && (moduleConfig.charAt(0) - '0') > 0 && (moduleConfig.charAt(0) - '0') <= 4))
						throw new InvalidRequestBodyArgumentException("Number of modules must be between 1 and 4");
					if(moduleConfig.charAt(2) != '-')
						throw new InvalidRequestBodyArgumentException("Invalid module config");
					if(!moduleConfig.substring(0, 2).equals(moduleConfig.substring(3, 5)))
						throw new InvalidRequestBodyArgumentException("Invalid module config");
				}
				else
					throw new InvalidRequestBodyArgumentException("id can't be null");

		} else
			throw new InvalidRequestBodyArgumentException("moduleConfig can't be null");
	}

	private void validatePvModule(JsonNode payload) {
		if (payload.has("pvParameters")) {

			if (payload.get("pvParameters").has("pvModule")) {

				if (payload.get("pvParameters").get("pvModule").hasNonNull("length")) {
					Double length = payload.get("pvParameters").get("pvModule").get("length").asDouble()/1000.0; //in mm
					
					if (length <= 0.0 || length > 5.0) // in metres
						throw new InvalidRequestBodyArgumentException("module length must be between 0 and 5");
				} else
					throw new InvalidRequestBodyArgumentException("module length can't be null");

				if (payload.get("pvParameters").get("pvModule").hasNonNull("width")) {
					Double width = payload.get("pvParameters").get("pvModule").get("width").asDouble()/1000.0; // in mm

					if (width <= 0.0 || width > 5.0) // in metres
						throw new InvalidRequestBodyArgumentException("module width must be between 0 and 5");
				} else
					throw new InvalidRequestBodyArgumentException("module width can't be null");

			} else
				throw new InvalidRequestBodyArgumentException("pvModule can't be null");

		} else
			throw new InvalidRequestBodyArgumentException("pvParameters can't be null");
	}
	
	private void validateGapBetweenModules(JsonNode payload) {
		
		/**
		 *  Validate length of one row, module config, length & width of pv module, tilt angle beforehand
		 */
		
		if (payload.has("pvParameters") && payload.get("pvParameters").hasNonNull("gapBetweenModules")) {
			
				Double gapBetweenModules = payload.get("pvParameters").get("gapBetweenModules").asDouble()/1000.0; //in metres
				
				Double gap = simulationFormulas.calculateMaxGapBetweenModules(payload.get("preProcessorToggles").get("lengthOfOneRow").asDouble(),
						176.0, payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(0) - '0', 
						payload.get("pvParameters").get("pvModule").get("length").asDouble()/1000.0, payload.get("pvParameters").get("pvModule").get("width").asDouble()/1000.0, 
						payload.get("pvParameters").get("tiltIfFt").asDouble(), 
						payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().contains("-"), 
						payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(1));
				
				if(gapBetweenModules < 0.0 || gapBetweenModules > gap)
					throw new InvalidRequestBodyArgumentException("gapBetweenModules must be between 0  and " + gap);

		} else
			throw new InvalidRequestBodyArgumentException("gepBetweenModules can't be null");
	}
	
	private void validateHeight(JsonNode payload) {
		if (payload.has("pvParameters") && payload.get("pvParameters").hasNonNull("height")) {
			
			Double height = payload.get("pvParameters").get("height").asDouble();
			
			/**
			 *  Validate length of one row, module config, length & width of pv module, tilt angle beforehand
			 */
			
			Double gap = payload.get("pvParameters").get("gapBetweenModules").asDouble()/1000.0; //in metres
			
			Double minHeight = simulationFormulas.calculateModuleHeight(payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(0) - '0', 
					payload.get("pvParameters").get("pvModule").get("length").asDouble()/1000.0, payload.get("pvParameters").get("pvModule").get("width").asDouble()/1000.0, 
					payload.get("pvParameters").get("tiltIfFt").asDouble(), 
					payload.get("pvParameters").get("moduleConfigs").get(0).get("moduleConfig").asText().trim().charAt(1),
					gap);
			
			if(height < minHeight || height > 10.0)
				throw new InvalidRequestBodyArgumentException("height must be between " + minHeight + " and 10");

		} else
			throw new InvalidRequestBodyArgumentException("height can't be null");
	}
	
	private void validateBedParameters(JsonNode payload) {
		if (payload.has("cropParameters") &&  payload.get("cropParameters").has("bedParameter")
				&& payload.get("cropParameters").get("bedParameter").hasNonNull("bedAngle")) {
			
			Double bedAngle = payload.get("cropParameters").get("bedParameter").get("bedAngle").asDouble();
			
			if(bedAngle < 0 || bedAngle > 75)
				throw new InvalidRequestBodyArgumentException("bedAngle must be between 0 and 75");
			
		} else
			throw new InvalidRequestBodyArgumentException("bedAngle can't be null");
		
		if (payload.has("cropParameters") && payload.get("cropParameters").has("bedParameter")
				&& payload.get("cropParameters").get("bedParameter").hasNonNull("noOfBeds") 
				&& payload.get("cropParameters").get("bedParameter").hasNonNull("bedWidth") 
				&& payload.get("cropParameters").get("bedParameter").hasNonNull("bedHeight")) {
			
			Integer bedCount = payload.get("cropParameters").get("bedParameter").get("noOfBeds").asInt();
			Double bedWidth = payload.get("cropParameters").get("bedParameter").get("bedWidth").asDouble();
			Double pitchOfRows = payload.get("preProcessorToggles").get("pitchOfRows").asDouble();
			Double bedHeight = payload.get("cropParameters").get("bedParameter").get("bedHeight").asDouble();
			Double bedAngle = payload.get("cropParameters").get("bedParameter").get("bedAngle").asDouble();
			
			Double maxWidth = pitchOfRows*1000.0/bedCount; //in mm
			Double maxHeight = bedWidth*Math.tan(Math.toRadians(bedAngle))/2.0;
			
			if(bedWidth < 100 || bedWidth > maxWidth)
				throw new InvalidRequestBodyArgumentException("bedWidth must be between 100mm and " + maxWidth + "mm");
			
			if(bedHeight < 0 || bedHeight > maxHeight)
				throw new InvalidRequestBodyArgumentException("bedHeight must be between 0mm and " + maxHeight + "mm");
			
			Double maxBeds = (pitchOfRows*1000.0)/simulationFormulas.calculateBedBottomWidth(bedAngle, bedWidth, bedHeight);
			
			if(bedCount < 1 || bedCount > maxBeds.intValue())
				throw new InvalidRequestBodyArgumentException("bedCount must be between 1 and " + maxBeds.intValue());
			
		} else
			throw new InvalidRequestBodyArgumentException("Bed count, height and width can't be null");
	}
	
	private void validateCropParameters(JsonNode payload) {
		
		if (payload.has("cropParameters") && payload.get("cropParameters").hasNonNull("isMulching")) {
			
			String isMulching = payload.get("cropParameters").get("isMulching").asText();
			
			if(!isMulching.equals("true") && !isMulching.equals("false"))
				throw new InvalidRequestBodyArgumentException("isMulching can only be true or false");
			
		} else
			throw new InvalidRequestBodyArgumentException("isMulching can't be null");
		
		if (payload.has("cropParameters") && payload.get("cropParameters").hasNonNull("irrigationType")) {
			
			String irrigationType = payload.get("cropParameters").get("irrigationType").asText();
			
			if(irrigationType.isBlank())
				throw new InvalidRequestBodyArgumentException("irrigationType can't be empty");
			
		} else
			throw new InvalidRequestBodyArgumentException("irrigationType can't be null");
	}
	
	private void validateCropCycles(JsonNode payload) {
		
		if (payload.has("cropParameters") && payload.get("cropParameters").has("cycles")) {
			
			JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
			if(cyclesNode.isArray())
			{
				List<Pair<LocalDate, LocalDate>> cycleDates = new ArrayList<>();
				for(JsonNode cycle : cyclesNode)
				{
					Pair<LocalDate, LocalDate> pair = Pair.of(LocalDate.parse(cycle.get("cycleStartDate").asText()),
							LocalDate.parse(cycle.get("cycleStartDate").asText()).plusDays(cycle.get("duration").asLong()));
					cycleDates.add(pair);
					
					int duration = cycle.get("duration").asInt();
					
					
					// for interBedPattern validation
					Map<String,Boolean> cycleInterBedPatternMap = new HashMap<>();
					
					// adding bed names from bedPattern as key and it's found value as false
					if (cycle.hasNonNull("interBedPattern") && cycle.get("interBedPattern").isArray()
							&& cycle.get("interBedPattern").size() != 0) {
						for(JsonNode bedPattern : cycle.get("interBedPattern")) {
							cycleInterBedPatternMap.put(bedPattern.asText(), false);
						}
					}
					
					if(cycle.has("cycleBedDetails") && cycle.get("cycleBedDetails").size() > 0)
					{
						JsonNode cycleBedDetails = cycle.get("cycleBedDetails");
						if(cycleBedDetails.isArray())
						{
							for(JsonNode cropBed : cycleBedDetails)
							{
								
								// setting found bed name in the map's value as true
								if (cropBed.hasNonNull("bedName")) {
									String bedName = cropBed.get("bedName").asText();

									if (cycleInterBedPatternMap.containsKey(bedName)) {
										cycleInterBedPatternMap.put(bedName, true);
									}
								}
								else
								{
									throw new InvalidRequestBodyArgumentException("Bed Name can't be empty");
								}
								
								if(cropBed.has("cropDetails") && cropBed.get("cropDetails").size() > 0)
								{
									JsonNode cropNode = cropBed.get("cropDetails");
									if(cropNode.isArray())
									{
										for(JsonNode crop : cropNode)
										{
											if(crop.hasNonNull("duration"))
											{
												if(crop.get("duration").asInt() > duration)
													throw new InvalidRequestBodyArgumentException("Crop duration can't be greater than cycle duration");
											}
											else
												throw new InvalidRequestBodyArgumentException("crop duration is null");
											
											if(!crop.hasNonNull("cropLabel"))
												throw new InvalidRequestBodyArgumentException("crop label is null");
											
											// check for maxAge and minStage
											if (crop.hasNonNull("plantMaxAge") && crop.hasNonNull("minStage")
													&& crop.get("plantMaxAge").asInt() < crop.get("minStage").asInt())
												throw new InvalidRequestBodyArgumentException(
														"Plant max age must not be less than min stage");
											
											if(crop.hasNonNull("opticalProperty") && !(crop.get("opticalProperty").hasNonNull("reflectionPAR")))
												throw new InvalidRequestBodyArgumentException("reflectionPAR is null");
											if(crop.hasNonNull("opticalProperty") && !(crop.get("opticalProperty").hasNonNull("reflectionNIR")))
												throw new InvalidRequestBodyArgumentException("reflectionNIR is null");
											if(crop.hasNonNull("opticalProperty") && !(crop.get("opticalProperty").hasNonNull("transmissionPAR")))
												throw new InvalidRequestBodyArgumentException("transmissionPAR is null");
											if(crop.hasNonNull("opticalProperty") && !(crop.get("opticalProperty").hasNonNull("transmissionNIR")))
												throw new InvalidRequestBodyArgumentException("transmissionNIR is null");
											
											double reflectivity_PAR = crop.get("opticalProperty").get("reflectionPAR").asDouble();
											double reflectivity_NIR = crop.get("opticalProperty").get("reflectionNIR").asDouble();
											double transmissivity_PAR = crop.get("opticalProperty").get("transmissionPAR").asDouble();
											double transmissivity_NIR = crop.get("opticalProperty").get("transmissionNIR").asDouble();
											
											if(reflectivity_NIR + transmissivity_NIR > 1.0)
												throw new InvalidRequestBodyArgumentException("reflectionNIR + transmissionNIR is greater than 1");
											if(reflectivity_PAR + transmissivity_PAR > 1.0)
												throw new InvalidRequestBodyArgumentException("reflectionPAR + transmissionPAR is greater than 1");
											
											
											//o1, o2, s1
											Double bedWidth = payload.get("cropParameters").get("bedParameter").get("bedWidth").asDouble();
											
											if(crop.hasNonNull("o1"))
											{
												if(crop.get("o1").asDouble() < 0 || crop.get("o1").asDouble() > bedWidth/2.0)
													throw new InvalidRequestBodyArgumentException("o1 must be between 0 and " + bedWidth/2.0);	
											}
											else
												throw new InvalidRequestBodyArgumentException("o1 can't be null");
											
											if(crop.hasNonNull("o2")) {
												if(crop.get("o2").asDouble() < 0 || crop.get("o2").asDouble() > bedWidth*2.0)
													throw new InvalidRequestBodyArgumentException("o2 must be between 0 and " + bedWidth*2.0);
											}
											else
												throw new InvalidRequestBodyArgumentException("o2 can't be null");
											
											if(crop.hasNonNull("s1")) {
												if(crop.get("s1").asDouble() < 100 || crop.get("s1").asDouble() > bedWidth*10.0)
													throw new InvalidRequestBodyArgumentException("s1 must be between 100mm and " + bedWidth*10.0);
											}
											else
												throw new InvalidRequestBodyArgumentException("s1 can't be null");
										}
									}
								}
								else
									throw new InvalidRequestBodyArgumentException("cropDetails can't be empty");
							}
						}
					}
					else
						throw new InvalidRequestBodyArgumentException("cycleBedDetails can't be empty");
					
					// If bedPattern is not empty and any key remained false means BedName is not matched with the bed names
					for (Map.Entry<String, Boolean> entry : cycleInterBedPatternMap.entrySet()) {
					    if (!entry.getValue()) {
					    	throw new InvalidRequestBodyArgumentException("Bed Name does not matches with inter cycle bed pattern");
					    }
					}
				}
				
				Collections.sort(cycleDates, new Comparator<Pair<LocalDate, LocalDate>>() {
					public int compare(Pair<LocalDate, LocalDate> o1, Pair<LocalDate, LocalDate> o2) {
						
						return o1.getFirst().compareTo(o2.getFirst());
					};
				});
				
				if(ChronoUnit.DAYS.between(cycleDates.get(0).getFirst(), cycleDates.get(cycleDates.size()-1).getSecond()) > 365)
				{
					throw new InvalidRequestBodyArgumentException("Cycle for more than 1 year is not allowed");
				}
				for(int i = 1;i < cycleDates.size();i++)
				{
					if(cycleDates.get(i).getFirst().isBefore(cycleDates.get(i-1).getSecond()))
						throw new InvalidRequestBodyArgumentException("Overlapping cycles are not allowed");
				}
				
			}
			else
				throw new InvalidRequestBodyArgumentException("cycles must be an array");
			
		} else
			throw new InvalidRequestBodyArgumentException("cycles can't be null");
	}
	
	private void validateBedAzimuth(JsonNode payload) {
		
		if (payload.has("cropParameters") && payload.get("cropParameters").has("bedParameter") &&
				payload.get("cropParameters").get("bedParameter").hasNonNull("bedAzimuth")) {
			
			Integer bedAzimuth = payload.get("cropParameters").get("bedParameter").get("bedAzimuth").asInt();
			
			if(bedAzimuth != 0 && bedAzimuth != 1)
				throw new InvalidRequestBodyArgumentException("bedAzimuth can only be 0 or 1");
			
		} else
			throw new InvalidRequestBodyArgumentException("bedAzimuth can't be null");
	}
	
	private void validateStartPointOffset(JsonNode payload) {
		if (payload.has("cropParameters") && payload.get("cropParameters").has("startPointOffset")) {
			
			Double startPointOffset = payload.get("cropParameters").get("startPointOffset").asDouble();
			Integer bedCount = payload.get("cropParameters").get("bedParameter").get("noOfBeds").asInt();
			Double bedWidth = payload.get("cropParameters").get("bedParameter").get("bedWidth").asDouble();
			Double pitchOfRows = payload.get("preProcessorToggles").get("pitchOfRows").asDouble()*1000.0; // in mm
			Double bedHeight = payload.get("cropParameters").get("bedParameter").get("bedHeight").asDouble();
			Double bedAngle = payload.get("cropParameters").get("bedParameter").get("bedAngle").asDouble();
			Double bedBottomWidth = simulationFormulas.calculateBedBottomWidth(bedAngle, bedWidth, bedHeight);
			
			Double bedSpacing = simulationFormulas.calculateBedSpacing(bedCount, pitchOfRows, bedBottomWidth);
			
//			if(bedCount == 1 && (startPointOffset < 0 || startPointOffset > pitchOfRows/2.0))
//				throw new InvalidRequestBodyArgumentException("startPointOffset must be between 0 and " + pitchOfRows/2.0);
//			else if(startPointOffset < 0 || startPointOffset > (bedBottomWidth + bedSpacing))
//				throw new InvalidRequestBodyArgumentException("startPointOffset must be between 0 and " + (bedBottomWidth + bedSpacing));
			if((startPointOffset < 0-pitchOfRows/2.0 || startPointOffset > pitchOfRows/2.0))
				throw new InvalidRequestBodyArgumentException("startPointOffset must be between " + (-pitchOfRows/2.0) +  " and " + pitchOfRows/2.0);

		} else
			throw new InvalidRequestBodyArgumentException("startPointOffset can't be null");
	}

	public void validateWeatherData(WeatherData data, SimulationType simulationType) {
		if (data == null)
			throw new InvalidRequestBodyArgumentException("Does not have weather for specified date(s)");
	}
}
