package com.acorn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;
import com.acorn.entity.LocationRoads;
import com.acorn.exception.NoDataFoundException;
import com.acorn.process.EateriesWithApiProcess;
import com.acorn.process.LocationProcess;
import com.acorn.process.openfeign.kakao.KeywordSearchProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;
import com.acorn.utils.ListUtil;
import com.acorn.utils.LocationConverter;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EateriesController {
	
	@Autowired
	private LocationProcess locationProcess;
	
	@Autowired
	private EateriesWithApiProcess eateriesWithApiProcess;
	
	@Autowired
	private LocationConverter locationConverter;
	
	@Autowired
	private KeywordSearchProcess keywordSearchProcess;
	
	// 카카오 API - "키워드로 장소 검색하기"에 따르면 최대 페이지의 사이즈는 15개까지만 요청 가능. 
	private final int MAX_PAGE_SIZE = 15;
	
	/**
	 * 지역을 랜덤으로 골라 그 지역의 음식점 정보들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param page 음식점 정보들 중 요청할 페이지 번호 (kakao API의 page 요청 파라미터 관련)
	 * @return
	 */
	@GetMapping("/eatery/locations/random")
	public ResponseEntity<ResponseJson> getEateriesByRandomLocations(
			@RequestParam(name = "page", defaultValue = "1") int page
	) {
		ResponseJson responseJson = null;
		
		// 랜덤으로 주소 한 곳을 얻어온다. 
		LocationRoads randomLocation = null;
		try {
			randomLocation = locationProcess.getRandomLocation();
			//throw new NoDataFoundException();  // For Test
		} catch (NoDataFoundException exception) {
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
		
		Pageable pageRequest = PageRequest.of(page, MAX_PAGE_SIZE);
		
		// 주소를 검색어로 입력하여 해당 주소 주변 식당을 검색
		Page<EateriesDto> eateries = eateriesWithApiProcess.getDataFromDbByLocation(
				randomLocation,
				pageRequest
		);
		log.info("eateries: " + eateries);
		
		if (eateries == null || eateries.getNumberOfElements() == 0) {
			// DB로부터 조회된 음식점 정보가 없을 경우, 외부 API로부터 정보를 얻어온다.
			String fullLocation = locationConverter.getLocationWithoutRoad(randomLocation);
			//Object apiResult = kakaoRestApi.getEateries(fullLocation);
			KeywordResponseDto apiResult = keywordSearchProcess.getApiResult(
					fullLocation, 1, randomLocation
			);
			
			log.info("apiResult");
			log.info(apiResult.toString());
			
			eateries = eateriesWithApiProcess.saveApi(apiResult.getDocuments());
		} 
		
		List<String> failedLocations = eateriesWithApiProcess.getFailedLocations(); 
		if (failedLocations != null && failedLocations.size() > 0) {
			if (eateries == null || eateries.getNumberOfElements() == 0) {
				// 위의 if문을 거쳤음에도 조회된 eateries가 없을 경우
				responseJson = ResponseJson.builder()
						.status(HttpStatus.NOT_FOUND)
						.message(ResponseStatusMessages.NO_DATA_FOUND)
						.build();
			} else {
				String allLocations = ListUtil.getStringList(failedLocations);
				String responseMessage = "API로부터 가져온 주소 중 DB 내 조회되지 않은 주소 존재. 주소 명단) ";
				responseMessage += allLocations;
				
				responseJson = ResponseJson.builder()
						.status(HttpStatus.PARTIAL_CONTENT) // 206
						.message(responseMessage)
						.data(eateries)
						.build();
			}
		} else {
			log.info("eateries size: " + eateries.getNumberOfElements());
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateries)
					.build();
		}
		
		return ResponseEntity
				.status(responseJson.getStatus())
				.body(responseJson);
	}

}
