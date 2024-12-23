package com.acorn.model;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.acorn.dto.ReviewEateriesDto;
import com.acorn.dto.ReviewImagesDto;
import com.acorn.dto.ReviewMembersDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.entity.Reviews;
import com.acorn.repository.ReviewImagesRepository;
import com.acorn.repository.ReviewsRepository;

@Repository
public class ReviewsModel {
	private ReviewsRepository reviewsRepository;
	private ReviewImagesRepository reviewImagesRepository;
	public ReviewsModel(ReviewsRepository reviewsRepository,ReviewImagesRepository reviewImagesRepository) {
		this.reviewsRepository = reviewsRepository;
		this.reviewImagesRepository=reviewImagesRepository;
	}
	public List<ReviewResponseDto> getReviewsByEateryNo(String eateryNo){
		return reviewsRepository.getReviewsByEateryNo(eateryNo).stream()
				.map(ReviewResponseDto::toDto)
				.toList();
	}
	public List<ReviewImagesDto> getReviewImagesByReviewNo(String reviewNo){
		return reviewImagesRepository.getReviewImagesByReviewNo(reviewNo).stream()
				.map(ReviewImagesDto::toDto)
				.toList();
	}
	public ReviewResponseDto getReviewResponseDto(Reviews reviews) {		
		return ReviewResponseDto.builder()
				.no(reviews.getNo())
				.rating(reviews.getRating())
				.content(reviews.getContent())
				.hasPhoto(reviews.getHasPhoto())
				.createdAt(reviews.getCreatedAt())
				.updatedAt(reviews.getUpdatedAt())
				.reviewMembersDto(ReviewMembersDto.toDto(reviews.getMembersMain()))
				.reviewEateriesDto(ReviewEateriesDto.toDto(reviews.getEateries()))
				.reviewImagesDtoList(reviewImagesRepository.getReviewImagesByReviewNo(reviews.getNo().toString()).stream().map(ReviewImagesDto::toDto).toList())
				.build();
	}
}
