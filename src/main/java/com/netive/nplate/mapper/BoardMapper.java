package com.netive.nplate.mapper;

import java.util.List;
import java.util.Map;

import com.netive.nplate.domain.SearchDTO;
import org.apache.ibatis.annotations.Mapper;

import com.netive.nplate.domain.BoardDTO;

@Mapper
public interface BoardMapper {
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
	// 게시글 조회수 증가
	public boolean cntPlus(Long idx);
	// 아이디 여러개로 조회
	List<BoardDTO> getBordListByIds(Map map);
	// 서치 키워드로 조회
	List<BoardDTO> getBordListByKeyword(SearchDTO dto);

}
