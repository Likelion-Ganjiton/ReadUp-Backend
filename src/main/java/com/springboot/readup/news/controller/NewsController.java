package com.springboot.readup.news.controller;

import com.springboot.readup.news.dto.NewsDetailResponse;
import com.springboot.readup.news.dto.TodayNewsResponse;
import com.springboot.readup.news.service.NewsService;
import com.springboot.readup.news.service.TodayNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final TodayNewsService todayNewsService;

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(@PathVariable Long newsId) {
        NewsDetailResponse response = newsService.getNewsDetail(newsId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<TodayNewsResponse> getTodayNews(
            @AuthenticationPrincipal String loginId
    ) {
        TodayNewsResponse response = todayNewsService.getTodayFixedNews(loginId);
        return ResponseEntity.ok(response);
    }
}