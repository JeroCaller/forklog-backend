package com.acorn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.acorn.api.openfeign")
public class AcornPrivateApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcornPrivateApplication.class, args);
	}

}
