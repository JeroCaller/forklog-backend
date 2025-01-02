package com.acorn.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.LikesDto;
import com.acorn.process.LikesProcess;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/eateries/comments/likes")
@RequiredArgsConstructor
public class LikesController {
	private final LikesProcess process;

	/* -- 좋아요 누르기 -- */
	@PostMapping
	public ResponseEntity<LikesDto> pressLike(@RequestBody LikesDto dto) {
		return ResponseEntity.ok(process.createLike(dto));
	}

	/* -- 로그인한 회원이 해당 음식점의 댓글에 남긴 모든 좋아요 읽기 -- */
	@GetMapping
	public ResponseEntity<List<LikesDto>> readLikes(@RequestParam("eateryNo") int eateryNo,
			@RequestParam("memberNo") int memberNo) {
		return ResponseEntity.ok(process.findLikesByEateryAndMember(eateryNo, memberNo));
	}
  
	/* -- 좋아요 취소하기 -- */
	@DeleteMapping("/{no}")
	public ResponseEntity<LikesDto> cancelLike(@PathVariable("no") int no) {
		process.deleteLike(no);
		return ResponseEntity.noContent().build();
	}
}
