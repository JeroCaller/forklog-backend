package com.acorn.exception.location;

/**
 * 주소 중분류 데이터 조회 실패 시 발생 시킬 커스텀 예외 클래스
 * 
 * @author JeroCaller (JJH)
 */
public class NoLocationMediumFoundException extends BaseLocationException {

	public NoLocationMediumFoundException() {
		super();
	}
	
	/**
	 * 별도로 메시지를 주고자 할 경우 사용
	 * 
	 * @author JeroCaller (JJH)
	 * @param message
	 */
	public NoLocationMediumFoundException(String message) {
		super(message);
	}
	
	/**
	 * 조회되지 않은 원인을 명확히 하고, 입력값을 예외 메시지에 함께 출력.
	 * 
	 * @author JeroCaller (JJH)
	 * @param failedBy
	 * @param failReason
	 */
	public NoLocationMediumFoundException(LocationSearchFailedBy failedBy, String failReason) {
		super(decideErrorMessage(failedBy, failReason));
	}
	
	/**
	 * 예외 발생 이유 별로 예외 메시지를 다르게 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationSearchFailedBy
	 * @param failReason
	 * @return
	 */
	private static String decideErrorMessage(
			LocationSearchFailedBy locationSearchFailedBy,
			String failReason
	) {
		String errorMessage = "";
		
		switch(locationSearchFailedBy) {
			case BY_NAME:
				errorMessage = "입력된 주소 중분류 조회 결과 없음. 입력한 주소 : " + failReason;
				break;
			case BY_UPPER:
				errorMessage = "입력한 주소 대분류에 의한 중분류 조회 결과 없음. 입력한 대분류 주소: " + failReason;
				break;
			default:
				errorMessage = "예외 메시지 미정의됨. 예외 이유: " + failReason;
		}
			
		return errorMessage;
	}
	
}
