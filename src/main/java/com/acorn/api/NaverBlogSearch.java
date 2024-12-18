package com.acorn.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class NaverBlogSearch { // 네이버 검색 API 예제 - 블로그 검색
	private Logger log = LoggerFactory.getLogger(NaverBlogSearch.class);

	@Value("${naver.search.clientId}")
	private String clientId; // 애플리케이션 클라이언트 아이디

	@Value("${naver.search.clientSecret}")
	private String clientSecret; // 애플리케이션 클라이언트 시크릿

	@Autowired
	private ObjectMapper objectMapper; // JSON 처리용 ObjectMapper

	/**
	 * 블로그명을 입력 받아 블로그 정보를 Map으로 반환
	 * @param blogName 블로그명
	 * @return Map 형태의 검색 결과
	 */
	public Map<String, Object> searchBlog(String blogName) {
		String text = URLEncoder.encode(blogName, StandardCharsets.UTF_8);
		log.info("검색어 : {}", blogName);
		String apiURL = "https://openapi.naver.com/v1/search/blog?query=".concat(text); // JSON 결과

		Map<String, String> requestHeaders = new HashMap<>();
		requestHeaders.put("X-Naver-Client-Id", clientId);
		requestHeaders.put("X-Naver-Client-Secret", clientSecret);

		String response = get(apiURL, requestHeaders);
		log.info("RESPONSE : {}", response);

		try { // JSON을 Map으로 변환
			return objectMapper.readValue(response, Map.class);
		} catch (IOException e) {
			throw new RuntimeException("JSON 응답을 Map으로 변환하는 데 실패했습니다.", e);
		}
	}

	private static String get(String apiUrl, Map<String, String> requestHeaders) {
		HttpURLConnection con = connect(apiUrl);
		try {
			con.setRequestMethod("GET");
			requestHeaders.forEach(con::setRequestProperty);

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
				return readBody(con.getInputStream());
			} else { // 오류 발생
				return readBody(con.getErrorStream());
			}
		} catch (IOException e) {
			throw new RuntimeException("API 요청과 응답 실패", e);
		} finally {
			con.disconnect();
		}
	}

	private static HttpURLConnection connect(String apiUrl) {
		try {
			URL url = new URI(apiUrl).toURL();
			return (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
		} catch (IOException e) {
			throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI 연결에 실패했습니다. : " + apiUrl, e);
		}
	}

	private static String readBody(InputStream body) {
		InputStreamReader streamReader = new InputStreamReader(body);

		try (BufferedReader lineReader = new BufferedReader(streamReader)) {
			StringBuilder responseBody = new StringBuilder();

			String line;
			while ((line = lineReader.readLine()) != null) {
				responseBody.append(line);
			}

			return responseBody.toString();
		} catch (IOException e) {
			throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
		}
	}
}