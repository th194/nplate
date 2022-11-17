package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.domain.SessionConstants;
import com.netive.nplate.service.MemberService;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
public class AdminController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberUtils memberUtils;

    // 관리자 로그인 페이지
    @GetMapping("/admin")
    public String adminLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        System.out.println("관리자 페이지 로그인 아이디: ");
        System.out.println(session.getAttribute(SessionConstants.MEMBER_ID));

        try {
            if (session.getAttribute(SessionConstants.MEMBER_ID) != null) {
                if ( session.getAttribute(SessionConstants.MEMBER_ID).equals("admin") ) {
                    return "bootstrap-template/admin";

                } else { // 일반 회원이 /admin 페이지로 들어온 경우
                    return "redirect:/";
                }
            }
        } catch(Exception e) {
//            e.printStackTrace();
        }
        return "member/admin-login";

    }

    // 로그인 후 이동 페이지
    @PostMapping("/admin")
    public String adminIndex(MemberDTO dto, Model model, HttpServletRequest request) throws NoSuchAlgorithmException {
        // admin 로그인 되어있으면 -> 관리자 페이지
        // 로그인 되어있지 않거나, 다른 계정으로 로그인 되어있으면 -> 첫페이지
        try {
            if (!dto.getId().equals("admin")) {
                return "redirect:/";
            }

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

                return "bootstrap-template/admin";
            }

        } catch (Exception e) {
            System.out.println("admin login 에러:");
            e.printStackTrace();
        }

        // 일단은 로그인 아이디가 관리자 계정이 아니거나 아이디 비밀번호가 틀리거나 에러일 경우 일반 로그인 페이지로 리다이렉트
        // todo 추후 처리 수정
        return "redirect:/";
    }

}
