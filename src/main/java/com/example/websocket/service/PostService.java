package com.example.websocket.service;

import com.example.websocket.dao.MemberRepository;
import com.example.websocket.dao.PostRepository;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import com.example.websocket.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.time.LocalDate;
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
        post.setContent(convertMarkdownToHtml(post.getContent()));
        return Optional.of(post);
    }
}
