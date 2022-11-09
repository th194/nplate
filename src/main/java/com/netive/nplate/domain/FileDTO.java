package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileDTO {
    private int fileId;         // 파일번호
    private String fileCode;    // 파일이미지코드(프로필사진: 아이디, 게시글: 글번호)
    private String orgNm;       // 원래파일이름
    private String savedNm;     // 파일임시이름
    private String savedPath;   // 파일저장경로

    public FileDTO(String fileCode, String orgNm, String savedNm, String savedPath) {
        this.fileCode = fileCode;
        this.orgNm = orgNm;
        this.savedNm = savedNm;
        this.savedPath = savedPath;
    }
}
