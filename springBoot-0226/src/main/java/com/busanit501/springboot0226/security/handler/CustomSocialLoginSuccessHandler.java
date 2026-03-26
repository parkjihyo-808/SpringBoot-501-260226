package com.busanit501.springboot0226.security.handler;

import com.busanit501.springboot0226.security.dto.MemberSecurityDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor

public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("===============onAuthenticationSuccess===============================");
        log.info("authentication.getPrincipal() : "+ authentication.getPrincipal());

        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();
        String encodedPw = memberSecurityDTO.getMpw();
        log.info("encodedPw : " + encodedPw);

        // 임의로 소셜 로그인시, 회원 패스워드 임의로 1111 로 지정함.
        if(memberSecurityDTO.isSocial() && (passwordEncoder.equals("1111"))
                || passwordEncoder.matches("1111",memberSecurityDTO.getMpw())){
            log.info("소셜로 로그인시 임시 비밀번호 1111 를 그대로 사용한 경우");
            // 임시, 회원 수정 페이지.
            response.sendRedirect("/member/modify");
            return;
        } else {
            response.sendRedirect("/board/list");
        }
    }
}