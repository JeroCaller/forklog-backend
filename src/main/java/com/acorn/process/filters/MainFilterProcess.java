package com.acorn.process.filters;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acorn.dto.MainFilterDto;
import com.acorn.repository.CategoryGroupsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainFilterProcess {
	private final CategoryGroupsRepository categoryGroupsRepository;
	
	/**
	 * 메인 페이지 내 필터 조건 중 음식 카테고리 데이터 반환
	 * 
	 * @return 카테고리 그룹 DTO : List에 저장 후 반환
	 */
	public List<MainFilterDto.CategoryGroups> getCategoryGroups() {
		
		// List<CategoryGroups> 로부터 Entity -> DTO 변환 
		return categoryGroupsRepository.findAll()
				.stream()
				// 하위 카테고리가 없는 경우 제외
				.filter(group -> group.getCategories().size() > 1)
				.map(group -> ( // CategoryGroup 엔티티 DTO로 변환(매핑) - 대분류
						MainFilterDto.CategoryGroups.builder()
							.no(group.getNo())
							.name(group.getName())
							.categories(group.getCategories()
									  .stream()
									  // 하위 카테고리명이 "기타"인 경우 제외
									  .filter(category -> !category.getName().equals("기타"))
									  .map(category -> ( // 소분류
											  MainFilterDto.CategoryGroups.Category.builder()
												.no(category.getNo())
												.name(category.getName())
												.build()))
									  // 카테고리 소분류명으로 정렬
									  .sorted(Comparator.comparing(MainFilterDto.CategoryGroups.Category::getName))
									  .collect(Collectors.toList()))
							.build()))
				.collect(Collectors.toList());
	}
}
