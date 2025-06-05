//package com.sunseed.simtool.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.convention.MatchingStrategies;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.sunseed.simtool.constant.SimulationType;
//import com.sunseed.simtool.constant.Status;
//import com.sunseed.simtool.entity.CropYield;
//import com.sunseed.simtool.entity.PVYield;
//import com.sunseed.simtool.entity.Simulation;
//import com.sunseed.simtool.entity.SimulationTask;
//import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
//import com.sunseed.simtool.helper.SimulationHelper;
//import com.sunseed.simtool.model.request.SimulationRequestDto;
//import com.sunseed.simtool.model.request.SimulationStatusDto;
//import com.sunseed.simtool.model.response.SimulationResponseDto;
//import com.sunseed.simtool.model.response.SimulationTaskDto;
//import com.sunseed.simtool.model.response.SimulationTaskStatusDto;
//import com.sunseed.simtool.rabbitmq.MessageProducer;
//import com.sunseed.simtool.repository.SimulationRepository;
//import com.sunseed.simtool.repository.SimulationTaskRepository;
//import com.sunseed.simtool.serviceimpl.SimulationServiceImpl;
//import com.sunseed.simtool.util.SimulationUtils;
//import com.sunseed.simtool.validation.SimulationValidator;
//import com.sunseed.simtool.validation.ValidationFormulas;
//
//@ExtendWith(MockitoExtension.class)
//public class SimulationServiceTests {
//
//	@Spy
//	private ModelMapper modelMapper = modelMapperConfig();
//	@Mock
//	private SimulationRepository simulationRepository;
//	@Mock
//	private SimulationTaskRepository simulationTaskRepository;
//	@Mock
//	private MessageProducer messageProducer;
//	@Spy
//	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//	@Mock
//	private SimulationHelper simulationHelper;
//	@Mock
//	private SimulationValidator simulationValidator;
//	@Mock
//	private ValidationFormulas validationFormulas;
//	@InjectMocks
//	private SimulationServiceImpl simulationServiceImpl;
//	
//	private SimulationStatusDto simulationStatusDto;
//	
//	private Simulation simulation;
//	
//	@BeforeEach
//	private void setup()
//	{
//		simulationStatusDto = new SimulationStatusDto();
//		simulationStatusDto.setSimulationId(1l);
//		simulationStatusDto.setStatus("Cancel");
//		
//		simulation = new Simulation();
//		simulation.setId(10l);
//		simulation.setTaskCount(10l);
//		simulation.setSimulationTasks(Collections.emptyList());
//	}
//	
//	private ModelMapper modelMapperConfig() {
//		ModelMapper modelMapper = new ModelMapper();
//		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//		return modelMapper;
//	}
//	
//	@Test
//	public void createSimulation_missingRunPayload()
//	{
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(null, 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Run payload can't be null"));
//	}
//	
//	@Test
//	public void createSimulation_missingRunId()
//	{
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(Collections.EMPTY_MAP, 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Run Id can't be null"));
//	}
//	
//	@Test
//	public void createSimulation_missingPreProcessors()
//	{
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(Map.of("id", "3"), 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Simulation type can't be null"));
//	}
//	
//	@Test
//	public void createSimulation_missingToggles()
//	{
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(Map.of("id","3", "preProcessorToggles", Collections.EMPTY_MAP), 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Simulation type can't be null"));
//	}
//	
//	@Test
//	public void createSimulation_alreadyExistForSameRunId()
//	{
//		when(simulationRepository.findByRunId(eq(3l))).thenReturn(Optional.of(new Simulation()));
//		
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(Map.of("id","3", "preProcessorToggles", Map.of("toggle","ONLY AGRI")), 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Simulation for runId: 3 already exists"));
//	}
//	
//	@Test
//	public void createSimulation()
//	{
//		when(simulationRepository.findByRunId(eq(3l))).thenReturn(Optional.empty());
//		
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.createSimulation(Map.of("id","3", "preProcessorToggles", Map.of("toggle","ONLY AGRI")), 1l, 2l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Simulation for runId: 3 already exists"));
//	}
//
////	@Test
////	public void createSimulation_validateSimulationForOneDay() //Used default run policy and run hours
////	{
////		SimulationRequestDto simulationRequestDto = SimulationRequestDto.builder()
////				.userProfileId(1l)
////				.projectId(2l)
////				.runPayload(null)
////				.build();
////		
////		when(simulationRepository.saveAndFlush(any(Simulation.class))).thenReturn(simulation);
////		
////		simulationServiceImpl.createSimulation(simulationRequestDto);
////		
////		ArgumentCaptor<Simulation> captor = ArgumentCaptor.forClass(Simulation.class);
////		verify(simulationRepository, times(1)).saveAndFlush(captor.capture());
////		
////		Simulation simulation = captor.getValue();
////		
////		assertNotNull(simulation);
////		assertEquals(simulation.getProjectId(), 2l);
////		assertEquals(simulation.getUserProfileId(), 1l);
////		assertEquals(simulation.getRunId(), 3l);
////		assertEquals(simulation.getStatus(), Status.QUEUED);
////		assertEquals(simulation.getTaskCount(), 12l);
////		assertEquals(simulation.getWithTracking(), Boolean.TRUE);
////		assertEquals(simulation.getRunPayload(), null);
////		assertEquals(simulation.getStartDate(), LocalDate.of(2024, 4, 1));
////		assertEquals(simulation.getEndDate(), LocalDate.of(2024, 4, 1));
////		assertEquals(simulation.getSimulationTasks().size(), 12);
////		
////		LocalDateTime start = LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(7, 0));
////		
////		for(SimulationTask st : simulation.getSimulationTasks()) {
////			assertEquals(st.getDate(), start);
////			start = SimulationUtils.nextSimulationTaskTime(null, start);
////		}
////	}
////	
////	@Test
////	public void createSimulation_validateSimulation() //Used default run policy and run hours
////	{
////		SimulationRequestDto simulationRequestDto = SimulationRequestDto.builder()
////				.userProfileId(1l)
////				.projectId(2l)
////				.runPayload(null)
////				.build();
////		
////		when(simulationRepository.saveAndFlush(any(Simulation.class))).thenReturn(simulation);
////		
////		simulationServiceImpl.createSimulation(simulationRequestDto);
////		
////		ArgumentCaptor<Simulation> captor = ArgumentCaptor.forClass(Simulation.class);
////		verify(simulationRepository, times(1)).saveAndFlush(captor.capture());
////		
////		Simulation simulation = captor.getValue();
////		
////		assertNotNull(simulation);
////		assertEquals(simulation.getProjectId(), 2l);
////		assertEquals(simulation.getUserProfileId(), 1l);
////		assertEquals(simulation.getRunId(), 3l);
////		assertEquals(simulation.getStatus(), Status.QUEUED);
////		assertEquals(simulation.getTaskCount(), 36l);
////		assertEquals(simulation.getWithTracking(), Boolean.FALSE);
////		assertEquals(simulation.getRunPayload(), null);
////		assertEquals(simulation.getStartDate(), LocalDate.of(2024, 4, 1));
////		assertEquals(simulation.getEndDate(), LocalDate.of(2024, 4, 30));
////		assertEquals(simulation.getSimulationTasks().size(), 36);
////		
////		LocalDateTime start = LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(7, 0));
////		
////		int counter = 0;
////		for(SimulationTask st : simulation.getSimulationTasks()) {
////			assertEquals(st.getDate(), start);
////			counter++;
////			if(counter >= 12) {
////				start = LocalDateTime.of(SimulationUtils.nextSimulationTaskDate(null, start.toLocalDate()), LocalTime.of(7, 0));
////				counter = 0;
////			}
////			else
////				start = SimulationUtils.nextSimulationTaskTime(null, start);
////		}
////	}
////	
////	@Test
////	public void createSimulation_validateResponse() //Used default run policy and run hours
////	{
////		SimulationRequestDto simulationRequestDto = SimulationRequestDto.builder()
////				.userProfileId(1l)
////				.projectId(2l)
////				.runId(3l)
////				.withTracking(Boolean.TRUE)
////				.runPayload(null)
////				.simulationType(SimulationType.ONLY_PV)
////				.startDate(LocalDate.of(2024, 4, 1))
////				.build();
////		
////		simulation = modelMapper.map(simulationRequestDto, Simulation.class);
////		simulation.setId(10l);
////		simulation.setStatus(Status.QUEUED);
////		simulation.setTaskCount(12l);
////		simulation.setEndDate(LocalDate.of(2024, 4, 1));
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).simulation(simulation).build();
////		
////		simulation.setSimulationTasks(List.of(simulationTask1, simulationTask2));
////		
////		when(simulationRepository.saveAndFlush(any(Simulation.class))).thenReturn(simulation);
////		
////		SimulationResponseDto simulationResponseDto = simulationServiceImpl.createSimulation(simulationRequestDto);
////		
////		assertNotNull(simulationResponseDto);
////		assertEquals(simulationResponseDto.getId(), 10l);
////		assertEquals(simulationResponseDto.getProjectId(), 2l);
////		assertEquals(simulationResponseDto.getUserProfileId(), 1l);
////		assertEquals(simulationResponseDto.getRunId(), 3l);
////		assertEquals(simulationResponseDto.getStatus(), Status.QUEUED);
////		assertEquals(simulationResponseDto.getTaskCount(), 12l);
////		assertEquals(simulationResponseDto.getWithTracking(), Boolean.TRUE);
////		assertEquals(simulationResponseDto.getRunPayload(), null);
////		assertEquals(simulationResponseDto.getStartDate(), LocalDate.of(2024, 4, 1));
////		assertEquals(simulationResponseDto.getEndDate(), LocalDate.of(2024, 4, 1));
////	}
////	
////	@Test
////	public void createSimulation_APVwithoutTracking() //Used default run policy and run hours
////	{
////		SimulationRequestDto simulationRequestDto = SimulationRequestDto.builder()
////				.userProfileId(1l)
////				.projectId(2l)
////				.runId(3l)
////				.withTracking(Boolean.FALSE)
////				.runPayload(null)
////				.simulationType(SimulationType.APV)
////				.startDate(LocalDate.of(2024, 4, 1))
////				.build();
////		
////		simulation.setSimulationType(SimulationType.APV);
////		simulation.setWithTracking(Boolean.FALSE);
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).simulation(simulation).build();
////		
////		simulation.setSimulationTasks(List.of(simulationTask1, simulationTask2));
////		
////		when(simulationRepository.saveAndFlush(any(Simulation.class))).thenReturn(simulation);
////		
////		simulationServiceImpl.createSimulation(simulationRequestDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(2)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 2);
////		
////		capturedTasks.forEach(tasks -> {
////			assertEquals(tasks.getPvStatus(), Status.QUEUED);
////			assertEquals(tasks.getAgriStatus(), Status.QUEUED);
////			assertNotNull(tasks.getEnqueuedAt());
////		});
////		
////		
////		verify(messageProducer, times(4)).sendMessage(any(), any(), any());
////	}
////	
////	@Test
////	public void createSimulation_APVwithTracking() //Used default run policy and run hours
////	{
////		SimulationRequestDto simulationRequestDto = SimulationRequestDto.builder()
////				.userProfileId(1l)
////				.projectId(2l)
////				.runId(3l)
////				.withTracking(Boolean.TRUE)
////				.runPayload(null)
////				.simulationType(SimulationType.APV)
////				.startDate(LocalDate.of(2024, 4, 1))
////				.build();
////		
////		simulation.setSimulationType(SimulationType.APV);
////		simulation.setWithTracking(Boolean.TRUE);
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).simulation(simulation).build();
////		
////		simulation.setSimulationTasks(List.of(simulationTask1, simulationTask2));
////		
////		when(simulationRepository.saveAndFlush(any(Simulation.class))).thenReturn(simulation);
////		
////		simulationServiceImpl.createSimulation(simulationRequestDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(2)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 2);
////		
////		capturedTasks.forEach(tasks -> {
////			assertEquals(tasks.getPvStatus(), Status.QUEUED);
////			assertEquals(tasks.getAgriStatus(), Status.PENDING);
////			assertNotNull(tasks.getEnqueuedAt());
////		});
////		
////		
////		verify(messageProducer, times(2)).sendMessage(any(), any(), any());
////	}
////	
////	@Test
////	public void updateStatus_simulationDoesNotExists()
////	{
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.empty());
////		
////		assertThrows(InvalidRequestBodyArgumentException.class, () -> {
////			simulationServiceImpl.updateStatus(simulationStatusDto);
////		});
////	}
////	
////	@Test
////	public void updateStatus_assertResultMap()
////	{
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(new Simulation()));
////		
////		when(simulationTaskRepository.countTaskByPvStatus(any())).thenReturn(
////				List.of(
////						SimulationTaskStatusDto.builder().status(Status.SUCCESS).taskCount(3l).build(),
////						SimulationTaskStatusDto.builder().status(Status.CANCELLED).taskCount(9l).build()
////						)
////				);
////		when(simulationTaskRepository.countTaskByAgriStatus(any())).thenReturn(
////				List.of(
////						SimulationTaskStatusDto.builder().status(Status.SUCCESS).taskCount(9l).build(),
////						SimulationTaskStatusDto.builder().status(Status.CANCELLED).taskCount(3l).build()
////						)
////				);
////		
////		Map<String, List<SimulationTaskStatusDto>> resultMap = simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		assertNotNull(resultMap);
////		assertEquals(resultMap.size(), 2);
////		assertNotNull(resultMap.get("pv"));
////		assertEquals(resultMap.get("pv").size(), 2);
////		resultMap.get("pv").forEach(pv -> {
////			if(pv.getStatus().equals(Status.SUCCESS))
////				assertEquals(pv.getTaskCount(), 3l);
////
////			if(pv.getStatus().equals(Status.CANCELLED))
////				assertEquals(pv.getTaskCount(), 9l);
////		});
////		
////		assertNotNull(resultMap.get("agri"));
////		assertEquals(resultMap.get("agri").size(), 2);
////		resultMap.get("agri").forEach(agri -> {
////			if(agri.getStatus().equals(Status.SUCCESS))
////				assertEquals(agri.getTaskCount(), 9l);
////
////			if(agri.getStatus().equals(Status.CANCELLED))
////				assertEquals(agri.getTaskCount(), 3l);
////		});
////	}
////	
////	@Test
////	public void updateStatus_invalidStatus_doNothing()
////	{
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(new Simulation()));
////		
////		simulationStatusDto.setStatus("Fail");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), any());
////	}
////	
////	@Test
////	public void updateStatus_cancelTasks()
////	{
////		List<SimulationTask> simulationTasks = List.of(
////				SimulationTask.builder().id(1l).pvStatus(Status.SUCCESS).agriStatus(Status.NA).build(),
////				SimulationTask.builder().id(2l).pvStatus(Status.PENDING).agriStatus(Status.NA).build(),
////				SimulationTask.builder().id(3l).pvStatus(Status.NA).agriStatus(Status.QUEUED).build(),
////				SimulationTask.builder().id(4l).pvStatus(Status.RUNNING).agriStatus(Status.SUCCESS).build(),
////				SimulationTask.builder().id(5l).pvStatus(Status.QUEUED).agriStatus(Status.PENDING).build(),
////				SimulationTask.builder().id(6l).pvStatus(Status.NA).agriStatus(Status.RUNNING).build()
////				);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(new Simulation()));
////		when(simulationHelper.cancelSimulation(any())).thenReturn(false, true, true, true);
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Cancel");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(5)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		capturedTasks.forEach(st -> {
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.CANCELLED);
////				assertEquals(st.getAgriStatus(), Status.NA);
////			}
////			if(st.getId() == 3l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.CANCELLED);
////			}
////			if(st.getId() == 4l)
////			{
////				assertEquals(st.getPvStatus(), Status.CANCELLED);
////				assertEquals(st.getAgriStatus(), Status.SUCCESS);
////			}
////			if(st.getId() == 5l)
////			{
////				assertEquals(st.getPvStatus(), Status.CANCELLED);
////				assertEquals(st.getAgriStatus(), Status.CANCELLED);
////			}
////			if(st.getId() == 6l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.CANCELLED);
////			}
////		});
////		
////		
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_pauseTasks()
////	{
////		List<SimulationTask> simulationTasks = List.of(
////				SimulationTask.builder().id(1l).pvStatus(Status.SUCCESS).agriStatus(Status.NA).build(),
////				SimulationTask.builder().id(2l).pvStatus(Status.PENDING).agriStatus(Status.NA).build(),
////				SimulationTask.builder().id(3l).pvStatus(Status.NA).agriStatus(Status.QUEUED).build(),
////				SimulationTask.builder().id(4l).pvStatus(Status.RUNNING).agriStatus(Status.SUCCESS).build(),
////				SimulationTask.builder().id(5l).pvStatus(Status.QUEUED).agriStatus(Status.PENDING).build(),
////				SimulationTask.builder().id(6l).pvStatus(Status.NA).agriStatus(Status.RUNNING).build()
////				);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(new Simulation()));
////		when(simulationHelper.cancelSimulation(any())).thenReturn(false, true, true, true);
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Pause");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(5)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		capturedTasks.forEach(st -> {
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.PAUSED);
////				assertEquals(st.getAgriStatus(), Status.NA);
////			}
////			if(st.getId() == 3l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.PAUSED);
////			}
////			if(st.getId() == 4l)
////			{
////				assertEquals(st.getPvStatus(), Status.PAUSED);
////				assertEquals(st.getAgriStatus(), Status.SUCCESS);
////			}
////			if(st.getId() == 5l)
////			{
////				assertEquals(st.getPvStatus(), Status.PAUSED);
////				assertEquals(st.getAgriStatus(), Status.PAUSED);
////			}
////			if(st.getId() == 6l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.PAUSED);
////			}
////		});
////		
////		
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_APVwithoutTracking()
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.APV);
////		simulation.setWithTracking(Boolean.FALSE);
////		
////		SimulationTask simulationTaskAgri = SimulationTask.builder().id(1l).pvStatus(Status.SUCCESS).agriStatus(Status.PAUSED).pvYields(new PVYield()).simulation(simulation).build();
////		SimulationTask simulationTaskAPV = SimulationTask.builder().id(2l).pvStatus(Status.PAUSED).agriStatus(Status.PAUSED).simulation(simulation).build();
////		SimulationTask simulationTaskPV = SimulationTask.builder().id(3l).pvStatus(Status.PAUSED).agriStatus(Status.SUCCESS).cropYields(new CropYield()).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTaskAgri, simulationTaskAPV, simulationTaskPV
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Resume");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(3)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 3);
////		capturedTasks.forEach(st -> {
////				
////			if(st.getId() == 1l)
////			{
////				assertEquals(st.getPvStatus(), Status.SUCCESS);
////				assertEquals(st.getAgriStatus(), Status.QUEUED);
////			}
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.QUEUED);
////				assertEquals(st.getAgriStatus(), Status.QUEUED);
////			}
////			if(st.getId() == 3l)
////			{
////				assertEquals(st.getPvStatus(), Status.QUEUED);
////				assertEquals(st.getAgriStatus(), Status.SUCCESS);
////			}
////		});
////		
////		verify(messageProducer, times(4)).sendMessage(any(), any(), any());
////		
////		
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_APVwithTracking()
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.APV);
////		simulation.setWithTracking(Boolean.TRUE);
////		
////		SimulationTask simulationTaskAgri = SimulationTask.builder().id(1l).pvStatus(Status.SUCCESS).agriStatus(Status.PAUSED).pvYields(new PVYield()).simulation(simulation).build();
////		SimulationTask simulationTaskAPV = SimulationTask.builder().id(2l).pvStatus(Status.PAUSED).agriStatus(Status.PAUSED).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTaskAgri, simulationTaskAPV
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Resume");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(2)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 2);
////		capturedTasks.forEach(st -> {
////				
////			if(st.getId() == 1l)
////			{
////				assertEquals(st.getPvStatus(), Status.SUCCESS);
////				assertEquals(st.getAgriStatus(), Status.QUEUED);
////			}
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.QUEUED);
////				assertEquals(st.getAgriStatus(), Status.PAUSED);
////			}
////			
////		});
////		
////		verify(messageProducer, times(2)).sendMessage(any(), any(), any());
////		
////		
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_onlyPV()
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.ONLY_PV);
////		simulation.setWithTracking(Boolean.TRUE);
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).pvStatus(Status.PAUSED).agriStatus(Status.NA).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).pvStatus(Status.PAUSED).agriStatus(Status.NA).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTask1, simulationTask2
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Resume");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(2)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 2);
////		capturedTasks.forEach(st -> {
////				
////			if(st.getId() == 1l)
////			{
////				assertEquals(st.getPvStatus(), Status.QUEUED);
////				assertEquals(st.getAgriStatus(), Status.NA);
////			}
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.QUEUED);
////				assertEquals(st.getAgriStatus(), Status.NA);
////			}
////			
////		});
////		
////		verify(messageProducer, times(2)).sendMessage(any(), any(), any());
////		
////		
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_onlyAgri()
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.ONLY_AGRI);
////		simulation.setWithTracking(Boolean.TRUE);
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).pvStatus(Status.NA).agriStatus(Status.PAUSED).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).pvStatus(Status.NA).agriStatus(Status.PAUSED).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTask1, simulationTask2
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		
////		simulationStatusDto.setStatus("Resume");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////		
////		ArgumentCaptor<SimulationTask> entityCaptor = ArgumentCaptor.forClass(SimulationTask.class);
////		verify(simulationTaskRepository, times(2)).saveAndFlush(entityCaptor.capture());
////		
////		List<SimulationTask> capturedTasks = entityCaptor.getAllValues();
////		
////		assertEquals(capturedTasks.size(), 2);
////		capturedTasks.forEach(st -> {
////				
////			if(st.getId() == 1l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.QUEUED);
////			}
////			if(st.getId() == 2l)
////			{
////				assertEquals(st.getPvStatus(), Status.NA);
////				assertEquals(st.getAgriStatus(), Status.QUEUED);
////			}
////			
////		});
////		
////		verify(messageProducer, times(2)).sendMessage(any(), any(), any());
////		
////		
////		verify(simulationTaskRepository, times(0)).getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.QUEUED.toString(), Status.RUNNING.toString(), Status.PENDING.toString()) ));
////		verify(simulationTaskRepository, times(1)).getBySimulationAndStatusIn(any(), eq(List.of(Status.PAUSED.toString())));
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_APV_throwJsonProcessingError() throws JsonProcessingException
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.APV);
////		simulation.setWithTracking(Boolean.FALSE);
////		
////		SimulationTask simulationTaskAgri = SimulationTask.builder().id(1l).pvStatus(Status.SUCCESS).agriStatus(Status.PAUSED).pvYields(new PVYield()).simulation(simulation).build();
////		SimulationTask simulationTaskAPV = SimulationTask.builder().id(2l).pvStatus(Status.PAUSED).agriStatus(Status.PAUSED).simulation(simulation).build();
////		SimulationTask simulationTaskPV = SimulationTask.builder().id(3l).pvStatus(Status.PAUSED).agriStatus(Status.SUCCESS).cropYields(new CropYield()).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTaskAgri, simulationTaskAPV, simulationTaskPV
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Json Processing Exception") {});
////		
////		simulationStatusDto.setStatus("Resume");
////		
//////		assertThrows(JsonProcessingException.class, () -> {
////			simulationServiceImpl.updateStatus(simulationStatusDto);
//////		});
////	}
////	
////	@Test
////	public void updateStatus_resumeTasks_notAPV_throwJsonProcessingError() throws JsonProcessingException
////	{
////		Simulation simulation = new Simulation();
////		simulation.setId(1l);
////		simulation.setSimulationType(SimulationType.ONLY_AGRI);
////		simulation.setWithTracking(Boolean.TRUE);
////		
////		SimulationTask simulationTask1 = SimulationTask.builder().id(1l).pvStatus(Status.NA).agriStatus(Status.PAUSED).simulation(simulation).build();
////		SimulationTask simulationTask2 = SimulationTask.builder().id(2l).pvStatus(Status.NA).agriStatus(Status.PAUSED).simulation(simulation).build();
////		
////		List<SimulationTask> simulationTasks = List.of(
////				simulationTask1, simulationTask2
////				);
////		simulation.setSimulationTasks(simulationTasks);
////		
////		when(simulationRepository.findById(anyLong())).thenReturn(Optional.of(simulation));
////		when(simulationTaskRepository.getBySimulationAndStatusIn(any(), eq(
////				List.of(Status.PAUSED.toString())))).thenReturn(simulationTasks);
////		when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Json Processing Exception") {});
////		
////		simulationStatusDto.setStatus("Resume");
////		
////		simulationServiceImpl.updateStatus(simulationStatusDto);
////	}
//	
//	
//	
//	@Test
//	public void getSimulationResult_simulationDoesNotExist()
//	{
//		when(simulationRepository.findById(any())).thenReturn(Optional.empty());
//		
//		InvalidRequestBodyArgumentException exception = assertThrows(InvalidRequestBodyArgumentException.class, () -> {
//			simulationServiceImpl.getSimulationResult(1l);
//		});
//		
//		assertTrue(exception.getMessage().contains("Simulation with id: 1 does not exist"));
//	}
//	
//	@Test
//	public void getSimulationResult_simulationExists()
//	{
//		Simulation simulation = new Simulation();
//		
//		SimulationTask simulationTask1 = new SimulationTask();
//		PVYield pvYield = new PVYield();
//		pvYield.setPvYield(12.12f);
//		pvYield.setFrontGain(34.56f);
//		pvYield.setRearGain(67.89f);
//		pvYield.setAlbedo(89.0f);
//		simulationTask1.setPvYields(pvYield);
//		simulationTask1.setDate(LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.NOON));
//		
//		SimulationTask simulationTask2 = new SimulationTask();
//		simulationTask2.setDate(LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.MAX));
//		
//		SimulationTask simulationTask3 = new SimulationTask();
//		simulationTask3.setDate(LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.MIDNIGHT));
//		List<CropYield> cropYields = new ArrayList<>();
//		cropYields.add(new CropYield(null,simulationTask3,1,"sorghum",, , 23.45f, 1, null));
//		cropYields.add(new CropYield(null, simulationTask3, "tomato", 78.45f, 1, null));
//		cropYields.add(new CropYield(null, simulationTask3, "sorghum", 6.55f, 2, null));
//		simulationTask3.setCropYields(cropYields);
//		
//		simulation.setSimulationTasks(List.of(simulationTask1, simulationTask2, simulationTask3));
//		
//		when(simulationRepository.findById(any())).thenReturn(Optional.of(simulation));
//		
//		Map<String, List<SimulationTaskDto>> result = simulationServiceImpl.getSimulationResult(1l);
//		
//		assertNotNull(result);
//		
//		List<SimulationTaskDto> simulationTaskDtos = result.get("data");
//		
//		assertNotNull(simulationTaskDtos);
//		assertEquals(simulationTaskDtos.size(), 3);
//		assertEquals(simulationTaskDtos.get(0).getDateTime().toString(), "2024-01-01T00:00");
//		assertNull(simulationTaskDtos.get(0).getPvYield());
//		assertNull(simulationTaskDtos.get(0).getFrontGain());
//		assertNull(simulationTaskDtos.get(0).getRearGain());
//		assertNull(simulationTaskDtos.get(0).getAlbedo());
//		assertEquals(simulationTaskDtos.get(0).getCarbonAssimilation().get("sorghum"), 30.0, 0.001);
//		assertEquals(simulationTaskDtos.get(0).getCarbonAssimilation().get("tomato"), 78.45, 0.001);
//		assertEquals(simulationTaskDtos.get(1).getDateTime().toString(), "2024-01-01T12:00");
//		assertEquals(simulationTaskDtos.get(1).getPvYield(), 12.12, 0.001);
//		assertEquals(simulationTaskDtos.get(1).getFrontGain(), 34.56, 0.001);
//		assertEquals(simulationTaskDtos.get(1).getRearGain(), 67.89, 0.001);
//		assertEquals(simulationTaskDtos.get(1).getAlbedo(), 89.0, 0.001);
//		assertNull(simulationTaskDtos.get(1).getCarbonAssimilation());
//		assertEquals(simulationTaskDtos.get(2).getDateTime(), LocalDateTime.of(LocalDate.of(2024, 1, 1), LocalTime.MAX));
//		assertNull(simulationTaskDtos.get(2).getPvYield());
//		assertNull(simulationTaskDtos.get(2).getFrontGain());
//		assertNull(simulationTaskDtos.get(2).getRearGain());
//		assertNull(simulationTaskDtos.get(2).getAlbedo());
//		assertNull(simulationTaskDtos.get(2).getCarbonAssimilation());
//		
//	}
//}
