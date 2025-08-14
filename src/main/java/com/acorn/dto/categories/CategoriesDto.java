package com.acorn.dto.categories;

import com.acorn.entity.Categories;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriesDto {

	private Integer no;
	private String name;
	private CategoryGroupsDto categoryGroupsDto;
	
	public static CategoriesDto toDto(Categories entity) {
		return CategoriesDto.builder()
			.no(entity.getNo())
			.name(entity.getName())
			.categoryGroupsDto(CategoryGroupsDto.toDto(entity.getGroup()))
			.build();
	}
}
