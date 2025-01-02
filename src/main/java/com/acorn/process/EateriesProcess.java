package com.acorn.process;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.acorn.dto.EateriesDto;
import com.acorn.entity.Eateries;
import com.acorn.repository.EateriesRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EateriesProcess {

	private final EateriesRepository eateriesRepository;
    
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
    
    /**
     * 특정 음식점의 조회수 1 증가 시키는 메서드.
     * 
     * 참고) 
     * Dirty Checking을 이용하여 update하는 방법 사용 시에는 
     * 업데이트 내용을 반영하기 위해 반드시 @Transactional 어노테이션을 부여해야 함.
     * 그렇지 않으면 내용이 DB에 반영되지 않음을 확인함.
     * 
     * @author JeroCaller (JJH)
     * @param eateriesNo
     * @return
     */
    @Transactional
    public boolean updateViewCount(int eateriesNo) {
    	Optional<Eateries> eateryOpt = eateriesRepository.findById(eateriesNo);
    	if (eateryOpt.isEmpty()) {
    		return false;  // 음식점 엔티티가 DB 내 미조회 시 false로 반환.
    	}
    	Eateries eatery = eateryOpt.get();
    	
    	// Dirty Checking을 이용하여 조회수 1 증가하도록 update.
    	// 즉, 새로운 엔티티 객체를 생성하여 repository에 save하는 방식이 아닌, 
    	// 이미 기존 영속성 컨텍스트에 존재하는 엔티티 객체 참조를 가져와 
    	// 해당 객체의 특정 필드값을 setter 메서드로 변경하는 방식임. 
    	// Dirty Checking 이용 시 repository.save()를 이용하지 않아도 
    	// 자동으로 업데이트 됨. 단 @Transactional 어노테이션을 반드시 부여해야 
    	// 실제 DB에 업데이트된 값이 반영됨. 
    	eatery.setViewCount(eatery.getViewCount() + 1);
    	
    	return true;
    }
    
}
