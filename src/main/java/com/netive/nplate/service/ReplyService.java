package com.netive.nplate.service;

import com.netive.nplate.domain.PageDTO;
import com.netive.nplate.domain.ReplyDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReplyService {
    // 댓글 등록
    public boolean registerReply(ReplyDTO params);

    // 댓글 수정
    public boolean updateReply(ReplyDTO params);

    // 댓글 삭제
    public boolean deleteReply(Long idx);

    // 댓글 목록
    public List<ReplyDTO> getReplyList(PageDTO params);

    // 게시글 별 댓글 갯수
    public int getReplyBoardCount(Long idx);

    // 관리자가 댓글 삭제
    public boolean adminDeleteReply(ReplyDTO params);
}
