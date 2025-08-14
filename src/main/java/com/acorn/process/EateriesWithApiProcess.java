package com.acorn.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.acorn.dto.openfeign.kakao.blog.BlogDocumentsDto;
import com.acorn.dto.openfeign.kakao.image.ImageDocumentDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordDocumentDto;
import com.acorn.dto.openfeign.kakao.keyword.KeywordResponseDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.exception.api.DuplicatedInDBException;
import com.acorn.process.openfeign.kakao.BlogSearchProcess;
import com.acorn.process.openfeign.kakao.ImageSearchProcess;
import com.acorn.process.openfeign.kakao.KeywordSearchProcess;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.response.ResponseJson;
import com.acorn.utils.ListUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 대용량의 API 데이터 호출하여 DB에 저장하는 용도의 서비스 클래스.
 * API 호출은 초기 DB 저장용으로만 사용한다.
 * API - DB 연동 로직이 긴 관계로, 음식점과 관계된 다른 로직 작성 필요 시 별도의 클래스에 작성 요망.
 *
 * @author JeroCaller
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EateriesWithApiProcess {
	
	private final EateriesRepository eateriesRepository;
	private final CategoriesRepository categoriesRepository;
	private final CategoryGroupsRepository categoryGroupsRepository;
	private final ImageSearchProcess imageSearchProcess;
	private final BlogSearchProcess blogSearchProcess;
	private final KeywordSearchProcess keywordSearchProcess;
	
	/**
	 * 대용량의 API 데이터 호출하여 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param searchKeyword - API의 검색어 요청 파라미터. 주로 "서울 강남구"와 같은 지역 중분류까지의 주소.
	 * @param startPage - API 응답 데이터 상 가져오기 시작하고자 하는 페이지 번호.
	 * @param requestApiDataNum - API로부터 요청하여 가져올 총 데이터 수. 예) 45개
	 * @return
	 */
	public ResponseJson saveEateriesAll(
		String searchKeyword,
		int startPage,
		int requestApiDataNum
	) {
		ResponseJson responseJson = null;
		
		int calledDataNum = 0;  // 현재까지 API 호출한 누적 개수 저장용.
		int currentPage = startPage;
		
		while (calledDataNum < requestApiDataNum) {
			KeywordResponseDto apiResult = keywordSearchProcess
				.getApiResult(searchKeyword, currentPage);
			
			if (ListUtil.isEmpty(apiResult.getDocuments())) {
				String message = "조회 결과 없음.";
				log.info(message);
				responseJson = ResponseJson.builder()
					.status(HttpStatus.NOT_FOUND)
					.message(message)
					.data(calledDataNum)
					.build();
				return responseJson;
			}
			
			try {
				calledDataNum += saveApi(apiResult.getDocuments());
			} catch (DuplicatedInDBException e) {
				calledDataNum += e.getSavedNum();
				
				// 데이터 중복 문제는 특정 페이지를 넘어가면 발생하는 것으로 보임. 
				// 그 이전 페이지까지는 정상적으로 처리되므로 일부 데이터만 처리되었다는 의미에서
				// Http status를 PARTIAL_CONTENT로 설정함.
				responseJson = ResponseJson.builder()
					.status(HttpStatus.PARTIAL_CONTENT) // 206
					.message(e.getMessage() + " " + e.getDuplicated().toString())
					.data(calledDataNum)
					.build();
				return responseJson;
			}
			
			// API 검색 상 현재 페이지가 끝 페이지이므로 break
			if (apiResult.getMeta().isEnd()) {
				String message = "마지막 응답 페이지 도달로 API DB 저장 작업 조기 종료.";
				log.info(message);

				// 원래 사용자가 원하고자 했던 데이터의 일부만 처리한 셈이므로 PARTIAL_CONTENT로 
				// response HTTP status로 지정함.
				responseJson = ResponseJson.builder()
					.status(HttpStatus.PARTIAL_CONTENT) // 206
					.message(message)
					.data(calledDataNum)
					.build();
				return responseJson;
			}

			++currentPage;
		}
		
		responseJson = ResponseJson.builder()
			.status(HttpStatus.OK)
			.message("DB 영속화 작업 완료")
			.data(calledDataNum)
			.build();
		log.info("현재 페이지 수: " + (currentPage - 1));
		return responseJson;
	}

	/**
	 * 조회된 API를 DB에 저장.
	 * 
	 * @author JeroCaller (JJH)
	 * @param response
	 * @return 
	 * @throws DuplicatedInDBException - API로부터 가져온 데이터가 DB에 이미 있을 경우 발생하는 커스텀 예외.
	 */
	@Transactional
	private int saveApi(List<KeywordDocumentDto> response) throws DuplicatedInDBException {
		List<Eateries> eateries = new ArrayList<Eateries>();

		for (KeywordDocumentDto document : response) {
			// 확인 결과 kaka API에서 특정 페이지 이상 넘기면 아무리 페이지 번호를 증가시켜도
			// 똑같은 데이터가 반복적으로 응답되는 현상을 확인함.
			// 따라서 중복 데이터 삽입을 방지.
			Optional<Eateries> duplicated = eateriesRepository
				.findByNameAndAddress(
					document.getPlaceName(),
					document.getRoadAddressName()
				);
			if (duplicated.isPresent()) {
				// 응답 데이터 전송을 위해 컨트롤러에 넘길 데이터를 커스텀 예외 객체에 저장.
				DuplicatedInDBException customException = new DuplicatedInDBException();
				customException.setDuplicated(duplicated.get());
				customException.setSavedNum(eateries.size());
				throw customException;
			}
			
			// 맨 첫 요소는 불필요한 정보.
			// API에서 제공하는 카테고리 정보의 길이가 동적이고, 어디까지 상세 정보를 제공하는 지 몰라
			// " > "을 구분자로 구분한 파편 정보들을 별도의 DTO가 아닌 String[]에 담도록 함.
			String[] categoryTokens = document.getCategoryName().split(" > ");

			Categories categoryEntity = null;

			// 소분류 항목 있을 경우.
			if (categoryTokens.length > 2) {
				categoryEntity = saveAndReturnCategory(categoryTokens[1], categoryTokens[2]);
			} else if (categoryTokens.length > 1) {
				categoryEntity = saveAndReturnCategory(categoryTokens[1], null);
			}

			//log.info("road address name from kakao api");
			//log.info(document.getRoadAddressName());
			
			// 검색 결과 정확성을 위해 주소 중분류까지 첨부하여 검색 
			// (중분류는 공백 기준 분할 후 나오는 두 번째 문자열 토큰까지만을 고려.)
			// ( 예) 경기 용인시 처인구 -> 경기 용인시 (원래는 용인시 처인구까지가 중분류임) )
			// placeName() : 음식점명
			String[] addressTokens = null;
			if (!document.getRoadAddressName().trim().isEmpty()) {
				addressTokens = document.getRoadAddressName().split(" ");
			} 
			String address = "";
			
			// API로부터 조회된 음식점의 도로명 정보가 아예 없는 경우도 있음.
			if (addressTokens != null) {
				address = addressTokens[0] + " " + addressTokens[1];
			}

			String apiQuery = document.getPlaceName() + " " + address;
			log.info("apiQuery : " + apiQuery);
			ImageDocumentDto imageDocumentDto = imageSearchProcess.getOneImage(apiQuery);
			BlogDocumentsDto blogDocumentsDto = blogSearchProcess.getOneBlog(apiQuery);
			
			String imageResult = "";
			String blogResult = "";
			
			log.info("image result");
			if (imageDocumentDto != null && imageDocumentDto.getImageUrl() != null) {
				imageResult = imageDocumentDto.getImageUrl();
				log.info(imageDocumentDto.toString());
			} else {
				log.info("image NOT FOUND");
			}
			
			log.info("blog result");
			if (blogDocumentsDto != null && blogDocumentsDto.getContents() != null) {
				blogResult = blogDocumentsDto.getContents();
				log.info(blogDocumentsDto.toString());
			} else {
				log.info("blog NOT FOUND");
			}
			
			Eateries newEateries = Eateries.builder()
				.name(document.getPlaceName())
				.longitude(new BigDecimal(document.getX()))
				.latitude(new BigDecimal(document.getY()))
				.address(document.getRoadAddressName())
				.category(categoryEntity)
				.tel(document.getPhone())
				.thumbnail(imageResult)
				.description(blogResult)
				.build();
			eateriesRepository.save(newEateries);
			eateries.add(newEateries);
		}
		
		return eateries.size();
	}

	/**
	 * API로부터 들어온 음식점 카테고리 정보를 DB로부터 조회하여 외래키 매핑 또는 DB 내에 해당 정보 없을 시 
	 * 해당 엔티티 새 생성 및 반환.
	 * 
	 * saveApi 메서드에서 사용됨.
	 * 
	 * @author JeroCaller (JJH)
	 * @param largeCate - 음식 대분류 카테고리명
	 * @param smallCate - 음식 소분류 카테고리명
	 * @return
	 */
	@Transactional
	private Categories saveAndReturnCategory(String largeCate, String smallCate) {
		Categories categoryEntity = null;
		
		// 음식점 카테고리 소분류가 입력되지 않아도 대분류만으로도 조회할 수 있도록 구성.
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
