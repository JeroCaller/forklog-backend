package com.acorn.api;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.model.CategoriesModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KakaoRestApi {
	private Logger log = LoggerFactory.getLogger(KakaoRestApi.class);

	@Autowired
	private KakaoRestOpenFeign kakaoRestOpenFeign;
	
	private final String CATEGORY_GROUP_CODE = "FD6";

	@Autowired
	private NaverBlogSearch naverBlogSearch;
	
	@Autowired
	private CategoriesModel categoriesModel;

	/**
	 * 카카오 키워드로 장소 검색하기 API
	 * 
	 * @param searchValue
	 * @return List<Map<String, String>>
	 */
	public Object getEateries(String searchValue) {
		log.info("searchValue : {}", searchValue);
		// API 응답 데이터
		Map<String, Object> response = kakaoRestOpenFeign.getEateries(CATEGORY_GROUP_CODE, searchValue);

		log.info("Kakao - API 응답 데이터 : {}", response);
		// 응답 데이터 처리
		List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");

		List<Map<String, Object>> eateries = new ArrayList<>();
		for (Map<String, Object> doc : documents) {
			// 음식 카테고리 분류 구분 (대분류 > 소분류)
			String[] categories = ((String) doc.get("category_name")).split(" > ");
			String[] locations = ((String) doc.get("address_name")).split(" ");
			
			Map<String, Object> eatery = new HashMap<>();
			eatery.put("name", (String) doc.get("place_name"));
			// 음식 카테고리 구분
			if (categories.length > 1) {
				eatery.put("category_group", categories[1]);
			}
			if (categories.length > 2) {
				eatery.put("categories", categories[2]);
			}
		 	// 지역 분류
			if (locations.length > 1) {
				eatery.put("location_group", locations[0]);
			}
			if (locations.length > 2) {
				eatery.put("locations", locations[1]);
			}
			eatery.put("phone", doc.get("phone"));
			eatery.put("longitude", doc.get("x"));
			eatery.put("latitude", doc.get("y"));

			log.info("{}", String.valueOf(eatery.getOrDefault("locations", "")) + " " + eatery.get("name"));

//			eatery.put("blog", naverBlogSearch.searchBlog(String.valueOf(eatery.getOrDefault("locations", "")) + " " +  eatery.get("name")));
			eateries.add(eatery);
		}

		categoriesModel.updateCategory(documents);

		return eateries;
	}

}
