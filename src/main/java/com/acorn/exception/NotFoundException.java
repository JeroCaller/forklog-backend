package com.acorn.exception;

/**
 * 데이터 부재 시 발생 예외
 *
 * @author EaseHee
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}