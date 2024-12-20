package com.acorn.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.api.KakaoRestApi;
import com.acorn.entity.Categories;
import com.acorn.model.CategoriesModel;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;

/**
 * Controller로부터의 요청 처리(비즈니스 로직 수행)
 * DB 요청 & API 요청 분리
 */
@Service
public class CategoriesProcess {
	
	// DB 연동 작업을 수행하는 Model
	@Autowired
	private CategoriesModel categoriesModel;
	
	// API 요청 작업을 수행하는 @Service
	@Autowired
	private KakaoRestApi kakaoRestApi;
	
	/**
	 * @param memberNo / 지역구 이름 (locationsName)
	 * 	1. 사용자 선호 음식 카테고리 조회
	 * 	2. 음식점 조회 시 카테고리, 지역 조건
	 * @return
	 */
	public Object getEateries() {
		return null;
	}
	
	
	

}
