package com.example.websocket.config;

import com.example.websocket.provider.JwtAuthenticationFilter;
import com.example.websocket.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Rest API이므로 basic auth 및 csrf 보안을 사용하지 않는다.
                .httpBasic((httpBasic) -> httpBasic
                    .disable()
                )
                .csrf((csrf) -> csrf
                  .disable()
                )
                // JWT를 사용하기 때문에 세션을 사용하지 않는다.
                .sessionManagement((sessionManagement) -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((request) -> request
                    //해당 API에 대해서는 모든 요청을 허가
                    .requestMatchers("/members/sign-in", "/resources/**","/static/**","/","index.html","/topic/**","/chat/**").permitAll()
                    // USER 권한이 있어야 요청할 수 있다.
                    .requestMatchers("members/test")
                            .hasAnyRole("USER")
                    // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                    .anyRequest().authenticated()
                )
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
        UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비번 암호화
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
