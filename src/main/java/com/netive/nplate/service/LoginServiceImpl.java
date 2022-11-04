package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public List<BoardDTO> getBordListById(SearchDTO params) {
        return loginMapper.getBordListById(params);
    }

    @Override
    public int countPostsById(String id) {
        return loginMapper.countPostsById(id);
    }

    @Override
    public List<BoardDTO> getLikes(Map map) {
        return loginMapper.getLikes(map);
    }
}
