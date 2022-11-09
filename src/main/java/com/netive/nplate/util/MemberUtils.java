package com.netive.nplate.util;

import com.netive.nplate.domain.FollowingDTO;
import com.netive.nplate.service.FollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MemberUtils {

    @Autowired
    private FollowingService followingService;

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


    // 팔로잉 한 사람 조회
    public List<String> getFollowingMember(String id) {
        System.out.println("팔로잉 한 아이디 조회=========");
        System.out.println("조회 할 아이디" + id);
        List<FollowingDTO> list = followingService.getFollowingMember(id);
        List<String> following = new ArrayList<String>();
        for (FollowingDTO followingDTO : list) {
            following.add(followingDTO.getFollowingId());
        }
        return following;
    }
}
