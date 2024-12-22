package com.acorn.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Eateries;

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
		JOIN e.locationRoads locR
		ON locR.no = :road_no
	""")
	List<Eateries> findByRoadNo(@Param("road_no") Integer roadNo);
}
