package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Getter
@Setter
@ToString
public class MemberDTO implements UserDetails {
    private String id;          // 아이디
    private String name;        // 이름
    private String sexCd;       // 성별(코드: F,M)
    private String birthday;    // 생년월일(YYYYMMDD)
    private String tel;         // 휴대폰번호(01012345678)
    private String pwd;         // 비밀번호
    private String email;       // 이메일
    private String area;        // 지역(코드)
    private String srbde;       // 가입일
    private String nickName;    // 닉네임
    private String profileImg;  // 프로필사진

    // 스프링 시큐리티 관련
    private String role;        // 회원 권한(ROLE_ADMIN, ROLE_USER)
    private Date expiredDate;   // 계정 만료 날짜
    private boolean accountNonExpired; // 계정 만료 여부(DB에는 없음)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.pwd;
    }

    @Override
    public String getUsername() {
        return this.id;
    }
    
    // 계정 만료여부
    @Override
    public boolean isAccountNonExpired() {
        return this.expiredDate.after(new Date());
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
