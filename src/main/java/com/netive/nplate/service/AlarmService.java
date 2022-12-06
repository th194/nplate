package com.netive.nplate.service;

import com.netive.nplate.domain.AlarmDTO;

import java.util.List;

public interface AlarmService {

    // 알람 등록
    public boolean registerAlarm(AlarmDTO alarmDTO);

    // 알람 목록
    public List<AlarmDTO> selectAlarm(String id);

    // 알람 수정
    public boolean updateAlarm(AlarmDTO alarmDTO);

    // 알람 삭제
    public boolean deleteAlarm(AlarmDTO alarmDTO);
}
