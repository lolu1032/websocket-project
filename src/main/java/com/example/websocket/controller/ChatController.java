package com.example.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("message")
    @SendTo("/topic/messages")
    public String chat(String message) {
        return message;
    }
}
