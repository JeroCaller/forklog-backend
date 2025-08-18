package com.acorn.jwt;

import com.acorn.common.CommonNames;
import com.acorn.common.Tokens;
import com.acorn.dto.auth.LoginRequestDto;
import com.acorn.dto.eateries.EateriesDto;
import com.acorn.entity.Categories;
import com.acorn.entity.CategoryGroups;
import com.acorn.entity.Eateries;
import com.acorn.entity.Favorites;
import com.acorn.entity.Members;
import com.acorn.repository.CategoriesRepository;
import com.acorn.repository.CategoryGroupsRepository;
import com.acorn.repository.EateriesRepository;
import com.acorn.repository.FavoritesRepository;
import com.acorn.repository.MembersRepository;
import com.acorn.repository.RefreshTokenRepository;
import com.acorn.response.ResponseStatusMessages;
import com.acorn.utils.cookie.CookieConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 안전한 리팩토링을 위한 JwtAuthenticationFilter 테스트.
 * 인증 및 인증된 사용자에게만 특정 자원 접근 가능하도록 작동하는지의 테스트를 통해
 * 해당 필터를 테스트.
 *
 * <p>Note)</p>
 * <p>
 *     {@code @ActiveProfiles("...")} - 값으로 명시된 profile을 찾아 그 안에 있는 설정값들을 가져온다.
 *     여기서 profile은 application-{...}.yml 과 같은 설정 파일들이라 볼 수 있다.
 *     이 모듈에서는 <code>src/test/resources/application-test.yml</code> 파일의 설정값들을 사용하도록
 *     설정하였다.
 * </p>
 *
 * @author JeroCaller
 */
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@Transactional  // 테스트 후 삽입 된 데이터들에 대해 자동 롤백이 가능하도록 설정함.
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private CategoryGroupsRepository categoryGroupsRepository;

    @Autowired
    private EateriesRepository eateriesRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private FavoritesRepository favoritesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieConfigurer cookieConfigurer;

    private final String MOCK_USER_EMAIL = "test@mail.com";

    @BeforeEach
    void setUpEachTest() {
        Members memberOne = membersRepository.save(
            Members.builder()
                .email(MOCK_USER_EMAIL)
                .password(passwordEncoder.encode("abc12345"))
                .role(CommonNames.ROLE_USER.getName())
                .roadAddress("서울 강남구")
                .build()
        );

        CategoryGroups koreanCategory = categoryGroupsRepository.save(
            CategoryGroups.builder()
                .name("한식")
                .build()
        );

        Categories meatCategory = categoriesRepository.save(
            Categories.builder()
                .name("고기")
                .group(koreanCategory)
                .build()
        );
        Categories bunsikCategory = categoriesRepository.save(
            Categories.builder()
                .name("분식")
                .group(koreanCategory)
                .build()
        );
        Categories gukBabCategory = categoriesRepository.save(
            Categories.builder()
                .name("국밥")
                .group(koreanCategory)
                .build()
        );

        Eateries eateriesOne = eateriesRepository.save(
            Eateries.builder()
                .name("내가 좋아하는 고깃집")
                .category(meatCategory)
                .build()
        );
        Eateries eateriesTwo = eateriesRepository.save(
            Eateries.builder()
                .name("내가 좋아하는 분식집")
                .category(bunsikCategory)
                .build()
        );
        Eateries eateriesThree = eateriesRepository.save(
            Eateries.builder()
                .name("아직 안 가본 고깃집")
                .category(meatCategory)
                .build()
        );
        Eateries eateriesFour = eateriesRepository.save(
            Eateries.builder()
                .name("아직 안 가본 분식집")
                .category(bunsikCategory)
                .build()
        );
        Eateries eateriesFive = eateriesRepository.save(
            Eateries.builder()
                .name("평범한 국밥집")
                .category(gukBabCategory)
                .build()
        );

        favoritesRepository.save(
            Favorites.builder()
                .status(1)
                .memberNo(memberOne.getNo())
                .eateryNo(eateriesOne.getNo())
                .build()
        );
        favoritesRepository.save(
            Favorites.builder()
                .status(1)
                .memberNo(memberOne.getNo())
                .eateryNo(eateriesTwo.getNo())
                .build()
        );
    }

    @AfterEach
    void cleanEachTest() {
        favoritesRepository.deleteAll();
        eateriesRepository.deleteAll();
        categoriesRepository.deleteAll();
        categoryGroupsRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        membersRepository.deleteAll();
    }

    @Test
    @DisplayName("테스트를 위한 데이터 DB 저장 여부 확인")
    void mockDataShouldBeSavedToDBForTest() {
        Members targetMember = membersRepository.findByEmail(MOCK_USER_EMAIL);
        assertThat(targetMember).isNotNull();
        assertThat(targetMember.getEmail()).isEqualTo(MOCK_USER_EMAIL);
        assertThat(targetMember.getRole()).isNotEmpty();
        assertThat(targetMember.getRole()).isEqualTo(CommonNames.ROLE_USER.getName());
        log.info("member info)");
        log.info("email: {}", targetMember.getEmail());
        log.info("no: {}", targetMember.getNo());
        log.info("password: {}", targetMember.getPassword());

        List<Categories> favCategories = categoriesRepository.findByMemberFavorite(targetMember);
        assertThat(favCategories.size()).isNotZero();
        assertThat(favCategories.size()).isEqualTo(2);

        List<Eateries> recommendedEateries = eateriesRepository
            .findByCategoryIn(favCategories, PageRequest.of(0, 10)).toList();
        assertThat(recommendedEateries.size()).isNotZero();
        assertThat(recommendedEateries.size()).isEqualTo(4);

        log.info("the member's favorite categories)");
        for (Categories favCate : favCategories) {
            log.info("대분류: {}, 소분류: {}", favCate.getGroup().getName(), favCate.getName());

            assertThat(Arrays.asList("고기", "분식").contains(favCate.getName())).isTrue();
            assertThat(favCate.getGroup().getName()).isEqualTo("한식");
        }

        log.info("추천 음식점 목록");

        // 사용자가 즐겨찾기한 카테고리들 중 하나에 포함되는 아직 즐겨찾기하지 않은 음식점 조회 여부 확인.
        boolean hasEateriesFour = false;

        // 사용자가 즐겨찾기하지 않은 카테고리의 음식점이 데이터 조회 결과에 포함되었는지 여부 확인.
        boolean hasEateriesFive = false;

        for (Eateries eateries : recommendedEateries) {
            log.info(
                "음식점명: {}, 카테고리 대분류: {}, 카테고리 소분류: {}",
                eateries.getName(),
                eateries.getCategory().getGroup().getName(),
                eateries.getCategory().getName()
            );

            if (eateries.getName().equals("아직 안 가본 분식집")) {
                hasEateriesFour = true;
            }

            assertThat(eateries.getCategory().getGroup().getName()).isEqualTo("한식");
            if (eateries.getCategory().getName().equals("국밥")) {
                hasEateriesFive = true;
            }
        }

        assertThat(hasEateriesFour).isTrue();
        assertThat(hasEateriesFive).isFalse();
    }

    @Test
    @DisplayName("테스트를 위한 스프링 컨텍스트 로드 여부 확인")
    void contextLoad() {
        log.info("테스트를 위한 스프링 컨텍스트 로드 완료");
    }

    @Test
    @DisplayName("로그인 테스트")
    void shouldBeAuthenticated() throws Exception {
        getAuthResult();
    }

    @Test
    @DisplayName("로그인 직후 보호된 자원에 접근 가능해야 한다.")
    void shouldBeAbleToAccessToProtectedResource() throws Exception {
        final MvcResult authResult = getAuthResult();
        String accessTokenValue = authResult.getResponse()
            .getCookie(Tokens.ACCESS_TOKEN.getTokenName())
            .getValue();

        // access token은 request header에, refresh token은 쿠키에 실어 요청한다.
        final MvcResult requestResult = mockMvc.perform(
            get("/main/members/eateries/recommends")
                .header(CommonNames.HEADER_AUTH.getName(),
                    CommonNames.HEADER_BEARER.getName() + accessTokenValue
                )
                .cookie(authResult.getResponse().getCookie(Tokens.REFRESH_TOKEN.getTokenName()))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(ResponseStatusMessages.READ_SUCCESS))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.content").exists())
            .andDo(print())
            .andReturn();

        // 보호된 자원(사용자가 즐겨찾기한 카테고리 기반 음식점 추천)에 대한 검증.
        List<EateriesDto> recommendedEateries = JsonPath.read(
            requestResult.getResponse().getContentAsString(),
            "$.data.content"
        );
        assertThat(recommendedEateries.isEmpty()).isFalse();
        assertThat(recommendedEateries.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("로그인 과정을 거치지 않아 유효한 jwt들이 없는 상태에서는 보호된 자원에 접근 불가능해야 한다.")
    void shouldNotBeAbleToAccessToProtectedResourcesWithoutValidTokens() throws Exception {
        final MvcResult requestResult = mockMvc.perform(
            get("/main/members/eateries/recommends")
        )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message")
                .value(ResponseStatusMessages.UNAUTHORIZED_MEMBER)
            )
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print())
            .andReturn();
    }

    @Test
    @DisplayName("""
        Access token은 없고 유효한 refresh token만 가지고 보호 자원 요청할 경우에도 정상 응답 받아야함.
    """)
    void shouldBeAbleToAccessToProtectedResourcesWhenThereIsOnlyValidRefreshToken() throws Exception {
        final MvcResult authResult = getAuthResult();

        // access token 없이 유효한 refresh token만 쿠키에 실어 요청
        final MvcResult requestResult = mockMvc.perform(
            get("/main/members/eateries/recommends")
            .cookie(authResult.getResponse().getCookie(Tokens.REFRESH_TOKEN.getTokenName()))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(ResponseStatusMessages.READ_SUCCESS))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.content").exists())
            .andDo(print())
            .andReturn();

        // 보호된 자원(사용자가 즐겨찾기한 카테고리 기반 음식점 추천)에 대한 검증.
        List<EateriesDto> recommendedEateries = JsonPath.read(
            requestResult.getResponse().getContentAsString(),
            "$.data.content"
        );
        assertThat(recommendedEateries.isEmpty()).isFalse();
        assertThat(recommendedEateries.size()).isEqualTo(4);

        // 유효한 access token이 응답과 함께 왔는지 검증.
        Cookie accessTokenCookie = requestResult.getResponse()
            .getCookie(Tokens.ACCESS_TOKEN.getTokenName());
        assertThat(accessTokenCookie).isNotNull();
        assertThat(accessTokenCookie.getValue()).isNotEmpty();
        assertThat(jwtUtil.validate(accessTokenCookie.getValue())).isTrue();
        assertThat(jwtUtil.extractUseremail(accessTokenCookie.getValue()))
            .isEqualTo(MOCK_USER_EMAIL);
    }

    @Test
    @DisplayName("Access token은 아예 없고, 만료된 리프레시 토큰을 가지고 보호된 자원 요청 시 거절당해야한다.")
    void shouldNotBeAbleToAccessToProtectedResourcesWithoutAccessTokenAndWithInvalidRefreshToken()
        throws Exception
    {
        String invalidRefreshToken = jwtUtil.create(MOCK_USER_EMAIL, 0);
        assertThat(jwtUtil.validate(invalidRefreshToken)).isFalse();

        // 유효한 쿠키에 만료된 리프레시 토큰 삽입.
        Cookie refreshTokenCookieForRequest = new Cookie(
            Tokens.REFRESH_TOKEN.getTokenName(),
            invalidRefreshToken
        );
        cookieConfigurer.configureCookie(refreshTokenCookieForRequest);
        refreshTokenCookieForRequest.setMaxAge(Tokens.REFRESH_TOKEN.getMaxAgeInSeconds());

        // access token은 없는 상태에서 만료된 refresh token을 쿠키에 담아 보호 자원 요청.
        final MvcResult requestResult = mockMvc.perform(
            get("/main/members/eateries/recommends")
                .cookie(refreshTokenCookieForRequest)
        )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message")
                .value(ResponseStatusMessages.UNAUTHORIZED_MEMBER)
            )
            .andDo(print())
            .andReturn();

        // 응답 쿠키 없음 검증.
        assertThat(requestResult.getResponse().getCookies().length).isZero();
    }

    @Test
    @DisplayName("모두 유효하지 않은 JWT 토큰들로 보호된 자원 요청 시 해당 자원을 응답받을 수 없어야 한다.")
    void shouldNotBeAbleToAccessToProtectedResourcesWithInvalidTokens() throws Exception {
        String invalidAccessToken = jwtUtil.create(MOCK_USER_EMAIL, 0);
        String invalidRefreshToken = jwtUtil.create(MOCK_USER_EMAIL, 0);
        assertThat(jwtUtil.validate(invalidAccessToken)).isFalse();
        assertThat(jwtUtil.validate(invalidRefreshToken)).isFalse();

        // 유효한 쿠키에 만료된 리프레시 토큰 삽입.
        Cookie refreshTokenCookieForRequest = new Cookie(
            Tokens.REFRESH_TOKEN.getTokenName(),
            invalidRefreshToken
        );
        cookieConfigurer.configureCookie(refreshTokenCookieForRequest);
        refreshTokenCookieForRequest.setMaxAge(Tokens.REFRESH_TOKEN.getMaxAgeInSeconds());

        // 요청 방식 자체는 올바른 방식으로 요청한다. 즉, access token은 Authorization 헤더에,
        // refresh token은 쿠키에 담아 요청.
        final MvcResult requestResult = mockMvc.perform(
            get("/main/members/eateries/recommends")
                .header(
                    CommonNames.HEADER_AUTH.getName(),
                    CommonNames.HEADER_BEARER + invalidAccessToken
                )
                .cookie(refreshTokenCookieForRequest)
        )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message")
                .value(ResponseStatusMessages.UNAUTHORIZED_MEMBER)
            )
            .andExpect(jsonPath("$.data").doesNotExist())
            .andDo(print())
            .andReturn();

        // 응답 쿠키 없음 검증.
        assertThat(requestResult.getResponse().getCookies().length).isZero();
    }

    /**
     * <p>인증 과정을 거친 후의 HttpResponse 객체를 반환.</p>
     * <p>테스트 검증 코드도 있음.</p>
     *
     * @return
     * @throws Exception
     */
    private MvcResult getAuthResult() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(MOCK_USER_EMAIL);
        loginRequestDto.setPassword("abc12345");

        final MvcResult authRequestResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
        Cookie accessTokenCookie = authRequestResult.getResponse()
            .getCookie(Tokens.ACCESS_TOKEN.getTokenName());
        Cookie refreshTokenCookie = authRequestResult.getResponse()
            .getCookie(Tokens.REFRESH_TOKEN.getTokenName());

        assertThat(accessTokenCookie).isNotNull();
        assertThat(accessTokenCookie.getValue()).isNotEmpty();
        assertThat(jwtUtil.extractUseremail(accessTokenCookie.getValue()))
            .isEqualTo(loginRequestDto.getEmail());
        assertThat(jwtUtil.validate(accessTokenCookie.getValue()))
            .isTrue();

        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie.getValue()).isNotEmpty();
        assertThat(jwtUtil.extractUseremail(refreshTokenCookie.getValue()))
            .isEqualTo(loginRequestDto.getEmail());
        assertThat(jwtUtil.validate(refreshTokenCookie.getValue()))
            .isTrue();

        return authRequestResult;
    }
}