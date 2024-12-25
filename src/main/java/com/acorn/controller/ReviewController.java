package com.acorn.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.acorn.dto.ReviewRequestDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.model.ReviewsModel;

@RestController
@RequestMapping("/review")
public class ReviewController {
	private ReviewsModel reviewsModel;
	public ReviewController(ReviewsModel reviewsModel) {
		this.reviewsModel=reviewsModel;
	}
	//회원별 리뷰 목록 조회
	@GetMapping("/member/{memberNo}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByMemberNo(@PathVariable("memberNo") String memberNo){
		return ResponseEntity.ok(reviewsModel.getReviewsByMemberNo(memberNo));
	}
	//음식점별 리뷰 목록 조회
	@GetMapping("/eatery/{eateryNo}")
	public ResponseEntity<List<ReviewResponseDto>> getReviewsByEateryNo(@PathVariable("eateryNo") String eateryNo){
		return ResponseEntity.ok(reviewsModel.getReviewsByEateryNo(eateryNo));
	}
	
//	파일 목록 받기 (@RequestParam("files") List<MultipartFile> files): 여러 파일을 한 번에 받을 수 있도록
//	List<MultipartFile> 타입으로 받습니다. 프론트엔드에서 여러 파일을 files[]로 전송해야 합니다.
	@PostMapping
	public ResponseEntity<String> registerReview(@ModelAttribute("inputDto") ReviewRequestDto inputDto,
												@RequestParam(value = "files", required = false) List<MultipartFile> files) throws Exception {
		try {
			reviewsModel.registReview(inputDto, files);
			return ResponseEntity.ok("리뷰 등록에 성공했습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
		}
	}
}
