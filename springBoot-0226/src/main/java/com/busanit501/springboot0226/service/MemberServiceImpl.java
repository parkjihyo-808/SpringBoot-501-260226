package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.domain.Member;
import com.busanit501.springboot0226.domain.MemberRole;
import com.busanit501.springboot0226.dto.MemberJoinDTO;
import com.busanit501.springboot0226.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final ModelMapper modelMapper; // dto <-> entity  타입 변환기
    private final MemberRepository memberRepository; // 디비에 로직처리 도구 crud
    private final PasswordEncoder passwordEncoder;  // 평문 패스워드, -> 암호화하는 도구.

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {

        // 1. 회원 가입하는 아이디가 존재하는지 확인.
        String mid = memberJoinDTO.getMid();
        boolean exist = memberRepository.existsById(mid);
        if(exist){
            throw new MidExistException();
        }

        // 2. 회원 가입 가능하면, 진행.
        Member member = modelMapper.map(memberJoinDTO, Member.class);
        // 화면에서 전달받은 패스워드는 일반 평문 패스워드 -> 암호화 해주기.
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
        // 권한 -> 일반 회원 , 관리자,
        member.addRole(MemberRole.USER);
        //
        log.info("멤버 서비스에서, 회원 가입 로직 진행중이고, 변환 된 데이터 확인 member: " + member);
        memberRepository.save(member);
    }
}
