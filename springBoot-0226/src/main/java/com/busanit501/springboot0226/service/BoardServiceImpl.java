package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.repository.BoardRepository;
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

    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
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
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
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
}
