package com.acorn.dto.openfeign.kakao.keyword;

import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카카오 API - "키워드로 장소 검색하기" 응답 데이터의 "meta" 
 * 부분을 담당하는 DTO
 *
 * <p>
 * 참고 자료) <br/>
 * <a href="https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword-response-body-meta">
 *     https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword-response-body-meta
 * </a>
 * </p>
 *
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeywordMetaDto {
	
	private int totalCount = 0;
	private int pageableCount = 0;
	private boolean isEnd;
	
	// 지금은 불필요한 정보라 생각되어 Map으로 처리함.
	private Map<String, Object> sameName;
}
