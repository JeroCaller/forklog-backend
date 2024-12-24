package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acorn.dto.AddressResponse;

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
			@RequestParam("x") String longitude,
			@RequestParam("y") String latitude,
			@RequestParam("radius") int radius,
			@RequestParam("page") int page
	);
	
	@GetMapping(value = "/local/geo/coord2address.json")
	AddressResponse convertAddress(
			@RequestParam("x") String longitude,
			@RequestParam("y") String latitude
	);
	
	@GetMapping(value = "local/search/address.json")
	AddressResponse convertRoadAddress(
			@RequestParam("query") String address
	);
}
