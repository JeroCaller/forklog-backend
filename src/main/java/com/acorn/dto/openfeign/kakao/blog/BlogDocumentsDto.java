package com.acorn.dto.openfeign.kakao.blog;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "블로그 검색하기"에서 블로그 정보 응답 일부인 documents를 받기 위한 DTO 클래스.
 * 
 * 참고 사이트
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@ToString // For Logging
public class BlogDocumentsDto {
	
	private String title;
	private String contents;
	private String url;
	private String blogname;
	private String thumbnail;
	private String datetime;
	
}
