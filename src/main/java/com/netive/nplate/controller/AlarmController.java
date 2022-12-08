package com.netive.nplate.controller;

import com.netive.nplate.domain.AlarmDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.domain.SessionConstants;
import com.netive.nplate.service.AlarmService;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;

    /**
     * 알람 목록
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/alarm/info")
    public HashMap<String, Object> selectAlarmList(HttpServletRequest request, Model model) {

        HashMap<String, Object> resMap = new HashMap<>();
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                System.out.println(session.getAttribute(SessionConstants.MEMBER_DTO));
                model.addAttribute("memberInfo", session.getAttribute(SessionConstants.MEMBER_DTO));
                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

                // 리스트 타입(피드: 팔로잉)
                SearchDTO searchDTO = new SearchDTO(memberId);
                searchDTO.setSearchType("following");
                model.addAttribute("search", searchDTO);

                // 좋아요
                List<Long> likeNumbers;
                if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
                    likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
                } else {
                    likeNumbers = boardUtils.getLikeNumbers(memberId);
                }
                model.addAttribute("likeNumbers", likeNumbers);

                // 팔로잉 처리
                List<Map> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<Map>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    if (followingIds.size() > 0) {
                        followingMembers = memberUtils.getFollowingsInfo(followingIds);
                    }

                    session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
                    session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
                }

                model.addAttribute("followingMembers", followingMembers);


                List<AlarmDTO> alarmList = alarmService.selectAlarm(memberId);
                resMap.put("result", alarmList);
                return resMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 알람 등록
     * @param alarmDTO
     * @return
     */
    @PostMapping("/alarm/register")
    public HashMap<String, Object> registerAlarm(HttpServletRequest request, AlarmDTO alarmDTO) {
        HashMap<String, Object> resMap = new HashMap<>();
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        alarmDTO.setNtcnSendMber(memberId);         // 세션 정보로 알람 보내는사람 id 세팅
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
        }
        boolean result = alarmService.registerAlarm(alarmDTO);
        resMap.put("result", result);
        return resMap;
    }

    /**
     * 알람 업데이트
     * @param alarmDTO
     * @return
     */
    @PostMapping("/alarm/update")
    public HashMap<String, Object> updateAlarm(AlarmDTO alarmDTO) {
        HashMap<String, Object> resMap = new HashMap<>();
        boolean result = alarmService.updateAlarm(alarmDTO);
        resMap.put("result", result);
        return resMap;
    }

    /**
     * 알람 삭제
     * @param request
     * @param alarmDTO
     * @return
     */
    @PostMapping("/alarm/delete")
    public HashMap<String, Object> deleteAlarm(HttpServletRequest request, AlarmDTO alarmDTO) {
        HashMap<String, Object> resMap = new HashMap<>();
        HttpSession session = request.getSession();

        String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        alarmDTO.setNtcnSendMber(memberId);
        boolean result = alarmService.deleteAlarm(alarmDTO);
        resMap.put("result", result);
        return resMap;
    }
}
