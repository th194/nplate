package com.netive.nplate.service;

import java.util.List;
import java.util.Map;

import com.netive.nplate.domain.SearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.mapper.BoardMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // 게시글 등록
    @Override
    public Long registerBoard(final BoardDTO board) {
        boardMapper.insertBoard(board);
        return board.getBbscttNo();
    }

    // 게시글 상세
    @Override
    public BoardDTO getBoardDetail(final Long idx) {
        return boardMapper.selectBoardDetail(idx);
    }

    // 게시글 수정
    @Override
    @Transactional
    public Long updateBoard(final BoardDTO board) {
        boardMapper.updateBoard(board);
        return board.getBbscttNo();
    }

    // 게시글 삭제
    @Override
    public Long deleteBoard(final Long idx) {
        boardMapper.deleteBoard(idx);
        return idx;
    }

    // 게시글 조회수 증가
    @Override
    public boolean cntPlus(Long idx) {
        return boardMapper.cntPlus(idx);
    }

    // 특정 ID 게시글 조회
    @Override
    public List<BoardDTO> getBordListById(SearchDTO dto) {
        return boardMapper.getBordListById(dto);
    }

    // 아이디 여러개로 조회(팔로잉)
    @Override
    public List<BoardDTO> getBordListByIds(Map map) {
        return boardMapper.getBordListByIds(map);
    }

    // 서치 키워드로 조회
    @Override
    public List<BoardDTO> getBordListByKeyword(SearchDTO dto) {
        return boardMapper.getBordListByKeyword(dto);
    }
}
