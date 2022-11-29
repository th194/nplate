package com.netive.nplate.component;

import com.netive.nplate.domain.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {
    // 로그인 한 사람 전체
    private static List<WebSocketSession> list = new ArrayList<>();

    Map<String, WebSocketSession> userSessionMap = new HashMap<>();

    /**
     * 클라이언트 접속 시 호출되는 메소드
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);
        String senderName = getUserName(session);
        userSessionMap.put(senderName, session);
        log.info(session + "클라이언트 접속");
    }

    /**
     * 소켓에 메시지 보낼 때 호출되는 메소드
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : " + payload);

        if(StringUtils.isNotEmpty(payload)) {
            String[] strs = payload.split(",");

            // 화면에서 넘겨줄 데이터
            String idx = strs[0];            // 게시글 번호
            String writer = strs[1];        // 글 작성자
            String loginUser = strs[2];     // 로그인 사용자
            String alarmEvent = strs[3];    // 알람 이벤트

            WebSocketSession event = userSessionMap.get(idx);

            if("alarm".equals(idx) && event != null) {
                TextMessage tmpMsg = new TextMessage(loginUser + "님에 의해 <a href='#'>" + idx + " 번 게시글이 삭제되었습니다.");

                event.sendMessage(tmpMsg);
            }
        }


        for(WebSocketSession sess : list) {
            sess.sendMessage(message);
        }
    }

    /**
     * 클라이언트 접속 해제 시 호출되는 메소드
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("클라이언트 접속 해제");
        userSessionMap.remove(session.getId());
        list.remove(session);
    }

    private String getUserName(WebSocketSession session) {

        return session.getId();
    }
}
