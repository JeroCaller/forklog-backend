package com.acorn.dto.eateries.reviews;

import com.acorn.entity.ReviewImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImagesResponseDto {
	private Integer no;
    private String imageUrl;
    private Integer reviewNo;
	// toDto
	public static ReviewImagesResponseDto toDto(ReviewImages reviewImages) {
		return ReviewImagesResponseDto.builder()
				.no(reviewImages.getNo())
				.imageUrl(reviewImages.getImageUrl())
				.reviewNo(reviewImages.getReviews().getNo())
				.build();
	}
}
