package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    // 회원 여러명(스프링 시큐리티)
    int addMembers(MemberDTO dto);
}
