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
        Page<Post> postPage = postService.finalAll(PageRequest.of(page, 15)); // 페이지 요청 설정
        model.addAttribute("postPage", postPage);
        // 현재 페이지와 총 페이지 수를 계산
        int totalPages = postPage.getTotalPages();
        int currentPage = page + 1; // 현재 페이지 (0-based에서 1-based로 변경)

        // 페이지네이션 범위 설정 (10개씩 보여줌)
        int startPage = ((currentPage - 1) / 10) * 10 + 1; // 현재 페이지가 속한 10개의 범위의 시작 페이지
        int endPage = Math.min(startPage + 9, totalPages); // 마지막 페이지는 총 페이지 수를 넘지 않도록 설정

        // 페이지 번호 리스트 생성
        List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                .boxed()
                .collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);

        // 다음 페이지 블록이 있는지 확인
        boolean hasNextPageBlock = endPage < totalPages;
        model.addAttribute("hasNextPageBlock", hasNextPageBlock);

        // 이전 페이지 블록이 있는지 확인
        boolean hasPreviousPageBlock = startPage > 1;
        model.addAttribute("hasPreviousPageBlock", hasPreviousPageBlock);

        return "index";
    }
    @GetMapping("/login")
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

    @GetMapping("/post/{id}")
    public String post(@PathVariable(name = "id") String id,HttpServletRequest request, Model model) {
        boolean isLogin = authService.isAuthenticated(request);
        model.addAttribute("isLogin",isLogin);
        return "post/post";
    }
}
