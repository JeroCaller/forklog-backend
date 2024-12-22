package com.acorn.process;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationRoads;
import com.acorn.repository.EateriesRepository;

/**
 * 
 * @author JeroCaller (JJH)
 */
@Service
public class EateriesProcess {
	
	@Autowired
	private EateriesRepository eateriesRepository;
	
	/**
	 * 주어진 도로명 주소에 해당하는 음식점 데이터들을 DB로부터 조회하여 반환
	 * 
	 * @param locationRoads 
	 * @return
	 */
	public List<EateriesDto> getDataFromDbBy(LocationRoads locationRoads) {
		List<Eateries> eateries = eateriesRepository.findByRoadNo(locationRoads.getNo());
		if (eateries == null) return null;
		return eateries.stream()
				.map(EateriesDto :: toDto)
				.collect(Collectors.toList());
	}
}
