package com.busanit501.springboot0226.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") // 댓글 테이블 내용만 조회를 하겠다. 부모 게시글는 조회 안하겠다.
//name = "idx_reply_board_bno" : 게시글 번호로 만든 인덱스라는 의미, ex) idx_테이블명_컬럼명
// columnList = "board_bno" : 어떤 컬럼을 기준으로 목록을 만들것인가? 결정.
// board_bno 이름은 클래스 이름이 아니라, 데이터베이스의 컬럼명이 들어간다.
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})

public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno; // 댓글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board; // 부모 게시글, 늦게 조회할 예정. 해당 데이터 조회할 당시에 테이블 검색을 하겠다.

    private String replyText; // 댓글 내용
    private String replyer; // 댓글 작성자

    public void changeText(String text) {
        this.replyText = text;
    }

    public void changeBoard(Board board) {
        this.board = board;
    }


}
