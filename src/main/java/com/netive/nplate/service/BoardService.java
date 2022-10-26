package com.netive.nplate.service;

import java.util.List;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.BoardFileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface BoardService {
	public boolean registerBoard(BoardDTO board);
	public boolean registerBoard(BoardDTO board, MultipartFile[] files, String path);
	public BoardDTO getBoardDetail(Long idx);
	public Long updateBoard(BoardDTO board);
	public BoardDTO findBoardIdx(Long idx);
	public boolean deleteBoard(Long idx);
	public List<BoardDTO> getBordList();
	public boolean cntPlus(Long idx);
	public List<BoardFileDTO> getFileList(Long idx);
}
