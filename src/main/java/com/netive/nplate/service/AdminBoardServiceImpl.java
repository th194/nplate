package com.netive.nplate.service;

import com.netive.nplate.domain.AdminBoardDTO;
import com.netive.nplate.domain.PageDTO;
import com.netive.nplate.mapper.AdminBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminBoardServiceImpl implements AdminBoardService{

    @Autowired
    private AdminBoardMapper adminBoardMapper;

    // 게시글 등록
    @Override
    public boolean registAdminBoard(AdminBoardDTO params) {
        int queryResult = 0;
        queryResult = adminBoardMapper.registAdminBoard(params);

        return (queryResult == 1) ? true : false;
    }

    // 게시글 목록
    @Override
    public List<AdminBoardDTO> adminBoardList(PageDTO page) {
        List<AdminBoardDTO> boardList = adminBoardMapper.adminBoardList(page);
        return boardList;
    }

    @Override
    public AdminBoardDTO adminBoardView(Long idx) {
        AdminBoardDTO dto = adminBoardMapper.adminBoardView(idx);
        return dto;
    }

    // 게시글 수정
    @Override
    public boolean updateAdminBoard(AdminBoardDTO params) {
        int queryResult = 0;
        queryResult = adminBoardMapper.updateAdminBoard(params);
        return (queryResult == 1) ? true : false;
    }

    // 게시글 조회수 증가
    @Override
    public boolean adminBoardCntPlus(Long idx) {
        return adminBoardMapper.adminBoardCntPlus(idx);
    }

    // 게시글 삭제
    @Override
    public boolean deleteAdminBoard(String params) {
        int queryResult = 0;
        queryResult = adminBoardMapper.deleteAdminBoard(params);
        return (queryResult == 1) ? true : false;
    }

    @Override
    public int adminBoardCount(PageDTO page) {
        return adminBoardMapper.adminBoardCount(page);
    }
}
