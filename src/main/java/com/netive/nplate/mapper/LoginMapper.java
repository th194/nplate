package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoginMapper {

    List<BoardDTO> getBordListById(String id);
}
