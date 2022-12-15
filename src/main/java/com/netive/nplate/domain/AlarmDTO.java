package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlarmDTO {
    private int ntcnNo;                                         // 알람 번호
    private String ntcnReceiveMber;                             // 알람 받는 사람
    private String ntcnSendMber;                                // 알람 보내는 사람
    private String ntcnKnd;                                     // 알람 종류 ( 삭제 : 'delete', 팔로잉 : 'following', 좋아요 : 'like', 댓글 : 'reply' )
    private Long ntcnTrgtNo;                                    // 알람 타겟 게시글 번호 (게시글 번호)
    private String ntcnTrgtSj;                                  // 알람 타겟 제목 (게시글 제목)
    private Long ntcnTrgtAnswerNo;                             // 알람 타겟 댓글 번호
    private String ntcnCn;                                      // 알람 내용
    private String ntcnCours;                                   // 게시글 경로
    private String ntcnOccrrncDt;                               // 알람 발생 일시
    private String ntcnCnfirmDt;                                // 알람 확인 일시
    private String ntcnCnfirmAt;                                // 알람 확인 여부 ( default 'N')
}
