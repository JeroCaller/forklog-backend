package com.acorn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Categories;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
	
	Integer findIdByName(String name);
	
	Categories findByName(String name);
	
	boolean existsByName(String name);
	
	/**
	 * 음식 카테고리 대분류, 중분류와 일치하는 유일한 Categories 엔티티 조회.
	 * 
	 * @author JeroCaller (JJH)
	 * @param largeCate
	 * @param smallCate
	 * @return
	 */
	@Query(value = """
			SELECT c 
			FROM Categories c
			JOIN c.group cg
			WHERE c.name = :smallCate AND
			cg.name = :largeCate
	""")
	Categories findByNames(
			@Param("largeCate") String largeCate,
			@Param("smallCate") String smallCate
	);
}
