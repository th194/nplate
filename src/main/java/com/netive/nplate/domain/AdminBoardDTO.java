package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminBoardDTO {                        // 공지글 DTO
    private Long mngrBbsNo;                         // 관리자 게시글 번호 (pk)
    private String mngrBbsSj;                       // 관리자 게시글 제목
    private String mngrBbsCn;                       // 관리자 게시글 내용
    private String mngrBbsWrter;                    // 관리자 게시글 작성자
    private String mngrBbsRgsde;                    // 관리자 게시글 등록일
    private String mngrBbsUpdde;                    // 관리자 게시글 수정일
    private String mngrBbsDelde;                    // 관리자 게시글 삭제일
    private String mngrBbsDeleteAt;                 // 관리자 게시글 삭제여부
    private Long mngrBbsRdcnt;                      // 관리자 게시글 조회수
}