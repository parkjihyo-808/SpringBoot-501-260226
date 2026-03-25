package com.busanit501.springboot0226.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity {

    @Id
    private String mid;

    private String mpw;
    private String email;
    private boolean del; // 삭제 여부

    private boolean social; // 카카오 로그인 여부, (소셜 로그인 여부)

    @ElementCollection(fetch = FetchType.LAZY) // 실제 데이터를 사용하는 시점에 로드 하겠다. 늦게 불러오기
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>(); //권한 ,USER, ADMIN

    public void changePassword(String newPassword) {
        this.mpw = newPassword;
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public void changeDel(boolean del) {
        this.del = del;
    }

    public void addRole(MemberRole role) {
        this.roleSet.add(role);
    }

    public void clearRole() {
        this.roleSet.clear();
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

}
