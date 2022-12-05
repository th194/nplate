package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageDTO {
    private String bbsNo;                   // 게시글 번호
    private String bbsSj;                   // 게시글 제목
    private String roomId;                  // 안 쓰지만 혹시 채팅방 만들면 사용
    private String alarmType;               // 알람 타입( 좋아요, 팔로우, 관리자가 게시글 삭제 )
    private String writer;                  // 게시글 작성자
    private String member;                  // 로그인 사용자
    private String message;                 // 메시지 내용
    private String date;                 // 알람 시간
}
