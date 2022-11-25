package com.netive.nplate.service;

import com.google.gson.JsonArray;
import com.netive.nplate.domain.BoardFileDTO;
import com.netive.nplate.domain.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    // 파일저장
    int saveFile(MultipartFile file, String id) throws IOException;

    // 파일 찾기
    FileDTO getFileInfo(String id);

    // 프로필 이미지 수정
    int updateFile(MultipartFile file, String id) throws IOException;

    // 프로필 이미지 삭제
    int deleteFile(String id) throws IOException;

    // 게시글 번호로 파일 조회
    List<BoardFileDTO> selectBoardFile(Long idx);

    // 게시판 파일명 등록
    int saveBoardFile(String fileNm, String fileNmTemp, String fileCours, Long idx);


    // 게시판 파일명 삭제
    int removeBoardFileByNm(String oldFile);
}
