package com.example.websocket.controller;

import com.example.websocket.dao.PostRepository;
import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import com.example.websocket.jwt.JwtTokenProvider;
import com.example.websocket.service.MemberService;
import com.example.websocket.service.PostService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    private final PostRepository postRepository;
    @PostMapping("/postSave")
    public ResponseEntity<String> postSave(
            @RequestParam("head-post") String headPost,
            @RequestParam("head-title") String headTitle,
            @RequestParam("head-count") int headCount,
            @RequestParam("head-date") String headDate,
            @RequestParam("head-position") String headPosition,
            @RequestParam("head-language") String headLanguage,
            @RequestParam("head-endDate") String headEndDate,
            @RequestParam("content") String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(authentication.getName());
        Member member = memberService.findByUsername(id); // ID로 Member 객체 찾기
        // Call the service method
        Post savedPost = postService.postSave(headPost, headTitle, headCount, headDate, headPosition, headLanguage, content,headEndDate, member);
        log.info("save={}",content);

        // Return a success response
        return ResponseEntity.ok("Post saved successfully with ID: " + savedPost.getId());
    }
}
