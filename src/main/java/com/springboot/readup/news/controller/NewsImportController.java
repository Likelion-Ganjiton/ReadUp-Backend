package com.springboot.readup.news.controller;

import com.springboot.readup.news.service.NewsImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class NewsImportController {

    private final NewsImportService newsImportService;

    @PostMapping("/json")
    public ResponseEntity<String> importJson(@RequestParam String path) {
        try {
            newsImportService.importJsonFile(path);
            return ResponseEntity.ok("경제 뉴스 저장 완료!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("오류: " + e.getMessage());
        }
    }
}