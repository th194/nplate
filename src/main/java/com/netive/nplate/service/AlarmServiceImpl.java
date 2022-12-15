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

        String kind = alarmDTO.getNtcnKnd();            // 알람 종류
        String getSj = alarmDTO.getNtcnTrgtSj();

        String subject = "";
        if( getSj != null ) {
            if(getSj.length() > 8 ) {
                subject = getSj.substring(0, 7) + "...";
            } else {
                subject = getSj;
            }
        }

        if(kind.equals("following")) {
            alarmDTO.setNtcnCn(" 님이 회원님을 팔로잉 합니다.");
            alarmDTO.setNtcnCours("/member/userInfo?id=" + alarmDTO.getNtcnSendMber());
        } else if (kind.equals("like")) {
            alarmDTO.setNtcnCn(" 님이 " + subject + " 게시물을 좋아합니다.");
            alarmDTO.setNtcnCours("/board/view.do?idx=" + alarmDTO.getNtcnTrgtNo());
        } else if (kind.equals("reply")) {
            alarmDTO.setNtcnCn(" 님이 " + subject + " 게시물에 댓글을 남겼습니다.");
            alarmDTO.setNtcnCours("/board/view.do?idx=" + alarmDTO.getNtcnTrgtNo());
        } else if (kind.equals("delete")) {
            alarmDTO.setNtcnCn("관리자에 의해 " + subject + " 게시물이 삭제되었습니다.");
        } else if (kind.equals("notice")) {
            alarmDTO.setNtcnCn("[공지] " + subject + " 가 등록되었습니다.");
            alarmDTO.setNtcnCours("/notice?idx=" + alarmDTO.getNtcnTrgtNo());
        } else if (kind.equals("replyDel")) {
            alarmDTO.setNtcnCn("관리자에 의해 " + subject + " 댓글이 삭제되었습니다.");
            alarmDTO.setNtcnCours("/board/view.do?idx=" + alarmDTO.getNtcnTrgtNo());
        }

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
