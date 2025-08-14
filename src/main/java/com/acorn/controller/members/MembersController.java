package com.acorn.controller.members;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.dto.members.ProfileDto;
import com.acorn.process.members.MembersProcess;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/main/mypage/members")
@RequiredArgsConstructor
public class MembersController {
	
	private final MembersProcess membersProcess;
	
	// 조회
	@GetMapping("/read")
	public ResponseEntity<?> readAccount() {
		return membersProcess.readAccount();
	}
	
	// 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateAccount(@RequestBody @Valid ProfileDto dto) {
		return membersProcess.updateAccount(dto);
	}
	
	// 탈퇴
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteAccount(@RequestBody @Valid ProfileDto dto) {
		return membersProcess.deleteAccount(dto);
	}
	
	// 닉네임 중복 검사
	@GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        return membersProcess.checkNicknameDuplication(nickname);
    }
	
	// 재욱 : memberNo 조회 API
	@GetMapping("/member-no")
	public ResponseEntity<?> getMemberNo() {
	    return membersProcess.getMemberNo();
	}
}
