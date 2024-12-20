package com.example.websocket.controller;

import com.example.websocket.SearchSpecification;
import com.example.websocket.dao.PostRepository;
import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Post;
import com.example.websocket.service.PagingService;
import com.example.websocket.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final PostRepository postRepository;
    @GetMapping(value = {"/","/posts"})
    public String main(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "category", defaultValue = "all") String category,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 15);
        Page<Post> postPage = postService.categoryView(category,pageable);

        model.addAttribute("postPage", postPage);
        model.addAttribute("selectedCategory", category);

        Map<String, Object> paging = pagingService.paging(postPage, page);
        model.addAllAttributes(paging);

        pageable = PageRequest.of(page,5);
        Page<Post> bestView = postService.findAllByOrderByViewsDesc(pageable);
        model.addAttribute("bestView",bestView);

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
    @GetMapping("/search")
    public String search(
            @RequestParam String searchList,
            @RequestParam String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 15);
        Specification<Post> specification = postService.search(searchList,search);

        Page<Post> postPage = postRepository.findAll(specification, pageable);
        model.addAttribute("postPage", postPage);
        model.addAttribute("searchList", searchList);
        model.addAttribute("search", search);

        return "searchPage";
    }

}
