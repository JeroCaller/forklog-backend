package com.acorn.process;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationRoads;
import com.acorn.entity.Locations;
import com.acorn.repository.EateriesRepository;

import lombok.RequiredArgsConstructor;

/**
 * 메인 페이지에서 음식점을 다루기 위한 서비스 클래스
 * 
 */
@Service
@RequiredArgsConstructor
public class EateriesMainProcess {
	
	private final EateriesRepository eateriesRepository;
	
	/**
	 * 주어진 지역 중분류에 해당하는 음식점 정보 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationsEntity
	 * @param pageRequest
	 * @return
	 */
	public Page<EateriesDto> getEateriesByLocation(
			Locations locationsEntity, 
			Pageable pageRequest
	) {
		Page<Eateries> eateries = eateriesRepository
				.findByLocation(locationsEntity, pageRequest);
		
		return eateries.map(EateriesDto :: toDto);
	}
	
	/**
	 * 주어진 도로명 주소에 해당하는 음식점 데이터들을 DB로부터 조회하여 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationRoads
	 * @return
	 */
	public Page<EateriesDto> getDataByLocationRoad(
			LocationRoads locationRoads, 
			Pageable pageRequest
	) {
		Page<Eateries> eateries = eateriesRepository.findByRoadNo(locationRoads.getNo(), pageRequest);

		if (eateries == null) return null;
		return eateries.map(entity -> EateriesDto.toDto(entity));
	}
}
