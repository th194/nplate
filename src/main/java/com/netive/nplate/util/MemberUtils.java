package com.netive.nplate.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class MemberUtils {

    // 암호화 분리
    public String encrypt(String str) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(str.getBytes());
        byte[] bytePwd = md.digest();

        StringBuffer sb = new StringBuffer();
        for (byte b : bytePwd) {
            sb.append(String.format("%02x", b));
        }

        String encPwd = sb.toString();
        System.out.println("암호화 비밀번호====");
        System.out.println(encPwd);


        return encPwd;
    }

}
