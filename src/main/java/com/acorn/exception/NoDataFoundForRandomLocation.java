package com.acorn.exception;

/**
 * 
 * @author JeroCaller (JJH)
 * @see com.acorn.process.LocationProcess.#getRandomLocation()
 */
public class NoDataFoundForRandomLocation extends Exception {
	
	/**
	 * 이 필드를 추가하지 않으면 경고가 떠서 추가함. 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String EXCEPTION_MESSAGE = """
		랜덤으로 조회된 주소가 없습니다. 랜덤 반복 횟수를 늘리거나 테이블 내 특정 데이터를 콕 집을 수 있는 
		확실한 다른 알고리즘이 필요한 듯 합니다.
	""";
	
	public NoDataFoundForRandomLocation() {
		// TODO NoDataFoundException으로 바꾸고 이 클래스 삭제하기
		super(EXCEPTION_MESSAGE);
	}
}
