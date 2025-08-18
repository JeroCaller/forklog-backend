package com.acorn.api.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acorn.dto.openfeign.kakao.blog.BlogRequestDto;
import com.acorn.dto.openfeign.kakao.blog.BlogResponseDto;
import com.acorn.dto.openfeign.kakao.geo.location.GeoLocationResponseDto;
import com.acorn.dto.openfeign.kakao.image.ImageRequestDto;
import com.acorn.dto.openfeign.kakao.image.ImageResponseDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordRequestDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;

/**
 * 카카오 API 호출하여 데이터를 받기 위한 OpenFeign 라이브러리 제공 인터페이스 사용. 
 */
@FeignClient(
	name = "KakaoRestOpenFeign",
	url = "https://dapi.kakao.com/v2",
	configuration = KakaoOpenFeignConfig.class
)
public interface KakaoRestOpenFeign {
	
	/**
	 * 카카오 API - "좌표를 주소로 변환하기" 요청 메서드.
	 * 경위도 좌표 (x, y) 입력 시 그에 해당하는 주소 정보를 받을 수 있다. 
	 *
	 * <p>
	 * 참고 사이트) <br/>
	 * <a href="https://developers.kakao.com/tool/rest-api/open/get/v2-local-geo-coord2address.%7Bformat%7D">
	 *     https://developers.kakao.com/tool/rest-api/open/get/v2-local-geo-coord2address.%7Bformat%7D
	 * </a>
	 * </p>
	 * 
	 * @author JeroCaller (JJH)
	 * @param x
	 * @param y
	 * @return
	 */
	@GetMapping(value = "/local/geo/coord2address.json")
	GeoLocationResponseDto getAddressFromCoordinate(
		@RequestParam("x") String x,
		@RequestParam("y") String y
	);
	
	/**
	 * 카카오 API - "키워드 장소 검색" 요청 메서드.
	 * 지역명과 같은 키워드 입력 시 그에 해당하는 음식점 정보들을 얻어올 수 있다. 
	 *
	 * <p>
	 * 참고) <br/>
	 * Get 요청에서 요청 파라미터를 DTO로 넘기고자 할 경우 
	 * 해당 인자 앞에 `@SpringQueryMap`을 붙여야 요청 정보가 제대로 전달됨. 
	 * 이를 작성하지 않는 경우 요청 정보가 전달되지 않아 검색 결과가 없는 것으로 조회되는 버그 발생.
	 * </p>
	 *
	 * <p>
	 * 참고 사이트) <br/>
	 * <a href="https://developers.kakao.com/tool/rest-api/open/get/v2-local-search-keyword.%7Bformat%7D">
	 *     https://developers.kakao.com/tool/rest-api/open/get/v2-local-search-keyword.%7Bformat%7D
	 * </a>
	 * </p>
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping(value = "/local/search/keyword.json")
	KeywordResponseDto getEateriesByKeyword(@SpringQueryMap KeywordRequestDto requestDto);
	
	/**
	 * 카카오 API - "이미지 검색하기" 요청 메서드. 
	 * 메인 화면에 보일 음식점 썸네일 사진 한 장 데이터 가져오기
	 *
	 * <p>
	 * 참고 사이트) <br/>
	 * <a href="https://developers.kakao.com/tool/rest-api/open/get/v2-search-image">
	 *     https://developers.kakao.com/tool/rest-api/open/get/v2-search-image
	 * </a>
	 * </p>
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping(value = "/search/image")
 	ImageResponseDto getEateryImage(@SpringQueryMap ImageRequestDto requestDto);
	
	/**
	 * 카카오 API - "블로그 검색하기" 요청 메서드
	 *  
	 * <p>특정 음식점의 상세 설명을 위해 블로그의 일부 글을 발췌</p>
	 *
	 * <p>
	 * 참고 사이트) <br/>
	 * <a href="https://developers.kakao.com/tool/rest-api/open/get/v2-search-blog">
	 *     https://developers.kakao.com/tool/rest-api/open/get/v2-search-blog
	 * </a>
	 * </p>
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping(value = "/search/blog")
	BlogResponseDto getEateryBlog(@SpringQueryMap BlogRequestDto requestDto);
}
