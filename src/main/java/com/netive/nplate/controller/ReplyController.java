package com.netive.nplate.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.MemberService;
import com.netive.nplate.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.*;

@RestController
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    @Autowired
    private MemberService memberService;

    /**
     * 댓글 목록
     * @param idx
     * @param params
     * @return
     */
    @GetMapping("/reply/{bbscttNo}")
    public JsonObject getReplyList(@PathVariable("bbscttNo") Long idx, @ModelAttribute("params") PageDTO params, HttpServletRequest request) {

        JsonObject jsonObj = new JsonObject();
        Gson gson = new Gson();
        HttpSession session = request.getSession();

        System.out.println("로그인 체크 ====================== ");
        System.out.println((boolean) session.getAttribute(SessionConstants.IS_LOGIN));
        System.out.println("로그인 체크 ====================== ");

        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
                String id = dto.getId();

                int count = replyService.getReplyBoardCount(idx);
                Pagination pagination = new Pagination(count, params);
                params.setPagination(pagination);

                List<ReplyDTO> replyList = replyService.getReplyList(params);
                PagingResponse<ReplyDTO> replyListPage = new PagingResponse<>(pagination);

                if(CollectionUtils.isEmpty(replyList) == false) {
                    JsonArray jsonArr = gson.toJsonTree(replyList).getAsJsonArray();
                    JsonElement pageInfo = gson.toJsonTree(replyListPage);
                    jsonObj.add("replyList", jsonArr);
                    jsonObj.add("pagination", pageInfo);
                    String[] memberInfo = new String[replyList.size()];
                    List<MemberDTO> memberList = new ArrayList<>();
                    List<String> memberProfile = new ArrayList<>();

                    // 댓글 리스트에 담겨있는 작성자 id로 작성자 정보 조회
                    for (int i = 0; i < replyList.size(); i++ ) {
                        memberInfo[i] = replyList.get(i).getAnswerWrter();
                        memberList.add(memberService.getUserInfo(memberInfo[i]));
                        memberProfile.add(memberList.get(i).getProfileImg());
                        JsonElement jEl = gson.toJsonTree(memberProfile);
                        jsonObj.add("profile", jEl);
                    }
                }
                jsonObj.addProperty("member", id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            jsonObj.addProperty("message", "세션 만료");
        }

        return jsonObj;
    }

    /**
     * 댓글 등록
     * @param
     * @param params
     * @return
     */
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    public JsonObject registerReply(@RequestBody final ReplyDTO params, HttpServletRequest request) {

        JsonObject jsonObj = new JsonObject();

        HttpSession session = request.getSession();


        System.out.println("로그인 체크 ====================== ");
        System.out.println((boolean) session.getAttribute(SessionConstants.IS_LOGIN));
        System.out.println("로그인 체크 ====================== ");
        System.out.println("params=========================");
        System.out.println(params);
        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
                String id = dto.getId();
                System.out.println("로그인 id = " + id);

                boolean isRegisterd = replyService.registerReply(params);
                jsonObj.addProperty("result", isRegisterd);
                jsonObj.addProperty("member", id);


            } catch (DataAccessException dae) {
                jsonObj.addProperty("message", "데이터베이스 처리 오류");
            } catch (Exception e) {
                jsonObj.addProperty("message", "시스템 문제 발생");
            }
        } else {
            jsonObj.addProperty("message", "세션 만료");
        }

        return jsonObj;
    }

    /**
     * 댓글 수정
     * @param boardIdx
     * @param params
     * @param request
     * @return
     */
    @RequestMapping(value = "/reply/{bbscttNo}", method = RequestMethod.PATCH)
    public JsonObject updateReply(@PathVariable(value="bbscttNo", required = false) Long boardIdx, @RequestBody final ReplyDTO params, HttpServletRequest request) {

        boolean isRegistered = false;
        JsonObject jsonObj = new JsonObject();

        HttpSession session = request.getSession();


        System.out.println("========================request");
        System.out.println(request);

        System.out.println("로그인 체크 ====================== ");
        System.out.println((boolean) session.getAttribute(SessionConstants.IS_LOGIN));
        System.out.println("로그인 체크 ====================== ");

        if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN)) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
                String id = dto.getId();
                System.out.println("로그인 id = " + id);
                if(params.getAnswerWrter().equals(id)) {
                    isRegistered = replyService.updateReply(params);
                }
                jsonObj.addProperty("result", isRegistered);
                jsonObj.addProperty("member", id);


            } catch (DataAccessException dae) {
                jsonObj.addProperty("message", "데이터베이스 처리 오류");
            } catch (Exception e) {
                jsonObj.addProperty("message", "시스템 문제 발생");
            }
        } else {
            jsonObj.addProperty("message", "세션 만료");
        }

        return jsonObj;
    }

    /**
     * 댓글 삭제
     * @param idx
     * @return
     */
    @DeleteMapping("/reply/{answerNo}")
    public JsonObject deleteReply(@PathVariable("answerNo") final Long idx) {
        JsonObject jsonObj = new JsonObject();

        try {
            boolean isDeleted = replyService.deleteReply(idx);
            jsonObj.addProperty("result", isDeleted);
        } catch (DataAccessException dae) {
            jsonObj.addProperty("message", "데이터베이스 처리 오류");
        } catch (Exception e) {
            jsonObj.addProperty("message", "시스템 문제 발생");
        }

        return jsonObj;
    }
}
