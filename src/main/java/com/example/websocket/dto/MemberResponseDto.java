package com.example.websocket.dto;

import com.example.websocket.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
/**
 * MemberRequestDto에서 회원가입 및 로그인 성공하면 클라에서 보여주는 정보는 이메일로 응답한다.
 */
public class MemberResponseDto {
    private String email;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(member.getEmail());
    }
}