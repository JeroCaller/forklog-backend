package com.acorn.controller;

import com.acorn.dto.EateriesDto;
import com.acorn.process.EateriesProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/main/eateries")
public class EateriesController {
    @Autowired
    private EateriesProcess eateriesProcess;

    @GetMapping("/{id}")
    public ResponseEntity<EateriesDto> getEateryById(@PathVariable("id") int id) {
        Optional<EateriesDto> eateryDto = eateriesProcess.getEateryDtoById(id);
        return eateryDto
            .map(ResponseEntity::ok)         // EateriesDto를 그대로 반환
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
