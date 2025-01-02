package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.LocationGroups;

public interface LocationGroupsRepository extends JpaRepository<LocationGroups, Integer> {
	
}
