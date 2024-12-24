package com.acorn.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
	private List<ReviewImagesResponseDto> reviewImagesResponseDto;

}