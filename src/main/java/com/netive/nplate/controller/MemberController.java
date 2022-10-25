package com.netive.nplate.controller;

import com.netive.nplate.domain.Area;
import com.netive.nplate.domain.FileDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.MemberService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileService fileService;


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
        model.addAttribute("area", Area.values());
        return "member/join";
    }

    // 회원 등록
    @PostMapping("/member/submit")
    public String submit(MemberDTO memberDTO, @RequestParam MultipartFile file) throws IOException {
        try {
            // 생일 문자열 형식 변경
            String formatDate = memberDTO.getBirthday().replaceAll("-", "");
            memberDTO.setBirthday(formatDate);

            int result = memberService.registerMember(memberDTO);
            if (result > 0) {
                fileService.saveFile(file, memberDTO.getId());
                return "member/index";
            } else {
                return "member/error";
            }
        } catch (Exception e) {
            return "member/error";
        }
    }


    // 회원 정보
    @GetMapping("/member/info")
    public String info(String id, Model model) throws IOException {
        MemberDTO memberDTO = memberService.getMemberInfo(id);
        FileDTO fileDTO = fileService.getFileInfo(id);

        // 생일 처리
        String birthday = memberDTO.getBirthday();
        String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
        memberDTO.setBirthday(formatDay);

        model.addAttribute("memberInfo", memberDTO);
        model.addAttribute("area", Area.values());
        return "member/info";
    }


    // 이미지 처리(컨트롤러 분리해야함)
    @GetMapping(value="/member/info/profile",  produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProfileImage(String id) throws IOException {
        FileDTO fileDTO = fileService.getFileInfo(id);
        String res = fileDTO.getSavedPath();
        InputStream inputStream = Files.newInputStream(Paths.get(res));
        return IOUtils.toByteArray(inputStream);
    }


    // 회원삭제
    @GetMapping("/member/delete")
    public String delete(String id, Model model) throws IOException {
        // 회원 DB 삭제처리
        memberService.deleteMember(id);

        // 프로필 사진 파일 및 DB 삭제
        fileService.deleteFile(id);

        return "redirect:/list";
    }

    // 회원정보 수정폼
    @GetMapping("member/updateForm")
    public String updateForm(String id, Model model) throws IOException {
        MemberDTO memberDTO = memberService.getMemberInfo(id);
        FileDTO fileDTO = fileService.getFileInfo(id);

        // 생일 처리
        String birthday = memberDTO.getBirthday();
        String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
        memberDTO.setBirthday(formatDay);

        memberDTO.setProfileImg(fileDTO.getSavedPath());
        model.addAttribute("memberInfo", memberDTO);
        model.addAttribute("area", Area.values());

        return "member/updateForm";
    }

    // 회원 정보 수정
    @PostMapping("/member/update")
    public String update(MemberDTO memberDTO, @RequestParam MultipartFile file, Model model) throws IOException {

        // 프로필 사진 수정
        fileService.updateFile(file, memberDTO.getId());

        // 그 외 정보 수정
        memberService.updateInfo(memberDTO);

        return "redirect:/list"; // 처리 수정해야함
    }

    // 회원가입 아이디 중복 조회
    @ResponseBody
    @GetMapping("member/checkOverlappedID")
    public String checkOverlappedID(String id) {
        boolean result = memberService.checkOverlappedId(id) <= 0;
        if (result) {
            return "usable";
        } else {
            return "overlapped";
        }
    }

}
