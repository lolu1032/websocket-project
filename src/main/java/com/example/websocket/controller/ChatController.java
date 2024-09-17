package com.example.websocket.controller;

import com.example.websocket.service.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    // 특정 채널에 메시지를 발행하는걸 불러와줬다.
    private final RedisPublisher redisPublisher;

    public ChatController(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }
    // 클라에서 받은 메시지를 publish을 통해 chat-room 특정 채널에 message를 보낸다
    @MessageMapping("message")
    public void chat(String message) {
        redisPublisher.publish("chat-room",message);
    }
}
