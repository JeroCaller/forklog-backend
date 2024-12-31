package com.acorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.location.LocationSplitDto;
import com.acorn.exception.BadAlgorithmException;
import com.acorn.exception.NoDataFoundException;
import com.acorn.exception.category.NoCategoryFoundException;
import com.acorn.process.EateriesMainProcess;
import com.acorn.process.GeoLocationProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;
import com.acorn.utils.LocationUtil;
import com.acorn.utils.PageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DB로부터 음식점(eateries) 정보 가져오는 컨트롤러 클래스.
 */
@RestController
@Slf4j
@RequestMapping("/main/locations")
@RequiredArgsConstructor
public class EateriesMainController {
	
	private final EateriesMainProcess eateriesMainProcess;
	private final GeoLocationProcess geoLocationProcess;
	
	/**
	 * 클라이언트에서 주소 입력 시 그에 해당하는 음식점 정보들을 반환. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/{location}")
	public ResponseEntity<ResponseJson> getEateriesByAddress(
			@PathVariable("location") String location,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		
		Pageable pageRequest = PageUtil.getPageRequestOf(page, size);
		Page<EateriesDto> eateries = eateriesMainProcess
				.getEateriesByLocation(location, pageRequest);
		if (PageUtil.isEmtpy(eateries)) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
	
	/**
	 * 지역 및 음식 카테고리 대분류 두 검색 조건이 적용되어 검색된 음식점 정보들을 반환.
	 * 
	 * 예를 들어 지역: 서울, 카테고리: 한식 입력 시 서울 지역 내 한식 카테고리에 해당하는 
	 * 모든 음식점 정보들을 페이징하여 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param largeId
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/{location}/categories/large/{lid}")
	public ResponseEntity<ResponseJson> getEateriesByLocationAndCategoryLarge(
			@PathVariable(name = "location") String location,
			@PathVariable(name = "lid") int largeId,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		
		Pageable pageRequest = PageUtil.getPageRequestOf(page, size);
		Page<EateriesDto> eateries = null;
		try {
			eateries = eateriesMainProcess
					.getEateriesByLocationAndCategoryLarge(
							location, 
							largeId, 
							pageRequest
					);
		} catch (NoCategoryFoundException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.build();
			return responseJson.toResponseEntity();
		}
		
		if (PageUtil.isEmtpy(eateries)) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
	
	/**
	 * 지역 및 음식 카테고리 소분류 두 검색 조건이 적용되어 검색된 음식점 정보들을 반환.
	 *
	 * 카테고리 소분류 ID만 있어도 카테고리 대분류를 알아낼 수 있음.
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param smallId
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/{location}/categories/small/{sid}")
	public ResponseEntity<ResponseJson> getEateriesByLocationAndCategorySmall(
			@PathVariable(name = "location") String location,
			@PathVariable(name = "sid") int smallId,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		
		Pageable pageRequest = PageUtil.getPageRequestOf(page, size);
		Page<EateriesDto> eateries = null;
		try {
			eateries = eateriesMainProcess
					.getEateriesByLocationAndCategorySmall(
							location, 
							smallId, 
							pageRequest
					);
		} catch (NoCategoryFoundException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.build();
			return responseJson.toResponseEntity();
		} 
		
		//log.info("eateries: " + eateries.toString());
		//log.info("" + eateries.getNumberOfElements());
		//log.info("" + eateries.getTotalElements());
		if (PageUtil.isEmtpy(eateries)) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.data(eateries)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
	
	/**
	 * 사용자 GPS 경위도 정보를 토대로 그 주변 음식점 정보를 반환. 
	 * 만약 사용자가 GPS 경위도 정보를 제공하지 않는 경우, x, y 요청 파라미터를 비우고 요청한다. 
	 * 그러면 DB 내 음식점 주소들 중 임의로(랜덤으로 하나를 골라 그 주소에 해당하는 음식점 정보들을 반환한다. 
	 * 
	 * 테스트용 x, y 입력값 예) 사용자 GPS 위치 정보 제공한다고 가정. 
	 * 예1) 강남역 2호선 (서울 강남구 강남대로 396) - x: 127.047377408384, y: 37.4981646510326
	 * 예2) 국립서울현충원 (서울 동작구 현충로 210) - x: 126.978408414947, y: 37.4998236507216
	 * -> swagger에서 확인.
	 * 
	 * 만약 사용자 GPS 위치 정보 미제공 시 DB 내 존재하는 주소들 중 하나를 랜덤으로 사용하는 것이 아니라, 
	 * "서울 강남구"처럼 기본값을 정해서 하고자 할 때는 클라이언트 측에서
	 * 이 클래스의 getEateriesByAddress() 메서드에 매핑된 REST API URI인 /main/locations/{locations}
	 * 이용하는 것을 권고 -> REST API URI ex): /main/locations/서울 강남구
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/user")
	public ResponseEntity<ResponseJson> getEateriesByUserLocation(
			@RequestParam(name = "x", defaultValue = "") String x,
			@RequestParam(name = "y", defaultValue = "") String y,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		Pageable pageRequest = PageUtil.getPageRequestOf(page, size);
		
		// 참고) 사용자 GPS 위치가 반드시 DB - eateries 내 존재하는 음식점들 중 하나의 
		// 위치(longitude, latitude 필드)와 반드시 정확히 일치한다는 보장이 없다. 
		// 따라서 카카오 API로부터 좌표를 주소로 변환하여 
		// 이 주소를 토대로 음식점을 검색하는 것이 타당하다고 생각함. 
		
		// 사용자가 GPS 위치(x, y)를 입력하지 않은 경우, DB - eateries에 존재하는 주소를 
		// 랜덤으로 하나 가져오게끔 한다.
		LocationSplitDto locationSplitDto = null;
		if (!x.isBlank() && !y.isBlank()) {
			locationSplitDto = geoLocationProcess
					.getOneLocationFromCoordinate(x, y);
		}
		
		Page<EateriesDto> eateries = null;
		String searchLocation = null; // 주소 대분류 - 중분류만으로 구성.
		
		if (locationSplitDto == null) {
			// API로부터 조회된 주소가 없을 경우, DB - eateries에 존재하는 주소를
			// 랜덤으로 하나 가져온다. 
			EateriesDto oneEatery = null;
			try {
				oneEatery = eateriesMainProcess.getOneEateriesByRandom();
			} catch (NoDataFoundException | BadAlgorithmException e) {
				responseJson = ResponseJson.builder()
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.message(e.getMessage())
						.build();
				return responseJson.toResponseEntity();
			} 
			
			searchLocation = LocationUtil
					.getLocationMediumStringByFull(oneEatery.getAddress());
			log.info("좌표로부터 주소 구성 방법 - DB로부터 랜덤으로 조회된 주소를 이용");
			
		} else {
			// API로부터 조회한 주소 데이터로 대분류 - 중분류까지의 주소 문자열 구성.
			// 예) "서울 강남구"
			searchLocation = locationSplitDto.getFullLocationByMedium();
			log.info("좌표로부터 주소 구성 방법 - 사용자 GPS 위치 이용");
		}
		
		log.info("구성된 검색용 주소 문자열 : " + searchLocation);
		
		eateries = eateriesMainProcess.getEateriesByLocation(
				searchLocation, 
				pageRequest
		);
		
		if (PageUtil.isEmtpy(eateries)) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
}
