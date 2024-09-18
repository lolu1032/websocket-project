package com.example.websocket.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// 클라이언트나 다른 시스템에서 Redis 서버로 메시지를 발행하는 역할을 합니다.
@Service
public class RedisPublisher {
    //  Redis 데이터베이스에 데이터를 저장하고, 데이터를 조회하는 작업을 처리한다
    private final RedisTemplate<String,String> redisTemplate;

    public RedisPublisher(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    // 특정 채널에 메시지를 발행하는 역할이다.
    public void publish(String roomId,String message) {
        redisTemplate.convertAndSend("chat-room-"+roomId,message);
    }
}
