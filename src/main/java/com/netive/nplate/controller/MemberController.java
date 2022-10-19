package com.netive.nplate.controller;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 회원목록
    @GetMapping("/list")
    public String list(Model model) {
        List<MemberDTO> memberList = memberService.getMemberList();
        model.addAttribute("memberList", memberList);
        return "member/list";
    }

    // 첫페이지
    @GetMapping("/")
    public String index() {
        return "member/index";
    }


}
