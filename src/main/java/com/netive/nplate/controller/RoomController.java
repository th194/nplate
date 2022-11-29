package com.netive.nplate.controller;

import com.netive.nplate.common.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class RoomController {

    private final ChatRoomRepository repository;

    /**
     * 채팅방 목록 조회
     * @return
     */
    @GetMapping("/rooms")
    public ModelAndView rooms() {
        ModelAndView mv = new ModelAndView("bootstrap-template/rooms");
        mv.addObject("list", repository.findAllRooms());
        return mv;
    }

    /**
     * 채팅방 개설
     * @param name
     * @param rttr
     * @return
     */
    @PostMapping("/room")
    public String create(@RequestParam String name, RedirectAttributes rttr) {
        rttr.addFlashAttribute("roomName", repository.createChatRoomDTO(name));
        return "redirect:/chat/rooms";
    }

    /**
     * 채팅방 참여
     * @param roomId
     * @param model
     */
    @GetMapping("/room")
    public String getRoom(String roomId, Model model) {
        log.info("채팅방 목록조회 RoomId " + roomId);
        Object memberInfo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String member = memberInfo.getId();
        log.info("=================================채팅서비스");
//        log.info("user name = " + memberInfo.getId());
        log.info("채팅방정보 ===================");
        System.out.println(repository.findRoomById(roomId));
        log.info("채팅방정보 ===================");
        model.addAttribute("username", memberInfo);
        model.addAttribute("room", repository.findRoomById(roomId));
        return "bootstrap-template/room";
    }
}
