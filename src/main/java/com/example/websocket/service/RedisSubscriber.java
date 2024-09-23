package com.example.websocket.service;

import com.example.websocket.dto.ChatRoomDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisSubscriber {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void handleMessage(String message) throws JsonProcessingException {
        ChatRoomDTO chatRoomDTO = objectMapper.readValue(message, ChatRoomDTO.class);
        String roomId = chatRoomDTO.getRoomId();
        String browserId = chatRoomDTO.getBrowserId();
        simpMessagingTemplate.convertAndSend("/topic/messages/" + roomId+browserId, chatRoomDTO);
    }
}
