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
    private String searchType;          // 검색 유형
    private int limitStart;             // 시작할 index
    private String memberId;            // 회원아이디

}
