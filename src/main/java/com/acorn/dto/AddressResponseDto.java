package com.acorn.dto;

import java.util.List;

import lombok.Data;

/**
 * 경위도를 도로명주소로 변환하는 로직에 사용되는 DTO
 * Key : Value 값이 겹겹이 쌓여 있는 구조로 별도의 (내부) 클래스로 분해
 *
 * @author EaseHee
 */
@Data
public class AddressResponseDto {

	private List<Document> documents;
	
    @Data
    public static class Document {
        private RoadAddress road_address; // 도로명 주소
        private Address address;         // 지번 주소
    }

    @Data
    public static class RoadAddress {
        private String address_name;     // 도로명 주소 필드
        private String road_name;        // 도로명
    }

    @Data
    public static class Address {
        private String address_name;     // 지번 주소 필드
    }
}
