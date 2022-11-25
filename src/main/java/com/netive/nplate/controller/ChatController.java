package com.netive.nplate.controller;

import com.netive.nplate.domain.ChatMessageDTO;
import com.netive.nplate.domain.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

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

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO message) {
        message.setMessage(message.getWriter() + " 님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO message) {
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
