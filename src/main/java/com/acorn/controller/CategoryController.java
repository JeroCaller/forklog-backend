package com.acorn.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.CategoriesDto;
import com.acorn.process.CategoryProcess;
import com.acorn.response.ResponseJson;
import com.acorn.response.ResponseStatusMessages;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/category")
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryProcess categoryProcess;
	
	/**
	 * 모든 음식점 카테고리 (대분류, 중분류 포함) 정보 응답.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@GetMapping("/")
	public ResponseEntity<ResponseJson> getCategoryAll() {
		ResponseJson responseJson = null;
		
		List<CategoriesDto> categoriesDto = categoryProcess.getAllCategories();
		if (categoriesDto.size() == 0) {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(ResponseStatusMessages.NO_DATA_FOUND)
					.build();
		} else {
			responseJson = ResponseJson.builder()
					.status(HttpStatus.OK)
					.message(ResponseStatusMessages.READ_SUCCESS)
					.data(categoriesDto)
					.build();
		}
		
		return responseJson.toResponseEntity();
	}
}
