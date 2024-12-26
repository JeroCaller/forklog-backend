package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.LocationGroups;

public interface LocationGroupsRepository extends JpaRepository<LocationGroups, Integer> {
	
	/**
	 * 찾고자 하는 지역 대분류 이름을 대입하여 조회.
	 * 
	 * @author JeroCaller (JJH)
	 * @param name - 찾고자 하는 지역 대분류명.
	 * @return
	 */
	LocationGroups findByName(String name);
}
