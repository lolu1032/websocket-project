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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ApiController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberRequestDto memberRequestDto) {
        try {
            authService.signup(memberRequestDto);
            /**
             * ResponseEntity.ok는 http상태코드 200 ok이다
             */
            return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
        } catch (RuntimeException e) {
            /**
             * badRequest 400의 본문을 Map.of형식으로 받는다 (JSON) 본문은 body로 받는다.
             * body를 사용한이유는 본문이 여러개여서 사용했다.
             */
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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