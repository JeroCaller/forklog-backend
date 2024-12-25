package com.acorn.exception;

/**
 * 조회된 데이터가 없을 때 발생시킬 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class NoDataFoundException extends Exception {

	/**
	 * 이 필드를 추가하지 않으면 경고가 떠서 추가함.
	 */
	private static final long serialVersionUID = 1L;
	
	public NoDataFoundException(String message) {
		super(message);
	}

}
