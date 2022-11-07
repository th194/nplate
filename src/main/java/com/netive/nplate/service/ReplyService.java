package com.netive.nplate.service;

import com.netive.nplate.domain.ReplyDTO;

import java.util.List;

public interface ReplyService {
    // 댓글 등록/수정
    public boolean registerReply(ReplyDTO params);
    // 댓글 삭제
    public boolean deleteReply(Long idx);
    // 댓글 목록
    public List<ReplyDTO> getReplyList(ReplyDTO params);
}
