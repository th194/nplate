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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    // 첫페이지
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            // 로그인 되어있으면 -> 피드(임시로 myInfo 지정)
            // 로그인 되어있지 않으면 -> 로그인 페이지
            if ((boolean) session.getAttribute("isLogOn")) { // 로그인 되어있으면
                MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");

                // 생일 처리
                String birthday = memberDTO.getBirthday();
                if (!birthday.contains("-")) {
                    String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
                    memberDTO.setBirthday(formatDay);
                }

                model.addAttribute("memberInfo", memberDTO);
                model.addAttribute("area", Area.values());
                return "member/myInfo";
            } else {
                return "member/index";
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "member/index";
        }
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

    // 로그인
    @PostMapping("/member/login")
    public String login(MemberDTO dto, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes rAttr) {
        MemberDTO memberDTO = memberService.login(dto.getId(), dto.getPwd());

        if(memberDTO != null) {
            HttpSession session = request.getSession();
            session.setAttribute("member", memberDTO);
            session.setAttribute("isLogOn", true);

            model.addAttribute("memberInfo", memberDTO);
            return "member/login";

        } else {
            rAttr.addAttribute("result", "loginFailed");
            return "member/error";
        }
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

    // 회원가입 페이지
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
        /*
        // info 띄우기 위한 처리
        model.addAttribute("memberInfo", memberDTO);
        model.addAttribute("area", Area.values());
        */
        return "redirect:/list";
    }


    // 내 정보 보기
    @GetMapping("/member/myInfo")
    public String myInfo(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

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
                model.addAttribute("area", Area.values());
                return "member/myInfo";
            } else {
                return "member/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "redirect:/";
        }

    }

    // 로그아웃
    @GetMapping("/member/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        /*
        session.removeAttribute("member");
        session.setAttribute("isLogOn", false);
        */
        session.invalidate();

        return "redirect:/";
    }


}
