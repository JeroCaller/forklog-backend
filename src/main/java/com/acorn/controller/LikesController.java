package com.acorn.controller;

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
	
	/* -- 좋아요 정보 읽기 -- */
	@GetMapping
	public ResponseEntity<LikesDto> readLike(@RequestParam(name = "memberNo") int memberNo,
		@RequestParam(name = "commentNo") int commentNo) {
		return ResponseEntity.ok(process.getLike(memberNo, commentNo));
	}
	
	/* -- 좋아요 취소하기 -- */
	@DeleteMapping("/{no}")
	public ResponseEntity<LikesDto> cancelLike(@PathVariable("no") int no) {
		process.deleteLike(no);
		return ResponseEntity.noContent().build();
	}
}
