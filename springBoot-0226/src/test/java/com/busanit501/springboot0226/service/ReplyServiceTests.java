package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.dto.ReplyDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testReplyRegister() {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("샘플 댓글 작성 서비스 테스트 내용")
                .replyer("샘플 사용자")
                // 주의사항, 각자 데이터베이스에 게시글 번호 확인 후 작업하기.
                .bno(99L)
                .build();
        Long rno = replyService.register(replyDTO);
        log.info("작성 후, 댓글 번호 : " + rno);
    }

    @Test
    public void testReplySelectOne() {
        // 각자 등록되 댓글 번호 확인 : 5L
        ReplyDTO replyDTO = replyService.read(7L);
        log.info("댓글 서비스 단위테스트 중, 댓글 조회 replyDTO : " + replyDTO);
    }

    @Test
    public void testReplyUpdateOne() {
        // 일단 디비에서, 5번으로 조회해서, 불러오고, 내용 변경 해서,, 그리고 수정하기.
        ReplyDTO replyDTO = replyService.read(7L);
        replyDTO.setReplyText("수정 서비스 테스트 !@!!!");
        // 각자 등록되 댓글 번호 확인 : 5L
        replyService.modify(replyDTO);
        log.info("댓글 서비스 단위테스트 중, 댓글 수정 데이터베이스로 확인 하기.  : ");
    }

    @Test
    public void testReplyDeleteOne() {
        replyService.remove(7L);
    }

    @Test
    public void testReplySelectList() {
        // 준비물, 화면에서, 전달받은 페이징 정보와, 검색 정보를 담은
        // PageRequestDTO 필요함.
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("샘플")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(90L, pageRequestDTO);
        log.info("댓글 서비스 테스트 , responseDTO 확인 : " + responseDTO);
    }

}

