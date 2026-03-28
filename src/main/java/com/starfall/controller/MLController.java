package com.starfall.controller;

import com.starfall.service.MLAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/compute")
public class MLController {

    private final MLAnalysisService mLAnalysisService;

    public MLController(MLAnalysisService mLAnalysisService) {
        this.mLAnalysisService = mLAnalysisService;
    }

    @PostMapping("/process")
    public ResponseEntity<HashMap<String, Object>> process(
            @RequestBody HashMap<String, Object> body) {

        HashMap<String, Object> result = mLAnalysisService.callPython(body);

        return ResponseEntity.ok(result);
    }
}