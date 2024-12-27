package com.acorn.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.entity.Locations;
import com.acorn.repository.EateriesRepository;

/**
 * 메인 페이지에서 음식점을 다루기 위한 서비스 클래스
 * 
 */
@Service
public class EateriesMainProcess {
	
	@Autowired
	private EateriesRepository eateriesRepository;
	
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
}
