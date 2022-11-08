package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {

    private int fileId;         // 파일번호
    private String fileCode;    // 파일이미지코드
    private String orgNm;       // 원래파일이름
    private String savedNm;     // 파일임시이름
    private String savedPath;   // 파일저장경로
    private Long bbscttNo;           // 게시글 번호

    @Override
    public String toString() {
        return "FileDTO{" +
                "fileId='" + fileId + '\'' +
                ", fileCode='" + fileCode + '\'' +
                ", orgNm='" + orgNm + '\'' +
                ", savedNm='" + savedNm + '\'' +
                ", savedPath='" + savedPath + '\'' +
                '}';
    }

    public FileDTO(String fileCode, String orgNm, String savedNm, String savedPath) {
        this.fileCode = fileCode;
        this.orgNm = orgNm;
        this.savedNm = savedNm;
        this.savedPath = savedPath;
    }

    public FileDTO(String fileCode, String orgNm, String savedNm, String savedPath, Long bbscttNo) {
        this.fileCode = fileCode;
        this.orgNm = orgNm;
        this.savedNm = savedNm;
        this.savedPath = savedPath;
        this.bbscttNo = bbscttNo;
    }
}
