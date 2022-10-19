package com.netive.nplate.mapper;

import com.netive.nplate.domain.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    List<MemberDTO> listMembers();
}
