package com.acorn.dto.categories;

import java.util.List;
import java.util.stream.Collectors;

import com.acorn.entity.CategoryGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 음식점 카테고리 필터 정보를 위한 카테고리 대분류 DTO. 카테고리 대분류 DTO안에 
 * 카테고리 소분류 DTO가 포함될 수 있게끔 구성함.
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGroupsFilterDto {
	
	private Integer no;
	private String name;
	private List<CategoriesFilterDto> categoriesFilterDtos;
	
	public static CategoryGroupsFilterDto toDto(CategoryGroups entity) {
		List<CategoriesFilterDto> categoriesFilterDtos = entity.getCategories()
			.stream()
			.map(CategoriesFilterDto :: toDto)
			.collect(Collectors.toList());
		
		return CategoryGroupsFilterDto.builder()
			.no(entity.getNo())
			.name(entity.getName())
			.categoriesFilterDtos(categoriesFilterDtos)
			.build();
	}
	
}
