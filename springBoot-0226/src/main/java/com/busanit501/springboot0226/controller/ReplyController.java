package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.dto.ReplyDTO;
import com.busanit501.springboot0226.service.ReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {
    // ResponseEntity -> 반환 타입으로 사용할 예정.
    // 전달 1) 데이터 2) http 상태 코드 같이 전달.
    // 예시)_ 댓글 작성 , http 200 ok

    // 설명
    // 화면 -> 서버
    // 화면에서 댓글 작성 값 -> json 형식 전달 -> 서버 전달.->
    // ->json -> 일반클래스 변환(ReplyDTO)변환,

    // 서버 -> 화면 , 전달,
    //Map<String,Long> 으로전달 -> jackson 직렬화 -> 문자열로 변환,
    // json 문자열 형식으로 변환.

    // @RequestBody, JSON 의 문자열로 전달을 받아요.
    // 원래의 일반 클래스로 역직렬화,
    // Jackson 컨버터 도구가, 알아서, 역직렬화 해줌.

    //ReplyService 만들어서, 작업해야함.
    private final ReplyService replyService;


    @Tag(name = "댓글 등록 post 방식",
            description = "댓글 등록을 진행함, post 형식으로")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult
    ) throws BindException {
        log.info(" ReplyController replyDTO: " + replyDTO);
//        log.info("ReplyController replyDTO 2222:===================== " + replyDTO);
        // 확인용, 더미 데이터 ,

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        // 실제 데이터베이스에 반영이 되는 서비스 기능으로 교체 작업.
        Long rno = replyService.register(replyDTO);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);

        // 하드코딩된 더미 작업
//        Map<String,Long> map = Map.of("rno",123L);
        // ResponseEntity.ok : 200, 정상 응답 코드 의미,
        // map : 데이터를 같이 전달.
        return ResponseEntity.ok(resultMap);
    }

    @Tag(name = "게시글에 대한 댓글 목록 조회 get 방식",
            description = "게시글 번호 bno 가 필요함. 화면에서 데이터를 보내줘야 합니다. " +
                    "댓글 목록 조회 진행함, get 형식으로")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(
            @PathVariable("bno") Long bno,
            PageRequestDTO pageRequestDTO
    ) {
        log.info(" ReplyController 게시글에 대한 댓글 목록 조회 , bno 확인 : " + bno);
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);
        return responseDTO;
    }

    @Tag(name = "댓글 하나 조회 get 방식",
            description = "댓글 번호 rno 가 필요함. 화면에서 데이터를 보내줘야 합니다. " +
                    "댓글 하나 조회 진행함, get 형식으로")
    @GetMapping(value = "/{rno}")
    public ReplyDTO getReplyDTO(
            @PathVariable("rno") Long rno
    ) {
        log.info(" ReplyController 댓글 하나 조회 , rno 확인 : " + rno);
        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;
    }

    @Tag(name = "댓글 삭제 Delete 방식",
            description = "댓글 번호 rno 가 필요함. 화면에서 데이터를 보내줘야 합니다. " +
                    "댓글 삭제 진행함, Delete 형식으로")
    @DeleteMapping(value = "/{rno}")
    public Map<String, Long> remove(
            @PathVariable("rno") Long rno
    ) {
        log.info(" ReplyController 댓글 삭제 , rno 확인 : " + rno);
        replyService.remove(rno);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }

    @Tag(name = "댓글 수정 put 방식",
            description = "댓글 수정을 진행함, put 형식으로")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> modify(
            @PathVariable("rno") Long rno,
            @RequestBody ReplyDTO replyDTO
    ) throws BindException {
        log.info(" ReplyController 댓글 수정 작업 , replyDTO: " + replyDTO);
        log.info(" ReplyController 댓글 수정 작업2 , 수정할 댓글 번혼 rno : " + rno);
        replyService.modify(replyDTO);
        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);
        return resultMap;
    }
}
