package com.acorn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.LocationRoads;

public interface LocationRoadsRepository extends JpaRepository<LocationRoads, Integer> {
	
	// 도로명주소 찾기
	Optional<LocationRoads> findByName(String name);
}
