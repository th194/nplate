package com.netive.nplate.service;

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

    // 회원 가입시 기본 프로필 이미지 지정
    int saveDefaultFile(String id);

    // 게시글 번호로 파일 조회
    List<FileDTO> selectBoardFile(Long idx);

    // 파일명 등록
    int saveBoardFile(String fileCode, String fileNm, String fileNmTemp, String fileCours);
}
