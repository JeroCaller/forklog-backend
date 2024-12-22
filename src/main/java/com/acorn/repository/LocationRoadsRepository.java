package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.acorn.entity.LocationRoads;


public interface LocationRoadsRepository extends JpaRepository<LocationRoads, Integer> {
	
	/**
	 * 
	 * @author JeroCaller (JJH)
	 * @return 현재 테이블 내 최고 PK 값
	 */
	@Query(value = "SELECT MAX(locR.no) FROM LocationRoads locR")
	Integer findByIdMax();
}
