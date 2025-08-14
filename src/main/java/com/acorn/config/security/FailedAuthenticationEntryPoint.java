package com.acorn.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

// FailedAuthenticationEntryPoint: 예외 상황(인증 실패)에 발생한 이벤트 처리를 위한 클래스
// AuthenticationEntryPoint :
// 스프링 시큐리티에서 인증이 필요한 리소스에 접근할 때 사용자가 인증되지 않았을 경우의 진입점을 정의하는 인터페이스, 인증 실패 시 어떤 응답을 반환할지를 결정한다.
public class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json"); // 응답 타입을 JSON으로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 상태 코드를 401로 설정
        response.getWriter().write("{\"code\": \"AF\", \"message\": \"Authorization Failed\"}"); // 예외 상황 발생 시 JSON 응답 작성
    }
}
