package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        // 자바 문법으로 검색 및 필터에 필요한 문장 작성(빌터 패턴 이용해 SQL 대신 자바 코드로 작성)

        QBoard board = QBoard.board; // Q 도메인 객체 이용

        JPQLQuery<Board> query = from(board); // select .. from board

        query.where(board.title.contains("t")); // where tilte like..

        // 추가2, 제목, 작성자 검색 조건 추가
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(board.title.contains("t"));
        booleanBuilder.or(board.content.contains("t"));

        // query , 조건 적용
        query.where(booleanBuilder);
        // 유효성 체크, bno 0보다 초과하는 조건
        query.where(board.bno.gt(0L));

        // 추가1, 페이징 처리
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // DB 서버로 호출해 데이터 받아오기
        long count = query.fetchCount(); // 조회된 데이터 갯수 확인

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board  = QBoard.board; // Q 도메인 객체를 이용
        JPQLQuery<Board> query = from(board); // select .. from board

        // 검색 조건 사용해보기.
        if(types != null && types.length > 0 && keyword != null) {
            // 여러 조건을 BooleanBuilder 를 이용해서, 조건의 묶음 만들기.
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type :types) {
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                } // end switch
            } // end for
            // 조건부 설정을 적용하기.
            query.where(booleanBuilder);
        } // end if
        // 간단한 유효성 체크 , bno > 0
        query.where(board.bno.gt(0L));

        // 페이징 조건, 적용하기.
        this.getQuerydsl().applyPagination(pageable,query);

        // 위의 준비물을 이용해서, 검색, 필터, 페이징의 결과를 반환해보기.
        List<Board> list = query.fetch(); // 페이징 처리가 된 10개 데이터 목록
        // 전체 갯수,
        long total = query.fetchCount();
        // Page 타입으로 전달하기,
        Page<Board> result = new PageImpl<Board>(list, pageable, total);

        return result;
    }
}
