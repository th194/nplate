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
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    // 관리자 첫페이지
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
        return "member/error";
    }


    // 회원목록
    @GetMapping("/admin/member/list")
    public String memberList(Model model) {
        List<MemberDTO> memberList = memberService.getMemberList();
        model.addAttribute("memberList", memberList);
        return "bootstrap-template/admin-member-list";
    }


    // 만료 회원목록
    @GetMapping("/admin/member/expired")
    public String expiredMembers(Model model) {
        List<MemberDTO> memberList = memberService.getMemberList();
        List<Map> sortList = new ArrayList<>();

        for (MemberDTO memberDTO : memberList) {
            if (!memberDTO.isAccountNonExpired()) {
                Map<String, Object> member = new HashMap<>();
                member.put("isAccountNonExpired", false);

                member.put("id", memberDTO.getId());
                member.put("name", memberDTO.getName());
                member.put("nickName", memberDTO.getNickName());
                member.put("srbde", memberDTO.getSrbde());

                // 데이트 타입 포맷
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                String strDate = simpleDateFormat.format(memberDTO.getExpiredDate());
                member.put("expiredDate", strDate);

                sortList.add(member);
            }
        }
        model.addAttribute("memberList", sortList);
        
        return "bootstrap-template/admin-expired-members";
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
    public String adminListById(@ModelAttribute("params") PageDTO params, @RequestParam(value = "id", required = false) String id, Model model) {
        SearchDTO sDTO = new SearchDTO(id);
        sDTO.setRecordSize(10);
        params.setMemberId(id);
        int count = boardService.countById(params);
        Pagination pagination = new Pagination(count, params);
        List<BoardDTO> list = boardService.getBordListById(sDTO);
        PagingResponse<BoardDTO> boardList = new PagingResponse<>(list, pagination);
        model.addAttribute("memberId", id);
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


    // 관리자 회원 정보 보기
    @GetMapping("/admin/member/info")
    public String info(String id, Model model) throws IOException {
        MemberDTO memberDTO = memberService.getMemberInfo(id);

        // 생일 처리
        String birthday = memberDTO.getBirthday();
        String formatDay = birthday.substring(0,4) + "-" + birthday.substring(4,6) + "-" + birthday.substring(6);
        memberDTO.setBirthday(formatDay);

        model.addAttribute("memberInfo", memberDTO);
        model.addAttribute("area", Area.values());

        return "bootstrap-template/admin-userInfo";
    }


    /**
     * 관리자 회원 삭제(만료)
     * 일반 회원 탈퇴의 경우 바로 계정이 삭제되지만, 관리자용 회원 삭제의 경우 만료일자가 오늘로 변경됨
     * (DB에는 남아있으나 로그인 불가)
     */
    @GetMapping("/admin/member/delete")
    public String deleteMember(String id) throws IOException {
        memberService.putoutMember(id);
        
        // todo 만료계정 일정시간 지난 후 삭제하는 프로그램 추가
        return "redirect:/admin/member/list";
    }

    
    /**
     * 관리자 회원 만료처리 취소
     */
    @GetMapping("/admin/member/enable")
    public String enableMember(String id) throws IOException {
        memberService.enableMember(id);
        return "redirect:/admin/member/list";
    }

}
