package com.acorn.dto;

import com.acorn.entity.ReviewImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImagesDto {
	private Integer no;
    private String imageUrl;
    private ReviewsDto reviewsDto;
	// toDto
	public static ReviewImagesDto toDto(ReviewImages reviewImages) {
		return ReviewImagesDto.builder()
				.no(reviewImages.getNo())
				.imageUrl(reviewImages.getImageUrl())
				.reviewsDto(ReviewsDto.toDto(reviewImages.getReviews()))
				.build();
	}
}
