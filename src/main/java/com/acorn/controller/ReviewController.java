package com.acorn.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.acorn.dto.ReviewImagesRequestDto;
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
	@Value("${file.upload-dir}")
	private String uploadDir;
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
//	const formData = new FormData();
//	formData.append("rating", 4.5);
//	formData.append("content", "Great review!");
//	formData.append("files", file1);
//	formData.append("files", file2); // 여러 파일 추가 가능
//	Axios로 파일 전송
//	axios.post("/review", formData, { headers: { "Content-Type": "multipart/form-data" } });
	@PostMapping
	public ResponseEntity<String> registerReview(@ModelAttribute ReviewRequestDto inputDto,
												@RequestParam("files") List<MultipartFile> files) throws Exception {
		List<ReviewImagesRequestDto> reviewImagesRequestDtoList = new ArrayList<>();
	    // 파일 처리 반복문
	    for (MultipartFile file : files) {
	        if (!file.isEmpty()) {
				String fileName = System.currentTimeMillis() + "_"+file.getOriginalFilename();
				Path uploadPath = Paths.get(uploadDir);
				if(Files.notExists(uploadPath)) Files.createDirectories(uploadPath);
				//resolve : fileName을 결합하여 최종 파일경로를 생성하는 메서드
				Path filePath = uploadPath.resolve(fileName);
				//파일 특정 경로로 복사, 이미 동일 파일 존재시 덮어쓰기
				//Files.copy() : InputStream으로부터 파일을 읽어 filePath위치에 파일을 저장
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				//사진 저장용 객체생성
				ReviewImagesRequestDto ReviewImagesRequestDto=new ReviewImagesRequestDto();
				ReviewImagesRequestDto.setImageUrl(filePath.toString());
				
	        }
	    }
		try {
			reviewsModel.registReview(reviewsModel.setReviewRequestDtoToEntity(dto));			
			return ResponseEntity.ok("리뷰 등록에 성공했습니다.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
		}
	}
}
