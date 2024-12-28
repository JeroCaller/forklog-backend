package com.acorn.exception.location;


/**
 * 주소 조회 실패 원인 모음
 * 
 * @author JeroCaller (JJH)
 */
public enum LocationSearchFailedBy {
	BY_UPPER, // 상위 카테고리 검색에 의한 조회 실패. 예) 중분류 검색 시 대분류 검색어 사용 - "사울"
	
	// 같은 카테고리 검색에 의한 조회 실패. 예) 중분류 검색 시 같은 중분류에 해당하는 이름 검색 - "강냠구"
	// 이름 외에 ID 등 다른 요소로 검색 실패했을 때 사용
	BY_ITSELF,  
	BY_NAME,   // 주소 이름 검색에 의한 조회 실패
	BY_OTHER, // 그 외 이유로 인한 조회 실패. 단순히 커스텀 메시지 작성하고자 할 때 사용
}
