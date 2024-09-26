package com.example.websocket.service.member;

import com.example.websocket.dto.JwtToken;

public interface MemberService {
    public JwtToken signin(String username,String password);
}
