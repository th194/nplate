package com.netive.nplate.service;

import com.netive.nplate.domain.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    // 파일저장
    int saveFile(MultipartFile file, String id) throws IOException;

    // 파일 찾기
    FileDTO getFileInfo(String id);

    // 프로필 이미지 수정
    int updateFile(MultipartFile file, String id) throws IOException;

    // 프로필 이미지 삭제
    int deleteFile(String id) throws IOException;
}
