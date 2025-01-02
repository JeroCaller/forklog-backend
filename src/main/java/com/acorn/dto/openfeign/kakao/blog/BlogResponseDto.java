package com.acorn.dto.openfeign.kakao.blog;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "블로그 검색하기" API 데이터를 받을 DTO 클래스.
 * 
 * 참고 자료
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-blog
 * 
 * 응답 데이터 예시)
 * {
 * "documents": [
 * {
 *    "blogname": "...",
 *    "contents": "...",
 *    "datetime": "2024-12-18T23:13:00.000+09:00",
 *    "thumbnail": "https://search2.kakaocdn.net/...",
 *    "title": "...",
 *    "url": "https://blog.naver.com/..."
 *    }
 * ],
 * "meta": {
 *    "is_end": false,
 * 	  "pageable_count": 800,
 * 	  "total_count": 6770
 *   }
 * }
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@ToString  // For Logging
public class BlogResponseDto {
	
	List<BlogDocumentsDto> documents;
	BlogMetaDto meta;
	
}
