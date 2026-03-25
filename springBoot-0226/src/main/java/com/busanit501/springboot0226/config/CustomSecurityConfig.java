package com.busanit501.springboot0226.config;

import com.busanit501.springboot0226.security.CustomUserDetailsService;
import com.busanit501.springboot0226.security.handler.Custom403Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@Log4j2
@RequiredArgsConstructor
// 시큐리티 설정 on 추가
@EnableWebSecurity
// 권한별 설정 추가
@EnableMethodSecurity()
public class CustomSecurityConfig {

    // 자동 로그인 순서1, 디비 접근 도구
    private final DataSource dataSource;
    // 시큐리티에서 로그인 처리를 담당하는 도구-,로그인한 유저 처리를 담당하는 부서
    private final CustomUserDetailsService customUserDetailsService;

    // 순서1
    // 여기 메서드에, 중요한 시큐리티 인증, 인가 설정을 모두 함.
    // 여기가 가장 중요한 설정.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("CustomSecurityConfig : SecurityFilterChain, 스프링 시작시, 검사를 한다. ");

        // 순서1
        // 폼 방식으로 로그인 하겠다.
        http.formLogin(
                formLogin ->
                        formLogin.loginPage("/member/login")
                // 기본 페이지로 설정.
//                Customizer.withDefaults()
        );

        // 순서2
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        // 순서3,
        //로그인 후, 성공시 리다이렉트 될 페이지 지정, 간단한 버전.
        http.formLogin(formLogin ->
                formLogin.defaultSuccessUrl("/board/list",true)
        );

        // 순서4,
        // 구버전 문법에서, 최신 문법으로 변경,
//        http.authorizeRequests()
        // 최신 Spring Security 6.x 람다 DSL 문법 적용
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**","/image/**").permitAll()
                        // 리스트는 기본으로 다 들어갈수 있게.
                        .requestMatchers("/", "/board/list","/logout", "/member/login","/images/**").permitAll()
                        // 로그인 후 확인 하기.
//                .requestMatchers("/board/register").hasRole("USER")
                        // 일반 유저와 관리자 모두 글쓰기 화면에 접근 가능
                        .requestMatchers("/board/register").hasAnyRole("USER", "ADMIN")
                        //
                        .requestMatchers("/admin","/board/modify").hasRole("ADMIN")
                        // 위의 접근 제어 목록 외의 , 다른 어떤 요청이라도 반드시 인증이 되어야 접근이 된다.
                        .anyRequest().authenticated()
        );

        // 순서5 , 자동로그인.
        http.rememberMe(
                httpSecurityRememberMeConfigurer
                        -> httpSecurityRememberMeConfigurer.key("12345678")
                        .tokenRepository(persistentTokenRepository()) // 밑에서, 토큰 설정 추가해야해서,
                        .userDetailsService(customUserDetailsService)
                        .tokenValiditySeconds(60*60*24*30) //30일
        );

        //순서6, 403 에러 처리 등록하기.
        http.exceptionHandling(
                exception -> {
                    exception.accessDeniedHandler(accessDeniedHandler());
                });

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

    // 자동 로그인 순서3,
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // 시큐리티에서 정의 해둔 구현체
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
    // 자동 로그인 순서3,


    // 403 핸들러 추가.
    // 설정 클래스에 추가하기.
    // 레스트용, Content-Type, application/json 형태 일 때만 동작을하고,
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }
}
