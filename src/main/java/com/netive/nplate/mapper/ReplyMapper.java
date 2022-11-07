package com.netive.nplate.mapper;

import com.netive.nplate.domain.ReplyDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    public int insertReply(ReplyDTO params);
    public ReplyDTO selectReplyDetail(Long idx);
    public int updateReply(ReplyDTO params);
    public int deleteReply(Long idx);
    public List<ReplyDTO> selectReplyList(ReplyDTO params);
    public int selectReplyTotalCount(ReplyDTO params);
}
