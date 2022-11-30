package com.netive.nplate.controller;

import com.netive.nplate.domain.ChatMessageDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.domain.SessionConstants;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    private final BoardUtils boardUtils;

    private final MemberUtils memberUtils;

    @GetMapping("/alarm")
    public String getAlarm(HttpServletRequest request, Model model) {
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

                return "bootstrap-template/list";
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return "bootstrap-template/alarm";
    }

//    @GetMapping("/chat")
//    public String chatGet(Model model){
//        log.info("ChatController, chat get()");
//        MemberDTO memberInfo = (MemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String member = memberInfo.getId();
//        log.info("=================================채팅서비스");
//        log.info("user name = " + memberInfo.getId());
//        model.addAttribute("member", member);
//        return "bootstrap-template/chat";
//    }

//    @MessageMapping(value = "/chat/enter")
//    public void enter(ChatMessageDTO message) {
//        message.setMessage(message.getWriter() + " 님이 채팅방에 참여하였습니다.");
//        template.convertAndSend("/sub/chat/room" + message.getRoomId(), message);
//    }

//    @MessageMapping(value = "/chat/message")
//    public void message(ChatMessageDTO message) {
//        String alarmType = message.getAlarmType();
//        template.convertAndSend("/sub/chat/room/", message);
//    }

    @MessageMapping(value = "/{memberId}")
    public void sendMessage(@DestinationVariable("memberId") String memberId, ChatMessageDTO message) {
        System.out.println("알림기능 컨트롤러 호출 sendMessage()");
        System.out.println(memberId);
        if(message.getAlarmType().equals("delete")) {
            message.setMessage("관리자에 의해 " + message.getBbsNo() + "번 게시물이 삭제되었습니다.");
        } else if (message.getAlarmType().equals("like")) {
            message.setMessage(message.getMember() + " 님이 " + message.getBbsNo() + "번 게시물을 좋아합니다.");
        } else if (message.getAlarmType().equals("following")) {
            message.setMessage(message.getMember() + " 님이 회원님을 팔로잉합니다.");
        }

        System.out.println(message.toString());
        template.convertAndSend("/sub/"+memberId, message);
    }
}
