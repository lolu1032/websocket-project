package com.example.websocket.controller;

import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Post;
import com.example.websocket.service.PagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PagingController {
    private final PagingService pagingService;

    @GetMapping("/posts")
    public Page<Post> getAllPosts(Pageable pageable) {
        return pagingService.finalAll(pageable);
    }
}
