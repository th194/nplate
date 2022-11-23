package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.PageDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.paging.PagingResponse;

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
	// 관리자가 게시글 삭제
	public void deleteAdminBoard(String params);
	// 게시글 조회수 증가
	public boolean cntPlus(Long idx);
	// 게시글 전체 목록
	public List<BoardDTO> getAllBoardList(PageDTO params);
	// 게시글 총 수
	public int count(PageDTO params);
	// 특정 사용자 게시글 수
	public int countById(PageDTO params);
	// 특정 ID 게시글 조회
	List<BoardDTO> getBordListById(SearchDTO dto);
	// 아이디 여러개로 조회(팔로잉)
	List<BoardDTO> getBordListByIds(Map map);
	// 서치 키워드로 조회
	List<BoardDTO> getBordListByKeyword(SearchDTO dto);
}
