package com.acorn.process;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.location.LocationGroupsFilterDto;
import com.acorn.entity.LocationGroups;
import com.acorn.repository.LocationGroupsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationProcess {
	
	private final LocationGroupsRepository locationGroupsRepository;
	
	/**
	 * 주소 대분류 및 소분류 DTO 조회 및 반환.
	 * 
	 * 주소 대분류 DTO 내부에 소분류 DTO가 포함되어 있는 구조.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	public List<LocationGroupsFilterDto> getLocationGroupsFilterAll() {
		List<LocationGroups> locationGroupsFilterDtos 
			= locationGroupsRepository.findAll();
		
		return locationGroupsFilterDtos.stream()
				.map(LocationGroupsFilterDto :: toDto)
				.collect(Collectors.toList());
	}
}
