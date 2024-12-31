package com.acorn.controller;

import com.acorn.dto.FavoritesDto;
import com.acorn.process.FavoritesProcess;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/main/eateries")
public class FavoritesController {
    private final FavoritesProcess favortesProcess;

    public FavoritesController(FavoritesProcess favortesProcess) {
        this.favortesProcess = favortesProcess;
    }
    
    // 즐겨찾기 수정(true, false)
    @PostMapping("/{no}/favorites")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable("no") int eateryNo,
            @RequestBody FavoritesDto favoritesDto
    ) {
    	favortesProcess.toggleFavorite(favoritesDto.getMemberNo(), eateryNo);
        return ResponseEntity.ok("즐겨찾기 성공적으로 수정됨");
    }
}
