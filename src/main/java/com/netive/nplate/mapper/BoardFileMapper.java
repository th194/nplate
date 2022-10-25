package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardFileMapper {
    public int insertFile(List<BoardFileDTO> attachList);

    public BoardFileDTO selectFileDetail(Long idx);

    public int deleteFile(Long boardIdx);

    public List<BoardFileDTO> selectFileList(Long boardIdx);

    public int selectFileTotalCount(Long boardIdx);


}
