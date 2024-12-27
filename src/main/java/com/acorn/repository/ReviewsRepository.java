package com.acorn.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer>{
	//eatery_no으로 Reviews리스트 조회
	@Query("SELECT r FROM Reviews r WHERE r.eateries.no=:eateryNo")
	List<Reviews> getReviewsByEateryNo(@Param("eateryNo") String eateryNo);
	
	//member_no으로 Reviews리스트 조회
	@Query("SELECT r FROM Reviews r WHERE r.membersMain.no=:memberNo")
	List<Reviews> getReviewsByMemberNo(@Param("memberNo") String memberNo);
}
