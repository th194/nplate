package com.netive.nplate.service;

import com.netive.nplate.domain.ReplyDTO;
import com.netive.nplate.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService{

    @Autowired
    private ReplyMapper replyMapper;

    // 댓글 등록/수정
    @Override
    public boolean registerReply(ReplyDTO params) {
        int queryResult = 0;

        // 댓글 번호가 없으면 등록
        if (params.getAnswerNo() == null) {
            queryResult = replyMapper.insertReply(params);
        // 댓글 번호가 있으면 수정
        } else {
            queryResult = replyMapper.updateReply(params);
        }
        // queryResult가 1이면 성공 그 외 실패
        return (queryResult == 1) ? true : false;
    }

    // 댓글 삭제
    @Override
    public boolean deleteReply(Long idx) {
        int queryResult = 0;

        queryResult = replyMapper.deleteReply(idx);

        // queryResult가 1이면 성공 그 외 실패
        return (queryResult == 1) ? true : false;
    }

    // 댓글 목록
    @Override
    public List<ReplyDTO> getReplyList(ReplyDTO params) {
        // 댓글 목록 담을 빈 배열 생성
        List<ReplyDTO> replyList = Collections.emptyList();

        // 댓글 수 조회
        int replyTotalCount = replyMapper.selectReplyTotalCount(params);

        // 댓글 수가 0 이상이면
        if (replyTotalCount > 0) {
            // 미리 생성했던 빈 배열에 댓글 목록 담음
            replyList = replyMapper.selectReplyList(params);
        }

        // 댓글 목록 리턴
        return replyList;
    }
}
