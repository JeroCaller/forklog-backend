package com.acorn.dto.openfeign.kakao.image;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * 카카오 API - "이미지 검색하기"에서의 응답 결과를 받기 위한 DTO 클래스
 * 
 * 참고 사이트
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
 * 
 * 응답 예시
 * {
  "documents": [
    {
      "collection": "blog",
      "datetime": "2024-01-26T00:00:48.000+09:00",
      "display_sitename": "티스토리",
      "doc_url": "https://...",
      "height": 794,
      "image_url": "https://blog.kakaocdn.net/.../.jpg",
      "thumbnail_url": "https://...",
      "width": 1123
    }
  ],
  "meta": {
    "is_end": false,
    "pageable_count": 1083,
    "total_count": 1085
  }
}
 * 
 * @author JeroCaller (JJH)
 * 
 */
@Getter
@ToString  // For Logging
public class ImageResponseDto {
	
	List<ImageDocumentDto> documents;
	ImageMetaDto meta;
	
}
