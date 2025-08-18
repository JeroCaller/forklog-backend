package com.acorn.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer>{

	/**
	 * eatery_no으로 Reviews리스트 조회
	 *
	 * @author rmk
	 * @param pageable
	 * @param eateryNo
	 * @return
	 */
	@Query("SELECT r FROM Reviews r WHERE r.eateries.no=:eateryNo ORDER BY r.createdAt DESC")
	Page<Reviews> getReviewsByEateryNo(Pageable pageable,@Param("eateryNo") String eateryNo);

	/**
	 * member_no으로 Reviews리스트 조회
	 *
	 * @author rmk
	 * @param pageable
	 * @param memberNo
	 * @return
	 */
	@Query("SELECT r FROM Reviews r WHERE r.membersMain.no=:memberNo ORDER BY r.createdAt DESC")
	Page<Reviews> getReviewsByMemberNo(Pageable pageable,@Param("memberNo") String memberNo);

	/**
	 * 각 음식점에 대한 평균 별점 조회
	 *
	 * @author jaeuk-choi
	 * @param eateryNo
	 * @return
	 */
	@Query("SELECT AVG(r.rating) FROM Reviews r WHERE r.eateries.no = :eateryNo")
    BigDecimal calculateAverageRatingByEateryNo(@Param("eateryNo") int eateryNo);
}
