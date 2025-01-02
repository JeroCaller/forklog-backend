package com.acorn.dto;

import com.acorn.entity.Categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 음식점 카테고리 필터 정보를 위한 카테고리 소분류 DTO. 카테고리 대분류 DTO안에 
 * 카테고리 소분류 DTO가 포함될 수 있게끔 구성함.
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesFilterDto {
	
	private Integer no;
	private String name;
	
	public static CategoriesFilterDto toDto(Categories entity) {
		return CategoriesFilterDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.build();
	}
	
}
