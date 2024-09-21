package com.example.websocket.controller;

import com.example.websocket.dao.ChatRoomRepository;
import com.example.websocket.dto.ChatRoomDTO;
import com.example.websocket.entity.ChatRoom;
import com.example.websocket.service.RedisPublisher;
import com.example.websocket.service.RedisSubscriber;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisSubscriber redisSubscriber;

    public ChatController(RedisPublisher redisPublisher, ChatRoomRepository chatRoomRepository, RedisSubscriber redisSubscriber) {
        this.redisPublisher = redisPublisher;
        this.chatRoomRepository = chatRoomRepository;
        this.redisSubscriber = redisSubscriber;
    }

    @MessageMapping("/createRoom")
    @SendTo("/topic/roomCreated")
    public ChatRoom createRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom = chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatRoomDTO chatRoomDTO) throws JsonProcessingException {
        String roomId = chatRoomDTO.getRoomId();
        String message = chatRoomDTO.getMessage();
        if (chatRoomRepository.existsById(Integer.parseInt(roomId))) {
            redisPublisher.publish(chatRoomDTO);
        } else {
            throw new IllegalStateException("방이없다");
        }
    }

    @MessageMapping("/checkRoom")
    @SendTo("/topic/roomCheck")
    public boolean checkRoom(String roomId) {
        return chatRoomRepository.existsById(Integer.parseInt(roomId));
    }

    @MessageExceptionHandler(IllegalStateException.class)
    @SendTo("/topic/error")
    public String handleIllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }
}