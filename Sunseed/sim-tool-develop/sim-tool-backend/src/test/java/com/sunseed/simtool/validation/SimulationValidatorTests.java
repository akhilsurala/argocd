package com.sunseed.simtool.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;

@ExtendWith(MockitoExtension.class)
public class SimulationValidatorTests {

	@Mock
	private ValidationFormulas validationFormulas;
	@InjectMocks
	private SimulationValidator simulationValidator;
	@Spy
	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	
	@Test
	public void test_validatePayload_onlyAgri()
	{
		Map<String, Object> payload = new HashMap<>();
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(null, SimulationType.ONLY_AGRI, false);
		}); //null map
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no latitude key
		
		payload.put("latitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //null latitude value
		
		payload.put("latitude", 23.45678);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no longitude
		
		payload.put("longitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //longitude null value
		
		payload.put("longitude", -89.5644);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no id
		
		payload.put("id", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //id null value
		
		payload.put("id", 2);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no preProcessorToggles
		
		payload.put("preProcessorToggles", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //preProcessorToggles null
		
		payload.put("preProcessorToggles", Collections.EMPTY_MAP);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no azimuth
		
		Map<String, Object> preProcessorToggles = new HashMap<>();
		preProcessorToggles.put("azimuth", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //azimuth null
		
		preProcessorToggles.put("azimuth", -90.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //azimuth less then 0
		
		preProcessorToggles.put("azimuth", 450.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //azimuth more than 360
		
		preProcessorToggles.put("azimuth", 135.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //null lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", 0.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //lengthOfOneRow less than 1
		
		preProcessorToggles.put("lengthOfOneRow", 550.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //lengthOfOneRow more than 500
		
		preProcessorToggles.put("lengthOfOneRow", 400.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //no pitchOfRows
		
		preProcessorToggles.put("pitchOfRows", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //null pitchOfRows
		
		preProcessorToggles.put("pitchOfRows", 1.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //pitchOfRows < 2
		
		preProcessorToggles.put("pitchOfRows", 12.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //pitchOfRows > 10
		
		preProcessorToggles.put("pitchOfRows", 8.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //null cropParameters
		
		Map<String, Object> cropParameters = new HashMap<>();
		cropParameters.put("bedAngle", null);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //null bedAngle
		
		cropParameters.put("bedAngle", -90.0);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedAngle < 0
		
		cropParameters.put("bedAngle", 80.0);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedAngle > 75
		
		cropParameters.put("bedAngle", 45.0);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedCC, height, width are null
		
		cropParameters.put("bedCC", 4);
		cropParameters.put("bedHeight", 1);
		cropParameters.put("bedWidth", 80);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedWidth < 100
		
		cropParameters.put("bedCC", 4);
		cropParameters.put("bedHeight", 1);
		cropParameters.put("bedWidth", 2100);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedWidth > maxWidth
		
		cropParameters.put("bedCC", 4);
		cropParameters.put("bedHeight", -2.0);
		cropParameters.put("bedWidth", 1000);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedHeight < 0
		
		cropParameters.put("bedCC", 4);
		cropParameters.put("bedHeight", 600);
		cropParameters.put("bedWidth", 1000);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedHeight > maxHeight
		
		when(validationFormulas.calculateBedBottomWidth(eq(45.0), eq(1000.0), eq(400.0))).thenReturn(1800.0);
		
		cropParameters.put("bedCC", 5);
		cropParameters.put("bedHeight", 400);
		cropParameters.put("bedWidth", 1000);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedCC > max
		
		cropParameters.put("bedCC", 4);
		cropParameters.put("bedHeight", 400);
		cropParameters.put("bedWidth", 1000);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //bedSection is null
		
		Map<String, Object> bedSection = new HashMap<>();
		bedSection.put("offsetFromCentreLine", null);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromCentreLine is null
		
		bedSection.put("offsetFromCentreLine", -2);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromCentreLine < 0
		
		bedSection.put("offsetFromCentreLine", 600);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromCentreLine > bedWidth/2
		
		bedSection.put("offsetFromCentreLine", 400);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromStart is null
		
		bedSection.put("offsetFromStart", -2);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromStart < 0
		
		bedSection.put("offsetFromStart", 2100);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //offsetFromStart > bedWidth*2
		
		bedSection.put("offsetFromStart", 1500);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //spacing is null
		
		bedSection.put("spacing", 80);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //spacing < 100
		
		bedSection.put("spacing", 11000);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //spacing > bedWidth*10
		
		bedSection.put("spacing", 5000);
		cropParameters.put("bedSection", bedSection);
		payload.put("cropParameters", cropParameters);
		assertDoesNotThrow(() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_AGRI, false); }); //All validations are correct
	}
	
	@Test
	public void test_validatePayload_onlyPV()
	{
		Map<String, Object> payload = new HashMap<>();
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(null, SimulationType.ONLY_PV, false);
		}); //null map
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no latitude key
		
		payload.put("latitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //null latitude value
		
		payload.put("latitude", 23.45678);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no longitude
		
		payload.put("longitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //longitude null value
		
		payload.put("longitude", -89.5644);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no id
		
		payload.put("id", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //id null value
		
		payload.put("id", 2);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no preProcessorToggles
		
		payload.put("preProcessorToggles", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //preProcessorToggles null
		
		payload.put("preProcessorToggles", Collections.EMPTY_MAP);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no azimuth
		
		Map<String, Object> preProcessorToggles = new HashMap<>();
		preProcessorToggles.put("azimuth", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //azimuth null
		
		preProcessorToggles.put("azimuth", -90.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //azimuth less then 0
		
		preProcessorToggles.put("azimuth", 450.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //azimuth more than 360
		
		preProcessorToggles.put("azimuth", 135.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //no lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //null lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", 0.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //lengthOfOneRow less than 1
		
		preProcessorToggles.put("lengthOfOneRow", 550.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //lengthOfOneRow more than 500
		
		preProcessorToggles.put("lengthOfOneRow", 400.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //pvParameters is null
		
		Map<String, Object> pvParameters = new HashMap<>();
		pvParameters.put("tiltIfFt", null);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //tilt angle is null
		
		pvParameters.put("tiltIfFt", -180.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //tilt angle < -90
		
		pvParameters.put("tiltIfFt", 100.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //tilt angle > 90
		
		pvParameters.put("tiltIfFt", 45.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleMaskPattern is null
		
		pvParameters.put("moduleMaskPattern", "010101010101");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleMaskPattern more than 10 bits
		
		pvParameters.put("moduleMaskPattern", "0000");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleMaskPattern all 0s
		
		pvParameters.put("moduleMaskPattern", "1111");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleMaskPattern all 1s
		
		pvParameters.put("moduleMaskPattern", "0101");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfigs is null
		
		List<Map<String, String>> moduleConfigs = new ArrayList<>();
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfigs is empty
		
		Map<String, String> moduleConfig = new HashMap<>();
		moduleConfig.put("id", "2");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is null
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2P-");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2A");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "AP");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "5L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2A-2A");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "AL-AL");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "5L-5L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "3L-4L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2P");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //pvModule is null
		
		Map<String, Object> pvModule = new HashMap<>();
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //length is null
		
		pvModule.put("length", -1);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //length < 0
		
		pvModule.put("length", 5200);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //length > 5m
		
		pvModule.put("length", 3000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //width is null
		
		pvModule.put("width", -1);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //width < 0
		
		pvModule.put("width", 6000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //width > 5
		
		pvModule.put("width", 2000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //gapBetweenModules is null
		
		
		pvParameters.put("gapBetweenModules", 0);
		payload.put("pvParameters", pvParameters);
		
		when(validationFormulas.calculateMaxGapBetweenModules(any(), any(), any(), any(), any(), any(), any(), eq('P'))).thenReturn(5.0);
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //gapBetweenModules < 0
		
		pvParameters.put("gapBetweenModules", 5200);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //gapBetweenModules > max
		
		pvParameters.put("gapBetweenModules", 4000);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //height is null
		
		when(validationFormulas.calculateModuleHeight(any(), any(), any(), any(), eq('P'), any())).thenReturn(2.0);
		
		pvParameters.put("height", 1.0);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //height < 2
		
		pvParameters.put("height", 11.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //height > 10
		
		pvParameters.put("height", 6.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //pitchOfRows is null
		
		when(validationFormulas.calculatePitch(any(), any(), any(), any(), eq('P'), any(), any())).thenReturn(5.0);
		
		preProcessorToggles.put("pitchOfRows", 4.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //pitchOfRows < min
		
		preProcessorToggles.put("pitchOfRows", 53.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); }); //pitchOfRows > 10*min
		
		preProcessorToggles.put("pitchOfRows", 10.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertDoesNotThrow(() -> { 
			simulationValidator.validatePayload(payload, SimulationType.ONLY_PV, false); });
		
	}
	
	@Test
	public void test_validatePayload_APV()
	{
		Map<String, Object> payload = new HashMap<>();
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(null, SimulationType.APV, false);
		}); //null map
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no latitude key
		
		payload.put("latitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //null latitude value
		
		payload.put("latitude", 23.45678);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no longitude
		
		payload.put("longitude", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //longitude null value
		
		payload.put("longitude", -89.5644);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no id
		
		payload.put("id", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //id null value
		
		payload.put("id", 2);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no preProcessorToggles
		
		payload.put("preProcessorToggles", null);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //preProcessorToggles null
		
		payload.put("preProcessorToggles", Collections.EMPTY_MAP);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no azimuth
		
		Map<String, Object> preProcessorToggles = new HashMap<>();
		preProcessorToggles.put("azimuth", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //azimuth null
		
		preProcessorToggles.put("azimuth", -90.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //azimuth less then 0
		
		preProcessorToggles.put("azimuth", 450.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //azimuth more than 360
		
		preProcessorToggles.put("azimuth", 135.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //no lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", null);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //null lengthOfOneRow 
		
		preProcessorToggles.put("lengthOfOneRow", 0.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //lengthOfOneRow less than 1
		
		preProcessorToggles.put("lengthOfOneRow", 550.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //lengthOfOneRow more than 500
		
		preProcessorToggles.put("lengthOfOneRow", 400.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> {
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //pvParameters is null
		
		Map<String, Object> pvParameters = new HashMap<>();
		pvParameters.put("tiltIfFt", null);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //tilt angle is null
		
		pvParameters.put("tiltIfFt", -180.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //tilt angle < -90
		
		pvParameters.put("tiltIfFt", 100.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //tilt angle > 90
		
		pvParameters.put("tiltIfFt", 45.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleMaskPattern is null
		
		pvParameters.put("moduleMaskPattern", "010101010101");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleMaskPattern more than 10 bits
		
		pvParameters.put("moduleMaskPattern", "0000");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleMaskPattern all 0s
		
		pvParameters.put("moduleMaskPattern", "1111");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleMaskPattern all 1s
		
		pvParameters.put("moduleMaskPattern", "0101");
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfigs is null
		
		List<Map<String, String>> moduleConfigs = new ArrayList<>();
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfigs is empty
		
		Map<String, String> moduleConfig = new HashMap<>();
		moduleConfig.put("id", "2");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is null
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2P-");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2A");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "AP");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "5L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2A-2A");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "AL-AL");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "5L-5L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "3L-4L");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //moduleConfig is invalid
		
		moduleConfigs.clear();
		moduleConfig.put("moduleConfig", "2P");
		moduleConfigs.add(moduleConfig);
		pvParameters.put("moduleConfigs", moduleConfigs);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //pvModule is null
		
		Map<String, Object> pvModule = new HashMap<>();
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //length is null
		
		pvModule.put("length", -1);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //length < 0
		
		pvModule.put("length", 5200);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //length > 5m
		
		pvModule.put("length", 3000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //width is null
		
		pvModule.put("width", -1);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //width < 0
		
		pvModule.put("width", 6000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //width > 5
		
		pvModule.put("width", 2000);
		pvParameters.put("pvModule", pvModule);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //gapBetweenModules is null
		
		
		pvParameters.put("gapBetweenModules", 0);
		payload.put("pvParameters", pvParameters);
		
		when(validationFormulas.calculateMaxGapBetweenModules(any(), any(), any(), any(), any(), any(), any(), eq('P'))).thenReturn(5.0);
		
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //gapBetweenModules < 0
		
		pvParameters.put("gapBetweenModules", 5200);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //gapBetweenModules > max
		
		pvParameters.put("gapBetweenModules", 4000);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //height is null
		
		when(validationFormulas.calculateModuleHeight(any(), any(), any(), any(), eq('P'), any())).thenReturn(2.0);
		
		pvParameters.put("height", 1.0);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //height < 2
		
		pvParameters.put("height", 11.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //height > 10
		
		pvParameters.put("height", 6.0);
		payload.put("pvParameters", pvParameters);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //pitchOfRows is null
		
		when(validationFormulas.calculatePitch(any(), any(), any(), any(), eq('P'), any(), any())).thenReturn(5.0);
		
		preProcessorToggles.put("pitchOfRows", 4.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //pitchOfRows < min
		
		preProcessorToggles.put("pitchOfRows", 53.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertThrows(InvalidRequestBodyArgumentException.class,() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); }); //pitchOfRows > 10*min
		
		preProcessorToggles.put("pitchOfRows", 10.0);
		payload.put("preProcessorToggles", preProcessorToggles);
		assertDoesNotThrow(() -> { 
			simulationValidator.validatePayload(payload, SimulationType.APV, false); });
		
	}
	
	@Test
	public void test_validateWeatherData()
	{
		
	}
}
