package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.BoardListReplyCountDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        // 기존 목록에, 댓글 갯수 포함된 , 서비스 메서드로 교체 작업.
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
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
    public String remove(Long bno,  RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중");

        boardService.remove(bno);
        redirectAttributes.addAttribute("result","removed");
        return "redirect:/board/list";
    }

}
