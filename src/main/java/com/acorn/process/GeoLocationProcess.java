package com.acorn.process;

import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.location.LocationSplitDto;
import com.acorn.dto.openfeign.kakao.geo.location.GeoLocationResponseDto;
import com.acorn.dto.openfeign.kakao.geo.location.GeoLocationResponseDto.GeoLocationDocumentsDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoLocationProcess {
	
	private final KakaoRestOpenFeign kakaoRestOpenFeign;
	
	/**
	 * 경위도 좌표에 해당하는 주소를 문자열 토큰 형태의 DTO로 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param x 
	 * @param y
	 * @return
	 */
	public LocationSplitDto getOneLocationFromCoordinate(String x, String y) {
		GeoLocationResponseDto responseDto = kakaoRestOpenFeign
				.getAddressFromCoordinate(x, y);
		
		// 조회된 데이터가 없을 경우 커스텀 예외 발생시키며 메서드 실행 중단.
		if (responseDto.getDocuments() == null || 
			responseDto.getDocuments().size() == 0) {
			return null;
		}
		
		// 결과 데이터(documents)가 여러 개더라도 첫 번째 데이터를 선택.
		GeoLocationDocumentsDto data = responseDto.getDocuments().get(0);
		if (data.getRoadAddress() == null || 
			data.getRoadAddress().getAddressName() == null ||
			data.getRoadAddress().getAddressName().isBlank()
		) {
			return null;
		}
		
		LocationSplitDto locationSplitDto = LocationSplitDto.builder()
				.largeCity(data.getRoadAddress().getRegion1depthName())
				.mediumCity(data.getRoadAddress().getRegion2depthName())
				.build();
		
		return locationSplitDto;
	}
}
