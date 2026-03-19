package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.QBoard;
import com.busanit501.springboot0226.domain.QReply;
import com.busanit501.springboot0226.dto.BoardListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Slf4j
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {
        // 자바 문법으로, 검색 및 필터에 필요한 문장을 작성.(SQL 대신에, 자바 코드로 작성.), 빌더 패턴을 이용해서요.

        QBoard board  = QBoard.board; // Q 도메인 객체를 이용

        JPQLQuery<Board> query = from(board); // select .. from board

        query.where(board.title.contains("t")); // where title like..

        // 추가2, 제목, 작성자 검색 조건 추가,
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.or(board.title.contains("t"));
        booleanBuilder.or(board.content.contains("t"));

        // query , 조건을 적용하기.
        query.where(booleanBuilder);
        // 유효성 체크, bno 0보다 초과하는 조건,
        query.where(board.bno.gt(0L));

        // 추가1, 페이징 처리
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // DB 서버로 호출해서, 데이터를 받아오기.
        long count = query.fetchCount(); // 조회된 데이터 갯수 확인.

        return null;
    }

    // String[] types, "t", "c", "tc"
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

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board); // select .. from board ; 같은 효과. 자바로 데이터베이스 작업중.
        // Board 테이블에, reply 테이블 2개를 합치는데, 조건이,
        //  Board 테이블의 bno 와, reply의 board.bno 같은 경우, 합친다.
        // 하나의 테이블에, Board 테이블 내용도 있고, Reply 테이블 내용도 같이 있어요.
        query.leftJoin(reply).on(reply.board.eq(board));
        query.groupBy(board);

        // 검색에 관련된 조건부 처리 , 이미 했음, 가져오기. 위의 메서드에 있음.
        //===================================================================================
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
        // 검색 조건 사용해보기.
        //===================================================================================

        // 자동으로, 엔티티 결과를 DTO 변환 작업.
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount")
        ));

        // 페이징 조건을 이용, 참고, 위에서 다 정의 및 만들어져 있다. 가져오기.
        //===================================================================================
        // 페이징 조건, 적용하기.
        this.getQuerydsl().applyPagination(pageable,dtoQuery);
        //===================================================================================

        // 위의 준비물) 1) 페이징  2) 검색 3) 목록에 댓글 준비물
        // 반환 타입 방법도 위의 조건과 같아서, 가져오기.
        //===================================================================================
// 위의 준비물을 이용해서, 검색, 필터, 페이징의 결과를 반환해보기.
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch(); // 페이징 처리가 된 10개 데이터 목록
        // 전체 갯수,
        long total = dtoQuery.fetchCount();
        // Page 타입으로 전달하기,
        Page<BoardListReplyCountDTO> result = new PageImpl<BoardListReplyCountDTO>(dtoList, pageable, total);

        return result;
        //===================================================================================
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board); // select .. from board ; 같은 효과. 자바로 데이터베이스 작업중.
        // Board 테이블에, reply 테이블 2개를 합치는데, 조건이,
        //  Board 테이블의 bno 와, reply의 board.bno 같은 경우, 합친다.
        // 하나의 테이블에, Board 테이블 내용도 있고, Reply 테이블 내용도 같이 있어요.
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));
        getQuerydsl().applyPagination(pageable,boardJPQLQuery);

        List<Board> boardList = boardJPQLQuery.fetch(); // 페이징 처리가 된 10개 데이터 목록

        boardList.forEach(board1 -> {
            log.info("bno : " + board1.getBno());
            log.info("이미지 셋 : " + board1.getImageSet());
            log.info("=================================");
        });


        return null;
    }
}
