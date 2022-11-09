package com.netive.nplate.mapper;

import com.netive.nplate.domain.FollowingDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowingMapper {
    // 회원 팔로잉
    int followMember(FollowingDTO dto);

    // 회원 팔로잉 삭제
    int unfollowMember(FollowingDTO dto);

    // 아이디로 팔로잉 회원 조회(내가 팔로잉하는 사람들 조회)
    List<FollowingDTO> getFollowingMember(String id);

}

