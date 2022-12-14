package com.netive.nplate.service;

import com.netive.nplate.domain.AdminBoardDTO;
import com.netive.nplate.domain.PageDTO;

import java.util.List;

public interface AdminBoardService {
    // 게시글 등록
    public boolean registAdminBoard(AdminBoardDTO params);

    // 게시글 목록
    public List<AdminBoardDTO> adminBoardList(PageDTO page);

    // 게시글 상세보기
    public AdminBoardDTO adminBoardView(Long idx);

    // 게시글 수정
    public boolean updateAdminBoard(AdminBoardDTO params);

    // 게시글 조회수 증가
    public boolean adminBoardCntPlus(Long idx);

    // 게시글 삭제
    public boolean deleteAdminBoard(String params);

    public int adminBoardCount(PageDTO page);
}
