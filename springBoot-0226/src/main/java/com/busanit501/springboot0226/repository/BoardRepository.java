package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Board;
import com.busanit501.springboot0226.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
// 아무 기능이 없지만, 우리는 기본 기능을 이용해서, CRUD 를 테스트 할수 있다.

    // @EntityGraph 를이용해서, 조인을 이용한, 쿼리 1번 호출, 조인을 할 대상, imageSet
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);
}
