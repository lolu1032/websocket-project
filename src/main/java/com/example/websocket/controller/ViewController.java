package com.example.websocket.controller;

import com.example.websocket.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final AuthService authService;
    @GetMapping("/")
    public String main(HttpServletRequest request, Model model) {
        boolean isLogin = authService.isAuthenticated(request);
        model.addAttribute("isLogin",isLogin);
        return "index";
    }
    @GetMapping("login")
    public String login(HttpServletRequest request, Model model) {
        boolean isLogin = authService.isAuthenticated(request);
        log.info("isLogin={}",isLogin);
        model.addAttribute("isLogin",isLogin);
        return "member/login";
    }
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}
