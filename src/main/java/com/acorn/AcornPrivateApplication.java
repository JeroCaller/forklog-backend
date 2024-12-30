package com.acorn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * 1. Page 객체를 JSON으로 응답 시 직렬화 이슈가 있다고 한다. 
 * 게다가, Page 객체를 그대로 응답 시 JSON 응답 구조가 복잡해지며, 
 * 콘솔창에서도 이와 관련된 경고 메시지가 출력된다. 
 * 따라서 스프링 버전 3.3부터 제공하는 아래 어노테이션을 다음 속성과 더불어 Application.java에 등록하면...
 * @EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
 * 
 * 개발자가 만든 DTO를 기준으로 직렬화가 진행된다. 
 * 그러면 JSON 응답 구조도 간결해지며, 해당 경고도 나타나지 않는다. 
 * 
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.acorn.api.openfeign")
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class AcornPrivateApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcornPrivateApplication.class, args);
	}

}
