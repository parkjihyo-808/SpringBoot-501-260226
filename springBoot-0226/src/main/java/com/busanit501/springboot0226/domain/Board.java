package com.busanit501.springboot0226.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
//추가 기능 : 애너테이션 옵션 추가.
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 추가작업 ,
@ToString(exclude = "imageSet" )// Board 객체를 문자열로 출력시, 첨부된 이미지들은 출력을 안함.
public class Board extends BaseEntity {

    // 엔티티 클래스는 실제 데이터 베이스의 테이블을 만드는 효과이므로, 반드시 pk 를 생성해야함.
    // 그래서, 필수로 @Id 이용해서, pk 표시를 의무적으로 해야함.
    @Id
    // 마리아 디비에서 사용하는 기본 자동 생성 정책을 이용함.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long bno; // 실제 데이터베이스의 테이블의 컬럼1

    //추가 기능2 , 옵션 추가
    @Column(length = 500, nullable = false)
    private  String title; // 실제 데이터베이스의 테이블의 컬럼2

    @Column(length = 2000, nullable = false)
    private  String content; // 실제 데이터베이스의 테이블의 컬럼3

    @Column(length = 50, nullable = false)
    private  String writer; // 실제 데이터베이스의 테이블의 컬럼4

    // 눈에 보이지 않지만, BaseEntity를 이용해서, regDate, modDate 도 추가가 될 예정.

    // 수정 메서드를 따로 만들기.
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //추가작업
    // 연관관계 설정 2번,
    // 게시글 1 <----> N 첨부 이미지, 양쪽에서 연관 관계 설정 모두 했음. 양방향.
//    @OneToMany
    // BoardImage 의 board 변수를 의미 , 해석 : 나는 연관관계 주인이 아니예요. BoardImage가 연관관계의 주인입니다.
    @OneToMany(mappedBy = "board",
            cascade = CascadeType.ALL, // 영속정 정의, all, 추가, 수정, 분리, 삭제, 하위에도 영향을 주겠다.
            fetch = FetchType.LAZY, // 현재 테이블 Board 테이블을 조회를 하는데, imageSet BoardImage 필요시, 조회할 때만
            orphanRemoval = true
    )
    // 테이블 접근해서 조회하겠다. -> 결론, 미리 조회를 안하겠다.
    //추가
    // 고악 객체 제거 속성 이용.
    @Builder.Default
    // 한번에 모아서 실행을 해보자.
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

    // 이미지 추가하는 메서드
    public void addImage(String uuid, String fileName) {

        // BoardImage 객체를 빌더 패턴으로 생성.
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                // 첨부 이미지를 3개를 첨부했으면,
                // 처음 객체를 생성시, imageSet.size() 0개.
                // 2번째 객체를 생성시, imageSet.size() 1개.
                // 3번째 객체를 생성시, imageSet.size() 2개.
                .build();
        imageSet.add(boardImage);
    }

    // 이미지 삭제하는 메서드
    public void clearImages() {
        imageSet.forEach(boardImage ->
                // 부모 게시글을 null 변환 시키면, 고아 객체가 되어서, 자동으로 삭제되는 효과를 줄 예정.
                // 참고로, 추가 옵션 설정이 필요함.
                boardImage.changeBoard(null));
        this.imageSet.clear();
    }
}
