package com.example.websocket.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final List<UserDetails> users = new ArrayList<>();

    public CustomUserDetailsService() {
        // 테스트용 사용자 추가
        users.add(org.springframework.security.core.userdetails.User.withUsername("user")
                .password("{noop}password") // {noop}은 비밀번호 암호화 방식을 설정하는 것
                .roles("USER") // 역할 추가
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}