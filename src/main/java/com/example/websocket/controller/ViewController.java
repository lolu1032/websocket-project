package com.example.websocket.controller;

import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Post;
import com.example.websocket.service.PagingService;
import com.example.websocket.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ViewController {
    private final PagingService pagingService;
    private final PostService postService;
    @GetMapping(value = {"/","/posts"})
    public String main(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "category", defaultValue = "all")
                       String category,Model model) {
        Pageable pageable = PageRequest.of(page, 15);
        Page<Post> postPage;

        if (category.equals("all")) {
            postPage = postService.findAllPosts(pageable);  // 전체 게시글 가져오기
        } else {
            postPage = postService.findPostsByCategory(category, pageable);  // 선택된 카테고리의 게시글만 가져오기
        }

        model.addAttribute("postPage", postPage);
        model.addAttribute("selectedCategory", category);

        Map<String, Object> paging = pagingService.paging(postPage, page);
        model.addAllAttributes(paging);

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
    @PostMapping("/posts")
    public String getPostsByCategory(@RequestParam String category, @RequestParam int page, Model model) {
        Page<Post> postPage;
        if ("all".equals(category)) {
            postPage = postService.findAllPosts(PageRequest.of(page, 15));
        } else {
            postPage = postService.findPostsByCategory(category, PageRequest.of(page, 15));
        }

        model.addAttribute("postPage", postPage);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(1, postPage.getTotalPages()).boxed().collect(Collectors.toList()));

        // 게시글 목록과 페이징을 포함하는 부분의 HTML을 반환
        return "fragments/posts :: postList, fragments/paging :: paging"; // Thymeleaf fragment의 이름을 반환
    }

}
