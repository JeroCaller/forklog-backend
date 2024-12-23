package com.acorn.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.ReviewResponseDto;
import com.acorn.model.ReviewsModel;

@RestController
@RequestMapping("/review")
public class ReviewController {
	private ReviewsModel reviewsModel;
	public ReviewController(ReviewsModel reviewsModel) {
		this.reviewsModel=reviewsModel;
	}
	@GetMapping
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByEateryNo(@RequestParam("eateryNo") String eateryNo){
		return ResponseEntity.ok(reviewsModel.getReviewsByEateryNo(eateryNo));
	}
}
