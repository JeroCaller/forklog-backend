package com.acorn.controller;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.process.EateriesProcess;
import com.acorn.process.ReviewsProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/main/eateries")
@RequiredArgsConstructor
public class EateriesController {
    private final EateriesProcess eateriesProcess;
	private final ReviewsProcess reviewsProcess;
	
	// 음식점 상세 보기
    @GetMapping("/{no}")
    public ResponseEntity<EateriesDto> getEateryById(@PathVariable("no") int no) {
        Optional<EateriesDto> eateryDto = eateriesProcess.getEateryDtoById(no);
        return eateryDto
            .map(ResponseEntity::ok)         // EateriesDto를 그대로 반환
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
	//음식점별 리뷰 목록 조회
	@GetMapping("/{no}/reviews")
	public ResponseEntity<Object> getReviewsByEateryNo(
				@RequestParam(value = "page", required = false, defaultValue = "1" ) int page,
				@PathVariable("no") String eateryNo){
		Page<ReviewResponseDto> list= reviewsProcess.getReviewsByEateryNo(eateryNo, page-1);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("page", page);
		return ResponseEntity.ok(map);
	}
	
	/**
	 * 사용자가 특정 음식점 클릭 시 해당 음식점의 조회수 1 증가. (update 기능)
	 * 
	 * 특정 음식점에 대한 기능이므로 EateriesMainController가 아닌 
	 * EateriesController에 작성함. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param no - 음식점 엔티티의 no
	 * @return
	 */
	@PutMapping("/{no}/view/counts")
	public ResponseEntity<ResponseJson> addViewCount(
			@PathVariable("no") int no
	) {
		ResponseJson responseJson = null;
		
		boolean isUpdated = eateriesProcess.updateViewCount(no);
		if (!isUpdated) {
			String addtionalMessage = " 입력한 음식점 번호가 조회되지 않았습니다.";
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.UPDATE_FAILED + addtionalMessage)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.UPDATE_SUCCESS)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
	
}
