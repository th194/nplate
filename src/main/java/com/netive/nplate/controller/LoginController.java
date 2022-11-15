package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.*;
import com.netive.nplate.service.*;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;

    @Autowired
    private LikesService likesService;


    // 첫페이지
    @GetMapping("/")
    public String index(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        try {
            // 로그인 되어있으면 -> 피드(임시로 myPage 지정)
            // 로그인 되어있지 않으면 -> 로그인 페이지
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) { // 로그인 되어있으면
                return "redirect:/feed";
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        session.invalidate();
        return "member/index";
    }


    // 로그인 후 마이페이지 이동
    @PostMapping("/feed")
    public String login(MemberDTO dto, Model model, HttpServletRequest request) throws NoSuchAlgorithmException {
        // 비밀번호 암호화 처리 추가
        String encPwd = memberUtils.encrypt(dto.getPwd());

        MemberDTO memberDTO = memberService.login(dto.getId(), encPwd);

        if(memberDTO != null) {
            HttpSession session = request.getSession();

            // 중복로그인 체크
            SessionConfig.getSessionidCheck(SessionConstants.MEMBER_DTO, dto.getId());

            session.setAttribute(SessionConstants.MEMBER_DTO, memberDTO);
            session.setAttribute(SessionConstants.MEMBER_ID, memberDTO.getId());
            session.setAttribute(SessionConstants.IS_LOGIN, true);

            model.addAttribute("memberInfo", memberDTO);

            String memberId = memberDTO.getId();

            // todo 중복소스 처리하기
            
            // 리스트 타입(피드: 팔로잉)
            SearchDTO searchDTO = new SearchDTO(memberId);
            searchDTO.setSearchType("following");
            model.addAttribute("search", searchDTO);

            // 좋아요
            List<Long> likeNumbers = boardUtils.getLikeNumbers(memberId);
            model.addAttribute("likeNumbers", likeNumbers);
            session.setAttribute(SessionConstants.LIKE_NUMBERS, likeNumbers);
            // 세션에 저장된 값 있으면 다시 DB 조회 하지 않음(좋아요 추가 및 삭제시 세션에 저장된 값 갱신)

            // 팔로잉 멤버
            List<String> followingIds = memberUtils.getFollowingMember(memberId);
            List<MemberDTO> followingMembers = memberUtils.getFollowingsInfo(followingIds);
            model.addAttribute("followingMembers", followingMembers);
            session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
            session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
            // 세션에 저장된 값 있으면 다시 DB 조회 하지 않음(팔로잉 추가 및 삭제시 세션에 저장된 값 갱신)

            return "bootstrap-template/list";

        } else {
            model.addAttribute("message", "아이디와 비밀번호를 다시 확인해주세요.");
            model.addAttribute("url", "/");
            return "member/error";
        }
    }


    // 마이페이지 주소로 이동
    @GetMapping("/feed")
    public String myPage(HttpServletRequest request, Model model) {
        System.out.println("피드 주소로 이동===========================");
        HttpSession session = request.getSession();
        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                model.addAttribute("memberInfo", session.getAttribute(SessionConstants.MEMBER_DTO));
                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

                // 리스트 타입(피드: 팔로잉)
                SearchDTO searchDTO = new SearchDTO(memberId);
                searchDTO.setSearchType("following");
                model.addAttribute("search", searchDTO);

                // 좋아요
                List<Long> likeNumbers;
                if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
                    likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
                } else {
                    likeNumbers = boardUtils.getLikeNumbers(memberId);
                }
                model.addAttribute("likeNumbers", likeNumbers);

                // 팔로잉 멤버 정보
                List<MemberDTO> followingMembers;
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    followingMembers = memberUtils.getFollowingsInfo(followingIds);
                }
                model.addAttribute("followingMembers", followingMembers);

                return "bootstrap-template/list";
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
        session.invalidate();
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/member/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
    }
    
    
    // 내가 쓴 게시글 목록
    @GetMapping("/member/board/list")
    public String openBoardList(@ModelAttribute("params") final SearchDTO params, Model model, HttpServletRequest request) {
        System.out.println("내가 쓴 게시글 목록========");
        HttpSession session = request.getSession();

        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
                String memberId = dto.getId();
                params.setMemberId(memberId); // 내 아이디 세팅

                model.addAttribute("memberInfo", dto);
                model.addAttribute("search", params);

                // 좋아요
                List<Long> likeNumbers;
                if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
                    likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
                } else {
                    likeNumbers = boardUtils.getLikeNumbers(memberId);
                }
                model.addAttribute("likeNumbers", likeNumbers);

                // 팔로잉 멤버 정보
                List<MemberDTO> followingMembers;
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    followingMembers = memberUtils.getFollowingsInfo(followingIds);
                }
                model.addAttribute("followingMembers", followingMembers);

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
        MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

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


        // 팔로잉 처리
        String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        List<MemberDTO> followingMembers;
        if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
            followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
        } else {
            List<String> followingIds = memberUtils.getFollowingMember(memberId);
            followingMembers = memberUtils.getFollowingsInfo(followingIds);
        }
        model.addAttribute("followingMembers", followingMembers);

        return "bootstrap-template/write";
    }


    // 내 정보 보기
    @GetMapping("/member/myInfo")
    public String myInfo(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

                // 생일 처리
                String birthday = memberDTO.getBirthday();
                if (!birthday.contains("-")) {
                    String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
                    memberDTO.setBirthday(formatDay);
                }

                model.addAttribute("memberInfo", memberDTO);
                model.addAttribute("area", Area.values());

                // 팔로잉 처리
                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
                List<MemberDTO> followingMembers;
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    followingMembers = memberUtils.getFollowingsInfo(followingIds);
                }
                model.addAttribute("followingMembers", followingMembers);

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
            System.out.println(session.getAttribute(SessionConstants.IS_LOGIN));

            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

                // 생일 처리
                String birthday = memberDTO.getBirthday();
                if (!birthday.contains("-")) {
                    String formatDay = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6);
                    memberDTO.setBirthday(formatDay);
                }

                model.addAttribute("memberInfo", memberDTO);
                model.addAttribute("area", Area.values());

                // 팔로잉 처리
                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
                List<MemberDTO> followingMembers;
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    followingMembers = memberUtils.getFollowingsInfo(followingIds);
                }
                model.addAttribute("followingMembers", followingMembers);

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
            if (session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO sessionDto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

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


    // 비밀번호 수정
    @PostMapping("/member/changePwd")
    public @ResponseBody ResponseEntity<String> updatePwd(MemberDTO dto, String changePwd, HttpServletRequest request) throws NoSuchAlgorithmException {
        // 리턴 값 처리
        ResponseEntity<String> resEnt = null;
        String msg = "<script>";
        msg += " alert('에러가 발생하였습니다. 잠시 후 다시 시도해주세요.');";
        msg += " location.replace('/'); ";
        msg += " </script>";

        // 세션
        HttpSession session = request.getSession();

        // 비밀번호 암호화 처리
        String encPwd = memberUtils.encrypt(dto.getPwd());

        try {
            if (session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO sessionDto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

                if (Objects.equals(sessionDto.getId(), dto.getId()) && Objects.equals(sessionDto.getPwd(), encPwd)) {
                    dto.setPwd(memberUtils.encrypt(changePwd));
                    memberService.updatePwd(dto);

                    session.invalidate();

                    msg = "<script>";
                    msg += " alert('비밀번호 변경이 완료되었습니다. 다시 로그인해주세요.');";
                    msg += " location.replace('/'); ";
                    msg += " </script>";

                } else {
                    // 비밀번호 틀린 경우의 처리
                    System.out.println("아이디, 비밀번호 틀림");

                    msg = "<script>";
                    msg += " alert('아이디와 비밀번호를 확인 후 다시 입력해주세요.');";
                    msg += " location.replace('/'); ";
                    msg += " </script>";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resEnt = new ResponseEntity<>(msg, HttpStatus.CREATED);
        return resEnt;
    }

}
