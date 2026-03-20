package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.BoardListAllDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

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
                .writer("이상용")
                .build();

        Long bno = boardService.register(boardDTO);
        log.info("등록된 bno 확인 : " + bno);
    }

    @Test
    public void testSelectOne() {
        Long bno = 102L;
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("하나조회 결과 boardDTO : " + boardDTO);

    }

    @Test
    public void testModify() {
        // 변경할 내용, 일단 디비에서 불러오고, 그리고 내용을 변경해서, 전달.
        // 처음부터, 102 번호의 엔티티 객체를 이용해도 되고,
        // 준비물 작업 ) , BoardDTO 준비 하기.
        // 각자 데이터베이스 데이터 확인 후 , 작업하기. 102L 아닐수 도 있다.
        BoardDTO boardDTO = boardService.readOne(102L);
        boardDTO.setTitle("수정 제목 변경 서비스 테스트 ");
        boardDTO.setContent("수정 내용 변경 서비스 테스트 ");
        boardService.modify(boardDTO);

    }

    @Test
    public void testRemove() {
        boardService.remove(102L);
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

    // 게시글 + 첨부 이미지 , 테스트
    @Test
    public void testRegisterBoardWithImage() {
        // 더미 게시글
        BoardDTO boardDTO = BoardDTO.builder()
                .title("첨부 이미지 추가 더미 게시글")
                .content("첨부 이미지 추가 더미 게시글 내용")
                .writer("이상용첨부이미지작업중")
                .build();

//         더미 파일 이름들
        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aa.png",
                        UUID.randomUUID()+"_bb.png",
                        UUID.randomUUID()+"_cc.png"
                )
        );
        Long bno = boardService.register(boardDTO);
        log.info("bno: " + bno);
    }

    // 게시글 + 첨부이미지를 포함한 하나조회 테스트
    // 상세보기, 조회 기능 단위 테스트
    @Test
    public void testReadWithImage() {

        // 데이터베이스는 각자 다릅니다.
        BoardDTO boardDTO = boardService.readOne(202L);
        log.info("testReadWithImage, 하나 조회 boardDTO : " + boardDTO);
        for(String fileImage : boardDTO.getFileNames()){
            log.info("각 이미지 파일명만 조회 : " + fileImage);
        }
    }

    // 첨부이미지를 가지고서, 수정 테스트
    // 수정, 첨부 이미지를 수정 할 경우,

    @Test
    public void testUpdateWithImages() {
        // 변경시, 변경할 더미 데이터, 임시, 601L
// 화면에서 넘어온 더미 데이터 만들기. DTO 타입.
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(202L)
                .title("제목 : 수정버전")
                .content("내용 : 수정버전")
                .build();

        // 더미 데이터에 첨부 이미지 파일 추가.
        // 경우의수,
        // 기존의 첨부 이미지들을 모두 지우고, 새로운 첨부 이미지를 추가.
        // 1) 기존 첨부이미지 3장, 모두 교체할 경우.
        // 예시)1.jpg,2.jpg,3.jpg -> 4.jpg, 5.jpg

        // 2) 기존 첨부이미지 3장, 2장 삭제, 1장 교체할 경우.
        // 예시)1.jpg(유지),2.jpg(삭제),3.jpg(삭제)
        //  4.jpg(추가), 5.jpg(추가) -> 1.jpg(유지), 4.jpg(추가), 5.jpg(추가)
        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_sampleImage.png",
                        UUID.randomUUID()+"_sampleImage2.png"
                )
        );
        //디비에서 조회하기.
        boardService.modify(boardDTO);
    }

    // 삭제 테스트 1) 댓글이 있는 경우, 2) 댓글 없는 경우
    @Test
    public void testDeleteBoardReplyWithImage() {
        Long bno = 200L;
        boardService.remove(bno);
    }

    // 목록 조회,
    // 모두조회, 게시글 + 댓글갯수 + 첨부 이미지들
    @Test
    @Transactional
    public void testSelectAllBoardWithReplyCountAndImage() {
        // 검색할 더미 데이터
        // 준비물 1) PageRequestDTO, 키워드, 페이지, 사이즈 정보가 다 있음.
        PageRequestDTO pageRequestDTO =
                PageRequestDTO.builder()
                        .page(1)
                        .type("tcw")
                        .keyword("샘플")
                        .size(10)
                        .build();

        PageResponseDTO<BoardListAllDTO> list = boardService.listWithAll(pageRequestDTO);
        log.info("testSelectAllBoardWithReplyCountAndImage _ list: " + list.toString());
    }
}