package com.busanit501.springboot0226.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") // BoardImage 조회시 Board 조회를 안함.
// Comparable, 비교를해서, -> 정렬를 하는게 목적.
public class BoardImage implements Comparable<BoardImage>{

    @Id
    private String uuid;

    private String fileName;

    // 게시글 하나에 여러 첨부이미지가 있을 경우, 순서를 정하는 번호
    private int ord;

    // 연관관계 설정 1번,
    @ManyToOne
    private Board board;

    @Override
    public int compareTo(BoardImage other) {
        // 결과가 음수 : 앞으로 배치.
        // 결과가 양수 : 뒤로 배치,
        return this.ord - other.ord;
    }

    // 엔티티 클래스에서, setter 만들지 않는다. 이유? 불변성을 유지하기위해서,
    // 지정된 작업외에는 변경이 안되도록 안전장치.
    // 따로 메서드를 만들어서, 수동으로 Board 객체를 변경.
    public void changeBoard(Board board) {
        this.board = board;
    }
}
