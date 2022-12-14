package com.netive.nplate.mapper;

import com.netive.nplate.domain.AdminBoardDTO;
import com.netive.nplate.domain.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminBoardMapper {
    // 게시글 등록
    public int registAdminBoard(AdminBoardDTO params);

    // 게시글 목록
    public List<AdminBoardDTO> adminBoardList(PageDTO params);

    // 게시글 상세보기
    public AdminBoardDTO adminBoardView(Long idx);

    // 게시글 수정
    public int updateAdminBoard(AdminBoardDTO params);

    // 게시글 조회수 증가
    public boolean adminBoardCntPlus(Long idx);

    // 게시글 삭제
    public int deleteAdminBoard(String params);

    // 게시글 수 카운트
    public int adminBoardCount(PageDTO page);
}
