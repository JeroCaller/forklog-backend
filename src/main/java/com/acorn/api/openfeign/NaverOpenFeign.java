package com.acorn.api.openfeign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "NaverBlogOpenFeign", url = "https://openapi.naver.com/v1/")
public interface NaverOpenFeign {
	@GetMapping("search/blog")
	Map<String, Object> searchBlog(@RequestParam("query") String blogName);
}
