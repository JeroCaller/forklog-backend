package com.acorn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.api.KakaoRestApi;
import com.acorn.dto.EateriesDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;
import com.acorn.entity.LocationRoads;
import com.acorn.exception.NoDataFoundForRandomLocation;
import com.acorn.process.EateriesProcess;
import com.acorn.process.LocationProcess;
import com.acorn.process.openfeign.kakao.KeywordSearchProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;
import com.acorn.utils.LocationConverter;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EateriesController {
	
	@Autowired
	private KakaoRestApi kakaoRestApi;
	
	@Autowired
	private LocationProcess locationProcess;
	
	@Autowired
	private EateriesProcess eateriesProcess;
	
	@Autowired
	private LocationConverter locationConverter;
	
	@Autowired
	private KeywordSearchProcess keywordSearchProcess;
	
	/**
	 * 지역을 랜덤으로 골라 그 지역의 음식점 정보들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param page 음식점 정보들 중 요청할 페이지 번호 (kakao API의 page 요청 파라미터 관련)
	 * @return
	 */
	@GetMapping("/eatery/locations/random")
	public ResponseEntity<Object> getEateriesByRandomLocations(
			@RequestParam(name = "page", defaultValue = "1") Integer page
	) {
		ResponseJson responseJson = null;
		
		// 랜덤으로 주소 한 곳을 얻어온다. 
		LocationRoads randomLocation = null;
		try {
			randomLocation = locationProcess.getRandomLocation();
			//throw new NoDataFoundForRandomLocation();  // For Test
		} catch (NoDataFoundForRandomLocation exception) {
			// 해당 예외 발생 시 이에 대한 JSON 응답 데이터 구성 및 반환
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND) // 응답 데이터에 포함시키는 용도
					.message(exception.getMessage())
					.build();
			
			return ResponseEntity
					.status(responseJson.getStatus())
					.body(responseJson);
		}
		
		// 위 예외 이외에 알수 없는 이유로 null값이 나온 경우에 대한 응답 데이터 생성.
		if (randomLocation == null) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
			
			return ResponseEntity
					.status(responseJson.getStatus())
					.body(responseJson);
		}
			
		// 주소를 검색어로 입력하여 해당 주소 주변 식당을 검색
		List<EateriesDto> eateries = eateriesProcess.getDataFromDbBy(randomLocation);
		log.info("eateries: " + eateries);
		
		if (eateries == null || eateries.size() == 0) {
			// DB로부터 조회된 음식점 정보가 없을 경우, 외부 API로부터 정보를 얻어온다.
			String fullLocation = locationConverter.getLocationWithoutRoad(randomLocation);
			//Object apiResult = kakaoRestApi.getEateries(fullLocation);
			KeywordResponseDto apiResult = keywordSearchProcess.getApiResult(
					fullLocation, 1, randomLocation);
			
			log.info("apiResult");
			log.info(apiResult.toString());
			
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(apiResult)
					.build();
			
			return ResponseEntity
					.status(responseJson.getStatus())
					.body(responseJson);
		} 
		
		log.info("eateries size: " + eateries.size());
		responseJson = ResponseJson.builder()
				.status(HttpStatus.OK)
				.message(ResponseStatusMessages.READ_SUCCESS)
				.data(eateries)
				.build();
		
		return ResponseEntity
				.status(responseJson.getStatus())
				.body(responseJson);
	}
}
