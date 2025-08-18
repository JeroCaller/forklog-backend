package com.acorn.exception;

/**
 * 조회된 데이터가 없을 때 발생시킬 커스텀 예외 클래스.
 * 자바 기본 제공 예외 클래스와 구분하기 위해 의도적으로 커스텀 예외 클래스를 정의함.
 * 
 * @author JeroCaller (JJH)
 */
public class NoDataFoundException extends Exception {
	
	public NoDataFoundException() {}
	
	public NoDataFoundException(String message) {
		super(message);
	}
}
