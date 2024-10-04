package com.example.websocket.controller;

import com.example.websocket.dto.JwtToken;
import com.example.websocket.dto.MemberRequestDto;
import com.example.websocket.dto.MemberResponseDto;
import com.example.websocket.dto.TokenRequestDto;
import com.example.websocket.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ApiController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(@RequestBody MemberRequestDto memberRequestDto) {
        return ResponseEntity.ok(authService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@RequestBody MemberRequestDto memberRequestDto, HttpServletResponse response) {
        JwtToken loginToken = authService.login(memberRequestDto,response);
        return ResponseEntity.ok(loginToken);
    }

    @PostMapping("/reissue")
    public ResponseEntity<JwtToken> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}