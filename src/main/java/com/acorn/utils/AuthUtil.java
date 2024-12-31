package com.acorn.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

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
}
