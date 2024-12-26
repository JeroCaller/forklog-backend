package com.acorn.dto;

import com.acorn.entity.CategoryGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGroupsDto {
	private Integer no;
	private String name;
	
	public static CategoryGroupsDto toDto(CategoryGroups entity) {
		return CategoryGroupsDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.build();
	}
}
