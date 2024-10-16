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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final PostService postService;
    @GetMapping(value = {"/","/posts"})
    public String main(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Post> postPage = postService.finalAll(PageRequest.of(page, 15)); // 페이지 요청 설정
        model.addAttribute("postPage", postPage);

        Map<String,Object> map = postService.paging(postPage,page);
        model.addAllAttributes(map);

        return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/post/{id}")
    public String post(@PathVariable(name = "id") String id) {
        return "post/post";
    }
}
