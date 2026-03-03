package com.busanit501.springboot0226.repository.search;

import com.busanit501.springboot0226.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    // 검색의 결과도 페이징 처리 예정
    Page<Board> search1(Pageable pageable);

    // 검색어(제목, 내용) 페이징 처리 적용하는 메소드
    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);
}
