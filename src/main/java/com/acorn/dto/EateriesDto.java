package com.acorn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class EateriesDto {
	private Integer no;
    private String name;	// 음식적 이름
    private BigDecimal rating;	// 음식점 별점
    private String address;	// 음식점 주소
    private String tel;		// 음식점 번호
    private String categoryName;	// 음식점 카테고리대분류
    private BigDecimal latitude;    // 위도
    private BigDecimal longitude;   // 경도
}