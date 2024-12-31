package com.acorn.exception;

/**
 * EateriesMainProcess.getOneEateriesByRandom()에 사용할 커스텀 예외 클래스.
 * 결과가 안 나올 때 나쁜 알고리즘으로 간주하고 일으킬 커스텀 예외 클래스.
 * 
 * @author JeroCaller (JJH)
 */
public class BadAlgorithmException extends Exception {
	
	public BadAlgorithmException(String message) {
		super(message);
	}
}
