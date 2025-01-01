package com.acorn.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.location.LocationGroupsFilterDto;
import com.acorn.process.LocationProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/locations/filter")
@RequiredArgsConstructor
public class LocationController {
	
	private final LocationProcess locationProcess;
	
	/**
	 * 지역 필터를 위한 지역 대분류 - 소분류 데이터 반환.
	 * 
	 * 지역 대분류 정보 내부에 소분류 데이터 존재.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("")
	public ResponseEntity<ResponseJson> getLocationGroupsFilterAll() {
		ResponseJson responseJson = null;
		
		List<LocationGroupsFilterDto> result = locationProcess
				.getLocationGroupsFilterAll();
		
		if (result.size() == 0) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(result)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
}
