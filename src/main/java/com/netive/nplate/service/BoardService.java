package com.netive.nplate.service;

import java.util.List;

import com.netive.nplate.domain.BoardDTO;

public interface BoardService {
	public boolean registerBoard(BoardDTO board);
	public BoardDTO getBoardDetail(Long idx);
	public boolean deleteBoard(Long idx);
	public List<BoardDTO> getBordList();
	public boolean cntPlus(Long idx);
}
