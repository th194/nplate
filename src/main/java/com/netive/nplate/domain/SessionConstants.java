package com.netive.nplate.domain;

/**
 * 세션명
 */
public class SessionConstants {

    // 회원 정보
    public static final String MEMBER_ID = "MEMBER_ID";     // 로그인 멤버 아이디
    public static final String MEMBER_DTO = "MEMBER_DTO";   // 로그인 멤버 DTO
    public static final String IS_LOGIN = "IS_LOGIN";       // 로그인 여부

    // 게시물
    public static final String LIKE_NUMBERS = "likeNumbers";       // 좋아하는 글번호
    public static final String FOLLOWING_IDS = "followingIds";  // 팔로잉 하는 사람들(ID 리스트)
    public static final String FOLLOWING_MEMBERS = "followingMembers";  // 팔로잉 하는 사람들(MemberDTO 리스트: ID,닉네임)

}
