package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;

import java.util.List;

public interface MemberService {

    // 회원 목록
    List<MemberDTO> getMemberList();

    // 회원 등록
    boolean registerMember(MemberDTO memberDTO);

    // 회원 정보 보기
    MemberDTO getMemberInfo(String id);

    // 로그인
    MemberDTO login(String id, String pwd);

    // 회원 탈퇴
    boolean deleteMember(String id);

    // 회원 정보 수정
    int updateInfo(MemberDTO memberDTO);
}
