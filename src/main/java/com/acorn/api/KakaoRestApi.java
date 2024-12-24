package com.acorn.api;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.AddressResponse;
import com.acorn.exception.NotFoundException;
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

	/**
	 * 경위도를 입력받아서 도로명 주소로 변경 (Kakao API : 좌표를 주소로 변환하기)
	 * 도로명 주소가 없을 경우 
	 * 지번 주소를 도로명 주소로 변환 (Kakao API : 주소를 좌표로 변환하기)
	 * 주소 데이터 전송 목적의 AddressResponse DTO 활용
	 * @return : 도로명 주소
	 */
	public String convertAddress(String lat, String lng) {
        AddressResponse response = kakaoRestOpenFeign.convertAddress(lng, lat);
        
        if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new NotFoundException("주소 데이터를 찾을 수 없습니다.");
        }

        AddressResponse.Document doc = response.getDocuments().get(0); 

        if (doc.getRoad_address() != null) {
            return doc.getRoad_address().getRoad_name(); // 도로명 주소 반환
        } else if (doc.getAddress() != null) {
            return convertRoadAddress(doc.getAddress().getAddress_name()); // 지번 주소 변환
        }

        throw new IllegalArgumentException("주소 데이터가 유효하지 않습니다.");
	}
	
	/**
	 * 지번 주소를 도로명 주소로 변환 (Kakao API : 주소를 좌표로 변환하기)
	 * @param address : 지번 주소
	 * @return : 도로명 주소
	 */
	public String convertRoadAddress(String address) {
        AddressResponse response = kakaoRestOpenFeign.convertRoadAddress(address);

        if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new NotFoundException("도로명 주소 데이터를 찾을 수 없습니다.");
        }

        AddressResponse.Document doc = response.getDocuments().get(0);

        if (doc.getRoad_address() == null) {
        	throw new IllegalArgumentException("도로명 주소 데이터가 유효하지 않습니다.");
        }
        
        return doc.getRoad_address().getRoad_name(); // 도로명 주소 반환
	}
}
