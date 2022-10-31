package com.netive.nplate.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {
	private Long bbscttNo;				// 글번호(PK)
	private String bbscttSj;			// 제목
	private String bbscttCn;			// 내용
	private String bbscttWrter;			// 작성자
	private String bbscttRgsde;			// 등록일
	private String bbscttUpdde;			// 수정일
	private Long bbscttRdcnt;			// 조회수
	private Long bbscttLike;			// 좋아요 수
	private String bbscttImage;			// 이미지
	private int bbscttGrade;			// 별점
	private String bbscttStore;			// 가게(장소) 이름
	private String bbscttHashtag;		// 해시태그
	private Double bbscttCchLa;			// 지도 위도
	private Double bbscttCchLo;			// 지도 경도

	private String type;				// 검색 타입
	private String keyword;				// 검색 내용
	public BoardDTO() {
	}

	public BoardDTO(String bbscttWrter) {
		this.bbscttWrter = bbscttWrter;
	}
}


