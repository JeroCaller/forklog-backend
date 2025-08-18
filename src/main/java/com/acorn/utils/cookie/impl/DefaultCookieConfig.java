package com.acorn.utils.cookie.impl;

import com.acorn.utils.cookie.CookieConfigurer;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class DefaultCookieConfig implements CookieConfigurer {

    @Override
    public void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 사용하는 경우 true로 변경
        cookie.setPath("/");
    }
}
