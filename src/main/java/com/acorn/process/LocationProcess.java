package com.acorn.process;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.dto.LocationSplitDto;
import com.acorn.entity.LocationRoads;
import com.acorn.exception.NoDataFoundForRandomLocation;
import com.acorn.repository.LocationRoadsRepository;
import com.acorn.repository.LocationsRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Service
@Slf4j
public class LocationProcess {
	
	// location_roads 테이블 내 PK 필드에서 숫자가 불연속적인 구간이 있을 수 있음.
	// 그래서 없는 번호가 있을 수 있으므로
	// 테이블 내 존재하는 PK 번호에 해당하는 데이터를 추출할 때까지 반복할 횟수 지정.
	private final int MAX_REPEAT_COUNT = 100;
	
	private final String LOCATIONS = "locations";
	private final String LOCATION_ROAD = "locationRoads";
	
	@Autowired
	private LocationRoadsRepository locationRoadsRepository;
	
	@Autowired
	private LocationsRepository locationsRepository;
	
	/**
	 * DB 내 도로명 주소를 랜덤으로 하나 조회.
	 * 
	 * @author JeroCaller (JJH)
	 * @return 랜덤으로 조회된 전체 도로명 주소
	 * @throws NoDataFoundForRandomLocation 
	 *  랜덤으로 조회한 PK 번호가 테이블 내 존재하지 않아 조회된 데이터가 없는 경우 발생하는 예외
	 */
	public LocationRoads getRandomLocation() throws NoDataFoundForRandomLocation {
		//Integer noMax = locationRoadsRepository.findByIdMax();
		Integer noMax = getMaxIdOf(LOCATION_ROAD);
		
		// location_roads 테이블 내 데이터 한 건을 랜덤으로 조회
		Random rand = new Random();
		LocationRoads randomEntity = null;
		
		for (int i = 0; i < MAX_REPEAT_COUNT; i++) {
			Integer randId = rand.nextInt(1, noMax);
			Optional<LocationRoads> optEntity 
				= locationRoadsRepository.findById(randId);
			if (optEntity.isEmpty()) continue;
			
			randomEntity = optEntity.get();
			break;
		}
		
		if (randomEntity == null) {
			throw new NoDataFoundForRandomLocation();
		}
		
		return randomEntity;
	}
	
	/**
	 * 전체 주소로부터 주소 대분류, 중분류, 도로명, 건물번호로 분할하여 반환
	 * 
	 * 예1) 서울 강남구 XX대로 => "서울", "강남구", "XX대로"
	 * 예2) 서울 강남구 OO대로 24-11 => "서울", "강남구", "OO대로", "24-11"
	 * 예3) 전북특별자치도 장수군 장수읍 군청길 19 => "전북특별자치도", "장수군", "장수읍 군청길", "19"
	 * 예4) 전북특별자치도 장수군 장수읍 군청길 => "전북특별자치도", "장수군", "장수읍 군청길"
	 * 예5) 서울 XX구 OO대로12번길 => "서울", "XX구", "OO대로12번길"
	 * 예6) 전북특별자치도 군산시 중앙로 177 => "전북특별자치도", "군산시", "중앙로", "177"
	 * 
	 * 공백 기준 문자열 분해 시 토큰이 최대 6개까지 나오는 걸로 확인됨
	 * 예) "경기", "용인시 처인구", "백암면 백원로", "15-12"
	 * 
	 * @param fullLocation
	 * @return
	 */
	public LocationSplitDto getSplitLocation(String fullLocation) {
		String[] splited = fullLocation.split(" ");
		log.info("splited length: " + splited.length);
		
		LocationSplitDto result = new LocationSplitDto();
		
		switch(splited.length) {
			case 1:
				result.setLargeCity(splited[0]);
				return result;
			case 2:
				result.setLargeCity(splited[0]);
				result.setMediumCity(splited[1]);
				return result;
		}
		
		// 경기 xx시 xx구 xx로 와 같이 주소 데이터가 있을 때 
		// "경기", "xx시 xx구", "xx로"와 같이 구분될 때도 있고 
		// "경기", "xx시", "xx구 xx로" 와 같이 구분될 때도 있음.
		// 따라서 특정 문자열 토큰이 주소의 중분류에 포함되는지 
		// 도로명에 포함되는지 확인하는 로직이 필요.
		String[] middleTokens = null;
		
		// 분해된 주소 파편의 마지막 요소가 건물 번호인지 도로명 주소인지 판별
		try {
			Integer.parseInt(splited[splited.length - 1].split("-")[0]);
			
			middleTokens = Arrays.copyOfRange(
					splited, 
					1, 
					splited.length - 1
			);
			result.setBuildingNo(splited[splited.length - 1]);
		} catch (NumberFormatException e) {
			// 건물 번호 아님이 판별됨.
			middleTokens = Arrays.copyOfRange(
					splited, 
					1, 
					splited.length
			);
		}
		
		result.setLargeCity(splited[0]);
		switch (middleTokens.length) {
			case 2:
				// 총 문자열 토큰이 4개인 경우. (그 중 마지막은 건물 번호)
				if (locationsRepository.findByName(middleTokens[0]) != null) {
					// findByName 결과가 존재한다면 splited의 1번째 토큰은 주소의 "중분류"로 판단한다.
					result.setMediumCity(middleTokens[0]);
					result.setRoadName(middleTokens[1]);
				} else {
					// findByName 결과 미존재 시 splited의 2, 3번째 토큰 모두 주소의 "중분류"로 판단한다. 
					result.setMediumCity(middleTokens[0] + " " + middleTokens[1]);
				}
				break;
			case 3:
				// 총 문자열 토큰이 5개인 경우. (그 중 마지막은 건물 번호)
				if (locationsRepository.findByName(middleTokens[0]) != null) {
					// findByName 결과가 존재한다면 splited의 1번째 토큰은 주소의 "중분류"로 판단한다.
					result.setMediumCity(middleTokens[0]);
					result.setRoadName(middleTokens[1] + " " + middleTokens[2]);
				} else {
					// findByName 결과 미존재 시 splited의 2, 3번째 토큰 모두 주소의 "중분류"로 판단한다.
					result.setMediumCity(middleTokens[0] + " " + middleTokens[1]);
					result.setRoadName(middleTokens[2]);
				}
				break;
			case 4:
				// 총 문자열 토큰이 6개인 경우. (그 중 마지막은 건물 번호)
				// splited 문자열 토큰의 2, 3번째는 중분류, 4, 5번째는 도로명을 구성함
				result.setMediumCity(middleTokens[0] + " " + middleTokens[1]);
				result.setRoadName(middleTokens[2] + " " + middleTokens[3]);
				break;
		}
		
		return result;
	}
	
	/**
	 * 현재 DB 내 locations 또는 location_road 테이블 내 최고 PK 값을 반환.
	 * 
	 * @param LOCATIONS | LOCATION_ROAD
	 * @return 
	 */
	private Integer getMaxIdOf(String locationName) {
		Integer maxId = null;
		
		switch (locationName) {
			case LOCATIONS:
				maxId = locationsRepository.findbyIdMax();
				break;
			case LOCATION_ROAD:
				maxId = locationRoadsRepository.findByIdMax();
				break;
			default:
				// 잘못된 입력
				return null;
		}
		
		if (maxId == null) {
			maxId = 1;
		}
		
		return maxId;
	}
	
}
