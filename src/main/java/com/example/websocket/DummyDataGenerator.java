//package com.example.websocket;
//
//
//import com.example.websocket.dao.MemberRepository;
//import com.example.websocket.dao.PostRepository;
//import com.example.websocket.entity.Member;
//import com.example.websocket.entity.Post;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.stream.IntStream;
//
//@Component
//public class DummyDataGenerator implements CommandLineRunner {
//
//    private final PostRepository postRepository;
//    private final MemberRepository memberRepository;
//
//    // 랜덤 생성기 초기화
//    private final Random random = new Random();
//
//    public DummyDataGenerator(PostRepository postRepository, MemberRepository memberRepository) {
//        this.postRepository = postRepository;
//        this.memberRepository = memberRepository;
//    }
//
//    @Override
//    public void run(String... args) {
//        // 더미 회원 생성
//        IntStream.rangeClosed(1, 10).forEach(i -> {
//            Member member = Member.builder()
//                    .email("user" + i + "@example.com")
//                    .password("password" + i)
//                    .name("User " + i)
//                    .authority(i % 2 == 0 ? Member.Authority.ROLE_ADMIN : Member.Authority.ROLE_USER)
//                    .build();
//            memberRepository.save(member);
//        });
//
//        // 모든 회원을 가져옴
//        var members = memberRepository.findAll();
//
//        // 더미 게시글 생성
//        IntStream.rangeClosed(1, 50).forEach(i -> {
//            Post post = new Post();
//            post.setTitle("Title " + i); // 제목 설정
//            post.setContent("This is the content of post " + i); // 내용 설정
//            post.setMember(members.get(random.nextInt(members.size()))); // 랜덤하게 회원 선택
//            postRepository.save(post);
//        });
//    }
//}