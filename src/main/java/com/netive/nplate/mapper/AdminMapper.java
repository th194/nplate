package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    // 회원 여러명(스프링 시큐리티)
    int addMembers(MemberDTO dto);

    // 권한별 회원 조회(만료되지 않음)
    List<MemberDTO> listMembersTypeRole(String role);

    // 만료 회원 조회
    List<MemberDTO> listExpiredMembers();

}
