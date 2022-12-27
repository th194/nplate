package com.netive.nplate.controller;

import com.netive.nplate.domain.AlarmDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.domain.SessionConstants;
import com.netive.nplate.service.AlarmService;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;

    /**
     * 알람 뷰 페이지
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/alarm")
    public String getAlarm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                System.out.println(session.getAttribute(SessionConstants.MEMBER_DTO));
                model.addAttribute("memberInfo", session.getAttribute(SessionConstants.MEMBER_DTO));
                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
                model.addAttribute("loginMember", memberId);

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
//                return "bootstrap-template/alarm";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "bootstrap-template/alarm";
    }

    /**
     * 알람 목록
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/alarm/info")
    @ResponseBody
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
    @ResponseBody
    public HashMap<String, Object> registerAlarm(HttpServletRequest request, AlarmDTO alarmDTO) {
        HashMap<String, Object> resMap = new HashMap<>();
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        alarmDTO.setNtcnSendMber(memberId);         // 세션 정보로 알람 보내는사람 id 세팅

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
    @ResponseBody
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
    @ResponseBody
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
