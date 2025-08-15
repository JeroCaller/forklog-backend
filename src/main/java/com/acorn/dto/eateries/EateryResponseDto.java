package com.acorn.dto.eateries;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author jeauk-choi
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EateryResponseDto {

	private Integer no;
    private String name;
    private String thumbnail;
    private String description;
    private String tel;
    private String address;
    private BigDecimal rating;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String categoryName;
}
