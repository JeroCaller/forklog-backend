package com.acorn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.LocationGroupsDto;
import com.acorn.dto.LocationsDto;
import com.acorn.exception.NoDataFoundException;
import com.acorn.process.LocationProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

@RestController
@RequestMapping("/main/locations")
public class LocationController {
	
	@Autowired
	private LocationProcess locationProcess;
	
	/**
	 * 서울, 경기 등 전국 주소 대분류를 전체 반환 (행정구역 17개)
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/city/large")
	public ResponseEntity<ResponseJson> getLocationLargeCityAll() {
		ResponseJson responseJson = null;
		
		List<LocationGroupsDto> result = locationProcess.getLocationGroupsAll();
		
		if (result == null || result.size() == 0) {
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
		
		return ResponseEntity
				.status(responseJson.getStatus())
				.body(responseJson);
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
		
		List<LocationsDto> locationsDto = null;
		try {
			 locationsDto = locationProcess.getLocationMediumAll(largeCity);
		} catch (NoDataFoundException e) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(e.getMessage())
					.build();
			
			return ResponseEntity
					.status(responseJson.getStatus())
					.body(responseJson);
		}
		
		if (locationsDto == null || locationsDto.size() == 0) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(locationsDto)
					.build();
		}
		
		return ResponseEntity
				.status(responseJson.getStatus())
				.body(responseJson);
	}
}
