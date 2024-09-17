package com.example.websocket.service;

import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// Redis 서버에서 메시지를 수신하고, 수신한 메시지를 클라이언트에게 전달하는 역할을 합니다.
@Service
public class RedisSubscriber {
    // 웹소캣에서 지원해주는 클래스이며 클라이언트와 서버 간의 메시지 송수신을 도와준다
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RedisSubscriber(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void handleMessage(String message) {
        simpMessagingTemplate.convertAndSend("/topic/messages",message);
    }
}
