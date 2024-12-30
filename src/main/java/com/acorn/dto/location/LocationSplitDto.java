package com.acorn.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 문자열로 구성된 도로명 주소로부터 각각 지역 대분류, 중분류, 도로명, 건물 번호를 담기 위한 
 * DTO 클래스
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
	
	/**
	 * 주소 대분류, 중분류를 합쳐 반환
	 * 예) largeCity = "서울", mediumCity = "강남구"
	 * => "서울 강남구"
	 * 
	 * @return 대분류도 없을 경우 빈 문자열 반환. 
	 * 중분류가 없을 경우 대분류만 반환. 예) "서울"
	 * 
	 */
	public String getFullLocationByMedium() {
		if (largeCity == null) return "";
		if (mediumCity == null) return largeCity;
		
		return largeCity + " " + mediumCity;
	}
}