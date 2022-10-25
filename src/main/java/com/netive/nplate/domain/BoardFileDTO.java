package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFileDTO {
    private Long fileNo;                // 파일 번호 pk
    private String fileImageCode;       // 파일 코드
    private String fileNm;              // 원본파일명
    private String fileNmTemp;          // 임시파일명
    private String fileCours;               // 파일경로
    private Long bbscttNo;
}
