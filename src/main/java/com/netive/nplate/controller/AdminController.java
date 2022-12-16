package com.netive.nplate.controller;

import com.netive.nplate.common.MessageDTO;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.*;
import com.netive.nplate.util.MemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminBoardService adminBoardService;

    @Autowired
    private AlarmService alarmService;
    

    // 관리자 첫페이지
    @GetMapping("/admin")
    public String adminMain(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String adminId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        System.out.println("관리자 페이지 로그인 아이디: ");
        System.out.println(session.getAttribute(SessionConstants.MEMBER_ID));
        
        // todo 모든 admin 페이지에 추가해야 함
        model.addAttribute("adminId", adminId);
        return "bootstrap-template/admin";
    }


    // 회원목록(일반유저)
    @GetMapping("/admin/member/list")
    public String memberList(Model model) {
        List<MemberDTO> memberList = adminService.getMemberList("user"); // 일반 유저 조회
//        List<MemberDTO> memberList = adminService.getMemberList("manager"); // 매니저
//        List<MemberDTO> memberList = adminService.getMemberList("admin"); // 어드민 조회
//        List<MemberDTO> memberList = adminService.getMemberList("expired"); // 만료회원 조회
//        List<MemberDTO> memberList = adminService.getMemberList("all"); // 모든회원 조회
        model.addAttribute("memberList", memberList);
        return "bootstrap-template/admin-member-list";
    }


    // 회원목록(매니저)
    @GetMapping("/admin/member/manager")
    public String managerList(Model model) {
        List<MemberDTO> memberList = adminService.getMemberList("manager");

        model.addAttribute("memberList", memberList);
        return "bootstrap-template/admin-manager-list";
    }


    // 만료 회원목록
    @GetMapping("/admin/member/expired")
    public String expiredMembers(Model model) {
        List<MemberDTO> memberList = adminService.getMemberList("expired"); // 만료회원 조회
        List<Map> members = new ArrayList<>();

        for (MemberDTO memberDTO : memberList) {
            Map<String, Object> member = new HashMap<>();
            member.put("isAccountNonExpired", false);

            member.put("id", memberDTO.getId());
            member.put("name", memberDTO.getName());
            member.put("nickName", memberDTO.getNickName());
            member.put("srbde", memberDTO.getSrbde());

            // 데이트 타입 포맷
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = simpleDateFormat.format(memberDTO.getExpiredDate());
            member.put("expiredDate", strDate);

            members.add(member);
        }
        model.addAttribute("memberList", members);
        
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
        PagingResponse<BoardDTO> boardList = new PagingResponse<>(list, params);
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
        int count = boardService.countById(id);
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);
        List<BoardDTO> list = boardService.getBordListById(sDTO);
        PagingResponse<BoardDTO> boardList = new PagingResponse<>(list, params);
        model.addAttribute("memberId", id);
        model.addAttribute("boardList", boardList);
        return "bootstrap-template/adminList";
    }

    /**
     * 공지사항 작성 페이지로 이동
     * @return
     */
    @GetMapping("/admin/adminNotice")
    public String adminNotice(@RequestParam(value = "idx", required = false) Long idx, Model model) {
        if(idx != null) {
            AdminBoardDTO board = adminBoardService.adminBoardView(idx);
            model.addAttribute("board", board);
        }

        return "bootstrap-template/adminNotice";
    }

    /**
     * 공지사항 등록
     * @return
     */
    @PostMapping("/admin/adminRegister")
    @ResponseBody
    public HashMap<String, Object> adminRegister(AdminBoardDTO params, HttpServletRequest request, Model model) {
        boolean result = false;
        HashMap<String, Object> resMap = new HashMap<>();
        HttpSession session = request.getSession();
        AlarmDTO alarm = new AlarmDTO();
        String adminId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        params.setMngrBbsWrter(adminId); // admin 로그인 ID

        Long idx = 0L;
        String subject = "";
        if(adminId.equals("admin")) {
            result = adminBoardService.registAdminBoard(params);
            idx = params.getMngrBbsNo();
            subject = params.getMngrBbsSj();
        }

        if(result) {
            List<MemberDTO> allMember = memberService.getAllUser();
            for(int i = 0; i < allMember.size(); i++) {
                String receiveId = allMember.get(i).getId();
                alarm.setNtcnReceiveMber(receiveId);
                alarm.setNtcnSendMber(adminId);
                alarm.setNtcnTrgtNo(idx);
                alarm.setNtcnKnd("notice");
                alarm.setNtcnTrgtSj(subject);
                alarmService.registerAlarm(alarm);
            }
            resMap.put("idx", idx);
        } else {
            resMap.put("error", "error");
        }
        return resMap;
    }

    /**
     * 공지사항 목록 보기
     * @param params
     * @param model
     * @return
     */
    @GetMapping("/admin/adminNoticeList")
    public String adminNoticeList(@ModelAttribute("params") PageDTO params, Model model) {

        int count = adminBoardService.adminBoardCount(params);
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        List<AdminBoardDTO> noticeList = adminBoardService.adminBoardList(params);
        PagingResponse<AdminBoardDTO> adminNoticeList = new PagingResponse<>(noticeList, params);
        model.addAttribute("adminNoticeList", adminNoticeList);
        return "bootstrap-template/adminNoticeList";
    }

    /**
     * 공지사항 수정
     * @param params
     * @param model
     * @return
     */
    @PostMapping("/admin/adminNoticeUpdate")
    public String adminNoticeUpdate(AdminBoardDTO params, Model model, HttpServletRequest request) {
        boolean result = false;
        HttpSession session = request.getSession();
        String adminId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
        Long idx = params.getMngrBbsNo();
        if (adminId.equals("admin")) {
            result = adminBoardService.updateAdminBoard(params);
        }
        MessageDTO message = new MessageDTO();
        if(result) {
            message = new MessageDTO("공지사항 수정이 완료되었습니다.", "/notice?idx="+idx, RequestMethod.GET, null);
        } else {
            message = new MessageDTO("공지사항 수정이 실패했습니다.", "/admin/adminNotice", RequestMethod.GET, null);
        }
        return showMessageAndRedirect(message, model);
    }

    /**
     * 공지사항 삭제
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/admin/adminNoticeDelete")
    public String adminNoticeDelete(Model model, HttpServletRequest request) {
        boolean result = false;
        HttpSession session = request.getSession();

        String adminId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

        String[] delMsg = request.getParameterValues("delArr");

        // delMsg 길이와 delWriter 길이는 같다.
        int size = delMsg.length;
        // 삭제할 게시글 수만큼 loop
        if(adminId.equals("admin")) {
            for ( int i = 0; i < size; i ++) {
                result = adminBoardService.deleteAdminBoard(delMsg[i]);
            }
        }

        MessageDTO message = new MessageDTO();
        if(result) {
            message = new MessageDTO("공지사항 삭제가 완료되었습니다.", "/admin/adminNoticeList", RequestMethod.GET, null);
        } else {
            message = new MessageDTO("공지사항 삭제가 실패했습니다.", "/admin/adminNoticeList", RequestMethod.GET, null);
        }
        return showMessageAndRedirect(message, model);
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
        String[] delMsg = request.getParameterValues("delArr");

        // delMsg 길이와 delWriter 길이는 같다.
        int size = delMsg.length;
        // 삭제할 게시글 수만큼 loop
        for ( int i = 0; i < size; i ++) {
            boardService.deleteAdminBoard(delMsg[i]);
        }

        return "redirect:/admin/adminList";
    }

    /**
     * 회원목록 전체 조회 (ID만)
     * @return
     */
    @GetMapping("/admin/allMember")
    @ResponseBody
    public HashMap<String, Object> getAllUser() {

        HashMap<String, Object> resMap = new HashMap<>();

        resMap.put("allMember", memberService.getAllUser());

        return resMap;
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
    @GetMapping("/admin/member/putout")
    public @ResponseBody String putoutMember(String id) {
        int result = adminService.putoutMember(id);

        if (result == 1) {
            return "success";
        }

        return "fail";
    }

    
    /**
     * 관리자 회원 만료처리 취소
     */
    @GetMapping("/admin/member/enable")
    public String enableMember(String id) {
        adminService.enableMember(id);
        return "redirect:/admin/member/list";
    }


    // 회원 계정 영구 삭제
    @GetMapping("/admin/member/delete")
    public String deleteMember(String id) throws IOException {
        MemberDTO memberDTO = memberService.getMemberInfo(id);

        // 회원 DB 삭제처리
        memberService.deleteMember(id);

        //프로필 사진 파일 및 DB 삭제(기본 이미지가 아닌 경우에만)
        if (!memberDTO.getProfileImg().equals("default")) {
            fileService.deleteFile(id);
        }

        return "redirect:/admin/member/expired";
    }


    // 회원 여러명 추가 폼
    @GetMapping("/admin/member/addForm")
    public String addMemberForm() {
        return "bootstrap-template/admin-add-member";
    }


    // 회원 여러명 추가
    @PostMapping("/admin/member/add")
    public String addMembers(MemberDTO memberDTO, int count, Model model) {
        try {
            System.out.println("회원 여러명 추가===========");

            // id 제외 모두 같은 값
            String id = memberDTO.getId();
            String pwd = memberDTO.getPwd();
            String name = memberDTO.getName();
            String nickName = memberDTO.getNickName();
            String tel = memberDTO.getTel();

            for (int i = 0; i < count; i++) {
                MemberDTO addMember = new MemberDTO();
                addMember.setId(id + "-" + i);
                addMember.setPwd(pwd);
                addMember.setName(name);
                addMember.setNickName(nickName);
                addMember.setTel(tel);
                userService.addMembers(addMember);
            }

            return "redirect:/admin/member/list";

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * 권한 변경
     */
    @GetMapping("/admin/member/changeRole")
    public @ResponseBody String changeMemberRole(String id) {
        try {
            System.out.println("일반회원 권한 매니저로 변경========");
            System.out.println("id" + id);

            int result = adminService.changeMemberRole(id);
            if (result == 1) {
                return "success";
            }
        } catch (Exception e) {
            System.out.println("에러");
            e.printStackTrace();
        }
        return "fail";
    }


    private String showMessageAndRedirect(final MessageDTO params, Model model) {
        model.addAttribute("params", params);
        return "common/messageRedirect";
    }

}
