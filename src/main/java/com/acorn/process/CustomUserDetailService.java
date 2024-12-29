package com.acorn.process;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.acorn.entity.Members;
import com.acorn.repository.MembersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final MembersRepository membersRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			
		Members members = membersRepository.findByEmail(email);
		
		if (members == null) {
            throw new UsernameNotFoundException("가입된 계정을 찾을 수 없습니다.");
        }
		
		return User.builder()
				.username(members.getEmail())
				.password(members.getPassword())
				.build();
	}
}
