package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acorn.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	
}
