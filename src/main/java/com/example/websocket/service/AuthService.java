package com.example.websocket.service;

import com.example.websocket.dao.MemberRepository;
import com.example.websocket.dao.RefreshTokenRepository;
import com.example.websocket.dto.JwtToken;
import com.example.websocket.dto.MemberRequestDto;
import com.example.websocket.dto.MemberResponseDto;
import com.example.websocket.dto.TokenRequestDto;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.RefreshToken;
import com.example.websocket.security.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * @param memberRequestDto
     * @return 이메일 중복이 아닐시 데이터베이스에 저장하기
     */
    @Transactional
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        /**
         * 이메일 중복확인
         */
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public JwtToken login(MemberRequestDto memberRequestDto,HttpServletResponse response) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken tokenDto = jwtTokenProvider.generateToken(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        createCookie(response, tokenDto.getAccessToken()); // accessToken을 쿠키에 저장

        // 5. 토큰 발급
        return tokenDto;
    }

    @Transactional
    public JwtToken reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        JwtToken tokenDto = jwtTokenProvider.generateToken(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }
    private void createCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true); // HTTP-Only 설정
        cookie.setSecure(false); // 개발 중이므로 false로 설정 (배포 시 true로 변경)
        cookie.setPath("/"); // 경로 설정
        cookie.setMaxAge(60 * 60); // 쿠키 만료 시간 설정 (1시간)
        response.addCookie(cookie); // 쿠키 추가
    }
}