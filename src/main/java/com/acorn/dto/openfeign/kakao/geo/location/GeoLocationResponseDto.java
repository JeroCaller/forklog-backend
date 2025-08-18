package com.acorn.dto.openfeign.kakao.geo.location;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카카오 API - "좌표를 주소로 변환하기" API 응답 데이터 구성 DTO 클래스. 
 * 아래의 참고 사이트에서 제공하는 응답 데이터 구조에 맞게 DTO 클래스를 구성하였음.
 *
 * <p>
 * 참고 사이트) <br/>
 * <a href="https://developers.kakao.com/tool/rest-api/open/get/v2-local-geo-coord2address.%7Bformat%7D">
 *     https://developers.kakao.com/tool/rest-api/open/get/v2-local-geo-coord2address.%7Bformat%7D
 * </a>
 * </p>
 *
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoLocationResponseDto {
	
	private List<GeoLocationDocumentsDto> documents;
	private GeoLocationMetaDto meta;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString // For logging
	public static class GeoLocationDocumentsDto {
		
		private GeoLocationAddressDto address;
		
		@JsonProperty("road_address")
		private GeoLocationRoadAddressDto roadAddress;
		
		@Getter
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		@ToString  // For logging
		@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
		public static class GeoLocationAddressDto {

			private String addressName;
			
			@JsonProperty("region_1depth_name")
			private String region1depthName;
			
			@JsonProperty("region_2depth_name")
			private String region2depthName;
			
			@JsonProperty("region_3depth_name")
			private String region3depthName;
			
			@JsonProperty("region_3depth_h_name")
			private String region3depthHName;
			
			private String mountainYn;
			private String mainAddressNo;
			private String subAddressNo;
			
		}
		
		@Getter
		@NoArgsConstructor
		@AllArgsConstructor
		@Builder
		@ToString // For logging
		@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
		public static class GeoLocationRoadAddressDto {
			
			private String addressName;
			
			@JsonProperty("region_1depth_name")
			private String region1depthName;
			
			@JsonProperty("region_2depth_name")
			private String region2depthName;
			
			@JsonProperty("region_3depth_name")
			private String region3depthName;
			
			private String roadName;
			private String undergroundYn;
			private String mainBuildingNo;
			private String subBuildingNo;
			private String buildingName;
			private String zoneNo;
			
		}
	}
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString  // For logging
	public static class GeoLocationMetaDto {
		
		@JsonProperty("total_count")
		int totalCount;
	}
}
