package com.busanit501.springboot0226.security;

import com.busanit501.springboot0226.domain.Member;
import com.busanit501.springboot0226.repository.MemberRepository;
import com.busanit501.springboot0226.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
// 시큐리티에서, 요구하는 조건이 있는데, 너희가 로그인 처리를 하려면,
// 내가 원하는 규격이 있는데, 거기에 맞춰서 작업 해줘.
//
public class CustomUserDetailsService implements UserDetailsService {

    // 주입
//    private final PasswordEncoder passwordEncoder;

    // 추가
    private final MemberRepository memberRepository;

//    public CustomUserDetailsService() {
//        this.passwordEncoder = new BCryptPasswordEncoder();
//        // 추가
//        this.memberRepository = memberRepository;
//    };




    // 이 메서드는 로그인시, 처리를 담당하는 메서드,
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: " + username);
        // 화면에서 로그인한 유저명을 받아서, 디비에 연결해서,
        // 비교후, 로그인 (회원가입 등. 작업을 진행함.)


        // 연습용
        // 더미 유저를 생성해서, 확인용.
//        UserDetails userDetails = User.builder().username("lsy3709")
//                // 평문으로 패스워드를 지정했는데,
////                .password("1234")
//                // 패스워드 암호화
//                .password(passwordEncoder.encode("1234"))
//                // 이유저의 권한, 인가 설정, 로그인한 유저,
////                .authorities("ROLE_USER")
//                //관리자
//                .authorities("ROLE_ADMIN")
//                .build();

        // 실제 데이터베이스 연동용.
        // 실제 데이터베이스에서, 디비 조회 후, 로그인 처리 확인.
        Optional<Member> result = memberRepository.getWithRoles(username);
        if(result.isEmpty()){
            throw new UsernameNotFoundException("해당 유저가 없습니다. ");
        }
        Member member = result.get();
        log.info("확인2 loadUserByUsername에서 화면으로부터 입력받은 로그인 정보로 ,디비 조회 확인. member : " + member);



        // MemberSecurityDTO ,사실은 반환 타입 , UserDetails 타입이다,
        // 왜? User 클래스를 상속을 받아서 ,
        MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                false,
                // 디비에 저장된 USER, ADMIN
                // -> 시큐리티에서 원하는 구조 인 ROLE_USER, ROLE_ADMIN 형태로 변경하기.
                member.getRoleSet().stream().map(memberRole ->
                        new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toList())
        );
        log.info("확인 3 loadUserByUsername에서 화면으로부터 입력받은 로그인 정보로 ,디비 조회 확인2. memberSecurityDTO : " + memberSecurityDTO);

        return memberSecurityDTO;
    }

}
