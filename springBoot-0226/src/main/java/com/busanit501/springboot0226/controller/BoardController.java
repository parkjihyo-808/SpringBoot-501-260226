package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.*;
import com.busanit501.springboot0226.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 물리 저장소 경로를 불러오기.
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        // 기존 목록에, 댓글 갯수 포함된 , 서비스 메서드로 교체 작업.
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

        // 기존 목록에, 댓글 갯수 + 첨부 이미지가 모두 포함된 메서드로 교체 작업.
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
        log.info("BoardController에서, responseDTO 확인 ," + responseDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    // 화면 제공
    @GetMapping("/register")
    public  void registerGet() {

    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, registerPost 작업중");

        // 서버에서 유효성 체크를 했을 경우
        if(bindingResult.hasErrors()) {
            log.info("BoardController 에서, registerPost , 유효성 오류 발생. ");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        // 유효성 체크를 통과한 경우.
        log.info("boardDTO 확인 : " + boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";

    }

    @GetMapping({"/read","/modify"})
    public  void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("BoardController 에서, read , boardDTO 확인 : " +boardDTO );
        model.addAttribute("dto",boardDTO);
    }

    @PostMapping("/modify")
    public String modify(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                         PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, modify 작업중");

        // 서버에서 유효성 체크를 했을 경우
        if(bindingResult.hasErrors()) {
            log.info("BoardController 에서, modify , 유효성 오류 발생. ");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno",boardDTO.getBno());
            return "redirect:/board/modify?"+link;
        }
        // 유효성 체크를 통과한 경우.
        log.info("boardDTO 확인 : " + boardDTO);
        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/read";

    }

    @PostMapping("/remove")
    // 삭제시, 화면에서 넘겨받은 , 삭제할 이미지 파일을 받을 준비 : BoardDTO 를 이용함.
//    public String remove(Long bno,  RedirectAttributes redirectAttributes) {
    public String remove(BoardDTO boardDTO,  RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중");

        Long bno = boardDTO.getBno();

        // 실무에서, 삭제시, 먼저 DB 의 내용먼저 삭제 후, 그다음에, 물리파일 삭제 하기.
        // 이유는 만약) 작업중 오류가 발생했을 때, DB에서 삭제가 되어야 정상적으로 화면에서, 출력이 안됨.
        // 2) 만약, DB 삭제가 이루어지지 않고, 먼저 물리 파일만, 삭제가 된 상황이면,
        //  화면에서, 없는 파일을 계속 가리킵니다. 그러면, 출력시 오류가 발생 되거나, 또는 UX 가 안좋습니다.

        // 순서1
        // 데이터베이스 삭제하고,
        boardService.remove(bno);

        // 순서2
        // 게시글에 첨부된 이미지 파일도 삭제.
        //추가
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            // uploadController 가져와서 사용한다.
            removeFiles(fileNames);
        }

        redirectAttributes.addAttribute("result","removed");
        return "redirect:/board/list";
    }

    // 추가, 이미지 파일 삭제하는 함수
    // 물리서버 , 첨부 이미지 삭제 함수.
    public void removeFiles(List<String> fileNames) {
        for (String filename : fileNames) {
            Resource resource = new FileSystemResource(uploadPath+ File.separator+filename);
//            String resourceName = resource.getFilename();

            // 리턴 타입 Map 전달,
            Map<String,Boolean> resultMap = new HashMap<>();
            boolean deleteCheck = false;
            try {
                // 파일 삭제시, 이미지 파일일 경우, 원본 이미지와 , 썸네일 이미지 2개 있어서
                // 이미지 파일 인지 여부를 확인 후, 이미지 이면, 썸네일도 같이 제거해야함.
                String contentType = Files.probeContentType(resource.getFile().toPath());
                // 삭제 여부를 업데이트
                // 원본 파일을 제거하는 기능. (실제 물리 파일 삭제 )
                deleteCheck =resource.getFile().delete();

                if (contentType.startsWith("image")) {
                    // 썸네일 파일을 생성해서, 파일 클래스로 삭제를 진행.
                    // uploadPath : C:\\upload\springTest
                    // File.separator : C:\\upload\springTest\test1.jpg
                    File thumbFile = new File(uploadPath+ File.separator,"s_"+ filename);
                    // 실제 물리 파일 삭제
                    thumbFile.delete();
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
            resultMap.put("result", deleteCheck);
//            return resultMap;
        }
    }

}
