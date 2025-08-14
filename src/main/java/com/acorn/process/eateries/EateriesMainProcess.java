package com.acorn.process.eateries;

import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acorn.dto.eateries.EateriesDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.exception.BadAlgorithmException;
import com.acorn.exception.NoDataFoundException;
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
	
	// getOneEateriesByRandom() 메서드 내 사용을 위한 용도.
	private final Random random = new Random();
	
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
	 * @param location - 문자열 형태의 지역 주소명
	 * @param largeId - 카테고리 대분류 엔티티의 ID
	 * @param pageRequset
	 * @return
	 * @throws NoCategoryFoundException 입력된 카테고리 대분류 ID(largeId)가 DB 
	 * 내에 조회되지 않은 경우 발생.
	 */
	// 각자 다른 repository로부터 조회하는 코드가 있으므로 이를 트랜잭션으로 묶는 게 
	// 좋을 것 같다는 판단 하에 @Transactional 어노테이션 부여함.
	@Transactional(readOnly = true)
	public Page<EateriesDto> getEateriesByLocationAndCategoryLarge(
			String location,
			int largeId,
			Pageable pageRequset
	) throws NoCategoryFoundException {
		Optional<CategoryGroups> categoryGroupOpt = categoryGroupsRepository
				.findById(largeId);

		CategoryGroups categoryGroups = categoryGroupOpt.orElseThrow(
				() -> new NoCategoryFoundException(
						"카테고리 대분류 ID: " + String.valueOf(largeId)
					)
		);
		
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
	 * @param location - 문자열 형태의 지역 주소명
	 * @param smallId - 카테고리 소분류 엔티티의 ID
	 * @param pageRequest
	 * @return
	 * @throws NoCategoryFoundException 입력된 카테고리 소분류 ID(largeId)가 DB 
	 * 내에 조회되지 않은 경우 발생.
	 */
	// 각자 다른 repository로부터 조회하는 코드가 있으므로 이를 트랜잭션으로 묶는 게 
	// 좋을 것 같다는 판단 하에 @Transactional 어노테이션 부여함.
	@Transactional(readOnly = true)
	public Page<EateriesDto> getEateriesByLocationAndCategorySmall(
			String location,
			int smallId,
			Pageable pageRequest
	) throws NoCategoryFoundException {
		Optional<Categories> categoryOpt = categoriesRepository.findById(smallId);
		Categories category = categoryOpt.orElseThrow(
				() -> new NoCategoryFoundException(String.valueOf(smallId))
		);
		//log.info("조회된 카테고리: " + category.toString() + " " + category.getGroup().getName());
		
		Page<Eateries> eateries = eateriesRepository
				.findByAddressContainsAndCategory(location, category, pageRequest);
		
		/*
		log.info("조회된 eateries 출력");
		eateries.forEach(eat -> {
			log.info("good");
			log.info(eat.toString());
			log.info(eat.getName());
		});
		*/
		
		return eateries.map(EateriesDto :: toDto);
	}
	
	/**
	 * DB 내 eateries 테이블로부터 랜덤으로 하나의 레코드를 뽑아 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @return
	 * @throws NoDataFoundException - 조회된 eateries 엔티티가 없을 경우 발생
	 * @throws BadAlgorithmException
	 */
	// 하나의 메서드에 repository 조회 작업이 둘 이상 있으므로 이를 트랜잭션으로 묶는 게 
	// 좋을 것 같다는 판단 하에 아래 어노테이션 부여함.
	@Transactional(readOnly = true)
	public EateriesDto getOneEateriesByRandom() 
			throws NoDataFoundException, BadAlgorithmException {
		Integer maxId = eateriesRepository.findIdMax();
		if (maxId == null) {
			throw new NoDataFoundException(
				"조회된 최대 ID값이 없습니다. DB에 데이터가 아예 없는 것인지 확인 필요"
			);
		}
		
		// DB에 PK값이 불연속적으로 있을 수도 있음. 
		// 이 경우 DB에 없는 랜덤 값이 반환될 수 있기에 값이 나올 때까지 
		// 랜덤 값을 발생시킬 횟수를 정한다. 
		final int MAX_ROTATE_NUM = 100;
		int count = 0;
		
		while (count < MAX_ROTATE_NUM) {
			int randId = random.nextInt(1, maxId);
			Optional<Eateries> eateriesOpt = eateriesRepository.findById(randId);
			if (eateriesOpt.isPresent()) {
				return EateriesDto.toDto(eateriesOpt.get());
			}
		}
		
		throw new BadAlgorithmException(
			"조회된 랜덤 음식점 정보가 없습니다. 반복 횟수를 늘리거나 다른 알고리즘이 필요할 듯 합니다."
		);
	}
	
}
