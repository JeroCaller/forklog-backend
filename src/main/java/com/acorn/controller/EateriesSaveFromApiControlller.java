package com.acorn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.process.EateriesWithApiProcess;
import com.acorn.response.ResponseJson;

import lombok.RequiredArgsConstructor;

/**
 * 지역 주소를 문자열로 입력 시 해당 지역 내 음식점 정보들을 API로 대량으로 불러와 DB에 저장하기 위한 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/development")  // 오로지 개발 용도로만 사용. 실제 클라이언트와의 소통 X
public class EateriesSaveFromApiControlller {
	private final EateriesWithApiProcess eateriesWithApiProcess;
	
	@GetMapping("/")
	public ResponseEntity<ResponseJson> saveEateriesFromApi(
			@RequestParam("query") String query,
			@RequestParam(name = "startPage", defaultValue = "1") int startPage,
			@RequestParam(name = "dataNum") int requestApiDataNum
	) {
		
		ResponseJson responseJson = eateriesWithApiProcess.saveEateriesAll(
				query, 
				startPage, 
				requestApiDataNum
		);
		
		return responseJson.toResponseEntity();
				
	}
}
