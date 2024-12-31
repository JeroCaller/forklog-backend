package com.acorn.process;

import com.acorn.dto.EateryResponseDto;
import com.acorn.entity.Eateries;
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
        List<Favorites> favoritesList = favoritesRepository.findByMemberNoAndStatus(memberNo, Boolean.TRUE); // status가 1인 즐겨찾기만 반환
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
    public void toggleFavorite(int memberNo, int eateryNo, Boolean status) {
        // 회원 번호와 음식점 번호로 기존 즐겨찾기 여부를 조회
        Optional<Favorites> favoriteOptional =
                favoritesRepository.findByMemberNoAndEateryNo(memberNo, eateryNo);

        if (favoriteOptional.isPresent()) {
            // 이미 존재하면 상태를 반전
            Favorites favorite = favoriteOptional.get();
            favorite.setStatus(status); // DTO에서 전달된 status로 업데이트
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
}
