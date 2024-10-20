package com.example.websocket.dao;

import com.example.websocket.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(Long id);

    boolean existsByEmail(String email);
}