package com.acorn.dto;

import com.acorn.entity.LocationRoads;
import com.acorn.entity.Locations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LocationRoads Entity에 대한 DTO
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
