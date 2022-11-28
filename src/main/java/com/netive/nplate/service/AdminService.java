package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;

import java.util.List;

public interface AdminService {

    // 회원 목록(권한에 따른 분류(일반회원, 관리회원, admin) / 만료 회원 목록)
    // type: user, manager, admin, expired
    List<MemberDTO> getMemberList(String type);

}
