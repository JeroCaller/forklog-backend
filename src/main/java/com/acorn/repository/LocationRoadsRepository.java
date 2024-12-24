package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.dto.LocationSplitDto;
import com.acorn.entity.LocationRoads;


public interface LocationRoadsRepository extends JpaRepository<LocationRoads, Integer> {
	
	/**
	 * 
	 * @author JeroCaller (JJH)
	 * @return 현재 테이블 내 최고 PK 값
	 */
	@Query(value = "SELECT MAX(locR.no) FROM LocationRoads locR")
	Integer findByIdMax();
	
	/**
	 * 대분류, 중분류, 도로명으로 분리된 주소 정보에 해당하는 LocationRoads 엔티티 조회
	 * 
	 * @author JeroCaller (JJH)
	 * @param dto
	 * @return
	 */
	@Query(value = """
		SELECT locR
		FROM LocationRoads locR
		JOIN locR.locations loc
		JOIN loc.locationGroups locG
		WHERE locR.name = :#{#dto.roadName} AND
		loc.name = :#{#dto.mediumCity} AND
		locG.name = :#{#dto.largeCity}
	""")
	LocationRoads findByFullLocation(
		 @Param("dto") LocationSplitDto dto
	);
}
