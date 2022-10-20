package com.netive.nplate.service;

import com.netive.nplate.domain.FileDTO;
import com.netive.nplate.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private String fileDir = "E:/test/";

    @Autowired
    private FileMapper fileMapper;

    @Override
    public int saveFile(MultipartFile file, String id) throws IOException {
        if (file.isEmpty()) {
            return 0;
        }

        // 원래 파일 이름
        String origName = file.getOriginalFilename();

        // 파일이름으로 쓸 uuid 생성
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출
        String extension = origName.substring(origName.lastIndexOf("."));

        // uuid와 확장자 결합
        String savedName = uuid + extension;

        // 파일 불러올 때 사용할 파일 경로
        String savedPath = fileDir + savedName;

        // 파일 dto 생성
        FileDTO dto = new FileDTO(id, origName, savedName, savedPath);

        // 로컬에 저장
        File savedFile = new File(savedName);
        file.transferTo(savedFile);

        // 데이터베이스에 파일 이름 저장
        return fileMapper.registerFile(dto);
    }

    @Override
    public FileDTO getFileInfo(String id) {
        return fileMapper.selectFileByCd(id);
    }
}
