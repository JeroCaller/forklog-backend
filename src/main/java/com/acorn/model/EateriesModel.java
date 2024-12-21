package com.acorn.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EateriesModel {
	@Autowired
	private EateriesRepository eateriesRepository;
	@Autowired
	private CategoriesRepository categoriesRepository;
	@Autowired
	private CategoriesModel categoriesModel;
	
	@Transactional
	public void updateEatery (Map<String, Object> eatery) {
		String groupName = String.valueOf(eatery.get("category_group"));
		String categoriesName = String.valueOf(eatery.get("categories"));
		
		categoriesModel.updateCategory(groupName, categoriesName);
		
		String name = String.valueOf(eatery.get("name"));
		BigDecimal longitude = new BigDecimal(String.valueOf(eatery.get("longitude")));
		BigDecimal latitude = new BigDecimal(String.valueOf(eatery.get("latitude")));

//		log.info("is true? : {}", eateriesRepository.existsByNameAndLongitudeAndLatitude(name, longitude, latitude));
		
		if (!eateriesRepository.existsByNameAndLongitudeAndLatitude(name, longitude, latitude)) {
			// 음식점 DB에 저장
			Eateries entity = Eateries.builder()
					.name(name)
					.longitude(longitude) // String -> BigDecimal 변환
					.latitude(latitude) // String -> BigDecimal 변환
					.category(categoriesRepository.findByName(categoriesName))
					.build();
			eateriesRepository.save(entity);
		}
	}
}
