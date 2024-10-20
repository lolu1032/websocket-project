package com.example.websocket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Table(name = "post")
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String title;
    private int count;
    private LocalDate day;
    private String position;
    private String language;
    /**
     * Lob = Large Object 대용량 데이터를 데이터베이스에 저장할 때 사용
     */
    @Lob
    private String content;
    /**
     *    @ManyToOne(fetch = FetchType.LAZY) 다대일 관계를 나타낸다. 지연로딩은 사용자가 직접 데이터에 접근할 때 데이터베이스에서 정보를 빼온다
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;
}
