package com.netive.nplate.service;

import java.util.List;

import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.mapper.BoardFileMapper;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.util.FileUtils;
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

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private BoardFileMapper boardFileMapper;

    // 게시글 등록
    @Override
    @Transactional
    public Long registerBoard(final BoardDTO board) {
//        int queryResult = 0;
//        if (board.getBbscttNo() == null) {
//            queryResult = boardMapper.insertBoard(board);
//        } else {
//            queryResult = boardMapper.updateBoard(board);
//        }
//        return (queryResult == 1) ? true : false;
        boardMapper.insertBoard(board);
        return board.getBbscttNo();
    }

//    @Override
//    public boolean registerBoard(BoardDTO board, MultipartFile[] files, String path) {
//        int queryResult = 1;
//
//        if (registerBoard(board) == false) {
//            return false;
//        }
//
//        File file = new File(path);
//
//        List<File> fileSrc = fileUtils.getImgFileList(file, path);
//
//
//        return (queryResult > 0);
//    }

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
//        int queryResult = 0;
//        queryResult = boardMapper.deleteBoard(idx);
//        System.out.println("삭제====================================="+queryResult);
//        return (queryResult == 1) ? true : false;
        boardMapper.deleteBoard(idx);
        return idx;
    }

    // 게시글 목록
    @Override
    public PagingResponse<BoardDTO> getBoardList(final SearchDTO params) {
//        List<BoardDTO> boardList = Collections.emptyList();
//        int boardTotalCount = boardMapper.selectBoardTotalCount(cri);
//        if (boardTotalCount > 0) {
//            boardList = boardMapper.selectBoardList(cri);
//        }
//        return boardList;
        int count = boardMapper.count(params);
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        List<BoardDTO> list = boardMapper.selectBoardList(params);
        return new PagingResponse<>(list, pagination);
    }

    // 게시글 총 갯수
//    @Override
//    public int selectBoardTotalCount(Criteria cri) {
//        return boardMapper.selectBoardTotalCount(cri);
//    }
//
//    // 게시글 조회수 증가
//    @Override
//    public boolean cntPlus(Long idx) {
//        return boardMapper.cntPlus(idx);
//    }
//
//    // 파일목록 가져오기
//    @Override
//    public List<BoardFileDTO> getFileList(Long idx) {
//        int fileTotalCnt = boardFileMapper.selectFileTotalCount(idx);
//        System.out.println("idx = " + idx);
//        System.out.println("fileTotalCnt = " + fileTotalCnt);
//        return boardFileMapper.selectFileList(idx);
//    }
//
//    @Override
//    public List<BoardDTO> getSearchList(Criteria cri) {
//
//        System.out.println("서비스 cri = " + cri);
//        return boardMapper.selectBoardSearchList(cri);
//    }


}
