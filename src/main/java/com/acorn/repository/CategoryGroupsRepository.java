package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.CategoryGroups;

public interface CategoryGroupsRepository extends JpaRepository<CategoryGroups, Integer> {

	/**
	 *
	 * @author EaseHee
	 * @param name
	 * @return
	 */
	CategoryGroups findIdByName(String name);

	/**
	 *
	 * @author EaseHee
	 * @param name
	 * @return
	 */
	boolean existsByName(String name);

	/**
	 *
	 * @author JeroCaller
	 * @param name
	 * @return
	 */
	CategoryGroups findByName(String name);
}
