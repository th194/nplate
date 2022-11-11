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
    private int answerGroup;                       // 댓글이 속한 댓글 번호      --> 대댓글용
    private int answerGroupDp;                     // 댓글의 깊이 ( 모댓글이면 0, 답글이면 1 ) --> 대댓글용
    private String answerDeleteAt;                 // 댓글 삭제 여부
    private String answerCn;                       // 댓글 내용
    private String answerWrter;                    // 작성자
    private String answerRgsde;                    // 등록일
    private String answerUpdde;                    // 수정일
}
