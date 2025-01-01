package com.acorn.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.acorn.common.MemberRole;
import com.acorn.dto.MembersResponseDto;

public class AuthUtil {
	
	/**
	 * 현재 사용자의 역할(ROLE)을 Authentication으로부터 추출.
	 * 
	 * @author JeroCaller (JJH)
	 * @param auth
	 * @return
	 */
	public static String getRole(Authentication auth) {
		
		String result = null;
		
		// 한 명의 사용자가 둘 이상의 권한을 가질 순 없다고 가정.
		for (GrantedAuthority gAuth : auth.getAuthorities()) {
			result = gAuth.getAuthority();
			break;
		}
		
		return result;
	}
	
	/**
	 * 현재 사용자가 비로그인 상태인지 확인.
	 * 
	 * @author JeroCaller (JJH)
	 * @param auth
	 * @return
	 */
	public static boolean isAnonymousUser(Authentication auth) {
		// 어떠한 이유로 사용자의 Role을 가져올 수 없다면 비로그인 사용자로 간주.
		if (AuthUtil.getRole(auth) == null) return true;
		return AuthUtil.getRole(auth).equals(MemberRole.ROLE_ANONYMOUS);
	}
	
}
