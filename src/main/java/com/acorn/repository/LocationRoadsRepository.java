package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.dto.LocationSplitDto;
import com.acorn.entity.LocationRoads;

public interface LocationRoadsRepository extends JpaRepository<LocationRoads, Integer> {
	
	/**
	 * DB에 데이터가 없는 경우 null을 반환한다고 함. 그래서 int 대신 Integer로 작성함.
	 * 
	 * @author JeroCaller (JJH)
	 * @return 현재 테이블 내 최고 PK 값
	 */
	@Query(value = "SELECT MAX(locR.no) FROM LocationRoads locR")
	Integer findByIdMax();
	
	/**
	 * 대분류, 중분류, 도로명으로 분리된 주소 정보에 해당하는 LocationRoads 엔티티 조회
	 * 
	 * 정확성을 위해 대분류, 중분류, 도로명 이름이 모두 일치하는지 조건을 JPQL에 첨부함.
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
