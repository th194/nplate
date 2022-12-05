package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LikesDTO {

    int index; // 좋아요 번호(PK) 추가 순서 관리
    String id; // 멤버 아이디
    Long bbscttNo; // 게시글 번호
    String likeRgsde; // 좋아요 날짜

    public LikesDTO() {
    }

    public LikesDTO(String id, Long bbscttNo) {
        this.id = id;
        this.bbscttNo = bbscttNo;
    }

}
