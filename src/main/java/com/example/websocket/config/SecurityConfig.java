package com.example.websocket.config;

import com.example.websocket.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CorsFilter corsFilter;

    private static final String[] AUTH_LIST = {
            "/",
            "/img/**",
            "/auth/**",
            "/api/**",
            "/login",
            "/static/**",
            "/resources/**",
            "/css/**",
            "/js/**",
            "/posts/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 인증 비활성화
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // CORS 필터 추가
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 핸들러
                        .accessDeniedHandler(jwtAccessDeniedHandler)) // 접근 거부 핸들러
                .headers(headers -> headers.frameOptions(
                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                ))  // Same Origin으로 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless 세션 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_LIST).permitAll() // 인증 없이 접근 가능한 API
                        .anyRequest().authenticated()) // 나머지 API는 인증 필요
                .formLogin((login) -> login
                        .loginPage("/login")
                        .permitAll())
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

