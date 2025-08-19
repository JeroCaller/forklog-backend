package com.acorn.process.auth;

import com.acorn.common.CommonNames;
import com.acorn.dto.auth.LoginResponseDto;
import com.acorn.dto.auth.LoginRequestDto;
import com.acorn.entity.Members;
import com.acorn.entity.RefreshToken;
import com.acorn.jwt.JwtAuthenticationProvider;
import com.acorn.process.CustomUserDetailService;
import com.acorn.repository.MembersRepository;
import com.acorn.repository.RefreshTokenRepository;
import com.acorn.utils.cookie.CookieUtil;
import com.acorn.utils.cookie.impl.DefaultCookieConfig;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 리팩토링을 위한 테스트 코드
 *
 * @author JeroCaller
 */
@ExtendWith(SpringExtension.class)
@Import({
    AuthProcessImpl.class,
    JwtAuthenticationProvider.class,
    CustomUserDetailService.class,
    BCryptPasswordEncoder.class,
    CookieUtil.class,
    DefaultCookieConfig.class
})
@Slf4j
// 테스트 편의성을 위해 jwt secret key를 기존 설정 파일로부터 가져온다.
@TestPropertySource(locations = "classpath:application-secret.yml")
class AuthProcessImplTest {

    @Autowired
    private AuthProcessImpl authProcessImpl;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CookieUtil cookieUtil;

    @MockitoBean
    private MailProcess mailProcess;

    @MockitoBean
    private MembersRepository membersRepository;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    private MockHttpServletResponse mockHttpServletResponse;

    @BeforeEach
    void setUp() {
        mockHttpServletResponse = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("테스트 환경에서 필요한 컨텍스트 로드 테스트")
    void contextLoadTest() {
        log.info("테스트 환경에서 필요한 컨텍스트 로드 완료");
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() {
        String rawPassword = "12345";
        Members memberOne = Members.builder()
            .no(1)
            .email("test@email.com")
            .password(passwordEncoder.encode(rawPassword))
            .role(CommonNames.ROLE_USER.getName())
            .status(CommonNames.STATUS_ACTIVE.getName())
            .build();

        Mockito.when(membersRepository.findByEmail(memberOne.getEmail()))
            .thenReturn(memberOne);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setNo(1);
        refreshTokenEntity.setEmail(memberOne.getEmail());
        refreshTokenEntity.setRefreshToken(jwtAuthenticationProvider.createRefreshToken(memberOne.getEmail()));
        Mockito.when(refreshTokenRepository.findByEmail(memberOne.getEmail()))
            .thenReturn(Optional.of(refreshTokenEntity));

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(memberOne.getEmail());
        loginRequestDto.setPassword(rawPassword);

        ResponseEntity<LoginResponseDto> authResult = (ResponseEntity<LoginResponseDto>)
            authProcessImpl.login(loginRequestDto, mockHttpServletResponse);

        assertThat(authResult.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Cookie accessTokenCookie = mockHttpServletResponse
            .getCookie(CommonNames.ACCESS_TOKEN_NAME.getName());
        Cookie refreshTokenCookie = mockHttpServletResponse
            .getCookie(CommonNames.REFRESH_TOKEN_NAME.getName());

        assertThat(accessTokenCookie).isNotNull();
        assertThat(jwtAuthenticationProvider.validate(accessTokenCookie.getValue())).isTrue();
        assertThat(jwtAuthenticationProvider.extractUseremail(accessTokenCookie.getValue()))
            .isEqualTo(memberOne.getEmail());

        assertThat(refreshTokenCookie).isNotNull();
        assertThat(jwtAuthenticationProvider.validate(refreshTokenCookie.getValue())).isTrue();
        assertThat(jwtAuthenticationProvider.extractUseremail(refreshTokenCookie.getValue()))
            .isEqualTo(memberOne.getEmail());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {
        ResponseEntity<?> logoutResult = authProcessImpl.logout(mockHttpServletResponse);
        assertThat(logoutResult.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());

        Cookie accessTokenCookie = mockHttpServletResponse
            .getCookie(CommonNames.ACCESS_TOKEN_NAME.getName());
        Cookie refreshTokenCookie = mockHttpServletResponse
            .getCookie(CommonNames.REFRESH_TOKEN_NAME.getName());

        assertThat(accessTokenCookie).isNotNull();
        assertThat(accessTokenCookie.getValue()).isNull();
        assertThat(accessTokenCookie.getMaxAge()).isZero();
        assertThat(jwtAuthenticationProvider.validate(accessTokenCookie.getValue())).isFalse();

        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie.getValue()).isNull();
        assertThat(refreshTokenCookie.getMaxAge()).isZero();
        assertThat(jwtAuthenticationProvider.validate(refreshTokenCookie.getValue())).isFalse();
    }
}