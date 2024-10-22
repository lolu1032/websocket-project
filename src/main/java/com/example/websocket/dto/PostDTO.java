package com.example.websocket.dto;

import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String category;
    private String title;
    private int count;
    private LocalDate day;
    private String position;
    private String language;
    private String content;
    private LocalDate endDate;
    private Member member;

    public Post toEntity() {
        return Post.builder()
                .category(this.category)
                .title(this.title)
                .count(this.count)
                .day(this.day)
                .position(this.position)
                .language(this.language)
                .content(this.content)
                .endDate(this.endDate)
                .member(this.member)
                .build();
    }
}
