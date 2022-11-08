package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
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
}
