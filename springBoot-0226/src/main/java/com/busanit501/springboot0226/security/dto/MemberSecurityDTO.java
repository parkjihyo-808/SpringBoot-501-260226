package com.busanit501.springboot0226.security.dto;

// 기존에 멤버 테이블 , 엔티티 클래스 생성시, User 만들어도 상관없지만, 스프링에서 제공하는
// 이 클래스를 반드시 이용해서 작업 하는 것을 추천,
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
// 시큐리티에서는 원하는 템플릿 구조,
// 원하는 타입 : UserDetails ,
// 간단한 방법은 시큐리티에서 제공하는 User 클래스를 상속 받으면됨.
public class MemberSecurityDTO extends User implements OAuth2User {
    private String mid;
    private String mpw;
    private String email;
    private boolean del;
    private boolean social;
    // 소셜 로그인시, 유저 정보를 담아둘 객체 정의, 맵 ,
    private Map<String, Object> props;


    public MemberSecurityDTO(String username, String password,
                             String email, boolean del, boolean social,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.mid = username;
        this.mpw = password;
        this.email = email;
        this.del = del;
        this.social = social;

    }
    @Override // 소셜 로그인한 유저 정보의 담아둔 map 조회
    public Map<String,Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public String getName() {
        return this.mid; // 로그인시, mid 와 비교해서 작업 할 예정.
    }

}
