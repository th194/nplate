package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    // 파일저장
    int saveFile(MultipartFile file, String id) throws IOException;
}
