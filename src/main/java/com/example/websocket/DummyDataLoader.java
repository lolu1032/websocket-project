//package com.example.websocket;
//
//import com.example.websocket.dao.MemberRepository;
//import com.example.websocket.dao.PostRepository;
//import com.example.websocket.entity.Member;
//import com.example.websocket.entity.Post;
//import com.example.websocket.entity.Member.Authority;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DummyDataLoader {
//
//    @Bean
//    public CommandLineRunner loadDummyData(MemberRepository memberRepository, PostRepository postRepository) {
//        return args -> {
//            // 5명의 Member 생성
//            for (int i = 1; i <= 5; i++) {
//                Member member = Member.builder()
//                        .email("user" + i + "@example.com")
//                        .password("password" + i)
//                        .name("User" + i)
//                        .authority(Authority.ROLE_USER)
//                        .build();
//                memberRepository.save(member);
//            }
//
//            // 50개의 Post 생성
//            for (int i = 1; i <= 50; i++) {
//                Post post = new Post();
//                post.setTitle("Post Title " + i);
//                post.setContent("This is the content of post number " + i);
//                post.setCategory("Category " + (i % 5)); // 5개 카테고리로 구분
//                post.setLanguage("Language " + (i % 3)); // 3개 언어로 구분
//                post.setCount(i); // count는 i로 설정
//
//                // Member 연결 (5명의 멤버 중 하나 랜덤 선택)
//                Member member = memberRepository.findById((long) ((i % 5) + 1)).orElseThrow();
//                post.setMember(member);
//
//                postRepository.save(post);
//            }
//        };
//    }
//}
