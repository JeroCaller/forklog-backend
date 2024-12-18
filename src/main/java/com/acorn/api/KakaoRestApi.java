package com.acorn.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class KakaoRestApi {
	private Logger log = LoggerFactory.getLogger(KakaoRestApi.class);

	@Value("${kakao.rest_api_key}")
	private String kakaoApiKey;
	
	@Value("${kakao.redirect_uri}")
	private String kakaoRedirectUri;

	/**
	 * 카카오 키워드로 장소 검색하기 API  
	 * @param searchValue
	 * @return
	 */
	public List<Map<String, String>> getEateries(String searchValue) {
		log.info("kakaoApiKey : {}", kakaoApiKey);
		log.info("searchValue : {}", searchValue);

		// RestTemplate : 
		RestTemplate restTemplate = new RestTemplate(); // DI로 처리할 것
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoApiKey);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.json?category_group_code=FD6&query=".concat(searchValue);

		// API 호출
		ResponseEntity<Map> response = restTemplate.exchange(apiURL, HttpMethod.GET, entity, Map.class);
		log.info("response : {}", response);
		
		// 응답 데이터 처리
		if (response.getStatusCode() == HttpStatus.OK) {
			List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");

			List<Map<String, String>> eateries = new ArrayList<>();
			for (Map<String, Object> doc : documents) {
				// 음식 카테고리 분류 구분 (대분류 > 소분류)
				String[] categories = ((String) doc.get("category_name")).split(" > ");
				
				Map<String, String> eatery = new HashMap<>();
				eatery.put("name", (String) doc.get("place_name"));
				// 카테고리 구분
				if (categories.length > 1) {
					eatery.put("category_group", categories[1]);
				}
				if (categories.length > 2) {
					eatery.put("categories", categories[2]);
				}
				eatery.put("longitude", (String) doc.get("x"));
				eatery.put("latitude", (String) doc.get("y"));
				eateries.add(eatery);
			}

			// 음식점 정보 출력
			log.info("eateries : {}", eateries);
			return eateries;
		}
		return null;
	}
	
	
//	public String getAccessToken(String code) {
//		
//	}
}
