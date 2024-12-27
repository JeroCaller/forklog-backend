package com.acorn.process;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.acorn.entity.MembersMain;
import com.acorn.repository.MembersMainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final MembersMainRepository membersMainRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			
		MembersMain membersMain = membersMainRepository.findByEmail(email);
		
		return User.builder()
				.username(membersMain.getEmail())
				.password(membersMain.getPassword())
				.build();
	}
}
