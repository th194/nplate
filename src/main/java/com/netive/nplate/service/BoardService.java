package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.paging.PagingResponse;

public interface BoardService {
//	public boolean registerBoard(BoardDTO board);
//	public boolean registerBoard(BoardDTO board, MultipartFile[] files, String path);
//	public BoardDTO getBoardDetail(Long idx);
//	public Long updateBoard(BoardDTO board);
//	public boolean deleteBoard(Long idx);
//	public List<BoardDTO> getBordList(Criteria cri);
//	public int selectBoardTotalCount(Criteria cri);
//	public boolean cntPlus(Long idx);
//	public List<BoardFileDTO> getFileList(Long idx);
//	public List<BoardDTO> getSearchList(Criteria cri);
	// 게시글 저장
	public Long registerBoard(BoardDTO board);
	// 게시글 상세보기
	public BoardDTO getBoardDetail(Long idx);
	// 게시글 수정
	public Long updateBoard(BoardDTO board);
	// 게시글 삭제
	public Long deleteBoard(Long idx);
	// 게시글 목록 조회
	public PagingResponse<BoardDTO> getBoardList(SearchDTO params);
}
