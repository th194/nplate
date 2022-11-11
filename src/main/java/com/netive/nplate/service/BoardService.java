package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;

import java.util.List;
import java.util.Map;

public interface BoardService {
	// 게시글 저장
	public Long registerBoard(BoardDTO board);
	// 게시글 상세보기
	public BoardDTO getBoardDetail(Long idx);
	// 게시글 수정
	public Long updateBoard(BoardDTO board);
	// 게시글 삭제
	public Long deleteBoard(Long idx);
	// 게시글 조회수 증가
	public boolean cntPlus(Long idx);
	// 아이디 여러개로 조회(팔로잉)
	List<BoardDTO> getBordListByIds(Map map);
	// 서치 키워드로 조회
	List<BoardDTO> getBordListByKeyword(SearchDTO dto);
}
