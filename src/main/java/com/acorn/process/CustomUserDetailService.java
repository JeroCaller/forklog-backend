package com.acorn.process;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(members.getRole()));
		
		UserDetails userDetails = new User(members.getEmail(), members.getPassword(), authorities);
		
		return userDetails;
	}
}
