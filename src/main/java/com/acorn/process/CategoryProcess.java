package com.acorn.process;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.CategoryGroupsFilterDto;
import com.acorn.entity.CategoryGroups;
import com.acorn.repository.CategoryGroupsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryProcess {

	private final CategoryGroupsRepository categoryGroupsRepository;
	
	/**
	 * 모든 카테고리 대분류를 반환. 카테고리 대분류 내부에 소분류 포함되어 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	public List<CategoryGroupsFilterDto> getAllCategoryGroups() {
		List<CategoryGroups> result = categoryGroupsRepository
				.findAll();
		
		return result.stream()
				.map(CategoryGroupsFilterDto :: toDto)
				.collect(Collectors.toList());
	}
}
