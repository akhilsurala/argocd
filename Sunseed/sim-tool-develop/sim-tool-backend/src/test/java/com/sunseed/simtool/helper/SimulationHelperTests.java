//package com.sunseed.simtool.helper;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.jcraft.jsch.JSchException;
//import com.rabbitmq.client.Channel;
//import com.sunseed.simtool.config.SimulationServer;
//import com.sunseed.simtool.constant.SceneType;
//import com.sunseed.simtool.constant.Status;
//import com.sunseed.simtool.entity.Simulation;
//import com.sunseed.simtool.entity.SimulationTask;
//import com.sunseed.simtool.rabbitmq.MessageProducer;
//import com.sunseed.simtool.repository.SimulationTaskRepository;
//import com.sunseed.simtool.util.ProcessBuilderUtils;
//
//@ExtendWith(MockitoExtension.class)
//public class SimulationHelperTests {
//
//	@Spy
//	private List<SimulationServer> simulationServers = List.of(
//			new SimulationServer("Server-1", "Bifacial", 4, 0, null, null, null),
//			new SimulationServer("Server-2", "Bifacial", 8, 8, null, null, null),
//			new SimulationServer("Server-3", "Helios", 4, 4, null, null, null),
//			new SimulationServer("Server-4", "Helios", 2, 2, null, null, null)
//			);
//	
//	@Mock
//	private SimulationTaskRepository simulationTaskRepository;
//	@Mock
//	private Channel channel;
//	@Mock
//	private MessageProducer messageProducer;
//	@Mock
//	private Future<?> future;
//	
//	@Spy
//	private ExecutorService executorService = Executors.newFixedThreadPool(4);
//	@Spy
//	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//	@Spy
//	private ConcurrentHashMap<String, Future<?>> tasksFuture = new ConcurrentHashMap<>();
//	
//	private MockedStatic<ProcessBuilderUtils> processBuilderMockedStatic;
//	
//	@InjectMocks
//	private SimulationHelper simulationHelper;
//	
//	@BeforeEach
//	private void setup(){
//		processBuilderMockedStatic = mockStatic(ProcessBuilderUtils.class);
//	}
//	
//	@AfterEach
//	private void tearDown()
//	{
//		processBuilderMockedStatic.close();
//	}
//	
//	@Test
//	public void test_isResourceAvailable() {
//		assertTrue(simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 3, 0));
//		assertTrue(simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 7, 0));
//		assertFalse(simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 7, 11));
//		assertFalse(simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 10, 0));
//		assertFalse(simulationHelper.isResourceAvailable(SceneType.BIFACIAL, 14, 0));
//		
//		assertTrue(simulationHelper.isResourceAvailable(SceneType.HELIOS, 1, 1));
//		assertTrue(simulationHelper.isResourceAvailable(SceneType.HELIOS, 3, 3));
//		assertFalse(simulationHelper.isResourceAvailable(SceneType.HELIOS, 5, 5));
//		assertFalse(simulationHelper.isResourceAvailable(SceneType.HELIOS, 7, 7));
//	}
//	
//	@Test
//	public void test_acquireResource()
//	{
//		SimulationServer server1 = simulationHelper.acquireResource(SceneType.BIFACIAL, 3, 0);
//		assertEquals(server1.getServerName(), "Server-1");
//		
//		SimulationServer server2 = simulationHelper.acquireResource(SceneType.BIFACIAL, 7, 0);
//		assertEquals(server2.getServerName(), "Server-2");
//		
//		assertNull(simulationHelper.acquireResource(SceneType.BIFACIAL, 3, 0));
//		
//		server1.getCpu().release(1);
//		
//		assertEquals(simulationHelper.acquireResource(SceneType.BIFACIAL, 2, 0).getServerName(), "Server-1");
//		
//		server1.getCpu().release(4);
//		server2.getCpu().release(7);
//		
//		
//		
//		SimulationServer server3 = simulationHelper.acquireResource(SceneType.HELIOS, 3, 3);
//		assertEquals(server3.getServerName(), "Server-3");
//		
//		SimulationServer server4 = simulationHelper.acquireResource(SceneType.HELIOS, 2, 2);
//		assertEquals(server4.getServerName(), "Server-4");
//		
//		assertNull(simulationHelper.acquireResource(SceneType.HELIOS, 1, 3));
//		
//		server3.getCpu().release(2);
//		server3.getGpu().release(2);
//		
//		assertEquals(simulationHelper.acquireResource(SceneType.HELIOS, 2, 2).getServerName(), "Server-3");
//		
//		server3.getCpu().release(3);
//		server3.getGpu().release(3);
//		server4.getCpu().release(2);
//		server4.getGpu().release(2);
//	}
//	
//	@Test
//	public void test_runSimulation_taskAlreadyCancelledOrPaused() {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.CANCELLED);
//		simulationTask.setAgriStatus(Status.CANCELLED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
////		assertFalse(simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.BIFACIAL));
//	}
//	
//	@Test
//	public void test_runSimulation_bifacial() {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
////		assertTrue(simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.BIFACIAL));
//	}
//	
//	@Test
//	public void test_runSimulation_helios() {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
////		assertTrue(simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.HELIOS));
//		
//	}
//	
//	@Test
//	public void runBifacialSimulation_success() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenReturn("Some Result");
//		
//		simulationHelper.runBifacialSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(1)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(1)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runBifacialSimulation_success_gotJsonProcessingException() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenReturn("Some Result");
//		doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());
//		
//		simulationHelper.runBifacialSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(1)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runBifacialSimulation_interrupted() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.PAUSED);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new InterruptedException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		simulationHelper.runBifacialSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(1)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runBifacialSimulation_encounteredSomeError_requeue() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new JSchException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		simulationHelper.runBifacialSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(1)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runBifacialSimulation_encounteredSomeError_requeue_gotIOException() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new JSchException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		doThrow(IOException.class).when(channel).basicNack(0, false, true);
//		
//		simulationHelper.runBifacialSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(1)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runHeliosSimulation_success() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenReturn("Some Result");
//		
//		simulationHelper.runHeliosSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(1)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(1)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runHeliosSimulation_success_gotJsonProcessingException() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenReturn("Some Result");
//		doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());
//		
//		simulationHelper.runHeliosSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(1)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runHeliosSimulation_interrupted() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.PAUSED);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new InterruptedException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		simulationHelper.runHeliosSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(1)).basicNack(0, false, false);
//		verify(channel, times(0)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runHeliosSimulation_encounteredSomeError_requeue() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new JSchException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		simulationHelper.runHeliosSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(1)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	@Test
//	public void runHeliosSimulation_encounteredSomeError_requeue_gotIOException() throws IOException
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		simulationTask.setSimulation(new Simulation());
//		
//		processBuilderMockedStatic.when(() -> ProcessBuilderUtils.runCommand(any(), any(), any())).thenThrow(new JSchException());
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		doThrow(IOException.class).when(channel).basicNack(0, false, true);
//		
//		simulationHelper.runHeliosSimulation(simulationTask, channel, 0);
//		
//		verify(channel, times(0)).basicAck(0, false);
//		verify(channel, times(0)).basicNack(0, false, false);
//		verify(channel, times(1)).basicNack(0, false, true);
//		verify(messageProducer, times(0)).sendMessage(any(), any(), any());
//	}
//	
//	/*------------------- Test Acknowledgement --------------------------*/
//
//	@Test
//	public void runSimulation_taskAlreadyCancelledOrPaused_testAcknowledgement() throws IOException {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.CANCELLED);
//		simulationTask.setAgriStatus(Status.CANCELLED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.BIFACIAL);
//		
//		verify(channel, times(1)).basicAck(0, false);
//	}
//	
//	@Test
//	public void runSimulation_taskAlreadyCancelledOrPaused_testAcknowledgement_ackFailed() throws IOException {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.CANCELLED);
//		simulationTask.setAgriStatus(Status.CANCELLED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		doThrow(IOException.class).when(channel).basicAck(0, false);
//		
//		simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.BIFACIAL);
//		
//		verify(channel, times(1)).basicAck(0, false);
//		
//		verify(channel, times(1)).basicNack(0, false, false);
//	}
//	
//	@Test
//	public void runSimulation_taskAlreadyCancelledOrPaused_testAcknowledgement_ackAndNackFailed() throws IOException {
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.CANCELLED);
//		simulationTask.setAgriStatus(Status.CANCELLED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		doThrow(IOException.class).when(channel).basicAck(0, false);
//		doThrow(IOException.class).when(channel).basicNack(0, false, false);
//		
////		assertFalse(simulationHelper.runSimulation(simulationTask, channel, 0, SceneType.BIFACIAL));
//		
//		verify(channel, times(1)).basicAck(0, false);
//		
//		verify(channel, times(1)).basicNack(0, false, false);
//	}
//	
//	/*------------------- Test Acknowledgement --------------------------*/
//	
//	@Test
//	public void test_simulationTaskCancelledOrPaused_taskDoesNotExists()
//	{
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.empty());
//		
//		assertFalse(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.BIFACIAL));
//	}
//	
//	@Test
//	public void test_simulationTaskCancelledOrPaused_taskCancelled()
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.CANCELLED);
//		simulationTask.setAgriStatus(Status.CANCELLED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		assertTrue(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.BIFACIAL));
//		assertTrue(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.HELIOS));
//	}
//	
//	@Test
//	public void test_simulationTaskCancelledOrPaused_taskPaused()
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.PAUSED);
//		simulationTask.setAgriStatus(Status.PAUSED);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		assertTrue(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.BIFACIAL));
//		assertTrue(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.HELIOS));
//	}
//	
//	@Test
//	public void test_simulationTaskCancelledOrPaused_taskNotCancelledOrPaused()
//	{
//		SimulationTask simulationTask = new SimulationTask();
//		simulationTask.setId(1l);
//		simulationTask.setPvStatus(Status.RUNNING);
//		simulationTask.setAgriStatus(Status.RUNNING);
//		
//		when(simulationTaskRepository.findById(any())).thenReturn(Optional.of(simulationTask));
//		
//		assertFalse(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.BIFACIAL));
//		assertFalse(simulationHelper.simulationTaskCancelledOrPausedOrSuccess(1l, SceneType.HELIOS));
//	}
//	
//	@Test
//	public void test_cancelSimulation() {
//		
//		when(future.cancel(true)).thenReturn(true, true, false);
//		
//		assertTrue(simulationHelper.cancelSimulation(2l));
//		tasksFuture.put(1 + "_bifacial", future);
//		tasksFuture.put(1 + "_helios", future);
//		tasksFuture.put(2 + "_bifacial", future);
//		assertEquals(tasksFuture.size(), 3);
//		assertTrue(simulationHelper.cancelSimulation(1l));
//		assertEquals(tasksFuture.size(), 1);
//		assertTrue(simulationHelper.cancelSimulation(2l));
//		assertEquals(tasksFuture.size(), 0);
//	}
//}
