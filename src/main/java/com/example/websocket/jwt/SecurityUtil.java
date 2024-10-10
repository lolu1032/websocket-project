package com.example.websocket.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * JWT토큰 인증된 사용자 정보의 ID를 가져옵니다.
 * @Throw 만약 인증 실패 시 예외처리를 담당합니다.
 */
public class SecurityUtil {
    private SecurityUtil() { }
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw  new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
