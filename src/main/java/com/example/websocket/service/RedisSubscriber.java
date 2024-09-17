package com.example.websocket.service;

import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriber {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RedisSubscriber(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void handleMessage(String message) {
        simpMessagingTemplate.convertAndSend("/topic/messages",message);
    }
}
