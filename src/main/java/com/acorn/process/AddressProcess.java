package com.acorn.process;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.AddressResponseDto;
import com.acorn.exception.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AddressProcess {
	private Logger log = LoggerFactory.getLogger(AddressProcess.class);

	@Autowired
	private KakaoRestOpenFeign kakaoRestOpenFeign;

	/**
	 * 경위도를 입력받아서 도로명 주소로 변경 (Kakao API : 좌표를 주소로 변환하기)
	 * 도로명 주소가 없을 경우 
	 * 지번 주소를 도로명 주소로 변환 (Kakao API : 주소를 좌표로 변환하기)
	 * 주소 데이터 전송 목적의 AddressResponseDto DTO 활용
	 * @return : 도로명 주소
	 */
	public String convertAddress(String lat, String lng) {
        AddressResponseDto response = kakaoRestOpenFeign.convertAddress(lng, lat);
        
        if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new NotFoundException("주소 데이터를 찾을 수 없습니다.");
        }

        AddressResponseDto.Document doc = response.getDocuments().get(0); 

        if (doc.getRoad_address() != null) {
            return doc.getRoad_address().getRoad_name(); // 도로명 주소 반환
        } else if (doc.getAddress() != null) {
            return convertRoadAddress(doc.getAddress().getAddress_name()); // 지번 주소 변환
        }

        throw new IllegalArgumentException("주소 데이터가 유효하지 않습니다.");
	}
	
	/**
	 * 지번 주소를 도로명 주소로 변환 (Kakao API : 주소를 좌표로 변환하기)
	 * @param address : 지번 주소
	 * @return : 도로명 주소
	 */
	public String convertRoadAddress(String address) {
        AddressResponseDto response = kakaoRestOpenFeign.convertRoadAddress(address);

        if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new NotFoundException("도로명 주소 데이터를 찾을 수 없습니다.");
        }

        AddressResponseDto.Document doc = response.getDocuments().get(0);

        if (doc.getRoad_address() == null) {
        	throw new IllegalArgumentException("도로명 주소 데이터가 유효하지 않습니다.");
        }
        
        return doc.getRoad_address().getRoad_name(); // 도로명 주소 반환
	}
}
