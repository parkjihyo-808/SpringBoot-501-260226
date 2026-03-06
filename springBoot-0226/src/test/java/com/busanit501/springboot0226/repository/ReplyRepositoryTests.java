package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.Reply;
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

    @Test
    public void testInsert() {
        // 각자 데이터베이스에, 게시글 하나에 더미 댓글 작성.
        Long bno = 124L;
        //  부모 게시글이 있고
        Board board = Board.builder()
                .bno(bno)
                .build();

        // 샘플 더미로 작성할 댓글도 필요함.
        Reply reply = Reply.builder()
                .board(board)
                .replyText("샘플 댓글3 ")
                .replyer("샘플 댓글 작성자3")
                .build();

        replyRepository.save(reply);
    }

    @Test
    public void testBoardReplies() {

        Long bno = 124L;

        Pageable pageable = PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno,pageable);

        result.getContent().forEach(reply -> {
            log.info("댓글 테스트 조회 페이징 처리 : " + reply);
        });
    }
}
