package com.example.websocket.service;

import com.example.websocket.dao.PostRepository;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Page<Post> finalAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}
