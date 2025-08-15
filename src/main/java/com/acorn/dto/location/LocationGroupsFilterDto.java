package com.acorn.dto.location;

import java.util.List;
import java.util.stream.Collectors;

import com.acorn.entity.LocationGroups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지역 필터 정보 반환용 DTO 클래스
 * 지역 대분류 DTO 내부에 지역 중분류 DTO가 포함되는 구조
 *
 * @author JeroCaller
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationGroupsFilterDto {
	
	private int no;
	private String name;
	private List<LocationsFilterDto> locationsFilterDtos;
	
	public static LocationGroupsFilterDto toDto(LocationGroups entity) {
		List<LocationsFilterDto> locationsFilterDtos = entity.getLocations()
			.stream()
			.map(LocationsFilterDto :: toDto)
			.collect(Collectors.toList());
		
		return LocationGroupsFilterDto.builder()
			.no(entity.getNo())
			.name(entity.getName())
			.locationsFilterDtos(locationsFilterDtos)
			.build();
	}
	
}
