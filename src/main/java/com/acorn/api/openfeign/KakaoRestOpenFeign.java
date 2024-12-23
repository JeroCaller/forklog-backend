package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acorn.dto.openfeign.kakao.keyword.KeywordRequestDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;

import java.util.Map;

@FeignClient(name="KakaoRestOpenFeign", url = "https://dapi.kakao.com/v2", configuration = KakaoOpenFeignConfig.class)
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
	
	/**
	 * 키워드 장소 검색 테스트
	 * 
	 * 참고)
	 * Get 요청에서 요청 파라미터를 DTO로 넘기고자 할 경우 
	 * 해당 인자 앞에 `@SpringQueryMap`을 붙여야 요청 정보가 제대로 전달됨. 
	 * 이를 작성하지 않는 경우 요청 정보가 전달되지 않아 검색 결과가 없는 것으로 조회되는 버그 발생.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping(value = "/local/search/keyword.json")
	KeywordResponseDto getEateriesByKeywordTest(@SpringQueryMap KeywordRequestDto requestDto);
}
