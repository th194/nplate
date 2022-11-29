package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    // 스프링 시큐리티 회원 여러명 등록(관리자)
    int addMembers(MemberDTO dto);
    
    // 권한별 회원 조회(만료되지 않음)
    List<MemberDTO> listMembersTypeRole(String role);

    // 만료 회원 조회
    List<MemberDTO> listExpiredMembers();

    // 전체 회원 조회
    List<MemberDTO> listMembers();

    // 회원 만료(관리자 페이지용 회원 삭제)
    int putoutMember(String id);

    // 회원 만료 취소
    int enableMember(String id);

    // 회원 권한 변경(일반 -> 매니저)
    int changeMemberRole(String id);
}
