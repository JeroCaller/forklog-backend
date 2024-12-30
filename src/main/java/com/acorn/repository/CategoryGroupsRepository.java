package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.CategoryGroups;

public interface CategoryGroupsRepository extends JpaRepository<CategoryGroups, Integer> {
	
	CategoryGroups findIdByName(String name);
	
	boolean existsByName(String name);
	
	CategoryGroups findByName(String name);
}
