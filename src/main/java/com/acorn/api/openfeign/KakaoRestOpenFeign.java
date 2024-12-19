package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="KakaoRestOpenFeign", url = "https://dapi.kakao.com/v2")
public interface KakaoRestOpenFeign {
	
	@PostMapping(value = "/local/search/keyword.json")
	Map<String, Object> getEateries (
			@RequestParam("category_group_code") String categoryGroupCode,
			@RequestParam("query") String searchValue
	);
	
}
