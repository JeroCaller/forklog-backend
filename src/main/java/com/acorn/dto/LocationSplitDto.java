package com.acorn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 주소의 대분류, 중분류, 도로명, 건물 번호(본번-부번 포함)을 각각 담는 DTO
 * 
 * 현재 사용되는 곳
 * com.acorn.utils.LocationConverter.getSplitLocation()
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString // For logging
public class LocationSplitDto {
	private String largeCity;
	private String mediumCity;
	private String roadName;
	private String buildingNo;
}
