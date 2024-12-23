package com.acorn.process;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordDocumentDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationRoads;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.LocationRoadsRepository;

/**
 * 
 */
@Service
public class EateriesProcess {
	
	@Autowired
	private EateriesRepository eateriesRepository;
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@Autowired
	private CategoryGroupsRepository categoryGroupsRepository;
	
	@Autowired
	private LocationRoadsRepository locationRoadsRepository;
	
	/**
	 * 주어진 도로명 주소에 해당하는 음식점 데이터들을 DB로부터 조회하여 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationRoads 
	 * @return
	 */
	public List<EateriesDto> getDataFromDbBy(LocationRoads locationRoads) {
		List<Eateries> eateries = eateriesRepository.findByRoadNo(locationRoads.getNo());
		if (eateries == null) return null;
		return eateries.stream()
				.map(EateriesDto :: toDto)
				.collect(Collectors.toList());
	}
	
	/**
	 * 조회된 API를 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param response
	 */
	public void saveApi(List<KeywordDocumentDto> response) {
		response.forEach(document -> {
			// 맨 첫 요소는 불필요한 정보.
			String[] categoryFragments = document.getCategoryName().split(" > ");
			
			Categories categoryEntity = null;
			
			// 소분류 항목 있을 경우.
			if (categoryFragments.length > 2) {
				categoryEntity = saveAndReturnCategory(categoryFragments[1], categoryFragments[2]);
			} else if (categoryFragments.length > 1) {
				categoryEntity = saveAndReturnCategory(categoryFragments[1], null);
			}
			
			// TODO ...
			// API 응답 데이터로부터 나온 도로명 주소를 DB로부터 조회한 후 
			// 이를 Eateries의 외래키를 충족시키도록 하는 로직 작성 필요
			document.getRoadAddressName();
			
			//TODO thumbnail, description 데이터도 필요.
			Eateries newEateries = Eateries.builder()
					.name(document.getPlaceName())
					.longitude(new BigDecimal(document.getX()))
					.latitude(new BigDecimal(document.getY()))
					//.locationRoads(locationRoads)
					.category(categoryEntity)
					.build();
			eateriesRepository.save(newEateries);
		});
		
	}
	
	/**
	 * API로부터 들어온 음식점 카테고리 정보를 DB로부터 조회하여 외래키 매핑
	 * 또는
	 * DB 내에 해당 정보 없을 시 해당 엔티티 새 생성 및 반환.
	 * 
	 * 
	 * saveApi 메서드에서 사용됨. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param largeCate
	 * @param smallCate
	 * @return
	 */
	private Categories saveAndReturnCategory(String largeCate, String smallCate) {
		Categories categoryEntity = null;
		
		if (smallCate == null || smallCate.trim().equals("")) {
			if (largeCate != null && !largeCate.trim().equals("")) {
				smallCate = "기타";
			} else {
				return null;
			}
		}
		
		// 음식 카테고리 대분류가 DB에 있는지 먼저 확인.
		CategoryGroups categoryGroupEntity = categoryGroupsRepository.findByName(largeCate);
		
		if (categoryGroupEntity == null) {
			// API로부터 새로 들어온 음식 대분류 카테고리 정보를 DB에 저장.
			categoryGroupEntity = categoryGroupsRepository.save(CategoryGroups.builder()
					.name(largeCate)
					.build());
		}
		
		categoryEntity = categoriesRepository.findByName(smallCate);
		if (categoryEntity == null) {
			// 새 카테고리 DB에 삽입
			categoryEntity = categoriesRepository.save(
				Categories.builder()
					.name(smallCate)
					.group(categoryGroupEntity)
					.build()
			);
		}
		
		return categoryEntity;
	}
}
