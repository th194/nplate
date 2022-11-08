package com.netive.nplate.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.domain.ReplyDTO;
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
import java.util.List;

@RestController
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    /**
     * 댓글 목록
     * @param idx
     * @param params
     * @return
     */
    @GetMapping("/reply/{bbscttNo}")
    public JsonObject getReplyList(@PathVariable("bbscttNo") Long idx, @ModelAttribute("params") ReplyDTO params, HttpServletRequest request, Model model) {

        JsonObject jsonObj = new JsonObject();

        HttpSession session = request.getSession();

        System.out.println("로그인 체크 ====================== ");
        System.out.println((boolean) session.getAttribute("isLogOn"));
        System.out.println("로그인 체크 ====================== ");

        if ((boolean) session.getAttribute("isLogOn")) {

            MemberDTO dto = (MemberDTO) session.getAttribute("member");
            String id = dto.getId();
            System.out.println("로그인 id = " + id);


            List<ReplyDTO> replyList = replyService.getReplyList(params);
            if(CollectionUtils.isEmpty(replyList) == false) {

            // JSON 날짜 형식 지정 ( 혹시 필요하다면 사용..)
//            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()).create();
//            JsonArray jsonArr = gson.toJsonTree(replyList).getAsJsonArray();

                JsonArray jsonArr = new Gson().toJsonTree(replyList).getAsJsonArray();
                jsonObj.add("replyList", jsonArr);
            }
            jsonObj.addProperty("member", id); // 세션에 로그인한 아이디는 무조건 화면으로 넘김

        }

        return jsonObj;
    }

    /**
     * 댓글 등록/수정
     * @param idx
     * @param params
     * @return
     */
    @RequestMapping(value = {"/reply", "/reply/{bbscttNo}"}, method = { RequestMethod.POST, RequestMethod.PATCH })
    public JsonObject registerReply(@PathVariable(value="bbscttNo", required = false) Long idx, @RequestBody final ReplyDTO params, HttpServletRequest request) {

        JsonObject jsonObj = new JsonObject();

        HttpSession session = request.getSession();

        System.out.println("로그인 체크 ====================== ");
        System.out.println((boolean) session.getAttribute("isLogOn"));
        System.out.println("로그인 체크 ====================== ");

        if ((boolean) session.getAttribute("isLogOn")) {
            try {
                MemberDTO dto = (MemberDTO) session.getAttribute("member");
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
            // 세션없을때 member/index로 보냄
            this.redirect();
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

    public ResponseEntity<?> redirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("member/index"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

}