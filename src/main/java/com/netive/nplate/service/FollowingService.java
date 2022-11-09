package com.netive.nplate.service;

import com.netive.nplate.domain.FollowingDTO;

import java.util.List;

public interface FollowingService {
    // 회원 팔로잉
    int followMember(FollowingDTO dto);

    // 회원 팔로잉 삭제
    int unfollowMember(FollowingDTO dto);

    // 아이디로 팔로잉 회원 조회(내가 팔로잉하는 사람들 조회)
    List<FollowingDTO> getFollowingMember(String id);

}
