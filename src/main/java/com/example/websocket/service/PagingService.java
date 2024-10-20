package com.example.websocket.service;

import com.example.websocket.dao.PostRepository;
import com.example.websocket.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PagingService {
    private final PostRepository postRepository;

    public Page<Post> finalAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Map<String,Object> paging(Page<Post> postPage, int page) {
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

        // 다음 페이지 블록이 있는지 확인
        boolean hasNextPageBlock = endPage < totalPages;

        // 이전 페이지 블록이 있는지 확인
        boolean hasPreviousPageBlock = startPage > 1;

        Map<String,Object> paging = new HashMap<>();
        paging.put("pageNumbers",pageNumbers);
        paging.put("hasNextPageBlock",hasNextPageBlock);
        paging.put("hasPreviousPageBlock",hasPreviousPageBlock);
        return paging;
    }
}
