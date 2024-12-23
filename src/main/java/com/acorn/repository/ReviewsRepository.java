package com.acorn.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer>{
	@Query("SELECT r FROM Reviews r WHERE r.eateries.no=:eateryNo")
	List<Reviews> getReviewsByEateryNo(@Param("eateryNo") String eateryNo);
}
