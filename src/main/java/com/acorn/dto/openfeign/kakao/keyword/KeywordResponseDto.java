package com.acorn.dto.openfeign.kakao.keyword;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 카카오 API - "키워드로 장소 검색하기" JSON 응답 데이터를 
 * 받기 위한 DTO
 * 
 * 카카오 API - "키워드로 장소 검색하기" JSON 응답 데이터 예시)  
 * {
 * "documents": [
 * {
 *    "address_name": "서울 강남구 삼성동 131",
 *    "category_group_code": "",
 *    "category_group_name": "",
 *    "category_name": "여행 > 관광,명소 > 문화유적 > 릉,묘,총",
 *    "distance": "",
 *    "id": "26967382",
 *    "phone": "02-568-1291",
 *    "place_name": "서울선릉과정릉",
 *    "place_url": "http://place.map.kakao.com/26967382",
 *    "road_address_name": "서울 강남구 선릉로100길 1",
 *    "x": "127.04892851392",
 *    "y": "37.5091105328378"
 *    },
 * {
 *    "address_name": "서울 강남구 신사동 668-33",
 *    "category_group_code": "AT4",
 *    "category_group_name": "관광명소",
 *    "category_name": "여행 > 관광,명소 > 테마거리",
 *    "distance": "",
 *    "id": "7990409",
 *    "phone": "",
 *    "place_name": "압구정로데오거리",
 *    "place_url": "http://place.map.kakao.com/7990409",
 *    "road_address_name": "",
 *    "x": "127.039152029523",
 *    "y": "37.5267558230172"
 *  },
 *  ...
 * ],
 * "meta": {
 *     "is_end": false,
 *     "pageable_count": 45,
 *     "same_name": {
 *         "keyword": "",
 *         "region": [],
 *         "selected_region": "서울 강남구"
 *     },
 *     "total_count": 100404
 *   }
 * }
 * 
 * @author JeroCaller (JJH)
 */
@Getter
@NoArgsConstructor
@ToString
public class KeywordResponseDto {
	
	List<KeywordDocumentDto> documents;
	KeywordMetaDto meta;
	
}
