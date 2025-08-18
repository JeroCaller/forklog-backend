package com.acorn.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.acorn.entity.Categories;
import com.acorn.entity.Eateries;

public interface EateriesRepository extends JpaRepository<Eateries, Integer> {

	/**
	 *
	 * @author EaseHee
	 * @param name
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	boolean existsByNameAndLongitudeAndLatitude(
		String name,
		BigDecimal longitude,
		BigDecimal latitude
	);

	/**
	 *
	 * @author JeroCaller
	 * @param address
	 * @param pageRequest
	 * @return
	 */
	Page<Eateries> findByAddressContaining(String address, Pageable pageRequest);
	
	/**
	 * 주어진 주소를 포함하면서 그와 동시에 주어진 음식점 카테고리 소분류를 만족시키는 
	 * 음식점 데이터를 페이징하여 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param address - 검색하고자 하는 지역 주소
	 * @param categories - 검색하고자 하는 음식점 카테고리 소분류
	 * @param pageRequest
	 * @return
	 */
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
	 * @param pageRequest
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
		Pageable pageRequest
	);

	/**
	 *
	 * @author JeroCaller
	 * @param name
	 * @param address
	 * @return
	 */
	Optional<Eateries> findByNameAndAddress(String name, String address);
	
	/**
	 * DB에 현존하는 PK 값 중 가장 큰 값을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	@Query(value = "SELECT MAX(e.no) FROM Eateries e")
	Integer findIdMax();
	
	/**
	 * 주어진 음식점 카테고리 소분류들 중 하나라도 해당하는 음식점들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param categories
	 * @param pageRequest
	 * @return
	 */
	Page<Eateries> findByCategoryIn(List<Categories> categories, Pageable pageRequest);

	/**
	 * 리뷰가 crud 될때마다 rating 업데이트
	 *
	 * @author jaeuk-choi
	 * @param eateryNo
	 * @param rating
	 */
	@Modifying
    @Query("UPDATE Eateries e SET e.rating = :rating WHERE e.no = :eateryNo")
    void updateRating(@Param("eateryNo") int eateryNo, @Param("rating") BigDecimal rating);
}
