package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class FollowingDTO {
    int index; // 팔로잉 번호(PK) 추가 순서 관리
    String memberId; // 멤버 아이디(팔로잉 신청한 사람)
    String followingId; // 팔로잉 아이디
    String followingRgsde;    // 팔로잉 날짜

    public FollowingDTO(String memberId, String followingId) {
        this.memberId = memberId;
        this.followingId = followingId;
    }
}
