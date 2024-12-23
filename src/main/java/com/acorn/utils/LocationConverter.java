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
	 * 예) "서울시", "강남구", "XX대로" => "서울시 강남구 XX대로" X 검색 안됨
	 * "서울시", "강남구" => "서울시 강남구" O 검색 잘됨
	 * 
	 * @author JeroCaller (JJH)
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
	
	/**
	 * 전체 주소로부터 주소 대분류, 중분류, 도로명, 건물번호로 분할하여 반환
	 * 
	 * 예1) 서울 강남구 XX대로 => "서울", "강남구", "XX대로"
	 * 예2) 서울 강남구 OO대로 24-11 => "서울", "강남구", "OO대로", "24-11"
	 * 예3) 전북특별자치도 장수군 장수읍 군청길 19 => "전북특별자치도", "장수군", "장수읍 군청길", "19"
	 * 예4) 전북특별자치도 장수군 장수읍 군청길 => "전북특별자치도", "장수군", "장수읍 군청길"
	 * 예5) 서울 XX구 OO대로12번길 => "서울", "XX구", "OO대로12번길"
	 * 
	 * @param fullLocation
	 * @return {"대분류", "중분류", "도로명", "건물번호"}
	 */
	public String[] getSplitLocation(String fullLocation) {
		String[] results = {null, null, null, null};
		String[] splited = fullLocation.split(" ");
		
		switch(splited.length) {
			case 5:
				results[0] = splited[0];
				results[1] = splited[1];
				results[2] = splited[2] + " " + splited[3]; // 2개가 하나의 도로명을 구성함.
				results[3] = splited[4];
				break;
			case 4:
				results[0] = splited[0];
				results[1] = splited[1];
				results[2] = splited[2] + " " + splited[3];
				break;
			case 3:
				results[0] = splited[0];
				results[1] = splited[1];
				results[2] = splited[2];
				break;
			case 2:
				results[0] = splited[0];
				results[1] = splited[1];
				break;
			case 1:
				results[0] = splited[0];
			default:
				break;
		}
		
		return results;
	}
}
