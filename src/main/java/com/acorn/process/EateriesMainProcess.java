package com.acorn.process;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.repository.EateriesRepository;

import lombok.RequiredArgsConstructor;

/**
 * 메인 페이지에서 음식점을 다루기 위한 서비스 클래스
 * 
 * (API 요청 관련 로직 전혀 없음. 오로지 DB로부터 읽어오기만.) 
 * 
 */
@Service
@RequiredArgsConstructor
public class EateriesMainProcess {
	
	private final EateriesRepository eateriesRepository;
	
	/**
	 * 지역 주소 입력 시 그와 비슷한 주소에 해당하는 음식점 정보들을 반환.
	 * 
	 * @param address
	 * @param pageRequest
	 * @return
	 */
	public Page<EateriesDto> getEateriesByAddressLike(String address, Pageable pageRequest) {
		Page<Eateries> eateries = eateriesRepository.findByAddressLike(address, pageRequest);
		return eateries.map(EateriesDto :: toDto);
	}
}
