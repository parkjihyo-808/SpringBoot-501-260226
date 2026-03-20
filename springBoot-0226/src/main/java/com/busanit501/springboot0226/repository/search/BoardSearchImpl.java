package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.QBoard;
import com.busanit501.springboot0226.domain.QReply;
import com.busanit501.springboot0226.dto.BoardImageDTO;
import com.busanit501.springboot0226.dto.BoardListAllDTO;
import com.busanit501.springboot0226.dto.BoardListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

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

    // 검색시, 검색어 조건, 댓글의 갯수 첨부,
    // 프로젝션을 이용해서, dto <-> entity 클래스 간에 서로 자동 변환
    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board); // select .. from board ; 같은 효과. 자바로 데이터베이스 작업중.
        // Board 테이블에, reply 테이블 2개를 합치는데, 조건이,
        //  Board 테이블의 bno 와, reply의 board.bno 같은 경우, 합친다.
        // 하나의 테이블에, Board 테이블 내용도 있고, Reply 테이블 내용도 같이 있어요.
        query.leftJoin(reply).on(reply.board.eq(board));
        // bno  title  content writer , rno  bno  replyText replyer
        // 121   test   test    lsy      1    121    댓글    댓글작성자
        // 121   test   test    lsy      2    121    댓글2    댓글작성자2

        // 120   test3   test3    lsy3   3    120    댓글33    댓글작성자33
        // 120   test3   test3    lsy3   4    120    댓글44    댓글작성자44

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

    // DTO <-> Entity 형변환
    // 1단계 , 서비스, 모델맵퍼 이용해서,
    // 2단계 , 디비에서 조회 하자마자, 바로 DTO변환,Projections.bean 이용,
    // 3단계, Tuple 타입을 이용해서, 변환. 복잡도 증가, 조건 설정 변경 쉬워짐.
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        // 기본 세팅.
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        JPQLQuery<Board> boardJPQLQuery = from(board);// select * from board
        // 조인 설정 , 게시글에서 댓글에 포함된 게시글 번호와 , 게시글 번호 일치
        boardJPQLQuery.leftJoin(reply).on(reply.board.bno.eq(board.bno));
        // bno  title  content writer , rno  bno  replyText replyer
        // 121   test   test    lsy      1    121    댓글    댓글작성자
        // 121   test   test    lsy      2    121    댓글2    댓글작성자2

        // 120   test3   test3    lsy3   3    120    댓글33    댓글작성자33
        // 120   test3   test3    lsy3   4    120    댓글44    댓글작성자44

        //기존 , 검색 조건 추가. 위의 내용 재사용.
        if (types != null && types.length > 0 && keyword != null) {
            // 여러 조건을 하나의 객체에 담기.
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                } // switch
            }// end for
            // where 조건을 적용해보기.
            boardJPQLQuery.where(booleanBuilder);
        } //end if
        // bno >0
        boardJPQLQuery.where(board.bno.gt(0L));

        boardJPQLQuery.groupBy(board); // 그룹 묶기
        // bno 125 , 게시글, 댓글 1  첨부 이미지 3개
        // bno 125 , 게시글, 댓글 2,  첨부 이미지 3개
        // bno 125 , 게시글, 댓글 3,  첨부 이미지 3개
        // bno 124 , 게시글, 댓글 1,  첨부 이미지 3개
        // bno 124 , 게시글, 댓글 2,  첨부 이미지 3개
        this.getQuerydsl().applyPagination(pageable, boardJPQLQuery); //페이징 당 10개 작업.
        // 기본 세팅.

        // 3단계, 튜플 이용해서, 데이터 형변환.
        // 현재 상황, : boardJPQLQuery 여기 객체에는 무엇이 들어가 있나요?
        // 박스안에 : 1) 게시글, 댓글 2개테이블 조인 2) 검색 조건 3) 그룹 바이도 4) 페이징 도 포함된 상태이다.
        // 비유 : boardJPQLQuery <-> DTO로 변환하는 중인데,
        // 여기에 중간 저장소 만들기 : tupleJPQLQuery
        // boardJPQLQuery <--->tupleJPQLQuery<---> DTO로 변환하는 중인데,
        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(
                // 게시글, 댓글의 갯수를 조회한 결과,
                board,reply.countDistinct()
        );
        // 튜플에서, 각 데이터를 꺼내서, 형변환 작업,
        // 꺼내는 형식이 조금 다름. 맵과 비슷

        // boardJPQLQuery <--->tupleJPQLQuery<---> DTO로 변환하는 중인데,
        // tupleList, 튜플의 타입으로 조인된 테이블의 내용이 담겨 있음.
        // 인덱스 순서, 0  ,  1
        // Tuple -> board,reply.countDistinct()
        List<Tuple> tupleList = tupleJPQLQuery.fetch(); //fetch() : 가져오기, 불러오다, 호출하다.

        // 형변환 작업, 디비에서 조회 후 바로, DTO로 변환 작업,
        List<BoardListAllDTO> dtoList =
                tupleList.stream().map(tuple -> {
                    // 디비에서 조회된 내용임.
                    Board board1 = (Board)tuple.get(board);
                    // 1 인덱스의미, 2번째 컬럼 요소
                    long replyCount = tuple.get(1, Long.class);
                    // DTO로 형변환 하는 코드,
                    BoardListAllDTO dto = BoardListAllDTO.builder()
                            // 보드 내용
                            .bno(board1.getBno())
                            .title(board1.getTitle())
                            .writer(board1.getWriter())
                            .regDate(board1.getRegDate())
                            // 댓글 갯수
                            .replyCount(replyCount)
                            .build();

                    // board1에 있는 첨부 이미지를 꺼내서, DTO 담기.
                    // 같이 형변환하기
                    // 첨부 이미지를 추가하는 부분, 첨부이미지_추가1,
                    // 게시글 1번에, 첨부 이미지가 3장있으면,
                    // 3장을 각각 BoardImageDTO -> 형변환.
                    List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                            .map(boardImage -> // 엔티티 클래스 -> DTO 로 변환중.
                                    BoardImageDTO.builder()
                                            .uuid(boardImage.getUuid())
                                            .fileName(boardImage.getFileName())
                                            .ord(boardImage.getOrd())
                                            .build()
                            ).collect(Collectors.toList());

                    // 최종 dto, 마지막, 첨부이미지 목록들도 추가.
                    dto.setBoardImages(imageDTOS);

                    return dto; // 댓글의 갯수 , 첨부 이미지 목록들.
                }).collect(Collectors.toList());

        // 위에 첨부
        // 페이징 된 데이터 가져오기.
        // 앞에서 사용했던, 검색 조건

        long totalCount = boardJPQLQuery.fetchCount();
        Page<BoardListAllDTO> page
                = new PageImpl<>(dtoList, pageable, totalCount);
        return page;
    }
}
