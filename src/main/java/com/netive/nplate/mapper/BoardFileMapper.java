package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardFileMapper {
    public int insertAttach(List<BoardFileDTO> attachList);

    public BoardFileDTO selectAttachDetail(Long idx);

    public int deleteAttach(Long boardIdx);

    public List<BoardFileDTO> selectAttachList(Long boardIdx);

    public int selectAttachTotalCount(Long boardIdx);


}
