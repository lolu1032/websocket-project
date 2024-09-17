package com.example.websocket.controller;

import com.example.websocket.service.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;

    public ChatController(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    @MessageMapping("message")
    public void chat(String message) {
        redisPublisher.publish("chat-room",message);
    }
}
