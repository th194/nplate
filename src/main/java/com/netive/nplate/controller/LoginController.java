package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.*;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class LoginController {

    @Autowired
    private BoardUtils boardUtils;

    @Autowired
    private MemberUtils memberUtils;


    // 첫페이지
    @GetMapping("/")
    public String index(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        try {
            // 로그인 되어있으면 -> 피드
            // 로그인 되어있지 않으면 -> 로그인 페이지
            MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
            if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && memberDTO != null) { // 로그인 되어있으면

                if (memberDTO.getRole().equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/feed";
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        session.invalidate();
        return "member/index";
    }


    // 첫페이지(피드) 주소로 이동
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
    public String loginSuccess(Authentication authentication, HttpServletRequest request) {
        System.out.println("로그인 성공======");

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
        HttpSession session = request.getSession();

        // 중복로그인 체크
        SessionConfig.getSessionidCheck(SessionConstants.MEMBER_DTO, memberDTO.getId());

        // 세션에 값 저장
        session.setAttribute(SessionConstants.MEMBER_DTO, memberDTO);
        session.setAttribute(SessionConstants.MEMBER_ID, memberDTO.getId());
        session.setAttribute(SessionConstants.IS_LOGIN, true);

        if (Objects.equals(memberDTO.getRole(), "ROLE_ADMIN") || Objects.equals(memberDTO.getRole(), "ROLE_MANAGER")) {
            return "redirect:/admin";
        } else if (!Objects.equals(memberDTO.getRole(), "ROLE_USER")) {
            session.invalidate();
        }
        return "redirect:/";
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
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/";
    }

}
