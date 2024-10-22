package com.example.websocket.controller;

import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Post;
import com.example.websocket.service.PagingService;
import com.example.websocket.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final PagingService pagingService;
    private final PostService postService;
    @GetMapping(value = {"/","/posts"})
    public String main(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Post> postPage = pagingService.finalAll(PageRequest.of(page, 15)); // 페이지 요청 설정
        model.addAttribute("postPage", postPage);

        Map<String,Object> map = pagingService.paging(postPage,page);
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

    @GetMapping("/post")
    public String post() {
        return "post/post";
    }

    @GetMapping("/post/{id}")
    public String postMain(@PathVariable String id, Model model) {
        Optional<Post> optionalPost = postService.postData(Long.valueOf(id));
        Post post = optionalPost.get();
        model.addAttribute("post",post);
        return "post/main";
    }
}
