package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.domain.SearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface LoginMapper {
    // 좋아요 누른 게시글 목록
    List<BoardDTO> getLikes(Map map);

    MemberDTO loginBySpringSecurity(String id);
}
