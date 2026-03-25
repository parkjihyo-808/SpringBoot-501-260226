package com.busanit501.springboot0226.security.handler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("Custom403Handler: Access Denied");
        // 서버 -> 화면 전달한 메세지 작성 작업,
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // 요청 정보 확인하기. JSON 인지 여부 확인.
        String contentType= request.getHeader("Content-Type");
        boolean jsonRequest = contentType.startsWith("application/json");
        log.info("json의 여부 확인: " +jsonRequest);
        // 일반 request ,
        if(jsonRequest){
            response.sendRedirect("/member/login?error=ACCESS_DENIED");
        } else {
            log.info("Custom403Handler: Access Denied");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error/403");
            dispatcher.forward(request,response);
        }
    }
}
