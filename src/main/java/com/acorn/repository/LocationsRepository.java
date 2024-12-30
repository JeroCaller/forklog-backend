package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Locations;

public interface LocationsRepository extends JpaRepository<Locations, Integer> {
	
}
