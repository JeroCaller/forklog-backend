package com.acorn.process;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.exception.category.NoCategoryFoundException;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메인 페이지에서 음식점을 다루기 위한 서비스 클래스
 * 
 * (API 요청 관련 로직 전혀 없음. 오로지 DB로부터 읽어오기만.) 
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EateriesMainProcess {
	
	private final EateriesRepository eateriesRepository;
	private final CategoriesRepository categoriesRepository;
	private final CategoryGroupsRepository categoryGroupsRepository;
	
	/**
	 * 지역 주소 입력 시 그와 비슷한 주소에 해당하는 음식점 정보들을 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param pageRequest
	 * @return
	 */
	public Page<EateriesDto> getEateriesByLocation(
			String location,
			Pageable pageRequest
	) {
		Page<Eateries> eateries = eateriesRepository
				.findByAddressContaining(location, pageRequest);
		return eateries.map(EateriesDto :: toDto);
	}
	
	/**
	 * 지역 및 카테고리 대분류 ID(PK) 검색 조건에 해당하는 음식점 정보 조회 및 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param largeId
	 * @param pageRequset
	 * @return
	 * @throws NoCategoryFoundException 
	 */
	public Page<EateriesDto> getEateriesByLocationAndCategoryLarge(
			String location,
			int largeId,
			Pageable pageRequset
	) throws NoCategoryFoundException {
		Optional<CategoryGroups> categoryGroupOpt = categoryGroupsRepository.findById(largeId);
		categoryGroupOpt.orElseThrow(
				() -> new NoCategoryFoundException(
						"카테고리 대분류 ID: " + String.valueOf(largeId)
					)
		);
		CategoryGroups categoryGroups = categoryGroupOpt.get();
		
		Page<Eateries> eateries = eateriesRepository.findByCategoryGroup(
				location, 
				categoryGroups.getNo(), 
				pageRequset
		);
		
		return eateries.map(EateriesDto :: toDto);
	}
	
	/**
	 * 지역 및 카테고리 소분류 ID(PK) 검색 조건에 해당하는 음식점 정보 조회 및 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param location
	 * @param smallId
	 * @param pageRequest
	 * @return
	 * @throws NoCategoryFoundException 
	 */
	public Page<EateriesDto> getEateriesByLocationAndCategorySmall(
			String location,
			int smallId,
			Pageable pageRequest
	) throws NoCategoryFoundException {
		Optional<Categories> categoryOpt = categoriesRepository.findById(smallId);
		categoryOpt.orElseThrow(() -> new NoCategoryFoundException(String.valueOf(smallId)));
		Categories category = categoryOpt.get();
		log.info("조회된 카테고리: " + category.toString() + " " + category.getGroup().getName());
		Page<Eateries> eateries = eateriesRepository
				.findByAddressContainingAndCategory(location, category, pageRequest);
		return eateries.map(EateriesDto :: toDto);
	}
}
