package com.acorn.utils.cookie;

import jakarta.servlet.http.Cookie;

/**
 * <p>
 *     공통으로 적용할 쿠키 설정을 위한 인터페이스.
 *     쿠키에 대해 적용할 설정들을 여러 개 만들고 원하는 설정을 적용하기 위해,
 *     그리고 테스트 용이성을 위해 인터페이스를 정의함.
 * </p>
 *
 * @author JeroCaller
 */
public interface CookieConfigurer {

    void configureCookie(Cookie cookie);
}
