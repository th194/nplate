package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.*;
import com.netive.nplate.service.*;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
            // 로그인 되어있으면 -> 피드
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


    // 마이페이지 주소로 이동
    @GetMapping("/feed")
    public String myPage(HttpServletRequest request, Model model) {
        System.out.println("피드 주소로 이동===========================");
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                System.out.println(session.getAttribute(SessionConstants.MEMBER_DTO));
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

                // 팔로잉 처리
                List<Map> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<Map>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    if (followingIds.size() > 0) {
                        followingMembers = memberUtils.getFollowingsInfo(followingIds);
                    }

                    session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
                    session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
                }

                model.addAttribute("followingMembers", followingMembers);

//                model.addAttribute("followings", followingMembers);

                return "bootstrap-template/list";
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
        session.invalidate();
        return "redirect:/";
    }


    // 스프링 시큐리티 로그인 성공
    @GetMapping("/login/success")
    public String loginSuccess(Model model, Authentication authentication, HttpServletRequest request) {

        System.out.println("임시 로그인 성공 페이지======");

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        System.out.println("로그인 성공 멤버 디티오");
        System.out.println(memberDTO.toString());


        HttpSession session = request.getSession();

        // 중복로그인 체크
        SessionConfig.getSessionidCheck(SessionConstants.MEMBER_DTO, memberDTO.getId());

        session.setAttribute(SessionConstants.MEMBER_DTO, memberDTO);
        session.setAttribute(SessionConstants.MEMBER_ID, memberDTO.getId());
        session.setAttribute(SessionConstants.IS_LOGIN, true);

        if (Objects.equals(memberDTO.getRole(), "ROLE_ADMIN")) {
            return "redirect:/admin";
        } else if (Objects.equals(memberDTO.getRole(), "ROLE_USER")){
            // 처리..
        } else {
            session.invalidate();
        }
        return "redirect:/";
        // todo 처리 수정해야함
    }


    // 스프링 시큐리티 로그인 실패
    @GetMapping("/login/fail")
    public String loginFail(Model model) {
        System.out.println("로그인 실패======");
        model.addAttribute("message", "아이디, 비밀번호를 확인 후 다시 로그인해주세요.");
        model.addAttribute("url", "/");

        return "member/error";
    }


    // 로그아웃
    @GetMapping("/member/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
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

                List<Map> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<Map>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    if (followingIds.size() > 0) {
                        followingMembers = memberUtils.getFollowingsInfo(followingIds);
                    }
                    session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
                    session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
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

                List<Map> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<Map>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    List<String> followingIds = memberUtils.getFollowingMember(memberId);
                    if (followingIds.size() > 0) {
                        followingMembers = memberUtils.getFollowingsInfo(followingIds);
                    }
                    session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
                    session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
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
        // 세션
        HttpSession session = request.getSession();

        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try {
            if (session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO sessionDto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

                // 기존 비밀번호와 입력한 비밀번호 일치여부 확인
                boolean result = passwordEncoder.matches(dto.getPwd(), sessionDto.getPassword());
                System.out.println("비밀번호 일치여부 확인 result: " + result);

                if (Objects.equals(sessionDto.getId(), dto.getId()) && result) {
                    memberService.deleteMember(dto.getId()); // 회원 DB 삭제처리

                    //프로필 사진 파일 및 DB 삭제(기본 이미지가 아닌 경우에만)
                    if (!sessionDto.getProfileImg().equals("default")) {
                        fileService.deleteFile(dto.getId());
                    }

                    model.addAttribute("message", "탈퇴가 완료되었습니다.");
                    model.addAttribute("url", "/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 에러난 경우에도...
        session.invalidate();
        return "member/index";
    }


    // 회원 정보 수정
    @PostMapping("/member/update")
    public String update(MemberDTO memberDTO, @RequestParam MultipartFile file) throws IOException {
        // 프로필 사진 수정
        if (!file.isEmpty()) {
            if (memberDTO.getProfileImg().equals("default")) {
                fileService.saveFile(file, memberDTO.getId());
            } else {
                fileService.updateFile(file, memberDTO.getId());
            }
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

        // 비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try {
            if (session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO sessionDto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);

                // 기존 비밀번호와 입력한 비밀번호 일치여부 확인
                boolean result = passwordEncoder.matches(dto.getPwd(), sessionDto.getPassword());
                System.out.println("비밀번호 일치여부 확인 result: " + result);

                if (Objects.equals(sessionDto.getId(), dto.getId()) && result) {
                    dto.setPwd(passwordEncoder.encode(changePwd));
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
