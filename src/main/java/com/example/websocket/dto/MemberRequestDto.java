package com.example.websocket.dto;

import com.example.websocket.entity.Member;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    private String email;
    private String password;
    private String name;

    /**
     * @param passwordEncoder
     * @return 회원가입을 하면 이메일과 비밀번호 암호화 권한을 빌드한다. 및 로그인 로직이있다.
     */
    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .name(name)
                /**
                 * password 암호화
                 */
                .password(passwordEncoder.encode(password))
                .authority(Member.Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}