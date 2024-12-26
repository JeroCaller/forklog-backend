package com.acorn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acorn.entity.ReviewImages;

public interface ReviewImagesRepository extends JpaRepository<ReviewImages, Integer> {
	@Query("SELECT r FROM ReviewImages r WHERE r.reviews.no=:reviewNo")
	List<ReviewImages> getReviewImagesByReviewNo(@Param("reviewNo")String reviewNo);
	
	@Query("SELECT r.no FROM ReviewImages r WHERE r.reviews.no=:reviewNo")
	List<Integer> findAllNoByReviewNo(@Param("reviewNo")String reviewNo);
	
	void deleteByImageUrl(String path);
	
}
