package com.acorn.process;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.repository.EateriesRepository;

@Service
public class EateriesProcess {
	@Autowired
    private EateriesRepository eateriesRepository;
    
    // 음식점 no로 상세정보
    public Optional<EateriesDto> getEateryDtoById(int no) {
        return eateriesRepository.findById(no).map(eatery -> {
            
            return EateriesDto.builder()
            		.no(eatery.getNo())
            		.name(eatery.getName())
            		.rating(eatery.getRating())
            		.thumbnail(eatery.getThumbnail())
            		.address(eatery.getAddress())
            		.tel(eatery.getTel())
            		.categoryName(eatery.getCategory().getName())
            		.latitude(eatery.getLatitude())
            		.longitude(eatery.getLongitude())
            		.build();
        });
    }
}
