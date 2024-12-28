package com.acorn.controller;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.process.EateriesProcess;
import com.acorn.process.ReviewsProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/{id}")
    public ResponseEntity<EateriesDto> getEateryById(@PathVariable("id") int id) {
        Optional<EateriesDto> eateryDto = eateriesProcess.getEateryDtoById(id);
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
}
