package com.netive.nplate.controller;

import com.netive.nplate.domain.Area;
import com.netive.nplate.domain.FileDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.MemberService;

import com.netive.nplate.util.MemberUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemberUtils memberUtils;


    // 회원목록
    @GetMapping("/list")
    public String list(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("memberID");
        if (Objects.equals(id, "admin")) {
            List<MemberDTO> memberList = memberService.getMemberList();
            model.addAttribute("memberList", memberList);
            return "member/list";
        } else {
            return "redirect:/";
        }
    }

    // 회원가입 페이지
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("area", Area.values());
        return "member/join";
    }

    // 회원 등록
    @PostMapping("/member/submit")
    public String submit(MemberDTO memberDTO, @RequestParam MultipartFile file, Model model) throws IOException {
        try {
            // 생일 문자열 형식 변경
            String formatDate = memberDTO.getBirthday().replaceAll("-", "");
            memberDTO.setBirthday(formatDate);

            // 비밀번호 암호회
            String encPwd = memberUtils.encrypt(memberDTO.getPwd());
            memberDTO.setPwd(encPwd);

            int result = memberService.registerMember(memberDTO);

            if (result > 0) {
                fileService.saveFile(file, memberDTO.getId());
                model.addAttribute("message", "가입이 완료되었습니다.");
                model.addAttribute("url", "/");
                return "member/index";
            } else {
                model.addAttribute("message", "else msg======");
                model.addAttribute("url", "/member/error");
                return "member/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "catch msg=======");
            model.addAttribute("url", "/member/error");
            return "member/error";
        }
    }


    // 회원 정보
    @GetMapping("/member/info")
    public String info(String id, Model model) throws IOException {
        MemberDTO memberDTO = memberService.getMemberInfo(id);

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
    @GetMapping("member/checkOverlappedID")
    public @ResponseBody String checkOverlappedID(String id) {
        boolean result = memberService.checkOverlappedId(id) <= 0;
        if (result) {
            return "usable";
        } else {
            return "overlapped";
        }
    }


    // 공통 에러페이지
    @GetMapping("/member/error")
    public String errorPage(Model model) {
        // todo 처리변경(임시 에러 처리)
        model.addAttribute("message", null);
        model.addAttribute("url", null);
        return "member/error";
    }


    // 테스트용 페이지
    @GetMapping("/member/test")
    public String testPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        try {
            System.out.println("로그인 여부");
            System.out.println(session.getAttribute("isLogOn"));

            if ((boolean) session.getAttribute("isLogOn")) {
                MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");

                // 생일 처리
                String birthday = memberDTO.getBirthday();
                if (!birthday.contains("-")) {
                    String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
                    memberDTO.setBirthday(formatDay);
                }

                model.addAttribute("memberInfo", memberDTO);
                return "member/test";
            } else {
                return "member/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "redirect:/";
        }
    }

}
