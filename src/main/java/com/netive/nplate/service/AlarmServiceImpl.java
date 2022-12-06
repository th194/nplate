package com.netive.nplate.service;

import com.netive.nplate.domain.AlarmDTO;
import com.netive.nplate.mapper.AlarmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService{

    @Autowired
    private AlarmMapper alarmMapper;

    // 알람 등록
    @Override
    public boolean registerAlarm(AlarmDTO alarmDTO) {
        int queryResult = 0;
        queryResult = alarmMapper.registerAlarm(alarmDTO);
        return (queryResult == 1) ? true : false;
    }

    // 알람 목록
    @Override
    public List<AlarmDTO> selectAlarm(String id) {
        List<AlarmDTO> alarmList = alarmMapper.selectAlarm(id);
        return alarmList;
    }

    // 알람 수정
    @Override
    public boolean updateAlarm(AlarmDTO alarmDTO) {
        int queryResult = 0;
        queryResult = alarmMapper.updateAlarm(alarmDTO);
        return (queryResult == 1) ? true : false;
    }

    // 알람 삭제
    @Override
    public boolean deleteAlarm(AlarmDTO alarmDTO) {
        int queryResult = 0;
        queryResult = alarmMapper.deleteAlarm(alarmDTO);
        return (queryResult == 1) ? true : false;
    }
}
