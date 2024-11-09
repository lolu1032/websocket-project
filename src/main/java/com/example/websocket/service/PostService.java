package com.example.websocket.service;

import com.example.websocket.SearchSpecification;
import com.example.websocket.dao.MemberRepository;
import com.example.websocket.dao.PostRepository;
import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import com.example.websocket.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    /**
     * Parser 객체써서 마크업 언어로 변환시켜줌
     * HtmlRendrer로 반환시켜줌
     */
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public Post postSave(String headPost, String headTitle, int headCount, String headDate, String headPosition, String headLanguage, String content,String endDate,Member member) {

        // Post 객체 생성
        PostDTO postDTO = new PostDTO();
        postDTO.setCategory(headPost);
        postDTO.setTitle(headTitle);
        postDTO.setCount(headCount);
        postDTO.setDay(headDate);
        postDTO.setPosition(headPosition);
        postDTO.setLanguage(headLanguage);
        postDTO.setContent(content);
        postDTO.setEndDate(endDate);
        postDTO.setMember(member);

        Post post = postDTO.toEntity();
        return postRepository.save(post);
    }

    public String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        var document = parser.parse(markdown); // Markdown을 파싱
        return renderer.render(document); // HTML로 렌더링
    }
    public Optional<Post> postData(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.get();
        post.incrementViews();
        postRepository.save(post);
        Post updatedPost = Post.builder()
                .category(post.getCategory()) // 카테고리 복사
                .title(post.getTitle())  // 타이틀 복사
                .count(post.getCount())  // 카운트 복사
                .day(post.getDay())  // 날짜 복사
                .position(post.getPosition())  // 포지션 복사
                .language(post.getLanguage())  // 언어 복사
                .content(convertMarkdownToHtml(post.getContent()))  // 컨텐츠 변환 후 설정
                .endDate(post.getEndDate())  // 종료일 복사
                .member(post.getMember())  // 멤버 복사
                .views(post.getViews())
                .build();
        return Optional.of(updatedPost);
    }
    public  Page<Post> findPostsByCategory(String category,Pageable pageable) {
        return postRepository.findByCategory(category,pageable);
    }

    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> findAllByOrderByViewsDesc(Pageable pageable) {
        return postRepository.findAllByOrderByViewsDesc(pageable);
    }
    public Specification<Post> search(String searchList,String search) {
        Specification<Post> specification = Specification.where(null);

        if ("all".equals(searchList)) {
            specification = specification.and(SearchSpecification.allSearch(search));
        } else if ("title".equals(searchList)) {
            specification = specification.and(SearchSpecification.titleSearch(search));
        } else {
            specification = specification.and(SearchSpecification.langSearch(search));
        }
        return specification;
    }
}
