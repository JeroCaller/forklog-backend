package com.acorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.EateriesDto;
import com.acorn.exception.category.NoCategoryFoundException;
import com.acorn.process.EateriesMainProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DB로부터 음식점(eateries) 정보 가져오는 컨트롤러 클래스.
 */
@RestController
@Slf4j
@RequestMapping("/main/location")
@RequiredArgsConstructor
public class EateriesMainController {
	
	private final EateriesMainProcess eateriesMainProcess;
	
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
		
		Pageable pageRequest = PageRequest.of(page, size);
		Page<EateriesDto> eateriesDto = eateriesMainProcess
				.getEateriesByLocation(location, pageRequest);
		if (eateriesDto.getNumberOfElements() == 0) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(eateriesDto)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
	
	/**
	 * 지역 및 음식 카테고리 대분류 두 검색 조건이 적용되어 검색된 음식점 정보들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param largeId
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/{location}/category/large/{lid}")
	public ResponseEntity<ResponseJson> getEateriesByLocationAndCategoryLarge(
			@PathVariable(name = "location") String location,
			@PathVariable(name = "lid") int largeId,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		
		// TODO 로직 구현
		Pageable pageRequest = PageRequest.of(page, size);
		
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
		
		if (eateries.getNumberOfElements() == 0) {
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
	 *지역 및 음식 카테고리 소분류 두 검색 조건이 적용되어 검색된 음식점 정보들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param smallId
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/{location}/category/small/{sid}")
	public ResponseEntity<ResponseJson> getEateriesByLocationAndCategorySmall(
			@PathVariable(name = "location") String location,
			@PathVariable(name = "sid") int smallId,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		ResponseJson responseJson = null;
		
		Pageable pageRequest = PageRequest.of(page, size);
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
		
		if (eateries.getNumberOfElements() == 0) {
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
