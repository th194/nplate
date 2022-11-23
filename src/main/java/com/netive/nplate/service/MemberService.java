package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;

import java.util.List;
import java.util.Map;

public interface MemberService {

    // 회원 목록
    List<MemberDTO> getMemberList();

    // 회원 정보 보기
    MemberDTO getMemberInfo(String id);

    // 로그인
    MemberDTO login(String id, String pwd);

    // 회원 탈퇴
    int deleteMember(String id);

    // 회원 정보 수정
    int updateInfo(MemberDTO memberDTO);

    // 아이디 중복조회
    int checkOverlappedId(String id);

    // 회원 비밀번호 수정
    int updatePwd(MemberDTO memberDTO);

    // 회원(팔로잉) 닉네임 조회
    List<MemberDTO> getFollowingInfo(Map map);

    // 회원 정보 보기(ID, 닉네임만 조회)
    MemberDTO getUserInfo(String id);
}
