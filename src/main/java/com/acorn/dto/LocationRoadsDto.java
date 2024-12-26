package com.acorn.dto;

import com.acorn.entity.LocationRoads;
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
public class LocationRoadsDto {
	private Integer no;
	private String name;
	private LocationsDto locationsDto;
	
	public static LocationRoadsDto toDto(LocationRoads entity) {
		return LocationRoadsDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.locationsDto(LocationsDto.toDto(entity.getLocations()))
				.build();
	}
}
