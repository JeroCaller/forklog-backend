package com.acorn.process;

import org.springframework.http.ResponseEntity;

import com.acorn.dto.ProfileDto;

public interface MembersProcess {
	
	ResponseEntity<?> readAccount();
	
	ResponseEntity<?> updateAccount(ProfileDto dto);
	
	ResponseEntity<?> deleteAccount(ProfileDto dto);
	
	ResponseEntity<?> checkNicknameDuplication(String nickname);
	
	//재욱
	ResponseEntity<?> getMemberNo();
}
