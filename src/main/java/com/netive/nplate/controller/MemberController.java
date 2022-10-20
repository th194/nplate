package com.netive.nplate.controller;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileService fileService;

    // 첫페이지
    @GetMapping("/")
    public String index() {
        return "member/index";
    }
    // 회원목록
    @GetMapping("/list")
    public String list(Model model) {
        List<MemberDTO> memberList = memberService.getMemberList();
        model.addAttribute("memberList", memberList);
        return "member/list";
    }

    // 회원가입 페이지
    @GetMapping("/join")
    public String join(Model model) {
        return "member/join";
    }

    // 회원 등록
    @PostMapping("/member/submit")
    public String submit(MemberDTO memberDTO, @RequestParam MultipartFile file) throws IOException {

        fileService.saveFile(file, memberDTO.getId());
        memberService.registerMember(memberDTO);
        return "member/index";
    }


}
