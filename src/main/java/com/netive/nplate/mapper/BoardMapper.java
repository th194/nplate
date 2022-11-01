package com.netive.nplate.mapper;

import java.util.List;

import com.netive.nplate.domain.Criteria;
import org.apache.ibatis.annotations.Mapper;

import com.netive.nplate.domain.BoardDTO;

@Mapper
public interface BoardMapper {
	public int insertBoard(BoardDTO params);
	public BoardDTO selectBoardDetail(Long idx);
	public int updateBoard(BoardDTO params);
	public int deleteBoard(Long idx);
	public List<BoardDTO> selectBoardList(Criteria cri);
	public int selectBoardTotalCount(Criteria cri);
	public boolean cntPlus(Long idx);
	public List<BoardDTO> selectBoardSearchList(Criteria cri);
}
