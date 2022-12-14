package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReplyDTO {
    private Long answerNo;                         // 댓글 번호 PK
    private Long bbscttNo;                         // 게시글 번호 FK
    private String answerCn;                       // 댓글 내용
    private String answerWrter;                    // 작성자
    private String answerRgsde;                    // 등록일
    private String answerUpdde;                    // 수정일
    private String answerUpdateAt;                 // 수정 여부
    private String answerDeleteAt;                 // 삭제 여부
}
