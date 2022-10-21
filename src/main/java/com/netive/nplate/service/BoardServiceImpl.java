package com.netive.nplate.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // 게시글 등록/수정
    @Override
    public boolean registerBoard(BoardDTO board) {
        int queryResult = 0;
        if (board.getBbscttNo() == null) {
            queryResult = boardMapper.insertBoard(board);
        } else {
            queryResult = boardMapper.updateBoard(board);
        }
        System.out.println("queryResult = " + queryResult);
        return (queryResult == 1) ? true : false;
    }

    // 게시글 상세
    @Override
    public BoardDTO getBoardDetail(Long idx) {
        return boardMapper.selectBoardDetail(idx);
    }

    // 게시글 삭제
    @Override
    public boolean deleteBoard(Long idx) {
        int queryResult = 0;
        BoardDTO boardDTO = boardMapper.selectBoardDetail(idx);

//		if(boardDTO != null && "N".equals(boardDTO.getDeleteYn())) {
//			queryResult = boardMapper.deleteBoard(idx);
//		} TODO 게시글 삭제 구현
        return (queryResult == 1) ? true : false;
    }

    // 게시글 목록
    @Override
    public List<BoardDTO> getBordList() {
        List<BoardDTO> boardList = Collections.emptyList();
        int boardTotalCount = boardMapper.selectBoardTotalCount();
        if (boardTotalCount > 0) {
            boardList = boardMapper.selectBoardList();
        }
        return boardList;
    }

    // 게시글 조회수 증가
    @Override
    public boolean cntPlus(Long idx) {
        return boardMapper.cntPlus(idx);
    }


}
