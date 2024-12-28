package com.acorn.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.CommentsDto;
import com.acorn.process.CommentsProcess;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/eateries/comments")
@RequiredArgsConstructor
public class CommentsController {
	private final CommentsProcess process;
	
	// Create
	@PostMapping
	public ResponseEntity<CommentsDto> createComment(@RequestBody CommentsDto dto) {
		return ResponseEntity.ok(process.createComment(dto));
	}
	
	// Read
	@GetMapping("/{eateryNo}")
	public ResponseEntity<List<CommentsDto>> getCommentsByEatery(@PathVariable("eateryNo") int eateryNo) {
		return ResponseEntity.ok(process.getCommentsByEatery(eateryNo));
	}
	
	// Update
	
	// Delete
}
