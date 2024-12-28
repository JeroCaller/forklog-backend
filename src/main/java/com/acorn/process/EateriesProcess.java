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

    public Optional<Eateries> getEateryById(int id) {
        return eateriesRepository.findById(id);
    }
    
    public Optional<EateriesDto> getEateryDtoById(int id) {
        return eateriesRepository.findById(id).map(eatery -> {
            // 주소를 조합
        	/*
            String fullAddress = String.format("%s시 %s %s",
                eatery.getRoad().getLocations().getLocationGroups().getName(),  //시
                eatery.getRoad().getLocations().getName(),                     //구
                eatery.getRoad().getName()                                    //길
            );
            */
            
            /*
            return new EateriesDto(
                eatery.getNo(),
                eatery.getName(),
                eatery.getRating(),
                fullAddress,
                eatery.getTel(),
                eatery.getCategory().getName(),
                eatery.getLatitude(),    // 위도 추가
                eatery.getLongitude()    // 경도 추가
            );*/
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
