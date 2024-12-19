package com.acorn.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.acorn.api.NaverBlogSearch;

@RestController
public class NaverController {
    @Autowired
    private NaverBlogSearch naverBlogSearch;

    @GetMapping("blog/{blogTitle}")
    public ResponseEntity<Map<String, Object>> getBlogInfo(@PathVariable("blogTitle") String blogTitle) {
        return ResponseEntity.ok().body(naverBlogSearch.searchBlog(blogTitle));
    }

    @GetMapping("locations")
    public List<Map<String, Object>> getLocations() {
        // 예제 데이터
        List<Map<String, Object>> locations = new ArrayList<>();
        locations.add(Map.of(
                "name", "서울시청",
                "lat", 37.5665,
                "lng", 126.9780
        ));

        locations.add(Map.of(
                "name", "남산타워",
                "lat", 37.5512,
                "lng", 126.9882
        ));

        locations.add(Map.of(
                "name", "에이콘아카데미",
                "lat", 37.4987,
                "lng", 127.0316
        ));
        return locations;
    }
}
