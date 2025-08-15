package com.acorn.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.process.EateriesWithApiProcess;
import com.acorn.response.ResponseJson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 지역 주소를 문자열로 입력 시 해당 지역 내 음식점 정보들을 API로 대량으로 불러와 DB에 저장하기 위한 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/main/data")  // 오로지 개발 용도로만 사용. 실제 클라이언트와의 소통 X
@Slf4j
public class EateriesSaveFromApiControlller {
	
	private final EateriesWithApiProcess eateriesWithApiProcess;
	
	/**
	 * 검색어 및 DB에 저장하고자 하는 레코드 수 지정 시 API 호출하여 가져온 데이터를 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param query - 지역명 입력 권고
	 * @param startPage - kakao API에서는 하나의 검색어 당 실질적으로 
	 * 1~3페이지(1페이지 당 데이터 수 15개일 경우)만 호출 가능. 그 이상은 똑같은 데이터가 반복됨.
	 * @param requestApiDataNum - API 호출하여 DB에 저장하고자 하는 데이터 수 지정. kakao API에서는 
	 * 최대 하나의 검색어 당 45개의 데이터만 가져올 수 있음. 
	 * @return
	 */
	@GetMapping("/eateries")
	public ResponseEntity<ResponseJson> saveEateriesFromApi(
		@RequestParam("query") String query,
		@RequestParam(name = "startPage", defaultValue = "1") int startPage,
		@RequestParam(name = "dataNum", defaultValue = "45") int requestApiDataNum
	) {
		ResponseJson responseJson = null;
		
		// 예상치 못한 예외 원인 추적을 위해 예외 캐치 코드 작성함.
		try {
			responseJson = eateriesWithApiProcess.saveEateriesAll(
				query,
				startPage,
				requestApiDataNum
			);
		} catch (Exception e) {
			log.error("예기치 못한 예외 발생");
			log.error(e.getClass().getName());
			log.error(e.getMessage());
			e.printStackTrace();  // 자세한 예외 원인 파악용
			
			responseJson = ResponseJson.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(e.getClass().getName() + " : " + e.getMessage())
				.build();
		}
		
		return responseJson.toResponseEntity();
	}
}
