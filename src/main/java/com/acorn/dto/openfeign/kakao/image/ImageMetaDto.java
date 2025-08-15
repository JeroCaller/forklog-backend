package com.acorn.dto.openfeign.kakao.image;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "이미지 검색하기" API 데이터의 meta에 해당하는 데이터를 받을 response DTO 클래스
 *
 * <p>
 * 참고 자료 <br/>
 * <a href="https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image">
 *     https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
 * </a>
 * </p>
 *
 * @author JeroCaller (JJH)
 */
@Getter
@ToString  // For Logging
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageMetaDto {
	
	private int totalCount = 0;
	private int pageableCount = 0;
	private boolean isEnd;
}
