package com.busanit501.springboot0226.security;

import com.busanit501.springboot0226.domain.Member;
import com.busanit501.springboot0226.domain.MemberRole;
import com.busanit501.springboot0226.repository.MemberRepository;
import com.busanit501.springboot0226.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    // 디비에서, 조회 하기 위한 도구 준비.
    private final MemberRepository memberRepository;
    // 평문 -> 암호화하는 도구.
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService에서, 인증한 유저 정보 확인 userRequest : "  + userRequest);

        // 동의항목에 동의한 수집 정보 3가지중에서, 일단 1) 닉네임 2) 프로필 이미지 링크 주소 3) 이메일
        // 이정보를 서버에서 사용하기 쉽게 , 데이터 변환 작업 하기.
        // userRequest 여기 객체에 들어가 있다. ====================전달 받은 정보 모두 사용 하는것은 아니다, 필요한 것만 뽑아 사용하기.
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("clientName 확인 : "+ clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String,Object> paramMap = oAuth2User.getAttributes();
        paramMap.forEach( (key,value) -> {
            log.info("==========oAuth2User.getAttributes() -> paramMap -> 해당 속성 조회 해보기. ============================");
            log.info("key : "+ key + ", value : " + value);
        });

        String email = null;

        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }

        log.info("email : "+ email);


//        return super.loadUser(userRequest);
        return generateDTO(email,paramMap);
//        return super.loadUser(userRequest);
    } // loadUser

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        // 디비에 해당 이메일로 가입한 유저 정보가 있는지 확인.
        Optional< Member> result = memberRepository.findByEmail(email);
        // 소셜 로그인시, 해당 이메일이 디비에 저장이 안된 경우.
        if(result.isEmpty()){
            // 회원 추가 하기, -- mid : 카카오 로그인 이메일 , / 패스워드 : 1111, 임의로 설정하기.
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1111"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);
            // 디비에 저장 후에는, 시큐리티에서 원하는 타입인 UserDetails 타입으로 변환해서, 반환하기.
            // 엔티티 클래스 타입 -> MemberSecurityDTO 타입으로 변경해야함.
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    email,"1111",email, false, true, Arrays.asList(new SimpleGrantedAuthority("ROLL_USER"))
            );
            // 소셜 로그인 정보를 담아둘 맵 객체를 , DTO 에 담아두기.
            memberSecurityDTO.setProps(params);
            log.info("소셜 로그인시, 이메일이 없는 경우, 디비에서 dto로 변환 작업 결과 ,memberSecurityDTO : "+ memberSecurityDTO);
            return memberSecurityDTO;

        }
        // 소셜 로그인시, 해당 이메일이 디비에 저장이 된 경우.
        else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    member.getMid(), member.getMpw(), member.getEmail(), member.isDel(), member.isSocial(),
                    member.getRoleSet().stream().map(memberRole ->
                            new SimpleGrantedAuthority("ROLE_"+memberRole.name())).collect(Collectors.toList())
            );
            log.info("소셜 로그인시, 이메일이 있는 경우, 디비에서 dto로 변환 작업 결과 ,memberSecurityDTO : "+ memberSecurityDTO);
            return  memberSecurityDTO;
        }

    } //generateDTO

    private String getKakaoEmail(Map<String, Object> paramMap) {
        log.info("================paramMap에서 이메일 정보 가져오기 ========================");
        Object value = paramMap.get("kakao_account");
        log.info("value : " + value);
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");
        log.info("email : " + email);
        return email;

    }

}
// 참고, 전달 받은 정보의 객체 트리 구조,
// 이메일 정보 가져오기, 해당 객체 트리 구조 예시2
//  ================paramMap에서 이메일 정보 가져오기 ========================
//CustomOAuth2UserService   : value : {profile_nickname_needs_agreement=false, profile_image_needs_agreement=false, profile={nickname=이상용, thumbnail_image_url=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_110x110.jpg, profile_image_url=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_640x640.jpg, is_default_image=false, is_default_nickname=false}, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=lsy3709@kakao.com}
//CustomOAuth2UserService   : email : lsy3709@kakao.com
// ===========================================
// [Principal=Name: [4370163525],
// Granted Authorities: [[OAUTH2_USER, SCOPE_account_email, SCOPE_profile_image, SCOPE_profile_nickname]],
// User Attributes: [{id=4370163525,
// connected_at=2025-07-28T02:27:44Z,
// properties={nickname=이상용,
// profile_image=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_640x640.jpg,
// thumbnail_image=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_110x110.jpg},
// kakao_account={profile_nickname_needs_agreement=false,
// profile_image_needs_agreement=false,
// profile={nickname=이상용,
// thumbnail_image_url=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_110x110.jpg,
// profile_image_url=http://k.kakaocdn.net/dn/si5pD/btsNAKP3qpo/k980AtXbaNVWWGtxirbYR1/img_640x640.jpg,
// is_default_image=false, is_default_nickname=false},
// has_email=true, email_needs_agreement=false, is_email_valid=true,
// is_email_verified=true, email=lsy3709@kakao.com}}], Credentials=[PROTECTED],
// Authenticated=true,
// Details=WebAuthenticationDetails
// [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=3327CB27ECAE85E8C7CEF0D9FD0997F5],
// Granted Authorities=[OAUTH2_USER, SCOPE_account_email, SCOPE_profile_image, SCOPE_profile_nickname]]]
// to HttpSession [org.apache.catalina.session.StandardSessionFacade@5c5d52af]
