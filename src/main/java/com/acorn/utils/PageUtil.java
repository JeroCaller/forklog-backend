package com.acorn.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtil {
	
	/**
	 * PageRequest.of(page, size)에 들어가는 페이지 번호는 zero-based이다. 
	 * 즉, 페이지 번호를 1 입력하면 내부적으로는 2번 페이지로 인식된다. 
	 * 0을 입력해야 1번째 페이지로 입력됨. 
	 * 그러나 현실에서는 보통 페이지 번호를 1부터 시작한다. 
	 * 이러한 혼란을 없애기 위해 one-based의 메서드로 재정의함.
	 * 
	 * @author JeroCaller (JJH)
	 * @param page - 1부터 시작하는 페이지 번호. 범위는 page >= 1이어야 함.
	 * @param size
	 * @return
	 */
	public static Pageable getPageRequestOf(int page, int size) {
		return PageRequest.of(page - 1, size);
	}
	
	/**
	 * PageRequest.of(page, size)에 들어가는 페이지 번호는 zero-based이다. 
	 * 즉, 페이지 번호를 1 입력하면 내부적으로는 2번 페이지로 인식된다. 
	 * 0을 입력해야 1번째 페이지로 입력됨. 
	 * 그러나 현실에서는 보통 페이지 번호를 1부터 시작한다. 
	 * 이러한 혼란을 없애기 위해 one-based의 메서드로 재정의함.
	 * 
	 * @author JeroCaller (JJH)
	 * @param page - 1부터 시작하는 페이지 번호. 범위는 page >= 1이어야 함.
	 * @param size
	 * @return
	 */
	public static Pageable getPageRequestOf(
			int page, 
			int size, 
			Sort sort
	) {
		return PageRequest.of(page - 1, size, sort);
	}
	
	/**
	 * 현재 Page의 개수가 0 또는 null인지 판별
	 * 
	 * @author JeroCaller (JJH)
	 * @param <T>
	 * @param pages
	 * @return
	 */
	public static <T> boolean isEmtpy(Page<T> pages) {
		return (pages == null || pages.getNumberOfElements() == 0);
	}
}
