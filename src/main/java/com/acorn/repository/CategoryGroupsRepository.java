package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.acorn.entity.CategoryGroups;

public interface CategoryGroupsRepository extends JpaRepository<CategoryGroups, Integer> {
	
	@Query
	CategoryGroups findIdByName(String name);
}
