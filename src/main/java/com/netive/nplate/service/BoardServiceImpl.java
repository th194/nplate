package com.netive.nplate.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.netive.nplate.domain.BoardFileDTO;
import com.netive.nplate.mapper.BoardFileMapper;
import com.netive.nplate.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.mapper.BoardMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private BoardFileMapper boardFileMapper;

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

    @Override
    public boolean registerBoard(BoardDTO board, MultipartFile[] files, String path) {
        int queryResult = 1;

        if (registerBoard(board) == false) {
            return false;
        }

        File file = new File(path);

        List<File> fileSrc = fileUtils.getImgFileList(file, path);

        System.out.println("fileSrc = >>>>>>>>>>>>>>>>> " + fileSrc);

        System.out.println("serviceImpl files >>>>>>>>>>>>>>>>> " + files);
        System.out.println("serviceImpl board >>>>>>>>>>>>>>>>> " + board);
        System.out.println("serviceImpl path >>>>>>>>>>>>>>>>> " + path);
        System.out.println("serviceImpl getBbscttNo() >>>>>>>>>>>>>>>>> " + board.getBbscttNo());

//        List<BoardFileDTO> fileList = fileUtils.uploadFiles(fileSrc, board.getBbscttNo(), path);


//        System.out.println("boardServiceImpl fileList =>>>>>>>>>>>>>>>>>>> " +fileList);
//
//        System.out.println("fileList validation >>>>>>>>>>>>>>>> " +CollectionUtils.isEmpty(fileList));

//        if(CollectionUtils.isEmpty(fileList) == false) {
//            queryResult = boardFileMapper.insertFile(fileList);
//            if(queryResult < 1) {
//                queryResult = 0;
//            }
//        }


        return (queryResult > 0);
    }

    // 게시글 상세
    @Override
    public BoardDTO getBoardDetail(Long idx) {
        return boardMapper.selectBoardDetail(idx);
    }

    // 게시글 수정
    @Override
    public Long updateBoard(BoardDTO board) {
        boardMapper.updateBoard(board);
        return board.getBbscttNo();
    }

    @Override
    public BoardDTO findBoardIdx(Long idx) {
        return boardMapper.findByIdx(idx);
    }

    // 게시글 삭제
    @Override
    public boolean deleteBoard(Long idx) {
        int queryResult = 0;
        queryResult = boardMapper.deleteBoard(idx);
        System.out.println("삭제====================================="+queryResult);
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

    // 파일목록 가져오기
    @Override
    public List<BoardFileDTO> getFileList(Long idx) {
        int fileTotalCnt = boardFileMapper.selectFileTotalCount(idx);
        System.out.println("idx = " + idx);
        System.out.println("fileTotalCnt = " + fileTotalCnt);
//        if (fileTotalCnt < 1) {
//            return Collections.emptyList();
//        }
        return boardFileMapper.selectFileList(idx);
    }


}
