package com.acorn.dto.eateries.reviews;

import java.math.BigDecimal;
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
public class ReviewRequestDto {

	private Integer no;
	private BigDecimal rating;
	private String content;
	private Integer memberNo;
	private Integer eateryNo;
}
