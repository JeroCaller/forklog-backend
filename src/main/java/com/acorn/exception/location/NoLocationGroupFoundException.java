package com.acorn.exception.location;

/**
 * 주소 대분류 조회되지 않을 시 사용할 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class NoLocationGroupFoundException extends BaseLocationException {
	
	private static final String ERROR_MESSAGE = "조회된 주소 대분류 데이터가 없습니다. 입력된 주소 대분류: ";
	
	public NoLocationGroupFoundException(String largeCity) {
		super(ERROR_MESSAGE + largeCity);
	}
	
	public NoLocationGroupFoundException() {
		super(ERROR_MESSAGE + "(입력값 없음)");
	}
}
