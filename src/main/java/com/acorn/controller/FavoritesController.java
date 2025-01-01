package com.acorn.controller;

import com.acorn.dto.FavoritesDto;
import com.acorn.process.FavoritesProcess;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/main/eateries")
public class FavoritesController {
    private final FavoritesProcess favoritesProcess;

    public FavoritesController(FavoritesProcess favortesProcess) {
        this.favoritesProcess = favortesProcess;
    }
    
    // 즐겨찾기 상태 확인
    @GetMapping("/{eateryNo}/favorites/{memberNo}")
    public ResponseEntity<Boolean> checkFavoriteStatus(
        @PathVariable("eateryNo") int eateryNo,
        @PathVariable("memberNo") int memberNo
    ) {
        boolean isFavorite = favoritesProcess.checkFavoriteStatus(memberNo, eateryNo);
        return ResponseEntity.ok(isFavorite);
    }
    
    // 즐겨찾기 수정(true, false)
    @PostMapping("/{no}/favorites")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable("no") int eateryNo,
            @RequestBody FavoritesDto favoritesDto
    ) {
    	favoritesProcess.toggleFavorite(favoritesDto.getMemberNo(), eateryNo, favoritesDto.getStatus());
        return ResponseEntity.ok("즐겨찾기 성공적으로 수정됨");
    }
    
    // 특정 음식점의 즐겨찾기 총 갯수 조회
    @GetMapping("/{eateryNo}/favorites/count")
    public ResponseEntity<Integer> getFavoritesCount(
        @PathVariable("eateryNo") int eateryNo
    ) {
    	int favoritesCount = favoritesProcess.getFavoritesCountForEatery(eateryNo);
        return ResponseEntity.ok(favoritesCount);
    }
}
