package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Categories;
import com.acorn.entity.Members;

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
	
	/**
	 * 사용자가 즐겨찾기한 모든 음식점들의 카테고리 소분류 엔티티 조회
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@Query(value = """
		SELECT DISTINCT e.category
		FROM Favorites f
		JOIN f.eatery e
		ON f.member = :#{#member}
	""")
	List<Categories> findByMemberFavorite(@Param("member") Members member);
}
