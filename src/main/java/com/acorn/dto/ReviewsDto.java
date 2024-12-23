package com.acorn.dto;

import com.acorn.entity.Reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsDto {
	private Integer no;
	// toDto
	public static ReviewsDto toDto(Reviews reviews) {
		return ReviewsDto.builder()
				.no(reviews.getNo())
				.build();
	}
}