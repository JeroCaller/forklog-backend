package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="KakaoRestOpenFeign", url = "https://dapi.kakao.com/v2")
public interface KakaoRestOpenFeign {
	
	@PostMapping(value = "/local/search/keyword.json")
	Map<String, Object> getEateriesByKeyword (
			@RequestParam("category_group_code") String categoryGroupCode,
			@RequestParam("query") String searchValue,
			@RequestParam("page") int page
	);
	
	@PostMapping(value = "/local/search/category.json")
	Map<String, Object> getEateriesByCategory (
			@RequestParam("category_group_code") String categoryGroupCode,
			@RequestParam("x") boolean longitude,
			@RequestParam("y") boolean latitude,
			@RequestParam("radius") int radius,
			@RequestParam("page") int page
			);
	
}
