package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        BoardDTO boardDTO = BoardDTO.builder()
                .title("오늘 점심 뭐 먹죠?")
                .content("오늘 점심 뭐 먹죠?")
                .writer("박지효")
                .build();

        Long bno = boardService.register(boardDTO);
        log.info("등록된 bno 확인 : " + bno);
    }

    @Test
    public void testSelectOne() {
        Long bno = 88L;
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("하나조회 결과 boardDTO : " + boardDTO);
    }

    @Test
    public void testModify() {
        // 변경할 내용, 일단 디비에서 불러오고, 그리고 내용을 변경해서, 전달.
        // 처음부터, 102 번호의 엔티티 객체를 이용해도 되고,
        // 준비물 작업 ) , BoardDTO 준비 하기.
        // 각자 데이터베이스 데이터 확인 후 , 작업하기. 102L 아닐수 도 있다.
        BoardDTO boardDTO = boardService.readOne(88L);
        boardDTO.setTitle("수정 제목 변경 서비스 테스트");
        boardDTO.setContent("수정 내용 변경 서비스 테스트 ");
        boardService.modify(boardDTO);
    }

    @Test
    public void testRemove() {
        boardService.remove(88L);
    }

    @Test
    public void testList() {
        // 준비물, 화면에서, 전달받은 페이징 정보와, 검색 정보를 담은
        // PageRequestDTO 필요함.
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("더미")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        log.info("responseDTO 확인 : " + responseDTO);
    }
}