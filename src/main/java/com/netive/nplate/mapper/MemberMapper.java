package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    // 회원 목록
    List<MemberDTO> listMembers();

    // 회원 등록
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

}
