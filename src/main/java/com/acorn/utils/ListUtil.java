package com.acorn.utils;

import java.util.List;

public class ListUtil {
	
	/**
	 * List<String> 내에 존재하는 문자열들을 \n을 구분자로 하는 하나의 문자열로 합친다. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param list
	 * @return
	 */
	public static String getStringList(List<String> list) {
		StringBuilder stringBuilder = new StringBuilder();
		
		list.forEach(token -> {
			stringBuilder.append(token + "\n");
		});
		
		return stringBuilder.toString();
	}
	
}
