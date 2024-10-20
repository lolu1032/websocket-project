package com.example.websocket.service;

import com.example.websocket.dao.MemberRepository;
import com.example.websocket.dao.PostRepository;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import com.example.websocket.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PostService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public Post postSave(String headPost, String headTitle, int headCount, String headDate, String headPosition, String headLanguage, String content,Member member) {

        // Post 객체 생성
        Post post = new Post();
        post.setCategory(headPost);
        post.setTitle(headTitle);
        post.setCount(headCount);
        post.setDay(LocalDate.parse(headDate));
        post.setPosition(headPosition);
        post.setLanguage(headLanguage);
        post.setContent(content);
        post.setMember(member);

        return postRepository.save(post);
    }
}
