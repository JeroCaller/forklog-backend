package com.acorn.dto;

import com.acorn.entity.LocationGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LocationGroups Entity에 대한 DTO
 * 
 * 순환참조 에러 방지를 위해, Entity 상에서 OneToMany
 * 어노테이션이 걸린 필드는 DTO에 포함시키지 않았음.
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationGroupsDto {
	private Integer no;
	private String name;
	
	public static LocationGroupsDto toDto(LocationGroups entity) {
		return LocationGroupsDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.build();
	}
}
