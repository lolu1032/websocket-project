package com.example.websocket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public  static final String BEARER_PREFIX = "Bearer";
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /**
         * resolveToken(request)
         * request를 통해 jwt토큰을 가져옵니다.
         */
        String jwt = getTokenFromCookies(request.getCookies());
//        String jwt = resolveToken(request);
        /**
         * StringUtils.hasText(jwt)
         * JWT가 빈백또는 null 빈문자열인지 확인한다. 세 가지일시 false를 내보낸다.
         * jwtTokenProvider.validateToken(jwt)
         * JWT옳은지 검증한다.
         */
        log.info("jwt={}",jwt);
        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    /**
     * @param request
     * @return JWT토큰만을 사용하기 위해 ""로 분리한다
     * String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
     * 클라이언트에서 요청을 보낼 때 요청 헤더에 AUTHORIZATION_HEADER의 값을 포함할 수 있다.
     * 받는값 Bearer <JWT_TOKEN> Bearer과 토큰이 포함된 문자열 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()); // "Bearer " 이후의 값만 추출
        }
        return null;
    }
}
