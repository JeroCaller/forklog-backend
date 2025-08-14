package com.acorn.process.openfeign.kakao;

import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.openfeign.kakao.keyword.KeywordRequestDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordSearchProcess {
	
	private final KakaoRestOpenFeign kakaoRestOpenFeign;
	
	/**
	 * 문자열 검색어로 API 검색.
	 * 
	 * @author JeroCaller (JJH)
	 * @param searchKeyword
	 * @param page
	 * @return
	 */
	public KeywordResponseDto getApiResult(String searchKeyword, int page) {
		KeywordRequestDto requestDto = KeywordRequestDto.builder()
			.query(searchKeyword)
			.page(page)
			//.categoryGroupCode("FD6")
			.build();

		//log.info("카테고리 코드 - " + requestDto.getCategory_group_code());
		//log.info(String.valueOf(requestDto.getCategory_group_code().length()));
		KeywordResponseDto result = kakaoRestOpenFeign.getEateriesByKeyword(requestDto);
		
		//log.info("키워드로 장소 검색하기 조회 결과");
		//log.info(result.toString());
		
		return result;
	}
	
}
