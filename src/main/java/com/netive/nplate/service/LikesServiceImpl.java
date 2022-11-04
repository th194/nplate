package com.netive.nplate.service;

import com.netive.nplate.domain.LikesDTO;
import com.netive.nplate.mapper.LikesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikesServiceImpl implements LikesService {

    @Autowired
    private LikesMapper likesMapper;

    @Override
    public int addLike(LikesDTO dto) {
        return likesMapper.addLike(dto);
    }

    @Override
    public List<LikesDTO> getLikes(String id) {
        return likesMapper.getLikes(id);
    }

    @Override
    public int deleteLike(LikesDTO dto) {
        return likesMapper.deleteLike(dto);
    }
}
