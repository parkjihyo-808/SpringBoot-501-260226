package com.busanit501.springboot0226.repository;

import com.busanit501.springboot0226.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,String> {
    // 부모테이블 : Member , 자식테이블 : MemberRole, <-> 1 : N
    // 영속성 컨텍스트의 특성상, 부모 테이블에 속해 있는 자식테이블 조회시,
    // 디비서버에 요청에 각각 매번하게됨. 그러면, 디비 서버 입장에서, 이왕이면,
    // 한번에 모아서 요청을 했으면, 낱개 왔다리 갔다리 안했으면 좋겠다.
    // 질문 query 도 조금 모아서 요청 해줘.
    // 2개의 테이블을 조인 함. 한번만 호출하면, 조인되어서 결과를 받을수 있다.
    //
    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(String mid);

    // 추가로, 소셜 로그인시, 유저 정보를 확인.
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByEmail(String email);

    // 기능추가, 유저의 패스워드를 변경하는 기능.
    @Modifying
    @Transactional
    @Query("update Member m set m.mpw = :mpw where m.mid = :mid")
    void updatePassword(@Param("mpw") String mpw, @Param("mid") String mid);
}
