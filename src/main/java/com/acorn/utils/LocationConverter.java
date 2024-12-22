package com.acorn.utils;

import org.springframework.stereotype.Component;

import com.acorn.entity.LocationRoads;

import lombok.extern.slf4j.Slf4j;

/**
 * 주소 변환 관련 클래스
 * 
 */
@Slf4j
@Component
public class LocationConverter {
	
	/**
	 * 세 테이블로 나눠진 도로명 주소를 공백을 구분자로 하여 하나로 합친다. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param roadEntity
	 * @return 예) "서울", "강남구", "XX대로" => "서울 강남구 XX대로"
	 */
	public String getFullLocation(LocationRoads roadEntity) {
		if (roadEntity == null) return null;
		
		String[] locationFragments = {
			roadEntity.getLocations().getLocationGroups().getName(),
			roadEntity.getLocations().getName(),
			roadEntity.getName()
		};
		
		String result = String.join(" ", locationFragments);
		log.info("전체 도로명 주소: " + result);
		return result;
	}
	
	/**
	 * 확인 결과, kakao API의 "키워드로 장소 검색하기"의 query라는 요청 파라미터에 
	 * 주소 입력 시 도로명까지 입력하면 검색 결과가 나오지 않음. 
	 * 따라서 주소의 중분류까지만 전체 주소로 구성한다. => 중분류까지만 작성하면 데이터 조회 결과가 나옴.
	 * 
	 * 예) "서울시", "강남구", "XX대로" => "서울시 강남구 XX대로"  X
	 * "서울시", "강남구" => "서울시 강남구" O
	 * 
	 * @param roadEntity
	 * @return "서울시", "강남구" => "서울시 강남구"
	 */
	public String getLocationWithoutRoad(LocationRoads roadEntity) {
		if (roadEntity == null) return null;
		
		String[] locationFragments = {
			roadEntity.getLocations().getLocationGroups().getName(),
			roadEntity.getLocations().getName()
		};
		
		String result = String.join(" ", locationFragments);
		log.info("상세 도로명 주소를 제외한 전체 주소: " + result);
		return result;
	}
}
