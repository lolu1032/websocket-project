package com.example.websocket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "member_id",updatable = false,unique = true,nullable = false)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String nickname;
    private String address;
    private String phone;
    private String profileImg;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                // SimpleGrantedAuthority는 권한을 나타내는 클래스다 ex) this.roles안에
                // USER이 들어가면 USER라는 문자열이 있으면 이를 new SimpleGrantedAuthority("USER")이라 반환함
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    // 계정이 만료되었는지를 판단한다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정이 잠겨 있지 않음을 의미한다
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // 인증 정보가 만료되지 않았음을 의미한다.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 계정이 활성화되었음을 의미하며 사용자는 정상적으로 시스템을 이용할 수 있습니다.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
