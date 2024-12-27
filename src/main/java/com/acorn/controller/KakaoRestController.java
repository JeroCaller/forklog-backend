package com.acorn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.process.AddressProcess;

@RestController
public class KakaoRestController {
	private Logger log = LoggerFactory.getLogger(KakaoRestController.class);

	@Autowired
	private AddressProcess addressProcess;


	@GetMapping("/address")
	public ResponseEntity<Object> convertAddress(@RequestParam("lat") String lat, @RequestParam("lng") String lng) {
		return ResponseEntity.ok().body(addressProcess.convertAddress(lat, lng));
	}

}
