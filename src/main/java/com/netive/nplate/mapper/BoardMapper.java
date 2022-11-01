package com.netive.nplate.mapper;

import java.util.List;

import com.netive.nplate.domain.SearchDTO;
import org.apache.ibatis.annotations.Mapper;

import com.netive.nplate.domain.BoardDTO;

@Mapper
public interface BoardMapper {
//	public int insertBoard(BoardDTO params);
//	public BoardDTO selectBoardDetail(Long idx);
//	public int updateBoard(BoardDTO params);
//	public int deleteBoard(Long idx);
//	public List<BoardDTO> selectBoardList(Criteria cri);
//	public int selectBoardTotalCount(Criteria cri);
//	public boolean cntPlus(Long idx);
//	public List<BoardDTO> selectBoardSearchList(Criteria cri);
	// 게시글 저장
	public void insertBoard(BoardDTO board);
	// 게시글 상세보기
	public BoardDTO selectBoardDetail(Long idx);
	// 게시글 수정
	public void updateBoard(BoardDTO board);
	// 게시글 삭제
	public void deleteBoard(Long idx);
	// 게시글 목록 조회
	public List<BoardDTO> selectBoardList(SearchDTO params);
	// 총 게시글 수 조회
	public int count(SearchDTO params);
}
