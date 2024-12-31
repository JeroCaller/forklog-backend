package com.acorn.process;

import com.acorn.entity.Eateries;
import com.acorn.entity.Favorites;
import com.acorn.repository.FavoritesRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FavoritesProcess {
    private final FavoritesRepository favoritesRepository;

    public FavoritesProcess(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }
    
    // 회원 번호로 음식점 리스트 조회(마이페이지)
    public List<Eateries> getFavoritesByMemberNo(int memberNo) {
        return favoritesRepository.findByMemberNo(memberNo);
    }

    @Transactional
    public void toggleFavorite(int memberNo, int eateryNo) {
    	// 회원 번호와 음식점 번호로 기존 즐겨찾기 여부를 조회
        Optional<Favorites> favoriteOptional =
                favoritesRepository.findByMemberNoAndEateryNo(memberNo, eateryNo);

        if (favoriteOptional.isPresent()) {
            // 이미 존재하면 상태를 반전
            Favorites favorite = favoriteOptional.get();
            favorite.setStatus(!favorite.getStatus());
            favoritesRepository.save(favorite);
        } else {
            // 즐겨찾기 데이터가 없으면 새로 등록
            Favorites newFavorite = Favorites.builder()
                    .memberNo(memberNo)
                    .eateryNo(eateryNo)
                    .status(true) // 기본 활성 상태로 설정
                    .build();
            favoritesRepository.save(newFavorite);
        }
    }
}
