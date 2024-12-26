package com.acorn.process.openfeign.kakao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.api.openfeign.KakaoRestOpenFeign;
import com.acorn.dto.openfeign.kakao.image.ImageDocumentDto;
import com.acorn.dto.openfeign.kakao.image.ImageRequestDto;
import com.acorn.dto.openfeign.kakao.image.ImageResponseDto;

@Service
public class ImageSearchProcess {
	
	@Autowired
	private KakaoRestOpenFeign kakaoRestOpenFeign;
	
	/**
	 * 쿼리에 대한 이미지 한 장의 정보만 반환.
	 * 
	 * @author JeroCaller (JJH)
	 * @param query
	 * @return
	 */
	public ImageDocumentDto getOneImage(String query) {
		ImageRequestDto requestDto = ImageRequestDto.builder()
				.query(query)
				.build();
		ImageResponseDto responseDto 
			= kakaoRestOpenFeign.getEateryImage(requestDto);
		
		ImageDocumentDto result = null;
		if (responseDto.getDocuments() != null) {
			result = responseDto.getDocuments().getFirst();
		}
		return result;
	}
}
