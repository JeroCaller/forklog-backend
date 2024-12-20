package com.acorn.model;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Repository;

import com.acorn.entity.Categories;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;

/**
 * 서비스 로직 중 DB 연동 작업 수행 (DAO)
 */
@Repository
public class CategoriesModel {
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	@Autowired
	private CategoryGroupsRepository categoryGroupsRepository;
	
//	/**
//	 * 
//	 * @param categoriesName
//	 * @return
//	 */
//	public Object getCategory(String categoriesName) {
//		
//	}
	
	/**
	 * DB에 데이터가 없을 경우 API의 데이터를 조회하여 DB에 저장
	 * @param categoriesName, groupName
	 */
//	public Object updateCategory(String categoriesName, String groupName) {
	public Object updateCategory(List<Map<String, Object>> documents) {
		try {
			for (Map<String, Object> document : documents) {
				String groupName = String.valueOf(document.get("category_group"));
				String categoriesName = String.valueOf(document.get("categories"));
				
				if (categoriesName == null) {
					return null;
				}
				
				Categories category = Categories.builder()
						.name(categoriesName)
						.group(categoryGroupsRepository.findIdByName(groupName))
						.build();
				categoriesRepository.save(category);
			}
			return "success";
		} catch (Exception e) {
			return "카테고리 저장 실패 : " + e;
		}
	}
}
