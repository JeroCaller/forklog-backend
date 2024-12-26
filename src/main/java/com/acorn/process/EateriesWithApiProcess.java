package com.acorn.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.dto.LocationSplitDto;
import com.acorn.dto.openfeign.kakao.blog.BlogDocumentsDto;
import com.acorn.dto.openfeign.kakao.image.ImageDocumentDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordDocumentDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.entity.LocationRoads;
import com.acorn.process.openfeign.kakao.BlogSearchProcess;
import com.acorn.process.openfeign.kakao.ImageSearchProcess;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.LocationRoadsRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * API로부터 가져온 데이터를 eateries 테이블에 저장하고, 그와 동시에 Eateries 엔티티를 반환하여 음식점 정보를 클라이언트에
 * 전송할 수 있게끔 하는 클래스.
 * 
 * API - DB 연동 로직이 긴 관계로, 음식점과 관계된 다른 로직 작성 필요 시 별도의 클래스에 작성 요망.
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
	private ImageSearchProcess imageSearchProcess;
	
	@Autowired
	private BlogSearchProcess blogSearchProcess;
	
	// saveApi() 메서드 내에서 DB 내 조회되지 않은 주소 수집용.
	private final List<String> failedLocations = new ArrayList<String>();
	
	/**
	 * saveApi() 메서드 내에서 DB 내 조회되지 않은 주소 수집한 내용 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 */
	public List<String> getFailedLocations() {
		return failedLocations;
	}

	/**
	 * 주어진 도로명 주소에 해당하는 음식점 데이터들을 DB로부터 조회하여 반환
	 * 
	 * @author JeroCaller (JJH)
	 * @param locationRoads
	 * @return
	 */
	public Page<EateriesDto> getDataFromDbByLocation(LocationRoads locationRoads, Pageable pageRequest) {
		Page<Eateries> eateries = eateriesRepository.findByRoadNo(locationRoads.getNo(), pageRequest);

		if (eateries == null)
			return null;
		return eateries.map(entity -> EateriesDto.toDto(entity));
	}

	/**
	 * 조회된 API를 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param response
	 * @return 
	 */
	public Page<EateriesDto> saveApi(List<KeywordDocumentDto> response){
		failedLocations.clear();
		List<Eateries> eateries = new ArrayList<Eateries>();
		
		// DB 내 조회되지 않은 주소 모음.
		//List<String> failedLocations = new ArrayList<String>();

		for (KeywordDocumentDto document : response) {
			// 맨 첫 요소는 불필요한 정보.
			// API에서 제공하는 카테고리 정보의 길이가 동적이고, 어디까지 상세 정보를 제공하는 지 몰라
			// " > "을 구분자로 구분한 파편 정보들을 별도의 DTO가 아닌 String[]에 담도록 함.
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
			//log.info("road address name from kakao api");
			//log.info(document.getRoadAddressName());

			LocationSplitDto locationSplitDto
					= locationProcess.getSplitLocation(document.getRoadAddressName());
			//log.info("location splited");
			//log.info(locationSplitDto.toString());

			LocationRoads locationRoadsEntity 
				= locationRoadsRepository.findByFullLocation(locationSplitDto);

			if (locationRoadsEntity == null) {
				failedLocations.add(document.getRoadAddressName());
				continue;
			}
			//log.info(locationRoadsEntity.toString());
			
			// 검색 결과 정확성을 위해 주소 중분류까지 첨부하여 검색
			// placeName() : 음식점명
			String apiQuery = document.getPlaceName() + " " 
					+ locationSplitDto.getLargeCity() + " " 
					+ locationSplitDto.getMediumCity();
			//log.info("apiQuery : " + apiQuery);
			ImageDocumentDto imageDocumentDto = imageSearchProcess.getOneImage(apiQuery);
			BlogDocumentsDto blogDocumentsDto = blogSearchProcess.getOneBlog(apiQuery);
			
			String imageResult = "";
			String blogResult = "";
			
			//log.info("image result");
			if (imageDocumentDto != null) {
				imageResult = imageDocumentDto.getImageUrl();
				//log.info(imageDocumentDto.toString());
			} else {
				//log.info("image NOT FOUND");
			}
			
			//log.info("blog result");
			if (blogDocumentsDto != null) {
				blogResult = blogDocumentsDto.getContents();
				//log.info(blogDocumentsDto.toString());
			} else {
				//log.info("blog NOT FOUND");
			}
			
			Eateries newEateries = Eateries.builder()
					.name(document.getPlaceName())
					.longitude(new BigDecimal(document.getX()))
					.latitude(new BigDecimal(document.getY()))
					.locationRoads(locationRoadsEntity)
					.category(categoryEntity)
					.tel(document.getPhone())
					.thumbnail(imageResult)
					.description(blogResult)
					.build();
			eateriesRepository.save(newEateries);
			eateries.add(newEateries);
		}

		List<EateriesDto> eateriesDto = eateries.stream()
				.map(EateriesDto::toDto)
				.collect(Collectors.toList());

		// List<DTO> -> Page<DTO>
		return new PageImpl<EateriesDto>(eateriesDto);

	}

	/**
	 * API로부터 들어온 음식점 카테고리 정보를 DB로부터 조회하여 외래키 매핑 또는 DB 내에 해당 정보 없을 시 해당 엔티티 새 생성 및
	 * 반환.
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
			categoryGroupEntity = categoryGroupsRepository.save(
					CategoryGroups.builder()
					.name(largeCate)
					.build()
			);
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
		//log.info("category Entity");
		//log.info(categoryEntity.toString());

		return categoryEntity;
	}
}
