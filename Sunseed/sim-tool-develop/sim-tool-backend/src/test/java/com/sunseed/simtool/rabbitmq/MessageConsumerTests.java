//package com.sunseed.simtool.rabbitmq;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.rabbitmq.client.Channel;
//import com.sunseed.simtool.constant.SceneType;
//import com.sunseed.simtool.constant.SimulationType;
//import com.sunseed.simtool.constant.Status;
//import com.sunseed.simtool.entity.CropYield;
//import com.sunseed.simtool.entity.PVYield;
//import com.sunseed.simtool.entity.Simulation;
//import com.sunseed.simtool.entity.SimulationTask;
//import com.sunseed.simtool.entity.TrackingTiltAngle;
//import com.sunseed.simtool.helper.SimulationHelper;
//import com.sunseed.simtool.repository.CropYieldRepository;
//import com.sunseed.simtool.repository.PVYieldRepository;
//import com.sunseed.simtool.repository.SimulationRepository;
//import com.sunseed.simtool.repository.SimulationTaskRepository;
//import com.sunseed.simtool.repository.TrackingTiltAngleRepository;
//
//@ExtendWith(MockitoExtension.class)
//public class MessageConsumerTests {
//
//	@Mock
//	private SimulationHelper simulationHelper;
//	@Mock
//	private SimulationRepository simulationRepository;
//	@Mock
//	private SimulationTaskRepository simulationTaskRepository;
//	@Mock
//	private CropYieldRepository cropYieldRepository;
//	@Mock
//	private PVYieldRepository pvYieldRepository;
//	@Mock
//	private TrackingTiltAngleRepository trackingTiltAngleRepository;
//	@Mock
//	private MessageProducer messageProducer;
//	@Mock
//	private Channel channel;
//	
//	@Spy
//	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//	
//	@InjectMocks
//	private MessageConsumer messageConsumer;
//	
//	@Test
//	public void test_receiveMessageFromQueuePV_runSimulationFailed() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setPvStatus(Status.QUEUED);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.BIFACIAL), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.BIFACIAL))).thenReturn(false);
//		
//		messageConsumer.receiveMessageFromQueuePV(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(0)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueuePV_resourceAvailableCheck() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setPvStatus(Status.QUEUED);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.BIFACIAL), any(), any())).thenReturn(false, true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.BIFACIAL))).thenReturn(false);
//		
//		messageConsumer.receiveMessageFromQueuePV(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(0)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueuePV_runSimulationSuccess_statusUpdationCheck_noSuchSimulationTask() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setPvStatus(Status.QUEUED);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.BIFACIAL), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.BIFACIAL))).thenReturn(true);
//		when(simulationTaskRepository.findById(eq(1l))).thenReturn(Optional.empty());
//		
//		messageConsumer.receiveMessageFromQueuePV(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(1)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueuePV_runSimulationSuccess_statusUpdationCheck() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setPvStatus(Status.QUEUED);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.BIFACIAL), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.BIFACIAL))).thenReturn(true);
//		when(simulationTaskRepository.findById(eq(1l))).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromQueuePV(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).findById(1l);
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.RUNNING);
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueueAgri_runSimulationFailed() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.QUEUED);
//		simulationTask.setPvStatus(Status.NA);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.HELIOS), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.HELIOS))).thenReturn(false);
//		
//		messageConsumer.receiveMessageFromQueueAgri(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(0)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueueAgri_resourceAvailableCheck() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.QUEUED);
//		simulationTask.setPvStatus(Status.NA);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.HELIOS), any(), any())).thenReturn(false, true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.HELIOS))).thenReturn(false);
//		
//		messageConsumer.receiveMessageFromQueueAgri(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(0)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueueAgri_runSimulationSuccess_statusUpdationCheck_noSuchSimulationTask() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.QUEUED);
//		simulationTask.setPvStatus(Status.NA);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.HELIOS), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.HELIOS))).thenReturn(true);
//		when(simulationTaskRepository.findById(eq(1l))).thenReturn(Optional.empty());
//		
//		messageConsumer.receiveMessageFromQueueAgri(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		verify(simulationTaskRepository, times(1)).findById(1l);
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromQueueAgri_runSimulationSuccess_statusUpdationCheck() throws JsonMappingException, JsonProcessingException, InterruptedException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setAgriStatus(Status.QUEUED);
//		simulationTask.setPvStatus(Status.NA);
//		
//		when(simulationHelper.isResourceAvailable(eq(SceneType.HELIOS), any(), any())).thenReturn(true);
////		when(simulationHelper.runSimulation(any(SimulationTask.class), any(Channel.class), eq(0l), eq(SceneType.HELIOS))).thenReturn(true);
//		when(simulationTaskRepository.findById(eq(1l))).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromQueueAgri(objectMapper.writeValueAsString(simulationTask), channel, 0);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).findById(1l);
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.RUNNING);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_taskDoesNotExist() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of("result","", "id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.empty());
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		verify(simulationTaskRepository, times(0)).save(any());
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacial_noResult() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of("id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		verify(simulationRepository, times(0)).save(any());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 0l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingPvYield() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n front_gain: 8000\n rear_gain: 2000\n albedo: 0.6\n tilt_angle: 45\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingFrontGain() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n tilt_angle: 45\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingRearGain() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n front_gain: 8000\n albedo: 0.6\n tilt_angle: 45\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingAlbedo() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n front_gain: 8000\n rear_gain: 2000\n tilt_angle: 45\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingFileUrl() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n tilt_angle: 45\n front_gain: 8000\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialFailed_missingTiltAngle() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n front_gain: 8000\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.TRUE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialSuccess_onlyPV_withoutTiltAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n front_gain: 8000\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.FALSE);
//		simulation.setTaskCount(12l);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<PVYield> pvYieldCaptor = ArgumentCaptor.forClass(PVYield.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(pvYieldRepository, times(1)).save(pvYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(trackingTiltAngleRepository, times(0)).save(any());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		PVYield capturedPvYield = pvYieldCaptor.getValue();
//		
//		assertEquals(capturedPvYield.getPvYield(), 10000, 0.001);
//		assertEquals(capturedPvYield.getFrontGain(), 8000, 0.001);
//		assertEquals(capturedPvYield.getRearGain(), 2000, 0.001);
//		assertEquals(capturedPvYield.getAlbedo(), 0.6, 0.001);
//		assertEquals(capturedPvYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.BIFACIAL);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 1l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialSuccess_onlyPV_withTiltAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n front_gain: 8000\n file_url: https://somelink\n tilt_angle: 45\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_PV);
//		simulation.setWithTracking(Boolean.TRUE);
//		simulation.setTaskCount(12l);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<PVYield> pvYieldCaptor = ArgumentCaptor.forClass(PVYield.class);
//		ArgumentCaptor<TrackingTiltAngle> trackingTiltAngleCaptor = ArgumentCaptor.forClass(TrackingTiltAngle.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(pvYieldRepository, times(1)).save(pvYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(trackingTiltAngleRepository, times(1)).save(trackingTiltAngleCaptor.capture());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		PVYield capturedPvYield = pvYieldCaptor.getValue();
//		TrackingTiltAngle capturedTrackingTiltAngle = trackingTiltAngleCaptor.getValue();
//		
//		assertEquals(capturedTrackingTiltAngle.getTiltAngle(), 45.0, 0.001);
//		assertEquals(capturedTrackingTiltAngle.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedPvYield.getPvYield(), 10000, 0.001);
//		assertEquals(capturedPvYield.getFrontGain(), 8000, 0.001);
//		assertEquals(capturedPvYield.getRearGain(), 2000, 0.001);
//		assertEquals(capturedPvYield.getAlbedo(), 0.6, 0.001);
//		assertEquals(capturedPvYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.BIFACIAL);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 1l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_helios_noResult() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of("id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_AGRI);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		verify(simulationRepository, times(0)).save(any());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.FAILED);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 0l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosFailed_missingCarbonAssimilation() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ntemperature: 45.0\nfile_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_AGRI);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.FAILED);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosFailed_missingTemperature() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ncarbon_assimilation: 23.45\nfile_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_AGRI);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.FAILED);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosFailed_missingFileUrl() throws JsonMappingException, JsonProcessingException
//	{
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ntemperature: 45.0\ncarbon_assimilation: 456.66\nSome Logs",
//				"id", 1l, "completedAt", LocalDateTime.now().toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_AGRI);
//		simulation.setWithTracking(Boolean.FALSE);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> argumentCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		
//		verify(simulationTaskRepository, times(1)).save(argumentCaptor.capture());
//		
//		SimulationTask capturedSimulationTask = argumentCaptor.getValue();
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.FAILED);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosSuccess_onlyAgri() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ntemperature: 45.0\ncarbon_assimilation: 456.66\nfile_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.ONLY_AGRI);
//		simulation.setWithTracking(Boolean.FALSE);
//		simulation.setTaskCount(12l);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<CropYield> cropYieldCaptor = ArgumentCaptor.forClass(CropYield.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(cropYieldRepository, times(1)).save(cropYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		CropYield capturedCropYield = cropYieldCaptor.getValue();
//		
//		assertEquals(capturedCropYield.getCarbonAssimilation(), 456.66, 0.001);
//		assertEquals(capturedCropYield.getTemperature(), 45.0, 0.001);
//		assertEquals(capturedCropYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.HELIOS);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 1l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialSuccess_APV_withoutTrackingAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n front_gain: 8000\n file_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.NA);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.APV);
//		simulation.setWithTracking(Boolean.FALSE);
//		simulation.setTaskCount(12l);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<PVYield> pvYieldCaptor = ArgumentCaptor.forClass(PVYield.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(pvYieldRepository, times(1)).save(pvYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(trackingTiltAngleRepository, times(0)).save(any());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		PVYield capturedPvYield = pvYieldCaptor.getValue();
//		
//		assertEquals(capturedPvYield.getPvYield(), 10000, 0.001);
//		assertEquals(capturedPvYield.getFrontGain(), 8000, 0.001);
//		assertEquals(capturedPvYield.getRearGain(), 2000, 0.001);
//		assertEquals(capturedPvYield.getAlbedo(), 0.6, 0.001);
//		assertEquals(capturedPvYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.BIFACIAL);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 0l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosSuccess_APV_withoutTrackingAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ntemperature: 45.0\ncarbon_assimilation: 456.66\nfile_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.NA);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.APV);
//		simulation.setWithTracking(Boolean.FALSE);
//		simulation.setTaskCount(12l);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<CropYield> cropYieldCaptor = ArgumentCaptor.forClass(CropYield.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(cropYieldRepository, times(1)).save(cropYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		CropYield capturedCropYield = cropYieldCaptor.getValue();
//		
//		assertEquals(capturedCropYield.getCarbonAssimilation(), 456.66, 0.001);
//		assertEquals(capturedCropYield.getTemperature(), 45.0, 0.001);
//		assertEquals(capturedCropYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.NA);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.HELIOS);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 0l);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_heliosSuccess_APV_withTrackingAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result","Some Logs\ntemperature: 45.0\ncarbon_assimilation: 456.66\nfile_url: https://somelink\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.HELIOS));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.PENDING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setSimulationType(SimulationType.APV);
//		simulation.setWithTracking(Boolean.TRUE);
//		simulation.setTaskCount(1l);
//
//		simulationTask.setSimulation(simulation);
//		
//		SimulationTask savedSimulationTask = new SimulationTask();
//		savedSimulationTask.setId(1l);
//		savedSimulationTask.setPvStatus(Status.SUCCESS);
//		savedSimulationTask.setAgriStatus(Status.SUCCESS);
//		savedSimulationTask.setScenes(new ArrayList<>());
//		savedSimulationTask.setSimulation(simulation);
//		
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(savedSimulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<CropYield> cropYieldCaptor = ArgumentCaptor.forClass(CropYield.class);
//		
//		verify(simulationTaskRepository, times(1)).save(simulationTaskCaptor.capture());
//		verify(cropYieldRepository, times(1)).save(cropYieldCaptor.capture());
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//		verify(simulationRepository, times(1)).save(any());
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getValue();
//		CropYield capturedCropYield = cropYieldCaptor.getValue();
//		
//		assertEquals(capturedCropYield.getCarbonAssimilation(), 456.66, 0.001);
//		assertEquals(capturedCropYield.getTemperature(), 45.0, 0.001);
//		assertEquals(capturedCropYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.PENDING);
//		assertEquals(capturedSimulationTask.getAgriStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.HELIOS);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 1l);
//		assertEquals(capturedSimulationTask.getSimulation().getStatus(), Status.SUCCESS);
//	}
//	
//	@Test
//	public void test_receiveMessageFromResultQueue_bifacialSuccess_APV_withTrackingAngle() throws JsonMappingException, JsonProcessingException
//	{
//		LocalDateTime completedAt = LocalDateTime.now();
//		String message = objectMapper.writeValueAsString(Map.of(
//				"result", "Some Logs\n pv_yield: 10000\n rear_gain: 2000\n albedo: 0.6\n front_gain: 8000\n file_url: https://somelink\n tilt_angle: 45\nSome Logs",
//				"id", 1l, "completedAt", completedAt.toString(), "type", SceneType.BIFACIAL));
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.PENDING);
//		simulationTask.setScenes(new ArrayList<>());
//		
//		Simulation simulation = new Simulation();
//		simulation.setTaskCount(12l);
//		simulation.setSimulationType(SimulationType.APV);
//		simulation.setWithTracking(Boolean.TRUE);
//		simulation.setStatus(Status.QUEUED);
//
//		simulationTask.setSimulation(simulation);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		when(simulationTaskRepository.save(any())).thenReturn(simulationTask);
//		
//		messageConsumer.receiveMessageFromResultQueue(message);
//		
//		ArgumentCaptor<SimulationTask> simulationTaskCaptor = ArgumentCaptor.forClass(SimulationTask.class);
//		ArgumentCaptor<PVYield> pvYieldCaptor = ArgumentCaptor.forClass(PVYield.class);
//		ArgumentCaptor<TrackingTiltAngle> trackingTiltAngleCaptor = ArgumentCaptor.forClass(TrackingTiltAngle.class);
//		
//		verify(simulationTaskRepository, times(2)).save(simulationTaskCaptor.capture());
//		verify(pvYieldRepository, times(1)).save(pvYieldCaptor.capture());
//		verify(messageProducer, times(1)).sendMessage(any(), any(), any());
//		verify(trackingTiltAngleRepository, times(1)).save(trackingTiltAngleCaptor.capture());
//		verify(simulationRepository, times(1)).save(any());
//		
//		TrackingTiltAngle capturedTrackingTiltAngle = trackingTiltAngleCaptor.getValue();
//		
//		assertEquals(capturedTrackingTiltAngle.getTiltAngle(), 45.0, 0.001);
//		assertEquals(capturedTrackingTiltAngle.getSimulationTask().getId(), 1l);
//		
//		SimulationTask capturedSimulationTask = simulationTaskCaptor.getAllValues().get(0);
//		SimulationTask queuedSimulationTask = simulationTaskCaptor.getAllValues().get(1);
//		PVYield capturedPvYield = pvYieldCaptor.getValue();
//		
//		assertEquals(capturedPvYield.getPvYield(), 10000, 0.001);
//		assertEquals(capturedPvYield.getFrontGain(), 8000, 0.001);
//		assertEquals(capturedPvYield.getRearGain(), 2000, 0.001);
//		assertEquals(capturedPvYield.getAlbedo(), 0.6, 0.001);
//		assertEquals(capturedPvYield.getSimulationTask().getId(), 1l);
//		
//		assertEquals(capturedSimulationTask.getPvStatus(), Status.SUCCESS);
//		assertEquals(capturedSimulationTask.getCompletedAt(), completedAt);
//		assertEquals(capturedSimulationTask.getScenes().size(), 1);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getType(), SceneType.BIFACIAL);
//		assertEquals(capturedSimulationTask.getScenes().get(0).getUrl(), "https://somelink");
//		assertEquals(capturedSimulationTask.getScenes().get(0).getSimulationTask().getId(), 1l);
//		
//		assertEquals(queuedSimulationTask.getAgriStatus(), Status.QUEUED);
//
//		assertEquals(capturedSimulationTask.getSimulation().getCompletedTaskCount(), 0l);
//		assertEquals(capturedSimulationTask.getSimulation().getStatus(), Status.QUEUED);
//	}
//}
