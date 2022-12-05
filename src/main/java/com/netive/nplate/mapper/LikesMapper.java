package com.netive.nplate.mapper;

import com.netive.nplate.domain.LikesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LikesMapper {

    // 좋아요 추가
    int addLike(LikesDTO dto);

    // 아이디로 좋아요 한 글 번호 조회
    List<LikesDTO> getLikes(String id);

    // 좋아요 삭제
    int deleteLike(LikesDTO dto);

    List<LikesDTO> getLikeOne(Long idx);
}
