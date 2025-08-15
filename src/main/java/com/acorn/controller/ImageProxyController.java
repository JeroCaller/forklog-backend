package com.acorn.controller;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 이미지 프록시를 제공하는 REST 컨트롤러 클래스.
 * 외부 URL로부터 이미지를 요청하고, 해당 이미지를 클라이언트에게 반환합니다.
 * 주로 CORS 문제 해결과 외부 이미지 접근 제한을 우회하기 위해 사용됩니다.
 *
 * @author jaeuk-choi
 */
@RestController
public class ImageProxyController {

    /**
     * 이미지 프록시 메서드
     * @param url 외부 이미지 URL (인코딩된 상태로 전달됨)
     * @return 이미지 바이트 배열과 함께 HTTP 응답
     */
    @GetMapping("/proxy/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam("url") String url) {
        try {
            // URL 이중 디코딩 - 한글이나 특수문자가 포함된 URL을 처리하기 위함
            String decodedUrl = URLDecoder.decode(
                URLDecoder.decode(url, StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
            );

            // RestTemplate 객체 생성 - HTTP 요청을 보내기 위한 스프링의 유틸리티 클래스
            RestTemplate restTemplate = new RestTemplate();

            // URL 객체 생성 - 호스트와 프로토콜 정보를 추출하기 위함
            URL urlObj = new URL(decodedUrl);

            // 요청 헤더 설정 - 실제 브라우저에서 보내는 것처럼 헤더를 구성
            HttpHeaders requestHeaders = new HttpHeaders();

            // User-Agent 설정 - 크롬 브라우저로 위장
            requestHeaders.set("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            );

            // Referer 설정 - 이미지 제공 사이트의 도메인을 referer로 사용
            requestHeaders.set("Referer", urlObj.getProtocol() + "://" + urlObj.getHost());
            
            // 추가 헤더 설정 - 브라우저와 유사한 환경 구성
            requestHeaders.set("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
            requestHeaders.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            requestHeaders.set("Cache-Control", "no-cache");
            requestHeaders.set("Connection", "keep-alive");

            // 요청 엔티티 생성 - 헤더만 포함된 HTTP 요청 객체
            HttpEntity<String> entity = new HttpEntity<>(requestHeaders);

            // 외부 이미지 URL로 HTTP GET 요청 실행
            ResponseEntity<byte[]> response = restTemplate.exchange(
        		decodedUrl,             // 요청 URL
                HttpMethod.GET,         // HTTP GET 메서드 사용
                entity,                 // 요청 헤더
                byte[].class            // 응답을 byte[]로 받음
            );

            // 응답 헤더 설정
            HttpHeaders responseHeaders = new HttpHeaders();
            
            // Content-Type 설정 - 원본 이미지의 타입을 유지
            if (response.getHeaders().getContentType() != null) {
                responseHeaders.setContentType(response.getHeaders().getContentType());
            } else {
                // 기본값으로 JPEG 설정 (타입을 알 수 없는 경우)
                responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            }

            // 캐시 설정 - 1년간 캐시 유지 (성능 최적화)
            responseHeaders.setCacheControl("public, max-age=31536000");
            responseHeaders.setExpires(System.currentTimeMillis() + 31536000000L);

            // 이미지 데이터와 함께 HTTP 200 OK 응답 반환
            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
            
        } catch (Exception e) {
            // 에러 발생시 로그 출력 후 400 Bad Request 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}