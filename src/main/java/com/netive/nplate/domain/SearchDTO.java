package com.netive.nplate.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchDTO {
    private int recordSize = 30;             // 페이지당 출력할 데이터 개수
    private String keyword;             // 검색 키워드
    private int limitStart;             // 시작할 index
    private String memberId;            // 회원아이디
    private String searchType;          // 리스트 유형(검색: search, 해시태그: hashtag, 팔로잉: following, 좋아한 글: like, ''이거나 null 이면 id 1개로 조회)


    public SearchDTO(String memberId) {
        this.memberId = memberId;
    }
}
