package com.netive.nplate.controller;

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
                return "bootstrap-template/admin";
            }
        } catch(Exception e) {
//            e.printStackTrace();
        }
        return "member/admin-login"; // todo 처리 변경

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

        List<BoardDTO> list = boardService.getAllBoardList(params);
        PagingResponse<BoardDTO> boardList = new PagingResponse<>(list, pagination);
        model.addAttribute("boardList", boardList);
        return "bootstrap-template/adminList";
    }

    /**
     * 관리자용 사용자별 게시글 목록 보기
     * @return
     */
    @GetMapping("/admin/adminListById")
    public String adminListById(String id) {

        return null;
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
