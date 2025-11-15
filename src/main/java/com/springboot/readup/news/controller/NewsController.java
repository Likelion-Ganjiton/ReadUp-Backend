package com.springboot.readup.news.controller;

import com.springboot.readup.news.dto.NewsDetailResponse;
import com.springboot.readup.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(@PathVariable Long newsId) {
        NewsDetailResponse response = newsService.getNewsDetail(newsId);
        return ResponseEntity.ok(response);
    }
}