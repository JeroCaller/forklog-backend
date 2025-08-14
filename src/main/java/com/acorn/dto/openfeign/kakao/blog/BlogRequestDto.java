package com.acorn.dto.openfeign.kakao.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카카오 API - "블로그 검색하기"에서 블로그 정보 요청을 위한 DTO 클래스.
 *
 * <p>
 * 참고 사이트 <br/>
 * <a href="https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog">
 *     https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog
 * </a>
 * </p>
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class BlogRequestDto {
	
	// sort 값 전용
	public static final String ACCURACY = "accuracy";
	public static final String RECENCY = "recency";
	
	@Builder.Default
	private String query = "";
	
	@Builder.Default
	private String sort = ACCURACY;
	
	@Builder.Default
	private int page = 1;
	
	@Builder.Default
	private int size = 1;
	
}
