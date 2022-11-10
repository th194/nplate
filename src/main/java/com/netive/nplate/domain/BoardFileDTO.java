package com.netive.nplate.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFileDTO {
    private Long fileNo;                // 파일 번호 pk
    private String fileNm;              // 원본파일명
    private String fileNmTemp;          // 임시파일명
    private String fileCours;               // 파일경로
    private Long bbscttNo;              // 게시글 번호

    public BoardFileDTO() {

    }

    public BoardFileDTO(String fileNm, String fileNmTemp, String fileCours, Long bbscttNo) {
        this.fileNm = fileNm;
        this.fileNmTemp = fileNmTemp;
        this.fileCours = fileCours;
        this.bbscttNo = bbscttNo;
    }
}



