package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.domain.Reply;
import com.busanit501.springboot0226.dto.*;
import com.busanit501.springboot0226.repository.BoardRepository;
import com.busanit501.springboot0226.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 작업 후 저장 또는 되돌리기 하기위해서, 필요함.
public class BoardServiceImpl implements BoardService{
// 화면으로부터 , 글작성 재료를 DTO 에 받아서, 엔티티 객체 타입으로 변환해서, 전달하는 용도.

    private final ModelMapper modelMapper; // DTO -> Entity 객체 변환
    private final BoardRepository boardRepository; // 실제 DB에 일을 시키는 기능.
    // 댓글 기능 추가,
    private final ReplyRepository replyRepository;

    @Override
    public Long register(BoardDTO boardDTO) {
        // Board board = modelMapper.map(boardDTO, Board.class);
        Board board = dtoToEntity(boardDTO);
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {

//        Optional<Board> result = boardRepository.findById(bno);
        Optional<Board> result = boardRepository.findByIdWithImages(bno);
        Board board = result.orElseThrow();
        // 변환 기능도, 메서드를 만들어서, 메서드 교체 작업.
//        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        BoardDTO boardDTO = entityToDto(board);
        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        // 1)디비에서 데이터를 가져오고, 가져온 내용을 화면에서, 2)변경할 내용으로 교체후, 3) 저장(수정)
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow();
        //2)
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        //3)
        // 첨부 이미지를 이용한 수정작업,
        // 기존 이미지를 모두 삭제 후, 새로운 이미지 추가
        board.clearImages();// 첨부 이미지의 부모 게시글 번호를 null 로 변경, 고아객체,

        // 화면으로부터, 첨부된 이미지가 있다면, 그러면, 추가.
        if(boardDTO.getFileNames() != null) {
            for(String fileName : boardDTO.getFileNames()) {
                // 예시
                // fileName : 5b418a60-407e-406e-991e-db88d35ea426_크롬기준-로컬스토리지 저장소 확인 방법.PNG
                // fileName : UUID_원본파일명
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }

        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        // 게시글을 삭제하면, 댓글은
        // 댓글의 존재 여부를 확인 후, 있으면 삭제하기.
        List<Reply> result = replyRepository.findByBoard_Bno(bno);
        boolean checkReplyList = result.isEmpty() ? false : true;
        if(checkReplyList) {
            replyRepository.deleteByBoard_Bno(bno);
        }
        // 게시글만 삭제, 참고로, 연관관계로, 게시글이 삭제가 되면,
        // 자동으로, 첨부 이미지는 삭제가됨.
        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        // result 안에 페이징 준비물이 많이 들어 있다.
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
        // 페이징 처리가 된 데이터 10개 목록 가져오고,
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        // 전체 갯수등 가져오기.
        int total = (int)result.getTotalElements();
        // PageResponseDTO 타입으로 객체를 생성.
        PageResponseDTO<BoardDTO> pageResponseDTO = PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();

        return pageResponseDTO;
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");
        // result 안에 페이징 준비물이 많이 들어 있다.
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);
        // 페이징 처리가 된 데이터 10개 목록 가져오고,
        // 자동으로, queryDSL 에서 자동으로 변환을 해주기 때문에, 수동으로 변환 안해도 됩니다.
        // searchWithReplyCount , 내부에서 확인.

//        List<BoardDTO> dtoList = result.getContent().stream()
//                .map(board -> modelMapper.map(board, BoardDTO.class))
//                .collect(Collectors.toList());
        // 전체 갯수등 가져오기.
        int total = (int)result.getTotalElements();
        // PageResponseDTO 타입으로 객체를 생성.
        PageResponseDTO<BoardListReplyCountDTO> pageResponseDTO = PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total(total)
                .build();

        return pageResponseDTO;
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 수정1, <========searchWithAll 교체 ========================
        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types,keyword,pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }
}
