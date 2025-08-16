package com.acorn.utils.cookie;

import com.acorn.common.Tokens;
import com.acorn.jwt.JwtUtil;
import com.acorn.utils.cookie.impl.DefaultCookieConfig;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 안전한 리팩토링을 위한 테스트.
 *
 * @author JeroCaller
 */
@ExtendWith(SpringExtension.class)
@Import({
    CookieUtil.class,
    DefaultCookieConfig.class,
    JwtUtil.class
})
@Slf4j
@TestPropertySource(locations = "classpath:application-secret.yml")
class CookieUtilTest {

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private CookieConfigurer cookieConfigurer;

    @Autowired
    private JwtUtil jwtUtil;

    private MockHttpServletResponse mockHttpServletResponse;
    private Cookie comparasionCookie;  // 비교를 위한 쿠키

    @BeforeEach
    void setUp() {
        mockHttpServletResponse = new MockHttpServletResponse();

        comparasionCookie = new Cookie("TEST-COOKIE", null);
        comparasionCookie.setHttpOnly(true);
        comparasionCookie.setSecure(false);
        comparasionCookie.setPath("/");
    }

    @Test
    @DisplayName("테스트 환경 조성 테스트")
    void contextLoadTest() {
        log.info("테스트 환경 조성 성공.");
    }

    @Test
    @DisplayName("JWT 토큰이 담긴 쿠키 생성 여부 확인")
    void addJwtTokenCookieTest() {
        String testEmail = "test@mail.com";
        addJwtTokenCookieTestWith(testEmail, Tokens.ACCESS_TOKEN);
        addJwtTokenCookieTestWith(testEmail, Tokens.REFRESH_TOKEN);
    }

    private void addJwtTokenCookieTestWith(String testEmail, Tokens whatToken) {
        String token = null;

        switch (whatToken) {
            case ACCESS_TOKEN:
                token = jwtUtil.createAccessToken(testEmail);
                break;
            case REFRESH_TOKEN:
                token = jwtUtil.createRefreshToken(testEmail);
                break;
        }

        if (token == null) {
            log.error("테스트 대상 토큰이 null입니다.");
            return;
        }

        cookieUtil.addTokenCookie(mockHttpServletResponse, whatToken, token);

        Cookie tokenCookie = mockHttpServletResponse.getCookie(whatToken.getTokenName());
        String resultToken = tokenCookie.getValue();
        assertThat(tokenCookie).isNotNull();
        assertThat(resultToken).isNotNull();
        assertThat(jwtUtil.validate(resultToken)).isTrue();
        assertThat(jwtUtil.extractUseremail(resultToken)).isEqualTo(testEmail);
        assertThat(tokenCookie.getMaxAge())
            .isEqualTo(whatToken.getMaxAgeInSeconds());
        assertThat(tokenCookie.isHttpOnly()).isEqualTo(comparasionCookie.isHttpOnly());
        assertThat(tokenCookie.getSecure()).isEqualTo(comparasionCookie.getSecure());
        assertThat(tokenCookie.getPath()).isEqualTo(comparasionCookie.getPath());
    }

    @Test
    @DisplayName("JWT 토큰이 담긴 쿠키 삭제 기능 동작 테스트")
    void deleteJwtCookieTest() {
        String testEmail = "test@mail.com";
        cookieUtil.addTokenCookie(
            mockHttpServletResponse,
            Tokens.ACCESS_TOKEN,
            jwtUtil.createAccessToken(testEmail)
        );
        cookieUtil.addTokenCookie(
            mockHttpServletResponse,
            Tokens.REFRESH_TOKEN,
            jwtUtil.createRefreshToken(testEmail)
        );

        assertThat(mockHttpServletResponse.getCookie(Tokens.ACCESS_TOKEN.getTokenName()))
            .isNotNull();
        assertThat(mockHttpServletResponse.getCookie(Tokens.REFRESH_TOKEN.getTokenName()))
            .isNotNull();
        assertThat(mockHttpServletResponse.getCookies().length).isEqualTo(2);

        cookieUtil.deleteCookie(mockHttpServletResponse, Tokens.ACCESS_TOKEN.getTokenName());
        cookieUtil.deleteCookie(mockHttpServletResponse, Tokens.REFRESH_TOKEN.getTokenName());
        assertThat(mockHttpServletResponse.getCookies().length).isEqualTo(4);

        // 동명의 maxAge가 0인 쿠키 존재 시 해당 쿠키가 삭제되었다고 가정.
        Cookie accessTokenCookieWithZeroAge = getCookieWithZeroAge(
            Tokens.ACCESS_TOKEN.getTokenName(),
            mockHttpServletResponse.getCookies()
        );
        assertThat(accessTokenCookieWithZeroAge).isNotNull();
        assertThat(accessTokenCookieWithZeroAge.isHttpOnly())
            .isEqualTo(comparasionCookie.isHttpOnly());
        assertThat(accessTokenCookieWithZeroAge.getSecure())
            .isEqualTo(comparasionCookie.getSecure());
        assertThat(accessTokenCookieWithZeroAge.getPath())
            .isEqualTo(comparasionCookie.getPath());

        Cookie refreshTokenCookieWithZeroAge = getCookieWithZeroAge(
            Tokens.REFRESH_TOKEN.getTokenName(),
            mockHttpServletResponse.getCookies()
        );
        assertThat(refreshTokenCookieWithZeroAge).isNotNull();
        assertThat(refreshTokenCookieWithZeroAge.isHttpOnly())
            .isEqualTo(comparasionCookie.isHttpOnly());
        assertThat(refreshTokenCookieWithZeroAge.getSecure())
            .isEqualTo(comparasionCookie.getSecure());
        assertThat(refreshTokenCookieWithZeroAge.getPath())
            .isEqualTo(comparasionCookie.getPath());
    }

    private Cookie getCookieWithZeroAge(String targetCookieName, Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(targetCookieName) && cookie.getMaxAge() == 0) {
                return cookie;
            }
        }

        return null;
    }
}