package com.acorn.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.LocationGroupsDto;
import com.acorn.dto.LocationsDto;
import com.acorn.exception.location.BaseLocationException;
import com.acorn.exception.location.NoLocationGroupFoundException;
import com.acorn.process.LocationProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/locations")
@RequiredArgsConstructor
public class LocationController {
	
	private final LocationProcess locationProcess;
	
	/**
	 * 서울, 경기 등 전국 주소 대분류를 전체 반환 (행정구역 17개)
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/city/large")
	public ResponseEntity<ResponseJson> getLocationLargeCityAll() {
		ResponseJson responseJson = null;
		
		List<LocationGroupsDto> result = null;
		try {
			result = locationProcess.getLocationGroupsAll();
		} catch (NoLocationGroupFoundException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
			return responseJson.toResponseEntity();
		}
		
		responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(result)
					.build();

		return responseJson.toResponseEntity();
	}
	
	/**
	 * 서울, 경기 등 대분류 주소 입력 시 그에 해당하는 모든 중분류 주소를 반환.
	 * 
	 * 예) 서울 -> 강남구, 강서구 등등...
	 * 
	 * @author JeroCaller (JJH)
	 * @param largeCity
	 * @return
	 */
	@GetMapping("/city/large/{name}/medium")
	public ResponseEntity<ResponseJson> getLocationMediumCityAllByLargeCity(
			@PathVariable(name = "name") String largeCity
	) {
		ResponseJson responseJson = null;
		
		boolean isException = true;
		List<LocationsDto> locationsDto = null;
		try {
			 locationsDto = locationProcess.getLocationMediumAll(largeCity);
			 isException = false;
		} catch (BaseLocationException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.build();
		}
		
		if (isException) {
			return responseJson.toResponseEntity();
		}
		
		responseJson = ResponseJson.builder()
				.status(HttpStatus.OK)
				.message(ResponseStatusMessages.READ_SUCCESS)
				.data(locationsDto)
				.build();

		return responseJson.toResponseEntity();
	}
}
