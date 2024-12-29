package com.acorn.process.openfeign.kakao;

import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.openfeign.kakao.blog.BlogDocumentsDto;
import com.acorn.dto.openfeign.kakao.blog.BlogRequestDto;
import com.acorn.dto.openfeign.kakao.blog.BlogResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogSearchProcess {
	
	private final KakaoRestOpenFeign kakaoRestOpenFeign;
	
	/**
	 * 블로그 한 건만 조사하여 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param query
	 * @return
	 */
	public BlogDocumentsDto getOneBlog(String query) {
		BlogRequestDto requestDto = BlogRequestDto.builder()
				.query(query)
				.build();
		BlogResponseDto responseDto 
			= kakaoRestOpenFeign.getEateryBlog(requestDto);
		BlogDocumentsDto result = null;
		if (responseDto != null && 
			responseDto.getDocuments() != null &&
			responseDto.getDocuments().size() != 0
		) {
			result = responseDto.getDocuments().getFirst();
		}
		return result;
	}
}
