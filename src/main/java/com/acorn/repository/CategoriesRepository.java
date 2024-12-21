package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.acorn.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	
	Integer findIdByName(String name);
	
	Categories findByName(String name);
	
	boolean existsByName(String name);
}
