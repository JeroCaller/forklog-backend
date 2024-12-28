package com.acorn.exception.location;

/**
 * 주소(location)에 대한 상위 커스텀 예외 클래스
 * 
 * 참고) RuntimeException을 상속받으면 throw 시 
 * 이클립스에서 throws 작성 강제 기능이 뜨지 않음. 
 * 따라서 이를 위해 Exception을 상속받도록 함.
 * 
 * @author JeroCaller (JJH)
 */
public class BaseLocationException extends Exception {
	
	protected String errorMessage;
	
	public BaseLocationException() {
		super();
	}
	
	public BaseLocationException(String message) {
		super(message);
	}
 }
