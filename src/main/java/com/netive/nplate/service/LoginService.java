package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.SearchDTO;

import java.util.List;

public interface LoginService {

    List<BoardDTO> getBordListById(SearchDTO parmas);

    // 글 개수 세기 todo 필요 없으면 지움
    int countPostsById(String id);
}
