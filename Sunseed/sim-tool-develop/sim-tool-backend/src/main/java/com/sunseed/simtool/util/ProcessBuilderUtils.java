package com.sunseed.simtool.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sunseed.simtool.exception.TaskTimeExceededException;
import com.sunseed.simtool.model.BaseServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessBuilderUtils {
	
	private static final int MAX_DISPLAY_NUMBER = 1000;
	private static final AtomicInteger DISPLAY_COUNTER = new AtomicInteger(3);
	private static final ConcurrentHashMap<Integer, String> ACTIVE_DISPLAYS = new ConcurrentHashMap<>();
	private static final ThreadLocal<Integer> THREAD_DISPLAY = ThreadLocal.withInitial(() -> null);
	
	private static final Object DISPLAY_LOCK = new Object();
	private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();
	
	private static Session createSession(BaseServer baseServer) {
		Session session = null;
		JSch jsch = new JSch();

		try {
			if (baseServer.getIdentityFile() != null && !baseServer.getIdentityFile().isBlank())
				jsch.addIdentity(baseServer.getIdentityFile());
			session = jsch.getSession(baseServer.getUsername(), baseServer.getHost(), 22);
			session.setPassword(baseServer.getPassword());
			// Set configurations
			session.setConfig("StrictHostKeyChecking", "no");
			try {
				session.connect();
			} catch (JSchException e) {
				log.error("Error connecting to host {}: {}", baseServer.getHost(), e.getMessage());
				throw e;
			}
			log.info("Session established...");
		} catch (Exception e) {
			log.info("Error in create session : " + e.getMessage());
		}

		return session;
	}
	
	public static String runCommandWithXvfb(BaseServer server, String command, Integer simulationMaxWaitTime) throws Exception {
        int displayNumber;
        synchronized (DISPLAY_LOCK) {
            displayNumber = DISPLAY_COUNTER.getAndIncrement();
            // Ensure the display number wraps around
            if (displayNumber >= MAX_DISPLAY_NUMBER) {
                DISPLAY_COUNTER.set(3); // Reset back to the start number if exceeds max
            }
            while (ACTIVE_DISPLAYS.containsKey(displayNumber)) {
                displayNumber = DISPLAY_COUNTER.getAndIncrement();
            }
            ACTIVE_DISPLAYS.put(displayNumber, Thread.currentThread().getName());
        }

        String display = ":" + displayNumber;
        THREAD_DISPLAY.set(displayNumber);

        log.info("Thread [{}] allocated display [{}]", Thread.currentThread().getName(), display);

        Session session = createSession(server);
        ChannelExec xvfbChannel = null;
        ChannelExec commandChannel = null;
        String responseString = null;

        try {
            // Start XVFB on the unique display
            log.info("Starting XVFB on display {}...", display);
            xvfbChannel = (ChannelExec) session.openChannel("exec");
            xvfbChannel.setCommand("Xvfb " + display + " -screen 0 1024x768x16");
            xvfbChannel.connect();
            Thread.sleep(5000); // Allow XVFB to initialize

            // Run the actual command with the DISPLAY set
            log.info("Executing command with XVFB on display {}...", display);
            commandChannel = (ChannelExec) session.openChannel("exec");
            commandChannel.setCommand("export DISPLAY=" + display + " && " + command);

            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            commandChannel.setOutputStream(responseStream);
            commandChannel.setErrStream(errorStream);
            commandChannel.connect();
            
            LocalDateTime start = LocalDateTime.now();

            log.info("Command executing...");
            while (!commandChannel.isEOF()) {
                if (Thread.currentThread().isInterrupted()) {
                    commandChannel.sendSignal("INT");
                    throw new InterruptedException("Task was cancelled");
                }
                
                if(ChronoUnit.MINUTES.between(start, LocalDateTime.now()) > simulationMaxWaitTime) {
                	commandChannel.sendSignal("INT");
            		throw new TaskTimeExceededException("Task exceeded max time limit");
            	}
            }

            responseString = new String(responseStream.toByteArray());
            log.info("Command executed.");

        } catch (JSchException | IOException | InterruptedException e) {
            log.error("Error during command execution: {}", e.getMessage());
            throw e;
        } finally {
            // Stop XVFB and clean up resources
            stopXvfb(session, display);
            THREAD_DISPLAY.remove();
            synchronized (DISPLAY_LOCK) {
                ACTIVE_DISPLAYS.remove(displayNumber);
            }
            log.info("Thread [{}] released display [{}]", Thread.currentThread().getName(), display);

            if (commandChannel != null) {
                commandChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
                log.info("Session disconnected.");
            }
        }

        return responseString;
    }
	
	private static void stopXvfb(Session session, String display) {
	    try {
	        // Command to find and kill only the specific Xvfb process associated with the given display
	        String command = "ps aux | grep 'Xvfb " + display + " ' | awk '{print $2}' | xargs kill -9";

	        ChannelExec stopXvfbChannel = (ChannelExec) session.openChannel("exec");
	        stopXvfbChannel.setCommand(command);
	        stopXvfbChannel.connect();

	        // Wait for the command to complete
	        while (!stopXvfbChannel.isClosed()) {
	            Thread.sleep(100);
	        }

	        stopXvfbChannel.disconnect();
	        log.info("Stopped XVFB on display {}", display);
	    } catch (Exception e) {
	        log.warn("Failed to stop XVFB on display {}: {}", display, e.getMessage());
	    }
	}
	
	public static Session getSession(BaseServer server) throws Exception {
        String serverKey = server.getHost() + "-" + server.getUsername();
        
        // If session is available and still connected, reuse it
        if (sessionPool.containsKey(serverKey)) {
            Session existingSession = sessionPool.get(serverKey);
            if (existingSession.isConnected()) {
                return existingSession;
            } else {
                // Remove disconnected session
                sessionPool.remove(serverKey);
            }
        }

        // Create new session
        Method createSessionMethod = ProcessBuilderUtils.class.getDeclaredMethod(
                "createSession", BaseServer.class);
        createSessionMethod.setAccessible(true);
        Session newSession = (Session) createSessionMethod.invoke(
                null,server);

        // Store new session in the pool
        if (newSession != null && newSession.isConnected()) {
            sessionPool.put(serverKey, newSession);
        }
        return newSession;
    }

    public static void closeAllSessions() {
        sessionPool.values().forEach(session -> {
            if (session.isConnected()) {
                session.disconnect();
            }
        });
        sessionPool.clear();
    }

	
	public static String runCommand(BaseServer server, String command, Integer simulationMaxWaitTime) throws Exception {
		
		log.info("Establishing session ....");
		Session session = createSession(server);
		
		ChannelExec channel = null;
		String responseString = null;
		try {
	
		log.info("Opening channel ..... ");
		
		channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        
        channel.setOutputStream(responseStream);
        channel.setErrStream(errorStream);
        channel.connect();
	    log.info("Channel Connected.....");
	    log.info("Simulation executing .... ");
	    
	    LocalDateTime start = LocalDateTime.now();
	        
        while (!channel.isEOF()) {
        	
        	if (Thread.currentThread().isInterrupted()) {
        		channel.sendSignal("INT");
                throw new InterruptedException("Task was cancelled");
            }
        	
        	if(ChronoUnit.MINUTES.between(start, LocalDateTime.now()) > simulationMaxWaitTime) {
        		channel.sendSignal("INT");
        		throw new InterruptedException("Task exceeded max time limit");
        	}
        }
        
        responseString = new String(responseStream.toByteArray());
        
        log.info("Simulation executed .... ");
	        
	    } catch (JSchException e) {
	        throw new JSchException("{}", e);
	    }
		finally {
	        if (channel != null) {
	        	log.info("Disconnecting channel ....");
	            channel.disconnect();
	            log.info("Disconnecting session ....");
	            session.disconnect();
	        }
	    }
		return responseString;
	}
	
	public static String runCommandBifacial(BaseServer server, String command, Integer simulationMaxWaitTime) throws Exception {
	    
		log.info("Establishing session ....");
		Session session = createSession(server);

		
	    ChannelExec channel = null;
	    String responseString = "";
	    try {

	        log.info("Opening channel ..... ");
	        
	        channel = (ChannelExec) session.openChannel("exec");
	        channel.setCommand(command);

	        InputStream inputStream = channel.getInputStream();
	        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	        
	        channel.setErrStream(errorStream);
	        channel.connect();
	        log.info("Channel Connected.....");
	        log.info("Simulation executing .... ");
	        LocalDateTime start = LocalDateTime.now();
	        
	        byte[] buffer = new byte[1024];
	        int readCount;
	        boolean isCompleted = false;
	        
	        while (!isCompleted) {
	            if (Thread.currentThread().isInterrupted()) {
	                channel.sendSignal("INT");
	                throw new InterruptedException("Task was cancelled");
	            }
	            
	            while ((readCount = inputStream.read(buffer)) > 0) {
	                String responseChunk = new String(buffer, 0, readCount);
	                responseString += responseChunk;
	                if (responseString.contains("Simulation_execution_completed")) {
	                    isCompleted = true;
	                    break;
	                }
	            }
	            
	            if(ChronoUnit.MINUTES.between(start, LocalDateTime.now()) > simulationMaxWaitTime) {
	            	channel.sendSignal("INT");
	        		throw new InterruptedException("Task exceeded max time limit");
	            }
	        }
	        
//	        if (!new String(errorStream.toByteArray()).isBlank()) {
//	            log.debug("ERROR: " + new String(errorStream.toByteArray()));
//	        }
	        
	        log.info("Simulation executed .... ");
	        
	    } catch (JSchException | IOException e) {
	        throw new Exception("Error during command execution", e);
	    } finally {
	        if (channel != null) {
	            log.info("Disconnecting channel ....");
	            channel.disconnect();
	            log.info("Disconnecting session ....");
	            session.disconnect();
	        }
	    }
	    return responseString;
	}

}
