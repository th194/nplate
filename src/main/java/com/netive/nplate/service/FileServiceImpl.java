package com.netive.nplate.service;

import com.google.gson.JsonArray;
import com.netive.nplate.domain.BoardFileDTO;
import com.netive.nplate.domain.FileDTO;
import com.netive.nplate.mapper.BoardFileMapper;
import com.netive.nplate.mapper.BoardMapper;
import com.netive.nplate.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${nplate.upload.path}")
    private String fileDir;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private BoardFileMapper boardFileMapper;

    @Autowired
    private BoardMapper boardMapper;

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
        File savedFile = new File(savedPath);
        file.transferTo(savedFile);

        // 데이터베이스에 파일 이름 저장
        return fileMapper.registerFile(dto);
    }

    @Override
    public FileDTO getFileInfo(String id) {
        return fileMapper.selectFileByCd(id);
    }

    @Override
    public int updateFile(MultipartFile file, String id) throws IOException {
        if (file.isEmpty()) {
            return 0;
        }

        String origName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = origName.substring(origName.lastIndexOf("."));
        String savedName = uuid + extension;
        String savedPath = fileDir + savedName;
        FileDTO dto = new FileDTO(id, origName, savedName, savedPath);

        // 로컬에 저장
        File savedFile = new File(savedPath);
        file.transferTo(savedFile);
        
        // 기존 파일 폴더에서 삭제
        FileDTO oldFile = fileMapper.selectFileByCd(id);
        Path oldFilePath = Paths.get(oldFile.getSavedPath());
        Files.delete(oldFilePath);

        // 데이터베이스에 파일 정보 수정
        return fileMapper.updateFile(dto);
    }

    @Override
    public int deleteFile(String id) throws IOException {
        // 파일 폴더에서 삭제
        FileDTO savedFile = fileMapper.selectFileByCd(id);
        Path savedFilePath = Paths.get(savedFile.getSavedPath());
        Files.delete(savedFilePath);
        return fileMapper.deleteFile(id);
    }

    @Override
    public int saveDefaultFile(String id) {
        // 원래 파일 이름
        String name = "user.jpg";

        // 파일 불러올 때 사용할 파일 경로
        String savedPath = fileDir + name;

        // 파일 dto 생성
        FileDTO dto = new FileDTO(id, name, name, savedPath);

        // 데이터베이스에 파일 이름 저장
        return fileMapper.registerFile(dto);
    }

    // 게시글 파일 목록 조회
    @Override
    public List<BoardFileDTO> selectBoardFile(Long idx) {
        return boardFileMapper.selectBoardFile(idx);
    }

    // 게시글 다중이미지 파일테이블 저장
    @Override
    public int saveBoardFile(String fileNm, String fileNmTemp, String fileCours, Long idx) {

        // 파일 dto 생성
        BoardFileDTO boardFileDTO = new BoardFileDTO(fileNm, fileNmTemp, fileCours, idx);
        System.out.println("이미지 테이블 저장 실행중==========================");
        // 데이터베이스에 파일 이름 저장
        return boardFileMapper.registerBoardFile(boardFileDTO);
    }

    @Override
    public int updateBoardFile(String fileNm, String fileNmTemp, String fileCours, Long idx) {


        return 0;
    }
}
