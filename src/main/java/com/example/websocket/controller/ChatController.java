package com.example.websocket.controller;

import com.example.websocket.dao.ChatRoomRepository;
import com.example.websocket.entity.ChatRoom;
import com.example.websocket.service.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    // 특정 채널에 메시지를 발행하는걸 불러와줬다.
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    public ChatController(RedisPublisher redisPublisher, ChatRoomRepository chatRoomRepository) {
        this.redisPublisher = redisPublisher;
        this.chatRoomRepository = chatRoomRepository;
    }
    // api/createRoom을 통해 방이름을 쓰고 보내면 방생성
    @MessageMapping("/createRoom")
    @SendTo("/topic/roomCreated")
    public ChatRoom createRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom = chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    // 클라에서 받은 메시지를 publish을 통해 chat-room 특정 채널에 message를 보낸다
    @MessageMapping("/sendMessage")
    public void sendMessage(String roomId,String message) {
        redisPublisher.publish(roomId,message);
    }
}
