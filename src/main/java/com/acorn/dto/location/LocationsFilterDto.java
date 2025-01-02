package com.acorn.dto.location;

import com.acorn.entity.Locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지역 필터 정보 반환용 DTO 클래스
 * 지역 대분류 DTO 내부에 지역 중분류 DTO가 포함되는 구조
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationsFilterDto {
	
	private int no;
	private String name;
	
	public static LocationsFilterDto toDto(Locations entity) {
		return LocationsFilterDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.build();
	}
	
}
