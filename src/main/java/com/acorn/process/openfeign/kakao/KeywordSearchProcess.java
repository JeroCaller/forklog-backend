package com.acorn.process.openfeign.kakao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.openfeign.kakao.keyword.KeywordDocumentDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordRequestDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationRoads;
import com.acorn.process.EateriesProcess;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeywordSearchProcess {
	
	@Autowired
	private KakaoRestOpenFeign kakaoRestOpenFeign;
	
	@Autowired
	private EateriesProcess eateriesProcess;
	
	/**
	 * 
	 * @author JeroCaller (JJH)
	 * @param searchKeyword
	 * @param page
	 * @param locationRoads - 검색 지역 DB 조회용
	 * @return
	 */
	public KeywordResponseDto getApiResult(String searchKeyword, Integer page, LocationRoads locationRoads) {
		KeywordRequestDto requestDto = KeywordRequestDto.builder()
				.query(searchKeyword)
				.page(page)
				//.categoryGroupCode("FD6")
				.build();
		/*
		log.info("카테고리 코드 - " + requestDto.getCategoryGroupCode());
		log.info(String.valueOf(requestDto.getCategoryGroupCode().length()));
		*/
		log.info("카테고리 코드 - " + requestDto.getCategory_group_code());
		log.info(String.valueOf(requestDto.getCategory_group_code().length()));
		KeywordResponseDto result = kakaoRestOpenFeign.getEateriesByKeywordTest(requestDto);
		
		//log.info("키워드로 장소 검색하기 조회 결과");
		//log.info(result.toString());
		
		// DB에 저장.
		eateriesProcess.saveApi(result.getDocuments());
		return result;
	}
	
}
