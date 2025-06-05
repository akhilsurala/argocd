package com.sunseed.config;

import com.sunseed.repository.NotificationRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final NotificationRepository notificationRepository;

    // Map to store WebSocket sessions using username as key
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract username from query parameters
        String username = getUsernameFromSession(session);
        System.out.println("username in after connection:" + username);
        if (username != null) {
            sessions.put(username, session);
            System.out.println("WebSocket connection established for username: " + username);
            this.sendNotificationCountToUser(username);
        } else {
            System.out.println("No username found in session. Closing WebSocket connection.");
            session.close();
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from clients if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Extract username from session and remove it
        String username = getUsernameFromSession(session);
        if (username != null) {
            sessions.remove(username);
            System.out.println("WebSocket connection closed for username: " + username);

            // Send a periodic ping message to keep the connection alive
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new PingMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Failed to send ping: " + e.getMessage());
                }
            }, 0, 30, TimeUnit.SECONDS); // Ping every 30 seconds

        } else {
            session.close();
        }

    }

    // send notification to client
    public void sendNotificationToUser(String username, String message) {
        WebSocketSession session = sessions.get(username);

        if (session != null) {
            synchronized (session) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                    } else {
                        System.out.println("WebSocket session is already closed for username: " + username);
                    }
                } catch (ClosedChannelException e) {
                    System.out.println("Attempted to send a message to a closed channel for username: " + username);
                } catch (IOException e) {
                    System.out.println("IO Exception while sending message to username: " + username + " due to: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error while sending message to username: " + username + " due to: " + e.getMessage());
                }
            }
        } else {
            System.out.println("No active WebSocket session found for username: " + username);
        }
    }


    private String getUsernameFromSession(WebSocketSession session) {
        // Extract username from query parameters or session attributes
        String query = session.getUri().getQuery();
        System.out.println("query is:" + query);
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "username".equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    @PreDestroy
    public void cleanup() {
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.GOING_AWAY);
                }
            } catch (IOException e) {
                System.out.println("Error closing WebSocket session: " + e.getMessage());
            }
        });
    }


// helper method to send count of notification to user
    public void sendNotificationCountToUser(String receiverEmail) {
        Long countOfNotification = notificationRepository.countByDestinationId(receiverEmail);
        this.sendNotificationToUser(receiverEmail, String.valueOf(countOfNotification));
    }
}

