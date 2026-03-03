package com.busanit501.springboot0226.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
// 추가 기능: 애너테이션 옵션 추가
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseEntity {

    // 엔티티 클래스는 실제 데이터 베이스의 테이블을 만드는 효과이므로, 반드시 pk 를 생성해야함.
    // 그래서, 필수로 @Id 이용해서, pk 표시를 의무적으로 해야함.
    @Id
    // 마리아 디비에서 사용하는 기본 자동 생성 정책을 이용함.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long bno; // 실제 데이터베이스의 테이블의 컬럼1

    // 추가 기능 2: 옵션 추가
    @Column(length = 500, nullable = false)
    private  String title; // 실제 데이터베이스의 테이블의 컬럼2

    @Column(length = 2000, nullable = false)
    private  String content; // 실제 데이터베이스의 테이블의 컬럼3

    @Column(length = 50, nullable = false)
    private  String writer; // 실제 데이터베이스의 테이블의 컬럼4

    // 눈에 보이지 않지만 BaseEntity를 이용해 regDate, modDate도 추가 될 예정

    // 수정 메서드 따로 만들기
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
