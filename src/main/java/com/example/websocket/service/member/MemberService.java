package com.example.websocket.service.member;

import com.example.websocket.dto.JwtToken;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {
    public JwtToken signIn(String username,String password);
}
