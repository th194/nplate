package com.netive.nplate.mapper;

import com.netive.nplate.domain.FileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

    int registerFile(FileDTO dto);

    FileDTO selectFileByCd(String cd);

    int updateFile(FileDTO dto);

    int deleteFile(String cd);

    List<FileDTO> selectBoardFile(Long idx);
}
