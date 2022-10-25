package com.netive.nplate.service;

import com.netive.nplate.domain.BoardDTO;

import java.util.List;

public interface LoginService {

    List<BoardDTO> getBordListById(String id);
}
