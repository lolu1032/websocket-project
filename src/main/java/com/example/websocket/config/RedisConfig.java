package com.example.websocket.config;

import com.example.websocket.service.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 레디스 서버와의 연결 관리
        container.setConnectionFactory(connectionFactory);
        // Redis의 "chat-room-*" 채널에서 실시간으로 메시지를 수신할 수 있는 설정을 의미
        container.addMessageListener(listenerAdapter,new PatternTopic("chat-room-*"));
        return container;
    }
    // handleMessage는 redisSubscriber 객체에서 reids 메시지를 처리하는 메서드
    // Redis 채널에서 수신한 메시지를 handleMessage 메서드로 전달해서 처리
    @Bean
    MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        return new MessageListenerAdapter(redisSubscriber,"handleMessage");
    }
}
