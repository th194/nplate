package com.netive.nplate.mapper;

import com.netive.nplate.domain.AlarmDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlarmMapper {

    // 알람 저장
    public int registerAlarm(AlarmDTO alarmDTO);

    // 알람 목록
    public List<AlarmDTO> selectAlarm(String id);

    // 알람 수정
    public int updateAlarm(AlarmDTO alarmDTO);

    // 알람 삭제
    public int deleteAlarm(AlarmDTO alarmDTO);
}
