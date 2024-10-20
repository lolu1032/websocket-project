package com.example.websocket.jwt;

import com.example.websocket.config.RedisConfig;
import com.example.websocket.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    private final Key key; // JWT 서명을 위한 key 객체 선언

    /**
     * StringRedisTemplate은 Spring FrameWork에서 제공하는 RedisTemplate의 문자열 버전
     */
    private final StringRedisTemplate redisTemplate;

    public JwtTokenProvider(@Value("${security.token}") String securitKey, StringRedisTemplate redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(securitKey); // Base64로 인코딩된 securitKey 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes); // securitKey를 이용하여 key객체 생성
        this.redisTemplate = redisTemplate;
    }
    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream() // Authentication에서 제공해주는 권한 생성
                .map(GrantedAuthority::getAuthority) // 각 권한을 문자열로 반환한다.
                .collect(Collectors.joining(",")); // 권한들을 ,로 구분한다 ROLE_USER,ROLE_ADMIN
        if (authorities.isEmpty()) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        // AccessToken 생성
        String accessToken = Jwts.builder()
                // sub은 JWT에서 미리 정의된 표준 클레임 이름중 하나.
                // name은 authentication.getName()값
                .setSubject(authentication.getName()) // payload : "sub" : "name"
                .claim(AUTHORITIES_KEY,authorities) // payload : "auth" : "ROLE_USER"
                .setExpiration(accessTokenExpiresIn) // payload : "exp" : 111100 만료시간
                .signWith(key, SignatureAlgorithm.HS512)//header : "alg" : "HS512"
                .compact(); // jwt 문자열 생성

        // RefreshToken 생성
        // 유효 기간이 만료된 토큰을 새로운 토큰으로 발급
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        /**
         * opsForValue()는 문자열 값을 다루기 위한 연산을 수행하는 ValueOperations 객체를 반환한다. 키-값 쌍으로 데이터를 저장 조회 가능
         */
        redisTemplate.opsForValue().set(authentication.getName(), refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return JwtToken.builder()
                .grentType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }
    public Authentication getAuthentication(String token) {
        // Payload의 데이터를 복호화하여 Claims 객체로 변환한다.
        // 예를 들어 Base64URL 형태를 디코딩해서 JSON형태로 다시 만들어 claims 객체에 넣는다
        Claims claims = parseClaims(token);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                // 문자열로 변환 후 , 로 나누어 배열을 만든다.
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new) // SimpleGrantedAuthority 객체로 바꾼다. SimpleGrantedAuthority 은 시큐리티에서 권한을 나타내는 클래스다
                .collect(Collectors.toList()); // SimpleGrantedAuthority("ROLE_USER") 반환한 객체를 리스트로 수집한다.
        // UserDetails는 시큐리티에서 사용자정보를 나타내는 객체
        // sub : 사용자 이름 , password, 권한을 객체 넣느다.
        UserDetails principal = new User(claims.getSubject(),"",authorities);
        /**
        * UserDetails에서 인증된 정보를 UsernamePasswordAuthenticationToken안에 principal과 authorities을 설정합니다.
         */
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }
    // Signature jwt 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * 사용자 정보를 보관하는 메소드
     * @param accessToken
     * @return 사용자 정보를 담고 있는 Claims 객체
     * 비밀키를 사용하여 JWT토큰을 검증하고 그 안에 있는 사용자 정보를 반환한다.
     * @throws ExpiredJwtException 토큰이 만료된 경우에도 예외에서 Claims를 추출하여 반환합니다.
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject(); // 사용자 ID가 'sub' 필드에 저장된 경우
    }

}
