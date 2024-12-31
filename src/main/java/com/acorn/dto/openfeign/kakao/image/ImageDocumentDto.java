package com.acorn.dto.openfeign.kakao.image;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "이미지 검색하기" API 데이터의 document에 해당하는 데이터를 받을 response DTO 클래스
 * 
 * 참고 자료
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@ToString  // For Logging
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageDocumentDto {
	
	private String collection;
	private String thumbnailUrl;
	private String imageUrl;
	private Integer width;
	private Integer height;
	private String displaySitename;
	private String docUrl;
	private String datetime;
	
}
