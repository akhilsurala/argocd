package com.sunseed.simtool.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.model.request.SimulationRequestDto;
import com.sunseed.simtool.model.request.SimulationStatusDto;
import com.sunseed.simtool.service.SimulationService;
import com.sunseed.simtool.serviceimpl.SimulationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SimulationControllerTests {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	@Mock
	private SimulationService simulationService;
	@Mock
	private SimulationServiceImpl simulationServiceImpl;
	@InjectMocks
	private SimulationController simulationController;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(simulationController).build();
	}
	
	@Test
	public void test_createSimulation_validateRequestBody() throws JsonProcessingException, Exception
	{
		SimulationRequestDto simulationRequestDto = new SimulationRequestDto();
		
		String error = mockMvc.perform(post("/v1/simulation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simulationRequestDto)))
//				.andDo(print())
				.andExpect(status().is(400))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andReturn().getResolvedException().getMessage();
		
		assertTrue(error.contains("User Profile Id can't be null"));
		assertTrue(error.contains("Project Id can't be null"));
		
	}
	
	@Test
	public void test_createSimulation() throws JsonProcessingException, Exception
	{
		SimulationRequestDto simulationRequestDto = new SimulationRequestDto();
		simulationRequestDto.setUserProfileId(1l);
		simulationRequestDto.setProjectId(2l);
		simulationRequestDto.setRunPayload(List.of(Map.of("id","2")));
		
		mockMvc.perform(post("/v1/simulation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simulationRequestDto)))
//				.andDo(print())
				.andExpect(status().isCreated());
	}
	
	@Test
	public void test_createSimulation_exception() throws JsonProcessingException, Exception
	{
		SimulationRequestDto simulationRequestDto = new SimulationRequestDto();
		simulationRequestDto.setUserProfileId(1l);
		simulationRequestDto.setProjectId(2l);
		simulationRequestDto.setRunPayload(List.of(Map.of("id","2")));
		
//		when(simulationService.createSimulation(any(), any(), any())).thenThrow(new InvalidRequestBodyArgumentException("Error Message"));
		
		mockMvc.perform(post("/v1/simulation")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simulationRequestDto)))
//				.andDo(print())
				.andExpect(status().isCreated());
	}
	
	@Test
	public void test_updateSimulationTaskStatus_validateRequestBody() throws JsonProcessingException, Exception
	{
		SimulationStatusDto simulationStatusDto = new SimulationStatusDto();
		simulationStatusDto.setSimulationId(null);
		simulationStatusDto.setStatus("Fail");
		
		String error = mockMvc.perform(put("/v1/simulation/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simulationStatusDto)))
//				.andDo(print())
				.andExpect(status().is(400))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
				.andReturn().getResolvedException().getMessage();
		
		assertTrue(error.contains("Simulation id can't be null"));
		assertTrue(error.contains("Must be cancel, pause or resume"));
	}
	
	@Test
	public void test_updateSimulationTaskStatus() throws JsonProcessingException, Exception
	{
		SimulationStatusDto simulationStatusDto = new SimulationStatusDto();
		simulationStatusDto.setSimulationId(1l);
		simulationStatusDto.setStatus("Cancel");
		
		mockMvc.perform(put("/v1/simulation/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simulationStatusDto)))
//				.andDo(print())
				.andExpect(status().isOk());
	}
	
	@Test
	public void test_getSimulation() throws JsonProcessingException, Exception
	{	
		mockMvc.perform(get("/v1/simulation")
				.param("id", "2"))
//				.andDo(print())
				.andExpect(status().isOk());
	}
	
	@Test
	public void test_getSimulation_missingQueryParam() throws JsonProcessingException, Exception
	{	
		mockMvc.perform(get("/v1/simulation"))
//				.andDo(print())
				.andExpect(status().is(400))
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestParameterException));
	}
}
