package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.Area;
import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.domain.SearchDTO;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.BoardService;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.LoginService;
import com.netive.nplate.service.MemberService;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Controller
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BoardService boardSerive;

    @Autowired
    private FileService fileService;

    @Autowired
    private MemberUtils memberUtils;


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

                return "bootstrap-template/feed";

            } else {
                return "member/index";
            }

        } catch (Exception e) {
            // e.printStackTrace();
            session.invalidate();
            return "member/index";
        }
    }


    // 로그인 후 마이페이지 이동
    @PostMapping("/member/myPage")
    public String login(MemberDTO dto, Model model, HttpServletRequest request) throws NoSuchAlgorithmException {

        // 비밀번호 암호화 처리 추가
        String encPwd = memberUtils.encrypt(dto.getPwd());

        MemberDTO memberDTO = memberService.login(dto.getId(), encPwd);

        if(memberDTO != null) {
            HttpSession session = request.getSession();

            // 중복로그인 체크
            SessionConfig.getSessionidCheck("member", dto.getId());

            session.setAttribute("member", memberDTO);
            session.setAttribute("memberID", memberDTO.getId());
            session.setAttribute("isLogOn", true);

            model.addAttribute("memberInfo", memberDTO);
            return "bootstrap-template/feed";

        } else {
            model.addAttribute("message", "아이디와 비밀번호를 다시 확인해주세요.");
            model.addAttribute("url", "/");
            return "member/error";
        }
    }


    // 마이페이지 주소로 이동
    @GetMapping("/member/myPage")
    public String myPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute("isLogOn") && session.getAttribute("member") != null) {
                model.addAttribute("memberInfo", session.getAttribute("member"));
                return "bootstrap-template/feed";
            } else {
                return "redirect:/";
            }

        } catch (Exception e) {
            return "redirect:/";
        }
    }

    // 로그아웃
    @GetMapping("/member/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
    }
    
    
    // 내가 쓴 게시글 목록 todo 리스트 임시 적용(페이징, 검색 추가해야함)
    @GetMapping("/member/board/list")
    public String openBoardList(@ModelAttribute("params") final SearchDTO params, Model model, HttpServletRequest request) {
        System.out.println("내가 쓴 게시글 목록========");
        HttpSession session = request.getSession();

        if ((boolean) session.getAttribute("isLogOn")) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute("member");
                String id = dto.getId();
                params.setMemberId(id);

                int count = loginService.countPostsById(id); // 글 개수

                Pagination pagination = new Pagination(count, params);
                params.setPagination(pagination);

                System.out.println("params ========================" + params.toString());

                List<BoardDTO> boardList = loginService.getBordListById(params); // 특정 아이디로 조회 글목록
                PagingResponse<BoardDTO> response = new PagingResponse<>(boardList, pagination);

                model.addAttribute("memberInfo", dto);
                model.addAttribute("response", response);
                return "bootstrap-template/list";

            } catch (Exception e) {
                // todo 에러 발생시 처리 추가
                System.out.println("에러=======");
                e.printStackTrace();
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
                return "bootstrap-template/myInfo";
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

                return "bootstrap-template/updateForm";
            } else {
                return "member/error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "member/error";
        }
    }


    // 회원 탈퇴
    @PostMapping("/member/deleteAcc")
    public String delete(Model model, MemberDTO dto, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        HttpSession session = request.getSession();

        // 비밀번호 암호화 처리 추가
        String encPwd = memberUtils.encrypt(dto.getPwd());

        try {
            if (session.getAttribute("member") != null) {
                MemberDTO sessionDto = (MemberDTO) session.getAttribute("member");

                if (Objects.equals(sessionDto.getId(), dto.getId()) && Objects.equals(sessionDto.getPwd(), encPwd)) {
                    memberService.deleteMember(dto.getId()); // 회원 DB 삭제처리
                    fileService.deleteFile(dto.getId()); // 프로필 사진 파일 및 DB 삭제

                    model.addAttribute("message", "탈퇴가 완료되었습니다.");
                    model.addAttribute("url", "/");
                    session.invalidate();

                    return "member/index";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }


    // 회원 정보 수정
    @PostMapping("/member/update")
    public String update(MemberDTO memberDTO, @RequestParam MultipartFile file) throws IOException {

        // 프로필 사진 수정
        if (!file.isEmpty()) {
            fileService.updateFile(file, memberDTO.getId());
        }

        // 그 외 정보 수정
        memberService.updateInfo(memberDTO);

        return "redirect:/member/myInfo"; // 처리 수정해야함
    }
}
