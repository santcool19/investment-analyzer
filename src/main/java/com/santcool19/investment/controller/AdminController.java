package com.santcool19.investment.controller;

import com.santcool19.investment.service.AnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    private final AnalyzerService analyzerService;

    public AdminController(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }

    @PostMapping("/admin/process-now")
    public ResponseEntity<String> processNow() {
        String result = analyzerService.processNow();
        return ResponseEntity.ok(result);
    }
}

