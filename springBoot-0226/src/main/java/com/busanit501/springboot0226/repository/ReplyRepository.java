package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 아무 기능이 없어도, 상속을 받아서, 기본적인 CRUD 가능함.

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    // 부모 게시글 번호를 이용해서, 해당 댓글을 삭제하기.
    // 예시) 게시글 1번 , 댓글 3개 있으면,
    // 게시글 1번을 삭제시, where 조건부 해당 댓글 3개 삭제.
    void deleteByBoard_Bno(Long bno);
}
