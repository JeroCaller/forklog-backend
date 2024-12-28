package com.acorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.EateriesDto;
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
@RequestMapping("/main")
@RequiredArgsConstructor
public class EateriesMainController {
	
	private final EateriesMainProcess eateriesMainProcess;
	
	/**
	 * 클라이언트에서 주소 입력 시 그에 해당하는 음식점 정보들을 반환. 
	 * 
	 * @param location
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/location")
	public ResponseEntity<ResponseJson> getEateriesByAddress(
			@RequestParam("location") String location,
			@RequestParam("page") int page,
			@RequestParam("size") int size
	) {
		ResponseJson responseJson = null;
		
		Pageable pageRequest = PageRequest.of(page, size);
		Page<EateriesDto> eateriesDto = eateriesMainProcess
				.getEateriesByAddressLike(location, pageRequest);
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
}
