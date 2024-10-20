package com.example.websocket.service;

import com.example.websocket.dao.MemberRepository;
import com.example.websocket.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // 사용자 이름으로 Member 객체를 찾는 메소드
    public Member findByUsername(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); // 사용자가 없으면 예외 발생
    }
}
