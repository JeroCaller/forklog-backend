package com.acorn.dto.eateries.reviews;

import com.acorn.entity.Eateries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author rmk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEateriesDto {

	private Integer no;
	private String name;

	public static ReviewEateriesDto toDto(Eateries eateries) {
		return ReviewEateriesDto.builder()
			.no(eateries.getNo())
			.name(eateries.getName())
			.build();
	}
}
