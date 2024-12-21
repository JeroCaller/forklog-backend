package com.acorn.api;

import com.acorn.api.openfeign.NaverOpenFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NaverBlogSearch { // 네이버 검색 API 예제 - 블로그 검색
	private Logger log = LoggerFactory.getLogger(NaverBlogSearch.class);
	
	@Autowired
	private NaverOpenFeign naverOpenFeign;

	@Autowired
	private ObjectMapper objectMapper; // JSON 처리용 ObjectMapper

	/**
	 * 블로그명을 입력 받아 블로그 정보를 Map으로 반환
	 * @param searchValue 블로그명
	 * @return Map 형태의 검색 결과
	 */
	public Map<String, Object> searchBlog(String searchValue) {
//		log.trace("검색어 : {}", searchValue);

		Map<String, Object> response = naverOpenFeign.searchBlog(searchValue);
//		log.trace("조회한 블로그 글 : {}", response);
		return response;
	}

}