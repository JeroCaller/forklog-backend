package com.acorn.utils;

public class LocationUtil {
	
	/**
	 * 상세 주소까지 적힌 전체 주소 문자열을 공백 기준으로 나눈 후, 주소 중분류까지를 
	 * 다시 합쳐 문자열로 반환
	 * 
	 * 예) "서울 강남구 xx대로 11-22" => "서울 강남구"
	 * 
	 * @author JeroCaller (JJH)
	 * @param fullLocation
	 * @return
	 */
	public static String getLocationMediumStringByFull(String fullLocation) {
		String[] tokens = fullLocation.split(" ");
		if (tokens.length < 2) return null;
		
		return tokens[0] + " " + tokens[1];
	}
}
