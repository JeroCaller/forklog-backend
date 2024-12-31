package com.acorn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.acorn.dto.EateryResponseDto;
import com.acorn.dto.ReviewRequestDto;
import com.acorn.dto.ReviewResponseDto;
import com.acorn.entity.Eateries;
import com.acorn.process.FavoritesProcess;
import com.acorn.process.ReviewsProcess;
import com.acorn.repository.MembersRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/mypage")
@RequiredArgsConstructor
public class MypageController {
	private final ReviewsProcess reviewsProcess;
	
	private final FavoritesProcess favoritesProcess;
	
	private final MembersRepository membersRepository;
	
	//회원별 리뷰 목록 조회
	@GetMapping("/review/member/{no}")
	public ResponseEntity<Object> getReviewsByMemberNo(
				@RequestParam(value = "page", required = false, defaultValue = "1" ) int page,
				@PathVariable("no") String memberNo,Authentication authentication){
		Page<ReviewResponseDto> list= reviewsProcess.getReviewsByMemberNo(memberNo,page-1);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("page", page);
		return ResponseEntity.ok(map);
	}
	//별점 리뷰 등록
	//파일 목록 받기 (@RequestParam("files") List<MultipartFile> files): 여러 파일을 한 번에 받을 수 있도록
	//List<MultipartFile> 타입으로 받습니다. 프론트엔드에서 여러 파일을 files[]로 전송해야 합니다.
	//formData에 reviewNo 없이 보내야 합니다.
	@PostMapping("/review")
	public ResponseEntity<String> registerReview(@ModelAttribute ReviewRequestDto inputDto, Authentication authentication,
												@RequestParam(value = "files", required = false) List<MultipartFile> files) throws Exception {
	    if (files == null) {
	        files = new ArrayList<>();
	    }
		reviewsProcess.registReview(inputDto, files);
		return ResponseEntity.ok("리뷰 등록에 성공했습니다.");
	}
	//별점 리뷰 수정
	//formData에 reviewNo 넣어서 보내야 합니다.
	@PutMapping("/review/{no}")
	public ResponseEntity<String> updateReview(@ModelAttribute("inputDto") ReviewRequestDto inputDto,
					@PathVariable("no") String reviewNo,Authentication authentication,
					@RequestParam(value = "files", required = false) List<MultipartFile> files) throws Exception {
	    if (files == null) {
	        files = new ArrayList<>();
	    }
		reviewsProcess.updateReview(reviewNo,inputDto, files);
		return ResponseEntity.ok("리뷰 수정에 성공했습니다.");
	}
	//별점 리뷰 삭제
	@DeleteMapping("/review/{no}")
	public ResponseEntity<String> deleteReview(@PathVariable("no") String reviewNo,Authentication authentication)throws Exception{
		reviewsProcess.deleteReview(reviewNo);
		return ResponseEntity.ok("리뷰 삭제에 성공했습니다.");
	}
	
	// 마이페이지 즐겨찾기 조회 (@AuthenticationPrincipal : SecurityContext에서 인증된 사용자의 정보)
	@GetMapping("/favorites")
	public ResponseEntity<List<EateryResponseDto>> getFavoriteEateries(
	        @AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }
	    String email = userDetails.getUsername();
	    int memberNo = membersRepository.findNoByEmail(email);
	    
	    List<EateryResponseDto> favoriteEateries = favoritesProcess.getFavoritesByMemberNo(memberNo);
	    
	    return ResponseEntity.ok(favoriteEateries);
	}

}
