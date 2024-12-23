package com.acorn.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.acorn.entity.Reviews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
	private Integer no;
	private BigDecimal rating;
	private String content;
	private Boolean hasPhoto;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private ReviewMembersDto reviewMembersDto;
	private ReviewEateriesDto reviewEateriesDto;
	private List<ReviewImagesDto> reviewImagesDtoList;
	// toDto
	public static ReviewResponseDto toDto(Reviews reviews) {		
		return ReviewResponseDto.builder()
				.no(reviews.getNo())
				.rating(reviews.getRating())
				.content(reviews.getContent())
				.hasPhoto(reviews.getHasPhoto())
				.createdAt(reviews.getCreatedAt())
				.updatedAt(reviews.getUpdatedAt())
				.reviewMembersDto(ReviewMembersDto.toDto(reviews.getMembersMain()))
				.reviewEateriesDto(ReviewEateriesDto.toDto(reviews.getEateries()))
				.build();
	}
}