package com.netive.nplate.controller;

import com.netive.nplate.configuration.SessionConfig;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.BoardService;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.MemberService;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class AdminController {

    @Value("${nplate.upload.path}")
    private String filePath;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberUtils memberUtils;

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileService fileService;

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


    // 회원목록
    @GetMapping("/admin/member/list")
    public String memberList(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        if (Objects.equals(id, "admin")) {
            List<MemberDTO> memberList = memberService.getMemberList();
            model.addAttribute("memberList", memberList);

            return "bootstrap-template/admin-member-list";
        } else {
            return "redirect:/";
        }
    }


    /**
     * 관리자용 게시글 목록
     * @return
     */
    @GetMapping("/admin/adminList")
    public String adminBoardList(@ModelAttribute("params") PageDTO params, Model model) {



        int count = boardService.count(params);
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        System.out.println("params===================");
        System.out.println(params);
        System.out.println("params===================");

        List<BoardDTO> list = boardService.getAllBoardList(params);
        PagingResponse<BoardDTO> boardList = new PagingResponse<>(list, pagination);
        model.addAttribute("boardList", boardList);
        return "bootstrap-template/adminList";
    }

    /**
     * 관리자용 게시글 상세보기
     * @param idx
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/admin/adminView")
    public String openBoardDetail(@RequestParam final Long idx, Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
        model.addAttribute("memberInfo", memberDTO);

        BoardDTO board = boardService.getBoardDetail(idx);
        model.addAttribute("board", board);

        String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

        // 팔로잉 여부 확인
        List<String> followingIds;
        if (session.getAttribute(SessionConstants.FOLLOWING_IDS) != null) {
            followingIds = (List<String>) session.getAttribute(SessionConstants.FOLLOWING_IDS);
        } else {
            followingIds = memberUtils.getFollowingMember(memberId);
            session.setAttribute(SessionConstants.FOLLOWING_IDS, followingIds);
        }
        boolean isFollowing = followingIds.contains(board.getBbscttWrter());
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

        boardService.cntPlus(idx); // 조회수 증가 추가

        return "bootstrap-template/adminView";
    }

    /**
     * 관리자용 게시글 삭제 함수
     * @param request
     * @return
     */
    @PostMapping("/admin/delete")
    public String adminBoardDelete(HttpServletRequest request) {

        String fileNmTemp = "";
        String[] delMsg = request.getParameterValues("delArr");
        int size = delMsg.length;

        // 삭제할 게시글 수만큼 loop
        for ( int i = 0; i < size; i ++) {
            Long idx = Long.parseLong(delMsg[i]);
            List<BoardFileDTO> boardFileList = fileService.selectBoardFile(idx);
            // 파일 갯수만큼 loop
            for ( int j = 0; j < boardFileList.size(); j ++) {
                fileNmTemp = boardFileList.get(j).getFileNmTemp();

                File rmFile = new File(filePath + fileNmTemp);
                // 서버에서 파일 삭제
                if (rmFile.exists()) {
                    rmFile.delete();
                }
            }
            boardService.deleteAdminBoard(delMsg[i]);
        }

        return "redirect:/admin/adminList";
    }
}
