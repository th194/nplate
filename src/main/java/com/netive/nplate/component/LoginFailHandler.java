package com.netive.nplate.component;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("로그인 실패 핸들러");
        exception.printStackTrace();

        String errorCd;

        if(exception instanceof BadCredentialsException) {
            // 자격증명 실패(아이디 존재히지 않거나, 비밀번호가 틀림)
            errorCd = "00";
        } else if (exception instanceof UsernameNotFoundException) {
            // 아이디 존재하지 않음(스프링부트 보안으로 BadCredentialsException 처리됨 구분하려면 따로 처리 추가)
            errorCd = "11";
        } else if (exception instanceof AccountExpiredException) {
            // 만료 계정
            errorCd = "99";
        } else {
            // 그 외
            errorCd = "44";
        }

        setDefaultFailureUrl("/login/fail?code=" + errorCd);
        super.onAuthenticationFailure(request, response, exception);
    }
}
