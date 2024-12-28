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
	
	/**
	 * List 객체가 null이거나 요소가 없는지 파악.
	 * 
	 * @author JeroCaller (JJH)
	 * @param <E>
	 * @param list
	 * @return
	 */
	public static <E> boolean isEmpty(List<E> list) {
		return (list == null || list.size() == 0);
	}
	
}
