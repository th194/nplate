package com.netive.nplate.util;

import com.netive.nplate.domain.FollowingDTO;
import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.service.FollowingService;
import com.netive.nplate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberUtils {

    @Autowired
    private FollowingService followingService;

    @Autowired
    private MemberService memberService;


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


    // 팔로잉 멤버 인포 조회(위의 메소드랑 합칠 수 있으면 합침)
    public List<Map> getFollowingsInfo(List<String> followingIds) {
        System.out.println("팔로잉 멤버 인포 조회=========");

        Map<String, Object> followingMap = new HashMap<>();
        followingMap.put("followingIds", followingIds);

        List<MemberDTO> followingMembers = memberService.getFollowingInfo(followingMap);

        List<Map> followings = new ArrayList<>();
        for (MemberDTO dto : followingMembers) {
            Map testMap = new HashMap<>();
            testMap.put("id", dto.getId());
            testMap.put("nickName", dto.getNickName());
            testMap.put("profileImg", dto.getProfileImg());
            followings.add(testMap);
        }

        System.out.println("팔로잉 맵: ");
        System.out.println(followings);
        return followings;
    }
}
