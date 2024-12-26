package com.acorn.dto;

import com.acorn.entity.Locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationsDto {
	private Integer no;
	private String name;
	private LocationGroupsDto locationGroupsDto;
	
	public static LocationsDto toDto(Locations entity) {
		return LocationsDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.locationGroupsDto(LocationGroupsDto.toDto(entity.getLocationGroups()))
				.build();
	}
}
