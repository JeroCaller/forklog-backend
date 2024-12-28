package com.acorn.dto;

import java.math.BigDecimal;

import com.acorn.entity.Eateries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EateriesDto {
	private Integer no;
	private String name;
	
	@Builder.Default
	private int viewCount = 0;
	
	private String thumbnail;
	private String description;
	private String address;
	private String tel;
	private String categoryName;
	
	@Builder.Default
	private BigDecimal rating = new BigDecimal(0.0);
	
	@Builder.Default
	private BigDecimal longitude = new BigDecimal(0.0); 
	
	@Builder.Default
	private BigDecimal latitude = new BigDecimal(0.0);
	
	private CategoriesDto categoryDto;
	
	private LocationRoadsDto locationRoadsDto;
	
	public static EateriesDto toDto(Eateries entity) {
		return EateriesDto.builder()
				.no(entity.getNo())
				.name(entity.getName())
				.viewCount(entity.getViewCount())
				.thumbnail(entity.getThumbnail())
				.description(entity.getDescription())
				.tel(entity.getTel())
				.rating(entity.getRating())
				.address(entity.getAddress())
				.longitude(entity.getLongitude())
				.latitude(entity.getLatitude())
				.categoryDto(CategoriesDto.toDto(entity.getCategory()))
//				.locationRoadsDto(LocationRoadsDto.toDto(entity.getRoad()))
				.build();
	}
}
