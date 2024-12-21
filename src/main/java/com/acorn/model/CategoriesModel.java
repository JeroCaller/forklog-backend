package com.acorn.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 서비스 로직 중 DB 연동 작업 수행 (DAO)
 */
@Repository
public class CategoriesModel {
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	@Autowired
	private CategoryGroupsRepository categoryGroupsRepository;
	
	/**
	 * DB에 데이터가 없을 경우 API의 데이터를 조회하여 DB에 저장
	 * 
	 * @param categoriesName, groupName
	 */
	public void updateCategory(String groupName, String categoriesName) {
		// 카테고리 이름 유효성 검사 후 DB에 존재 여부 확인
		if (isValid(categoriesName) && !categoriesRepository.existsByName(categoriesName)) {
			// 대분류 카테고리도 DB에 없을 경우 저장
			if (!categoryGroupsRepository.existsByName(groupName)) {
				CategoryGroups group = CategoryGroups.builder().name(groupName).build();
				categoryGroupsRepository.save(group);
			}
			// 소분류 카테고리 DB에 저장
			Categories category = Categories.builder()
					.name(categoriesName)
					.group(categoryGroupsRepository.findIdByName(groupName)) // 대분류 이름을 기준으로 PK 치환
					.build();
			categoriesRepository.save(category);
		}
	}
	
    /**
     * 이름 유효성 검사
     * 
     * @param -> null 체크, "null" 체크, 공백 체크
     * @return 유효 여부
     */
    public static boolean isValid (String name) {
        return name != null && 
               !name.equalsIgnoreCase("null") && 
               !name.isBlank();
    }
}
