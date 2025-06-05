//package com.sunseed.simtool.util;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStream;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.jcraft.jsch.ChannelExec;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.Session;
//
//@ExtendWith(MockitoExtension.class)
//public class ProcessBuilderUtilsTest {
//
//	@Mock
//	private Session session;
//
//	@Mock
//	private ChannelExec channel;
//
//	@InjectMocks
//	private ProcessBuilderUtils processBuilderUtils;
//
//	@BeforeEach
//	    public void setUp() throws JSchException {
//	        when(session.openChannel("exec")).thenReturn(channel);
//	    }
//
//	@Test
//	public void testRunCommandSuccess() throws Exception {
//		String command = "echo 'Hello, World!'";
//
//        when(channel.isConnected()).thenReturn(true, false);
//
//        doAnswer(invocation -> {
//            ((OutputStream) invocation.getArguments()[0]).write("Hello, World!".getBytes());
//            return null;
//        }).when(channel).setOutputStream(any(OutputStream.class));
//
//        String result = ProcessBuilderUtils.runCommand(session, command);
//
//        verify(channel).setCommand(command);
//        verify(channel).connect();
//        verify(channel).disconnect();
//
//        assertEquals("Hello, World!", result);
//	}
//
//	@Test
//	public void testRunCommandInterrupted() throws Exception {
//		String command = "long-running-command";
//		when(channel.isConnected()).thenReturn(true);
//		doAnswer(invocation -> {
//			Thread.currentThread().interrupt();
//			return null;
//		}).when(channel).connect();
//
//		assertThrows(InterruptedException.class, () -> ProcessBuilderUtils.runCommand(session, command));
//
//		verify(channel).setCommand(command);
//		verify(channel).connect();
//		verify(channel).disconnect();
//	}
//
//	@Test
//	public void testRunCommandJSchException() throws Exception {
//		String command = "invalid-command";
//		when(session.openChannel("exec")).thenThrow(new JSchException("Session error"));
//
//		assertThrows(JSchException.class, () -> ProcessBuilderUtils.runCommand(session, command));
//	}
//}
