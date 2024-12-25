package com.acorn.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
	private BigDecimal rating;
	private String content;
	private Boolean hasPhoto;
	private Integer memberNo;
	private Integer eateryNo;
}
