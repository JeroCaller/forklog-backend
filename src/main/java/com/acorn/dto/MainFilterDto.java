package com.acorn.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * Main 페이지 첫 접근 시 Filter에 채워질 데이터 전송용 DTO
 * 1. 음식 카테고리 : 대분류(한식 등)를 선택하면 소분류(국밥 등)가 출력
 * 2. 지역 카테고리 : 대분류(도시)를 선택하면 소분류(지역구)가 출력 "테이블 삭제"
 *
 * @author EaseHee
 */
public class MainFilterDto {
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CategoryGroups {

		private Integer no;
		private String name;
		private List<Category> categories; 
		
		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Category {

			private Integer no;
			private String name;
		}
	}
}