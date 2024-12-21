package com.acorn.api;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.model.CategoriesModel;
import com.acorn.model.EateriesModel;

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
	
	@Autowired
	private EateriesModel eateriesModel;

	/**
	 * 카카오 키워드로 장소 검색하기 API
	 * 
	 * @param searchValue
	 * @return List<Map<String, String>>
	 */
	public Object getEateries(String searchValue) {
		List<Map<String, Object>> eateries = new ArrayList<>();
		int count = 1;
		int page = 1;
		do {
			// API 응답 데이터
			Map<String, Object> response = kakaoRestOpenFeign.getEateriesByKeyword(CATEGORY_GROUP_CODE, searchValue, count);
			
			
			Map<String, Object> meta = (Map<String, Object>) response.get("meta");
			page = (Integer) meta.get("pageable_count");
//			log.info("page counts : {}", page);
				// 응답 데이터 처리
				List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
				
				
				for (Map<String, Object> doc : documents) {
					// 음식 카테고리 분류 구분 (대분류 > 소분류)
					String[] categories = ((String) doc.get("category_name")).split(" > ");
					String[] locations = ((String) doc.get("road_address_name")).split(" ");
					
					Map<String, Object> eatery = new HashMap<>();
					eatery.put("name", (String) doc.get("place_name"));
					
					// 음식 카테고리 구분
					if (categories.length > 1) { // 대분류 항목이 있을 경우 Map에 저장
						eatery.put("category_group", categories[1]);
					}
					if (categories.length > 2) { // 소분류 항목이 있을 경우 Map에 저장
						eatery.put("categories", categories[2]);
					}
					
					// 지역 분류
					if (locations.length > 1) { //
						eatery.put("location_group", locations[0]);
					}
					if (locations.length > 2) {
						eatery.put("locations", locations[1]);
					}
					if (locations.length > 3) {
						eatery.put("location_road", locations[2].substring(0, locations[2].indexOf("로") + 1));
					}
					
					eatery.put("phone", doc.get("phone"));
					eatery.put("longitude", doc.get("x"));
					eatery.put("latitude", doc.get("y"));
					
//					log.info("{}", String.valueOf(eatery.getOrDefault("locations", "")) + " " + eatery.get("name"));
					
//			eatery.put("blog", naverBlogSearch.searchBlog(String.valueOf(eatery.getOrDefault("locations", "")) + " " +  eatery.get("name")));
					eateries.add(eatery);
					eateriesModel.updateEatery(eatery);
				}
				
			count++;
		} while (count < 5);
//				categoriesModel.updateCategory(eateries);

		return eateries;
	}

}
