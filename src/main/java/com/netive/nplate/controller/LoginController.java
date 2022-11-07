package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.*;
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
import java.util.*;

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

    @Autowired
    private LikesService likesService;


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


                // 좋아요 추가
                List<LikesDTO> likes = likesService.getLikes(id);
                List<Long> likeNumbers = new ArrayList<Long>();
                for (LikesDTO likesDTO : likes) {
                    System.out.println("like 디티오 프린트====");
                    System.out.println(likesDTO);
                    System.out.println("넘버===" + likesDTO.getBbscttNo());

                    likeNumbers.add(likesDTO.getBbscttNo());
                }

                model.addAttribute("likeNumbers", likeNumbers);
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

        model.addAttribute("memberInfo", memberDTO);

        if (idx != null) {
            BoardDTO board = boardSerive.getBoardDetail(idx);
            model.addAttribute("board", board);
        }

//        if (idx == null) {
//            model.addAttribute("board", new BoardDTO(memberDTO.getId()));
//
//        } else {
//            BoardDTO boardDTO = boardSerive.getBoardDetail(idx);
//            if(boardDTO == null) {
//                return "redirect:member/board/list";
//            }
//            model.addAttribute("board", boardDTO);
//        }

        return "bootstrap-template/write";
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


    // 좋아한 게시글 목록(북마크)
    @GetMapping("/member/board/likePosts")
    public String lisePosts(Model model, HttpServletRequest request, String keyword) {
        System.out.println("좋아한 게시글 목록========");
        HttpSession session = request.getSession();

        if ((boolean) session.getAttribute("isLogOn")) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute("member");
                String id = dto.getId();

                List<LikesDTO> likes = likesService.getLikes(id);

                Map <String, Object> map = new HashMap<>();
                List<Long> likeNumbers = new ArrayList<Long>();

                for (LikesDTO likesDTO : likes) {
                    likeNumbers.add(likesDTO.getBbscttNo());
                }

                String tag = "";
                if (keyword != null) {
                    tag = keyword;
                }

                map.put("keyword", tag);
                map.put("likeNumbers", likeNumbers);

                List<BoardDTO> likePosts = loginService.getLikes(map);
                System.out.println("글목록 크기:" + likePosts.size());
                // todo likePosts 좋아요 누른 순서대로 정렬되게 처리 추가

                model.addAttribute("likePosts", likePosts);
                model.addAttribute("memberInfo", dto);

                return "bootstrap-template/likes";

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
}
