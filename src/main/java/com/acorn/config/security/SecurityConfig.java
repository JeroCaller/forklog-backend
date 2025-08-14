package com.acorn.config.security;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.acorn.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration // 스프링 설정 클래스
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

	@Value("${file.upload-dir}")
	private String uploadBaseDir;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		// CORS 설정과 CSRF 비활성화, 세션 관리 정책 설정
		httpSecurity
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
				.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
				.httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리 정책 설정
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/main/mypage/**").hasRole("USER")
						.requestMatchers("/", "/auth/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**","/main/eateries/**", "/uploads/**", "/main/**", "/development/**", "/proxy/image/**", "/chat/**", "/ws/**", "/pub/**", "/index.html", "/static/**", "/*.png", "/favicon.ico").permitAll()
						.anyRequest().authenticated() // 그외 다른 요청은 인증 필요
				)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(new FailedAuthenticationEntryPoint())); // 인증 실패 시 처리 로직 설정

		return httpSecurity.build(); // 설정 완료 후 SecurityFilterChain 반환
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	// CORS 설정 메서드
	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern("http://localhost:*"); // React의 로컬 서버 주소
		configuration.addAllowedOriginPattern("https://*.sel4.cloudtype.app"); // Cloudtype 패턴의 주소 와일드카드로 허용 설정
		configuration.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
		configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
		configuration.setAllowCredentials(true); // 인증 정보 포함 요청 허용 (쿠키 등)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정을 적용
		return source;
	}

	@Bean
	public PasswordEncoder PasswordEncoder() {
		return new BCryptPasswordEncoder(); // BCryptPasswordEncoder 인스턴스 반환
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//정적리소스(이미지,css,js,데이터 경로) 추가 설정
		//지금은 업로드 경로 설정이 목적
		//Path uploadDir = Paths.get("./uploads");
		Path uploadDir = Paths.get(uploadBaseDir);
		//uploads 절대 경로 얻기
		String uploadPath = uploadDir.toFile().getAbsolutePath();
		//	/uploads/test.png라는 url이 들어오면 uploads디렉토리 내의 test.png를 반환
		registry.addResourceHandler("/uploads/**").addResourceLocations("file:"+uploadPath+"/");
		//"file:"+uploadPath+"/" : 파일 시스템의 uploads 디렉토리 경로를 나타냄
		//"file:"접두사를 붙임으로 해서 이 경로가 파일 시스템의 경로임을 지정한다.
	}
	
	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring().requestMatchers("/uploads/**");
	}
}