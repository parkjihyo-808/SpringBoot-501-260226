package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.Reply;
import com.busanit501.springboot0226.dto.BoardListReplyCountDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testInsert() {
        // 각자 데이터베이스에, 게시글 하나에 더미 댓글 작성.
        Long bno = 10L;
        //  부모 게시글이 있고
        Board board = Board.builder()
                .bno(bno)
                .build();

        // 샘플 더미로 작성할 댓글도 필요함.
        Reply reply = Reply.builder()
                .board(board)
                .replyText("샘플 댓글 3")
                .replyer("샘플 댓글 작성자 3")
                .build();

        replyRepository.save(reply);
    }

    @Test
    public void testBoardReplies() {

        Long bno = 10L;

        Pageable pageable = PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno,pageable);

        result.getContent().forEach(reply -> {
            log.info("댓글 테스트 조회 페이징 처리 : " + reply);
        });
    }

    // 게시글 표시에 댓글 갯수 추가해서 조회하기.
    @Test
    public void testSelectWithReplyCount() {
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by("bno").descending());

        // 전달할 준비물
        // 1) 검색어, 2) 검색 유형
        String keyword = "오늘";
        String[] types = {"t", "w", "c"};

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        log.info("result.getTotalElements()전체 갯수 :" + result.getTotalElements());
        log.info("result.getTotalPages()총페이지등 :" + result.getTotalPages());
        log.info("result.getContent() 페이징된 결과물 10개 :" + result.getContent());
        log.info("result.getNumber() 현재 페이지 번호 :" + result.getNumber());
        log.info("result.getSize() 크기  :" + result.getSize());
        log.info("result.hasNext() 다음  :" + result.hasNext());
        log.info("result.hasPrevious() 이전  :" + result.hasPrevious());
    }
}
