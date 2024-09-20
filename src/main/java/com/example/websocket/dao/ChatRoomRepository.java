package com.example.websocket.dao;

import com.example.websocket.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    boolean existsById(int id);
    ChatRoom save(ChatRoom chatRoom);
}
