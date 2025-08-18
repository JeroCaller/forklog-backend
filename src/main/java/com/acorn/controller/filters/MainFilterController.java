package com.acorn.controller.filters;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.MainFilterDto;
import com.acorn.process.filters.MainFilterProcess;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/filter")
@RequiredArgsConstructor
@Slf4j
public class MainFilterController {

	private final MainFilterProcess mainFilterProcess;
	
	/**
	 * 페이지 첫 진입 시 필터 조건을 반환
	 *
	 * @author EaseHee
	 * @return 카테고리 대분류/소분류 List에 저장 후 반환
	 */
	@GetMapping
	public ResponseEntity<List<MainFilterDto.CategoryGroups>> getFilter() {
//		List<MainFilterDto.CategoryGroups> list = mainFilterProcess.getCategoryGroups();
//		log.info("Category Groups : {}" , list);
//		return ResponseEntity.ok().body(list);
		return ResponseEntity.ok().body(mainFilterProcess.getCategoryGroups());
	}
}
