package com.acorn.dto.openfeign.kakao.keyword;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카카오 API - "키워드로 장소 검색하기" 응답 데이터의 "document" 
 * 부분을 담당하는 DTO
 *
 * <p>
 * 참고 자료) <br/>
 * <a href="https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword-response-body-document">
 *   https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword-response-body-document
 * </a>
 * </p>
 *
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeywordDocumentDto {
	
	private String id;
	private String placeName;
	private String categoryName;
	private String categoryGroupCode;
	private String categoryGroupName;
	private String phone;
	private String addressName;
	private String roadAddressName;
	private String x;
	private String y;
	private String placeUrl;
	private String distance;
}
