package com.example.websocket.controller;

import com.example.websocket.entity.Post;
import com.example.websocket.service.AuthService;
import com.example.websocket.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final AuthService authService;
    private final PostService postService;
    @GetMapping(value = {"/","/posts"})
    public String main(@RequestParam(value = "page", defaultValue = "0") int page, HttpServletRequest request, Model model) {
        boolean isLogin = authService.isAuthenticated(request);
        model.addAttribute("isLogin",isLogin);
        Page<Post> postPage = postService.finalAll(PageRequest.of(page, 12)); // 페이지 요청 설정
        model.addAttribute("postPage", postPage);
        List<Integer> pageNumbers = IntStream.rangeClosed(1, Math.min(postPage.getTotalPages(), 10))
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);
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
