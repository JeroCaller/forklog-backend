package com.acorn.process;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.CategoriesDto;
import com.acorn.entity.Categories;
import com.acorn.repository.CategoriesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryProcess {
	
	private final CategoriesRepository categoriesRepository;
	
	/**
	 * 모든 카테고리 분류를 가져온다. 카테고리 소분류 내부에 대분류 포함되어 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	public List<CategoriesDto> getAllCategories() {
		List<Categories> result = categoriesRepository.findAll();
		return result.stream()
				.map(CategoriesDto :: toDto)
				.collect(Collectors.toList());
	}
}
