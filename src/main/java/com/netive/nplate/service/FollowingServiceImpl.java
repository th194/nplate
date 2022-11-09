package com.netive.nplate.service;

import com.netive.nplate.domain.FollowingDTO;
import com.netive.nplate.mapper.FollowingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingServiceImpl implements FollowingService{

    @Autowired
    private FollowingMapper followingMapper;

    @Override
    public int followMember(FollowingDTO dto) {
        return followingMapper.followMember(dto);
    }

    @Override
    public int unfollowMember(FollowingDTO dto) {
        return followingMapper.unfollowMember(dto);
    }

    @Override
    public List<FollowingDTO> getFollowingMember(String id) {
        return followingMapper.getFollowingMember(id);
    }
}
