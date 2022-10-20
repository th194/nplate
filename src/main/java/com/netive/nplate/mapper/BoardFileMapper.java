package com.netive.nplate.mapper;

import com.netive.nplate.domain.BoardFileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardFileMapper {
    public void insertFile(BoardFileDTO file);
    public void deleteFile(String fileNo);
    public List<BoardFileDTO> findByBno(Long bno);
}
