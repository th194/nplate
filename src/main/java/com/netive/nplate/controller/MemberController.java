package com.netive.nplate.controller;

import com.netive.nplate.domain.*;
import com.netive.nplate.service.*;

import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private FollowingService followingService;

    @Autowired
    private UserService userService;

    @Autowired
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;

    @Value("${nplate.upload.path}")
    private String fileDir;


    // 회원가입 페이지
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("area", Area.values());
        return "member/join";
    }


    // 회원 등록
    @PostMapping("/member/submit")
    public String registerMember(MemberDTO memberDTO, @RequestParam MultipartFile file, Model model) throws IOException {
        try {
            // 생일 문자열 형식 변경
            String formatDate = memberDTO.getBirthday().replaceAll("-", "");
            memberDTO.setBirthday(formatDate);

            if (file.isEmpty()) { // 파일 없는 경우
//                fileService.saveDefaultFile(memberDTO.getId());
                memberDTO.setProfileImg("default");
            } else {
                fileService.saveFile(file, memberDTO.getId());
                memberDTO.setProfileImg(memberDTO.getId());
            }

            userService.registerMember(memberDTO);

            model.addAttribute("message", "가입이 완료되었습니다.");
            model.addAttribute("url", "/");
            return "member/index";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "catch msg=======");
            model.addAttribute("url", "/member/error");
            return "member/error";
        }
    }


    // 이미지 처리(컨트롤러 분리해야함)
    @GetMapping(value="/member/info/profile",  produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProfileImage(String id) throws IOException {
        // id = 이미지 코드(profileImg)
        String res;
        if (id.equals("default")) {
            res = fileDir + "user.jpg";
        } else {
            FileDTO fileDTO = fileService.getFileInfo(id);
            res = fileDTO.getSavedPath();
        }
        InputStream inputStream = Files.newInputStream(Paths.get(res));
        byte[] image = IOUtils.toByteArray(inputStream);
        inputStream.close();
        return image;
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
                List<Map> followingMembers = new ArrayList<>();
                if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
                    followingMembers = (List<Map>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
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
