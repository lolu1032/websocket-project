package com.example.websocket.service;

import com.example.websocket.dao.MemberRepository;
import com.example.websocket.dao.RefreshTokenRepository;
import com.example.websocket.dto.JwtToken;
import com.example.websocket.dto.MemberRequestDto;
import com.example.websocket.dto.MemberResponseDto;
import com.example.websocket.dto.TokenRequestDto;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.RefreshToken;
import com.example.websocket.jwt.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;

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
        /**
         * 회원가입 유효성검사 칸
         */
        ResponseEntity<Map<String, Object>> validationResult = validation(memberRequestDto);
        if (validationResult != null) {
            throw new RuntimeException((String) validationResult.getBody().get("error"));
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

        createCookie(response,tokenDto.getRefreshToken());

        log.info("tokenDto={}",tokenDto);
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

        String key = "accessToken:" + authentication.getName();
        redisTemplate.opsForValue().set(key, tokenDto.getAccessToken(), 60, TimeUnit.MILLISECONDS);


        // 토큰 발급
        return tokenDto;
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 AccessToken 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위해 유효 기간을 0으로 설정
        response.addCookie(cookie);

        // 2. 쿠키에서 JWT AccessToken 추출
        String accessToken = getJwtFromCookies(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            // 3. AccessToken으로부터 사용자 정보 가져오기
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            // 4. Redis에서 RefreshToken 삭제
            String userId = authentication.getName();
            jwtTokenProvider.getRedisTemplate().delete(userId); // Redis에서 RefreshToken 삭제

            log.info("User {} logged out successfully", userId);
        } else {
            log.warn("No valid access token found for logout");
        }
    }

    private void createCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken",token);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가능하도록 설정
        cookie.setSecure(true);    // HTTPS에서만 전송 (HTTPS 환경에서 권장)
        cookie.setPath("/");       // 쿠키의 경로 설정
        cookie.setMaxAge(60 * 60 * 24 * 7); // 쿠키 유효 기간 (예: 1시간)
        response.addCookie(cookie); // 쿠키를 응답에 추가
    }
    private ResponseEntity<Map<String,Object>> validation(MemberRequestDto memberRequestDto) {
        Map<String,Object> response = new HashMap<>();
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        String nameRegex = "^[a-zA-Z가-힣]+$"; // 한글과 영어를 모두 허용

        if (!memberRequestDto.getEmail().matches(emailRegex)) {
            response.put("error", "유효하지 않은 이메일 형식입니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (!memberRequestDto.getPassword().matches(passwordRegex)) {
            response.put("error", "유효하지 않은 비밀번호 형식입니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (!memberRequestDto.getName().matches(nameRegex)) {
            response.put("error", "유효하지 않은 이름 형식입니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return null; // 모든 검증 통과
    }
    // 쿠키에서 JWT를 추출하는 메서드
    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();  // accessToken 값 반환
                }
            }
        }
        return null;  // 쿠키에 accessToken이 없을 경우
    }
}