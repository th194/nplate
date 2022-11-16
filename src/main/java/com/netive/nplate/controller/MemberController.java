package com.netive.nplate.controller;

import com.netive.nplate.domain.*;
import com.netive.nplate.service.*;

import com.netive.nplate.util.BoardUtils;
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
import java.util.*;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileService fileService;

    @Autowired
    private LikesService likesService;

    @Autowired
    private FollowingService followingService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;


    // 회원목록
    @GetMapping("/list")
    public String list(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute(SessionConstants.MEMBER_ID);
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

                if (file.isEmpty()) { // 파일 없는 경우
                    fileService.saveDefaultFile(memberDTO.getId());
                } else {
                    fileService.saveFile(file, memberDTO.getId());
                }

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
        byte[] image = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return image;
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


    // 회원 정보 수정 todo 관리 페이지 처리 수정
    @PostMapping("/member/adminUpdate")
    public String update(MemberDTO memberDTO, @RequestParam MultipartFile file, Model model) throws IOException {

        if (!file.isEmpty()) { // 프로필 사진 수정
            fileService.updateFile(file, memberDTO.getId());
        }

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


    // 다른사람 정보 보기
    @GetMapping("/member/userInfo")
    public String userPage(Model model, HttpServletRequest request, String id) {
        HttpSession session = request.getSession();

        try {
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
                MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
                model.addAttribute("memberInfo", memberDTO);

                // 아이디로 멤버 정보 조회
                MemberDTO userDTO = memberService.getUserInfo(id);
                model.addAttribute("userInfo", userDTO);

                // 리스트 타입(유저페이지: 특정유저 글)
                SearchDTO searchDTO = new SearchDTO(id);
                model.addAttribute("search", searchDTO);


                String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

                // 좋아요
                List<Long> likeNumbers;
                if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
                    likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
                } else {
                    likeNumbers = boardUtils.getLikeNumbers(memberId);
                }
                model.addAttribute("likeNumbers", likeNumbers);


                // 팔로잉 여부 확인
                List<String> followingIds;
                if (session.getAttribute(SessionConstants.FOLLOWING_IDS) != null) {
                    followingIds = (List<String>) session.getAttribute(SessionConstants.FOLLOWING_IDS);
                } else {
                    followingIds = memberUtils.getFollowingMember(memberId);
                    session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
                }
                boolean isFollowing = followingIds.contains(id);
                System.out.println("팔로잉하고있는지 isFollowing================= " + isFollowing);
                model.addAttribute("isFollowing", isFollowing);

                // 메뉴 팔로잉 처리
                List<MemberDTO> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
                } else {
                    if (followingIds.size() > 0) {
                        followingMembers = memberUtils.getFollowingsInfo(followingIds);
                    }
                    session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, followingMembers);
                }

                model.addAttribute("followingMembers", followingMembers);

                return "bootstrap-template/userInfo";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "member/error";
    }


    // 회원 팔로잉
    @GetMapping("/member/following")
    public @ResponseBody String followingMember(HttpServletRequest request, String id) {
        System.out.println("회원 팔로잉 컨트롤러 시작=========");
        System.out.println("팔로잉 할 아이디" + id);

        HttpSession session = request.getSession();
        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_ID) != null) {
            String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
            FollowingDTO followingDTO = new FollowingDTO(memberId, id);

            int result = followingService.followMember(followingDTO);

            if (result > 0) {
                System.out.println("팔로잉 성공=========");
                session.setAttribute(SessionConstants.FOLLOWING_IDS, null);
                session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, null);
                return "success";
            } else {
                System.out.println("팔로잉 실패=========");
            }
        }
        System.out.println("팔로잉 실패=========");
        return "fail";
    }


    // 팔로잉 취소
    @GetMapping("/member/unfollowing")
    public @ResponseBody String unfollowingMember(HttpServletRequest request, String id) {
        System.out.println("회원 언팔로잉 컨트롤러 시작=========");
        System.out.println("언팔로잉 할 아이디" + id);

        HttpSession session = request.getSession();
        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_ID) != null) {
            String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
            FollowingDTO followingDTO = new FollowingDTO(memberId, id);

            int result = followingService.unfollowMember(followingDTO);

            if (result > 0) {
                System.out.println("언팔로잉 성공=========");
                session.setAttribute(SessionConstants.FOLLOWING_IDS, null);
                session.setAttribute(SessionConstants.FOLLOWING_MEMBERS, null);
                return "success";
            } else {
                System.out.println("언팔로잉 실패=========");
            }
        }
        System.out.println("언팔로잉 실패=========");
        return "fail";
    }
}
