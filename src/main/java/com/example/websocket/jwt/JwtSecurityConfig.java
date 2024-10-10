package com.example.websocket.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT 토큰을 인증하는 시큐리티 필터 역할
 */
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void configure(HttpSecurity http) {
        JwtFilter cusomFilter = new JwtFilter(jwtTokenProvider);
        http.addFilterBefore(cusomFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
