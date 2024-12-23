package com.acorn.dto.openfeign.kakao.keyword;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 API - "키워드로 장소 검색하기" API 요청 DTO
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeywordRequestDto {
	private String query;
	
	@Builder.Default
	/*@JsonProperty("category_group_code")
	private String categoryGroupCode = "FD6";
	*/
	// JsonProperty, JsonNaming이 먹히지 않아 부득이하게 스네이크 케이스로 작성함...
	private String category_group_code = "FD6";
	
	private String x;
	private String y;
	private String radius;
	private String rect;
	private Integer page;
	private Integer size;
	private String sort;
}
