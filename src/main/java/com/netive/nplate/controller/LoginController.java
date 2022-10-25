package com.netive.nplate.controller;

import com.netive.nplate.domain.Area;
import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.BoardService;
import com.netive.nplate.service.LoginService;
import com.netive.nplate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BoardService boardSerive;


    // 첫페이지
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            // 로그인 되어있으면 -> 피드(임시로 myPage 지정)
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
                return "member/myPage";
            } else {
                return "member/index";
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "member/index";
        }
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
            return "member/myPage";

        } else {
            rAttr.addAttribute("result", "loginFailed");
            return "member/error";
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
    
    
    // 내가 쓴 게시글 목록
    @GetMapping("/member/board/list")
    public String openBoardList(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if ((boolean) session.getAttribute("isLogOn")) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute("member");
                List<BoardDTO> boardList = loginService.getBordListById(dto.getId());
                model.addAttribute("boardList", boardList);
                return "board/list";
            } catch (Exception e) {
                return "member/index";
            }
        } else {
            return "member/index";
        }
    }


    // 게시글 작성
    @GetMapping("/member/board/write")
    public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");

        if (idx == null) {
            model.addAttribute("board", new BoardDTO(memberDTO.getId()));

        } else {
            BoardDTO boardDTO = boardSerive.getBoardDetail(idx);
            if(boardDTO == null) {
                return "redirect:member/board/list";
            }
            model.addAttribute("board", boardDTO);
        }

        return "board/write";
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

    // 회원정보 수정폼
    @GetMapping("member/myInfoUpdateForm")
    public String myInfoUpdateForm(Model model, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();

        try {
            System.out.println("로그인 여부");
            System.out.println(session.getAttribute("isLogOn"));

            if ((boolean) session.getAttribute("isLogOn")) {
                MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");

                // 생일 처리
                String birthday = memberDTO.getBirthday();
                if (!birthday.contains("-")) {
                    String formatDay = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6);
                    memberDTO.setBirthday(formatDay);
                }

                model.addAttribute("memberInfo", memberDTO);
                model.addAttribute("area", Area.values());

                return "member/updateForm";
            } else {
                return "member/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "member/error";
        }
    }


}
