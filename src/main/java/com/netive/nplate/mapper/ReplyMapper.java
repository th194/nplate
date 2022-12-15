package com.netive.nplate.mapper;

import com.netive.nplate.domain.PageDTO;
import com.netive.nplate.domain.ReplyDTO;
import com.netive.nplate.paging.Pagination;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    public int insertReply(ReplyDTO params);
    public ReplyDTO selectReplyDetail(Long idx);
    public int updateReply(ReplyDTO params);
    public int deleteReply(Long idx);
    public List<ReplyDTO> selectReplyList(PageDTO page);
    public int selectReplyTotalCount(PageDTO params);
    public int selectReplyBoardCount(Long idx);
    public int adminDeleteReply(ReplyDTO params);
}
