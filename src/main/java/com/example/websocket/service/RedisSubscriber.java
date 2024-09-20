package com.example.websocket.service;

import com.example.websocket.dto.ChatRoomDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// Redis 서버에서 메시지를 수신하고, 수신한 메시지를 클라이언트에게 전달하는 역할을 합니다.
@Service
@Slf4j
public class RedisSubscriber {
    // 웹소캣에서 지원해주는 클래스이며 클라이언트와 서버 간의 메시지 송수신을 도와준다
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void handleMessage(String message) throws JsonProcessingException {
        ChatRoomDTO chatRoomDTO = objectMapper.readValue(message, ChatRoomDTO.class);
        String roomId = chatRoomDTO.getRoomId();
        simpMessagingTemplate.convertAndSend("/topic/messages/"+roomId,message);
    }
}
