package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberMapper {
    // 회원 등록(스프링 시큐리티)
    int registerMember(MemberDTO dto);

    // 회원 아이디로 조회
    MemberDTO selectMemberById(String id);

    // 로그인
    MemberDTO login(String id, String pwd);

    // 회원탈퇴
    int deleteMember(String id);

    // 회원정보 수정
    int updateInfo(MemberDTO dto);

    // 회원 아이디 중복 조회
    int checkOverlappedId(String id);

    // 비밀번호 수정
    int updatePwd(MemberDTO dto);

    // 회원(팔로잉) 닉네임 조회
    List<MemberDTO> getFollowingInfo(Map map);

    // 회원 정보 보기(ID, 닉네임만 조회)
    MemberDTO getUserInfo(String id);
}
