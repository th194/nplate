package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;

import java.util.List;

public interface MemberService {

    // 회원 목록
    List<MemberDTO> getMemberList();

    // 회원 등록
    boolean registerMember(MemberDTO memberDTO);
}
