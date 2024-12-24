package com.acorn.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.LocationSplitDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordDocumentDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationGroups;
import com.acorn.entity.LocationRoads;
import com.acorn.entity.Locations;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.LocationRoadsRepository;
import com.acorn.utils.LocationConverter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Service
@Slf4j
public class EateriesWithApiProcess {
	
	@Autowired
	private EateriesRepository eateriesRepository;
	
	@Autowired
	private CategoriesRepository categoriesRepository;
	
	@Autowired
	private CategoryGroupsRepository categoryGroupsRepository;
	
	@Autowired
	private LocationRoadsRepository locationRoadsRepository;
	
	@Autowired
	private LocationProcess locationProcess;
	
	@Autowired
	private LocationConverter locationConverter;
	
	/**
	 * 주어진 도로명 주소에 해당하는 음식점 데이터들을 DB로부터 조회하여 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationRoads 
	 * @return
	 */
	public Page<EateriesDto> getDataFromDbByLocation(
			LocationRoads locationRoads, 
			Pageable pageRequest
	) {
		Page<Eateries> eateries = eateriesRepository.findByRoadNo(
				locationRoads.getNo(),
				pageRequest
		);
		
		if (eateries == null) return null;
		return eateries.map(entity -> EateriesDto.toDto(entity));
	}
	
	/**
	 * 조회된 API를 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param response
	 */
	public Page<EateriesDto> saveApi(List<KeywordDocumentDto> response) {
		List<Eateries> eateries = new ArrayList<Eateries>();
		
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
			
			// 응답 데이터로부터 나온 도로명 주소를 DB로부터 조회한 후 
			// 이를 Eateries의 외래키를 충족시키도록 하는 로직.
			log.info("road address name from kakao api");
			log.info(document.getRoadAddressName());
			
			LocationSplitDto locationSplitDto = locationConverter
					.getSplitLocation(document.getRoadAddressName());
			log.info("location splited");
			log.info(locationSplitDto.toString());
			
			LocationRoads locationRoadsEntity = locationRoadsRepository
					.findByFullLocation(locationSplitDto);
			
			// 만약 조회된 엔티티가 없다면 DB에 등록되지 않은 주소로 간주하고 
			// 새 주소를 DB에 저장.
			if (locationRoadsEntity == null) {
				/*
				LocationGroups locationGroupsEntity = LocationGroups.builder()
						.name(locationSplitDto.getLargeCity())
						.build();
				Locations locationsEntity = Locations.builder()
						.name(locationSplitDto.getMediumCity())
						.locationGroups(locationGroupsEntity)
						.build();
				locationRoadsEntity = LocationRoads.builder()
						.name(locationSplitDto.getRoadName())
						.locations(locationsEntity)
						.build();
				locationRoadsEntity = locationRoadsRepository.save(locationRoadsEntity);
				*/
				locationRoadsEntity = locationProcess.saveFull(locationSplitDto);
			}
			log.info(locationRoadsEntity.toString());
			
			//TODO thumbnail, description 데이터도 필요.
			Eateries newEateries = Eateries.builder()
					.name(document.getPlaceName())
					.longitude(new BigDecimal(document.getX()))
					.latitude(new BigDecimal(document.getY()))
					.locationRoads(locationRoadsEntity)
					.category(categoryEntity)
					.tel(document.getPhone())
					.build();
			eateriesRepository.save(newEateries);
			eateries.add(newEateries);
		});
		
		List<EateriesDto> eateriesDto = eateries.stream()
				.map(EateriesDto :: toDto)
				.collect(Collectors.toList());
		
		// List<DTO> -> Page<DTO>
		return new PageImpl<EateriesDto>(eateriesDto);
		
		/*
		return eateries.stream()
				.map(EateriesDto :: toDto)
				.collect(Collectors.toList());
		*/
	}
	
	/**
	 * API로부터 들어온 음식점 카테고리 정보를 DB로부터 조회하여 외래키 매핑
	 * 또는
	 * DB 내에 해당 정보 없을 시 해당 엔티티 새 생성 및 반환.
	 * 
	 * saveApi 메서드에서 사용됨. 
	 * 
	 * @author JeroCaller (JJH)
	 * @param largeCate - 음식 대분류 카테고리
	 * @param smallCate - 음식 소분류 카테고리
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
		
		categoryEntity = categoriesRepository.findByNames(largeCate, smallCate);
		if (categoryEntity == null) {
			// 새 카테고리 DB에 삽입
			categoryEntity = categoriesRepository.save(
				Categories.builder()
					.name(smallCate)
					.group(categoryGroupEntity)
					.build()
			);
		}
		log.info("category Entity");
		log.info(categoryEntity.toString());
		
		return categoryEntity;
	}
}
