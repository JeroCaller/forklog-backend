package com.acorn.process.eateries.reviews;

import com.acorn.dto.eateries.EateryResponseDto;
import com.acorn.entity.Favorites;
import com.acorn.repository.FavoritesRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoritesProcess {
    private final FavoritesRepository favoritesRepository;

    public FavoritesProcess(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }
    
 // 회원 번호로 음식점 리스트 조회(마이페이지) - status가 1인 것만 반환
    public List<EateryResponseDto> getFavoritesByMemberNo(int memberNo) {
        List<Favorites> favoritesList = favoritesRepository.findByMemberNoAndStatus(memberNo, 1); // status가 1인 즐겨찾기만 반환
        // 해당 Favorites 리스트를 EateryResponseDto로 변환해서 반환
        return favoritesList.stream()
                .map(f -> new EateryResponseDto(
                        f.getEatery().getNo(),
                        f.getEatery().getName(),
                        f.getEatery().getThumbnail(),
                        f.getEatery().getDescription(),
                        f.getEatery().getTel(),
                        f.getEatery().getAddress(),
                        f.getEatery().getRating(),
                        f.getEatery().getLongitude(),
                        f.getEatery().getLatitude(),
                        f.getEatery().getCategory().getName()
                ))
                .collect(Collectors.toList());
    }

    // 즐겨찾기 상태 토글
    @Transactional
    public void toggleFavorite(int memberNo, int eateryNo, int status) {
        // 회원 번호와 음식점 번호로 기존 즐겨찾기 여부를 조회
    	Optional<Favorites> optionalFavorite = favoritesRepository.findByMemberNoAndEateryNo(memberNo, eateryNo);

    	if (optionalFavorite.isPresent()) {
            Favorites favorite = optionalFavorite.get();
            // 상태를 요청받은 status로 설정
            favorite.setStatus(status);
            favoritesRepository.save(favorite);
        } else {
            // 즐겨찾기 데이터가 없으면 새로 등록
            Favorites newFavorite = Favorites.builder()
                    .memberNo(memberNo)
                    .eateryNo(eateryNo)
                    .status(status) // status 값 설정
                    .build();
            favoritesRepository.save(newFavorite);
        }
    }
    
    // 즐겨찾기 체크 확인
    public boolean checkFavoriteStatus(int memberNo, int eateryNo) {
        return favoritesRepository.existsByMemberNoAndEateryNo(memberNo, eateryNo);
    }
    
    // 특정 음식점의 즐겨찾기된 수를 조회(즐겨찾기 COUNT)
    public int getFavoritesCountForEatery(int eateryNo) {
        return favoritesRepository.countFavoritesByEateryNo(eateryNo);
    }
}
