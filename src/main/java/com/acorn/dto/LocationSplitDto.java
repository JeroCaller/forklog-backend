package com.acorn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
