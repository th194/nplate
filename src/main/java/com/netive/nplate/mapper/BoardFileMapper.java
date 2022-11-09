package com.netive.nplate.mapper;

import com.google.gson.JsonArray;
import com.netive.nplate.domain.BoardFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardFileMapper {

    // 게시판 파일 저장
    public int registerBoardFile(BoardFileDTO dto);

    // 게시판 파일 목록
    public List<BoardFileDTO> selectBoardFile(Long idx);

    // 게시판 파일 수정
    public int updateBoardFile(BoardFileDTO dto);

    // 게시판 파일 삭제
    public int deleteBoardFile(Long idx);
}
