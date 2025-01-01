package com.acorn.controller;

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
 */
@RestController
public class ImageProxyController {

    /**
     * 이미지 프록시 메서드
     * @param url 외부 이미지 URL
     * @return 이미지 바이트 배열과 함께 HTTP 응답
     */
    @GetMapping("/proxy/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam("url") String url) {
        try {
            // URL 디코딩 (주어진 URL을 UTF-8로 디코딩)
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
            
            // RestTemplate 객체 생성 (외부 URL에 대한 HTTP 요청을 보내기 위해 사용)
            RestTemplate restTemplate = new RestTemplate();
            
            // 요청 헤더 설정
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.set("User-Agent", "Mozilla/5.0"); // 사용자 에이전트 설정 (브라우저로 위장)
            requestHeaders.set("Referer", "https://www.naver.com"); // Referer 헤더 설정 (특정 사이트에서 온 요청으로 위장)
            
            // 요청 엔티티 (헤더만 포함)
            HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
            
            // 외부 이미지 URL로 HTTP GET 요청을 보내고 응답을 byte[] 형식으로 받음
            ResponseEntity<byte[]> response = restTemplate.exchange(
                decodedUrl,             // 요청 URL
                HttpMethod.GET,         // HTTP GET 메서드 사용
                entity,                 // 요청 헤더
                byte[].class            // 응답을 byte[]로 받음
            );
            
            // 응답 헤더 설정 (이미지 반환을 위한 Content-Type 및 캐시 설정)
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_JPEG);  // 이미지 타입을 JPEG로 설정 (필요시 다른 타입도 설정 가능)
            responseHeaders.setCacheControl("no-store");           // 캐시를 사용하지 않도록 설정
            
            // 이미지 바이트 배열을 클라이언트에 반환
            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            // 예외 발생 시 HTTP 400 (BAD_REQUEST) 응답 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
