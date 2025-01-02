package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Locations;

public interface LocationsRepository extends JpaRepository<Locations, Integer> {
	
	List<Locations> findByName(String name);
	
}
