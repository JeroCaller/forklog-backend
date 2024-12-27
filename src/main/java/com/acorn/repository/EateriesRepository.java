package com.acorn.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Eateries;
import com.acorn.entity.Locations;

public interface EateriesRepository extends JpaRepository<Eateries, Integer> {
	
	boolean existsByNameAndLongitudeAndLatitude(String name, BigDecimal longitude, BigDecimal latitude);
	
	/**
	 * eateries 테이블의 road_no 외래키 필드에 해당하는 특정 도로명 주소에 
	 * 포함된 모든 음식점들을 DB로부터 가져온다. 
	 * 
	 * @param roadNo
	 * @return
	 */
	@Query(value = """
		SELECT e
		FROM Eateries e
		JOIN e.road locR
		ON locR.no = :road_no
	""")
	Page<Eateries> findByRoadNo(
			@Param("road_no") Integer roadNo,
			Pageable pageRequest
	);
	
	/**
	 * 지역 중분류에 해당하는 Locations 엔티티 입력 시 
	 * 이 지역에 해당하는 음식점들을 조회.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@Query(value = """
		SELECT e
		FROM Eateries e
		JOIN e.road locR
		JOIN locR.locations loc
		JOIN loc.locationGroups locG
		WHERE loc.name = :#{#locParam.name} AND
		locG.name = :#{#locParam.locationGroups.name}
	""")
	Page<Eateries> findByLocation(
			@Param("locParam") Locations locations, 
			Pageable pageRequest
	);
}
