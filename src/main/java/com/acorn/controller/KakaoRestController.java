package com.acorn.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.api.KakaoRestApi;

@RestController
public class KakaoRestController {
	private Logger log = LoggerFactory.getLogger(KakaoRestController.class);

	@Autowired
	private KakaoRestApi kakaoRestApi;

	@GetMapping("/eatery")
	public ResponseEntity<Object> getEateries(@RequestParam("query") String searchValue) {
		return ResponseEntity.ok().body(kakaoRestApi.getEateries(searchValue));
	}

	@GetMapping("/address")
	public ResponseEntity<Object> convertAddress(@RequestParam("lat") String lat, @RequestParam("lng") String lng) {
		return ResponseEntity.ok().body(kakaoRestApi.convertAddress(lat, lng));
	}

}