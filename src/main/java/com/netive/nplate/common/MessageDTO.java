package com.netive.nplate.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class MessageDTO {
    private String message;             // 사용자에게 전달할 메시지
    private String redirectUri;         // 리다이렉트 URI
    private RequestMethod method;       // HTTP 요청 메소드
    private Map<String, Object> data;   // 화면(View)로 전달할 데이터(파라미터)

    public MessageDTO() {};
}
