package com.netive.nplate.mapper;

import com.netive.nplate.domain.FileDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

    int registerFile(FileDTO dto);

    FileDTO selectFileByCd(String cd);
}
