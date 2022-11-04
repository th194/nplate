package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;

import java.util.List;
import java.util.Map;

public interface LoginService {

    List<BoardDTO> getBordListById(SearchDTO parmas);

    // 글 개수 세기
    int countPostsById(String id);

    // 좋아요 한 글 목록
    List<BoardDTO> getLikes(Map map);
}
