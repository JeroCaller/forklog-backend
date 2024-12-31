package com.acorn.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.acorn.entity.Categories;
import com.acorn.entity.Eateries;

public interface EateriesRepository extends JpaRepository<Eateries, Integer> {
	
	boolean existsByNameAndLongitudeAndLatitude(String name, BigDecimal longitude, BigDecimal latitude);	
	Page<Eateries> findByAddressContaining(String address, Pageable pageRequest);
	
	Page<Eateries> findByAddressContainsAndCategory(
			String address, 
			Categories categories,
			Pageable pageRequest
	);
	
	/**
	 * 음식 카테고리 대분류 및 지역명으로 검색
	 * 
	 * @author JeroCaller (JJH)
	 * @param address
	 * @param categoryGroupNo
	 * @param pageRequset
	 * @return
	 */
	@Query(value = """
		SELECT e
		FROM Eateries e
		JOIN e.category c
		JOIN c.group cg
		WHERE e.address like %:address% AND
		cg.no = :paramNo
	""")
	Page<Eateries> findByCategoryGroup(
			@Param("address") String address, 
			@Param("paramNo") int categoryGroupNo, 
			Pageable pageRequset
	);
	
	Optional<Eateries> findByNameAndAddress(String name, String address);
	
	/**
	 * DB에 현존하는 PK 값 중 가장 큰 값을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@Query(value = "SELECT MAX(e.no) FROM Eateries e")
	Integer findIdMax();
}
