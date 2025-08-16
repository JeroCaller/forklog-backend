package com.acorn.utils.cookie;

import com.acorn.common.Tokens;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author JeroCaller
 */
@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final CookieConfigurer cookieConfigurer;

    /**
     * 토큰을 쿠키에 담아 응답.
     *
     * @param httpServletResponse
     * @param whatToken 토큰 종류
     * @param tokenValue 토큰값. JWT의 경우 encode된 문자열 형태의 토큰값.
     */
    public void addTokenCookie(
        HttpServletResponse httpServletResponse,
        Tokens whatToken,
        String tokenValue
    ) {
        Cookie newCookie = new Cookie(whatToken.getTokenName(), tokenValue);
        cookieConfigurer.configureCookie(newCookie);
        newCookie.setMaxAge(whatToken.getMaxAgeInSeconds());
        httpServletResponse.addCookie(newCookie);
    }

    /**
     * 클라이언트가 보유한 특정 쿠키를 삭제.
     *
     * @param httpServletResponse
     * @param cookieName 삭제하고자 하는 쿠키명
     */
    public void deleteCookie(HttpServletResponse httpServletResponse, String cookieName) {
        Cookie cookieToDelete = new Cookie(cookieName, null);
        cookieConfigurer.configureCookie(cookieToDelete);
        cookieToDelete.setMaxAge(0);
        httpServletResponse.addCookie(cookieToDelete);
    }
}
