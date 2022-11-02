package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoginMapper {

    List<BoardDTO> getBordListById(SearchDTO params);

    // 글 개수 세기 todo 필요 없으면 지움
    int countPostsById(String id);
}
