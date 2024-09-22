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
    // 클라에서 방을 생성하면 JPA로 인해 디비에 방을 생성하는 역할을 한다.
    @MessageMapping("/createRoom")
    @SendTo("/topic/roomCreated")
    public ChatRoom createRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomName);
        chatRoom = chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    // 클라에서 api/sendMessage를 부른다면 디비에 입력한 방 번호가 존재하는지 확인 후 레디스쪽에 메시지를 보낸다
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatRoomDTO chatRoomDTO) throws JsonProcessingException {
        String roomId = chatRoomDTO.getRoomId();
        if (chatRoomRepository.existsById(Integer.parseInt(roomId))) {
            redisPublisher.publish(chatRoomDTO);
        } else {
            throw new IllegalStateException("방이없다");
        }
    }
    // 방이 있는지 체크한다.
    @MessageMapping("/checkRoom")
    @SendTo("/topic/roomCheck")
    public boolean checkRoom(String roomId) {
        return chatRoomRepository.existsById(Integer.parseInt(roomId));
    }
    // 에러 코드다.
    @MessageExceptionHandler(IllegalStateException.class)
    @SendTo("/topic/error")
    public String handleIllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }
}