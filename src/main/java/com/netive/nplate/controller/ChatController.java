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

    @MessageMapping(value = "/{memberId}")
    public void sendMessage(@DestinationVariable("memberId") String memberId, ChatMessageDTO message) {
        String subject = "";
        String kind = message.getAlarmType();
        if(message.getBbsSj() != null) {
            if(message.getBbsSj().length() > 8) {
                subject = message.getBbsSj().substring(0, 7) + "...";
            } else {
                subject = message.getBbsSj();
            }
        }

        if(kind.equals("delete")) {
            message.setMessage("관리자에 의해 " + subject + " 게시물이 삭제되었습니다.");
        } else if (kind.equals("like")) {
            message.setMessage(message.getMember() + " 님이 " + subject + " 게시물을 좋아합니다.");
        } else if (kind.equals("following")) {
            message.setMessage(message.getMember() + " 님이 회원님을 팔로잉합니다.");
        } else if (kind.equals("reply")) {
            message.setMessage(message.getMember() + " 님이 회원님의 게시물에 댓글을 남겼습니다.");
        } else if (kind.equals("notice")) {
            message.setMessage("[공지] " + subject + " 가 등록되었습니다.");
        } else if (kind.equals("replyDel")) {
            message.setMessage("관리자에 의해 " + subject + " 댓글이 삭제되었습니다.");
        }
        template.convertAndSend("/sub/"+memberId, message);
    }
}
