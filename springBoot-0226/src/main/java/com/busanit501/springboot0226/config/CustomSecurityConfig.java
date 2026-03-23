package com.busanit501.springboot0226.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    // 순서1
    // 여기 메서드에, 중요한 시큐리티 인증, 인가 설정을 모두 함.
    // 여기가 가장 중요한 설정.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("CustomSecurityConfig : SecurityFilterChain, 스프링 시작시, 검사를 한다. ");

        // 폼 방식으로 로그인 하겠다.
        http.formLogin(
//                formLogin ->
//                        formLogin.loginPage("/member/login")
                // 기본 페이지로 설정.
                Customizer.withDefaults()
        );
        return http.build();
    }

    // 순서2
    // css, js, 등 정적 자원은 시큐리티 필터에서 제외하기
    // 임포트 선택시 참고:  import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("시큐리티 동작 확인 ====webSecurityCustomizer======================");
        return (web) ->
                web.ignoring()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    //순서3
    // 패스워드 암호화를 해주는 도구, 스프링 설정.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
