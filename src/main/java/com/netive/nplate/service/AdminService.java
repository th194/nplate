package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;

import java.util.List;

public interface AdminService {
    // 회원 목록(권한에 따른 분류(일반회원, 관리회원, admin) / 만료 회원 목록 / 전체)
    // type: user, manager, admin, expired, all
    List<MemberDTO> getMemberList(String type);

    // 회원 만료(관리자 페이지용 회원 삭제)
    int putoutMember(String id);

    // 회원 만료(관리자 페이지용 회원 삭제) 취소
    int enableMember(String id);

    // 회원 권한 변경
    int changeMemberRole(String id);
}
