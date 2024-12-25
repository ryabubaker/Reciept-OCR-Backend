// src/main/java/com/example/receipt_backend/websocket/WebSocketNotificationService.java
package com.example.receipt_backend.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts a general message to all subscribers.
     *
     * @param message The message to broadcast.
     */
    public void broadcastMessage(String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
        log.info("Broadcasted message to /topic/notifications: {}", message);
    }

    /**
     * Asynchronously broadcasts a message to all subscribers.
     *
     * @param message The message to broadcast.
     */
    public void broadcastMessageAsync(String message) {
        // Using a separate thread to avoid blocking
        new Thread(() -> broadcastMessage(message)).start();
    }
}
