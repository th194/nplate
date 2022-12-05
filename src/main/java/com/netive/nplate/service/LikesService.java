package com.netive.nplate.service;

import com.netive.nplate.domain.LikesDTO;

import java.util.List;

public interface LikesService {
    // 좋아요 추가
    int addLike(LikesDTO dto);

    // 아이디로 좋아요 한 글 번호 조회
    List<LikesDTO> getLikes(String id);

    // 좋아요 삭제
    int deleteLike(LikesDTO dto);

    // 좋아요 한 게시물 1개
    List<LikesDTO> getLikeOne(Long idx);
}
