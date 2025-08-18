package com.acorn.dto.openfeign.kakao.blog;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "블로그 검색하기" API 데이터의 meta에 해당하는 데이터를 받을 response DTO 클래스
 *
 * <p>
 * 참고 자료<br/>
 * <a href="https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog">
 *     https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog
 * </a>
 * </p>
 *
 * <p>
 * 참고) <br/>
 * {@code @JsonNaming}: JSON으로 응답받아 이를 자바 DTO로 받을 때 만약 API 측에서
 * JSON 프로퍼티 키 네이밍 방식이 스네이크 케이스일 경우 이를 자바 내에서 카멜케이스로 변환하기 위해 
 * 사용됨. 외부로 들어오는 (또는 나가는) JSON 프로퍼티 키의 표기법을 명시한다.
 * </p>
 * <p>
 * {@code @JsonProperty}: 하나의 DTO 클래스 필드에 적용하는 어노테이션으로, 기능은 JsonNaming과 동일.
 * 다만 JsonNaming은 DTO 클래스 내 모든 필드에 적용하는 차이점이 있다. 
 * </p>
 *
 * @author JeroCaller (JJH)
 */
@Getter
@ToString  // For Logging
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BlogMetaDto {
	
	private int totalCount = 0;
	private int pageableCount = 0;
	private boolean isEnd;
	
}
